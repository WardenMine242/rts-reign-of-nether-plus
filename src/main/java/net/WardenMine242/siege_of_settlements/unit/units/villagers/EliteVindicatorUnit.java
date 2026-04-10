package net.WardenMine242.siege_of_settlements.unit.units.villagers;

import com.solegendary.siege_of_settlements.ability.Abilities;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.resources.ResourceName;
import com.solegendary.siege_of_settlements.resources.Resources;
import com.solegendary.siege_of_settlements.resources.ResourcesClientboundPacket;
import com.solegendary.siege_of_settlements.resources.ResourcesServerEvents;
import com.solegendary.siege_of_settlements.unit.goals.AbstractMeleeAttackUnitGoal;
import com.solegendary.siege_of_settlements.unit.units.villagers.VindicatorUnit;
import net.WardenMine242.siege_of_settlements.ability.abilities.EliteVindicatorBerserk;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.UUID;

public class EliteVindicatorUnit extends VindicatorUnit {
    public static final Abilities ABILITIES = new Abilities();
    static {
        ABILITIES.add(new EliteVindicatorBerserk(), Keybindings.keyQ);
    }

    private static final EntityDataAccessor<Integer> ATTACK_SWING_INDEX =
            SynchedEntityData.defineId(EliteVindicatorUnit.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DAMAGE_TIER =
            SynchedEntityData.defineId(EliteVindicatorUnit.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> BERSERK_ACTIVE =
            SynchedEntityData.defineId(EliteVindicatorUnit.class, EntityDataSerializers.BOOLEAN);
    private static final int HIT_DELAY_TICKS = 3;
    private static final float ATTACK_COOLDOWN_TICKS = 14.0F;
    private static final float BERSERK_ATTACK_COOLDOWN_MULT = 0.5F; // x2 attack speed
    private static final float BONUS_MELEE_RANGE = 0.85F;
    private static final float ELITE_ATTACK_RANGE = 1.0F;
    private static final float ELITE_MAX_HEALTH = 120.0F;
    private static final int MAX_DAMAGE_TIER = 4; // x2.0 max
    private static final int EMERALD_DRAIN_AMOUNT = 300;
    private static final int EMERALD_DRAIN_INTERVAL_TICKS = 20 * 60 * 10; // 10 minutes
    private static final String TAG_EMERALD_DRAIN_PROGRESS = "ronPlusEliteVindicatorEmeraldDrainProgress";
    private static final int BERSERK_DURATION_TICKS = 20 * 10;
    private static final int BERSERK_RAPID_HITS_PER_BURST = 30;
    private static final int BERSERK_RAPID_HIT_DELAY_TICKS = 10;
    private static final double BASE_MOVEMENT_SPEED = VindicatorUnit.movementSpeed; // 0.28
    private static final String TAG_BERSERK_TICKS_LEFT = "ronPlusEliteVindicatorBerserkTicksLeft";
    @Nullable
    private static final Field MELEE_GOAL_TICKS_UNTIL_NEXT_ATTACK_FIELD = resolveMeleeGoalCooldownField();

    @Nullable
    private UUID pendingAttackTargetUuid = null;
    private int pendingAttackTicks = 0;
    private int successfulHits = 0;
    @Nullable
    private UUID comboTargetUuid = null;
    @Nullable
    private UUID queuedBerserkStrikeTargetUuid = null;
    private int queuedBerserkStrikeTicks = 0;
    private int queuedBerserkStrikesRemaining = 0;
    private int lastSuccessfulHitTick = -100000;
    private int emeraldDrainProgress = 0;
    private int berserkTicksLeft = 0;
    private Abilities abilities = ABILITIES.clone();

    public EliteVindicatorUnit(EntityType<? extends Vindicator> entityType, Level level) {
        super(entityType, level);
        updateAbilityButtons();
    }

    @Nullable
    private static Field resolveMeleeGoalCooldownField() {
        try {
            Field field = AbstractMeleeAttackUnitGoal.class.getDeclaredField("ticksUntilNextAttack");
            field.setAccessible(true);
            return field;
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public void updateAbilityButtons() {
        abilities = ABILITIES.clone();
    }

    @Override
    public Abilities getAbilities() {
        return abilities;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, VindicatorUnit.movementSpeed)
                .add(Attributes.ATTACK_DAMAGE, VindicatorUnit.attackDamage)
                .add(Attributes.ARMOR, VindicatorUnit.armorValue)
                .add(Attributes.MAX_HEALTH, ELITE_MAX_HEALTH)
                .add(Attributes.FOLLOW_RANGE, com.solegendary.siege_of_settlements.unit.interfaces.Unit.getFollowRange())
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_SWING_INDEX, 0);
        this.entityData.define(DAMAGE_TIER, 0); // starts at x1.0 (base damage)
        this.entityData.define(BERSERK_ACTIVE, false);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!this.level().isClientSide() && target instanceof LivingEntity livingTarget) {
            // Prevent attack spam from restarting the same swing each tick.
            if (this.pendingAttackTicks > 0) {
                return true;
            }

            // While berserk rapid burst is running, extra melee goal hit attempts are ignored.
            if (isBerserkActive() && this.queuedBerserkStrikesRemaining > 0) {
                return true;
            }

            int next = this.entityData.get(ATTACK_SWING_INDEX) + 1;
            this.entityData.set(ATTACK_SWING_INDEX, next);

            // In berserk, damage must be applied immediately (no extra delayed hit frame).
            if (isBerserkActive()) {
                double dx = livingTarget.getX() - this.getX();
                double dz = livingTarget.getZ() - this.getZ();
                float strikeYaw = (float) (Mth.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
                this.setYRot(strikeYaw);
                this.yBodyRot = strikeYaw;
                this.yHeadRot = strikeYaw;
                this.getLookControl().setLookAt(livingTarget, 30.0F, 30.0F);

                // Berserk must use real x2 attack speed even vs vanilla targets with hurt-time iFrames.
                livingTarget.invulnerableTime = 0;
                livingTarget.hurtTime = 0;

                prepareComboForTarget(livingTarget);
                if (super.doHurtTarget(livingTarget)) {
                    onSuccessfulHit(livingTarget);
                    startBerserkRapidBurst(livingTarget);
                }
                this.pendingAttackTargetUuid = null;
                this.pendingAttackTicks = 0;
                return true;
            }

            // Deal damage on the downswing phase, not on swing start.
            this.pendingAttackTargetUuid = livingTarget.getUUID();
            this.pendingAttackTicks = HIT_DELAY_TICKS;
            return true;
        }
        return false;
    }

    public boolean isLeftHandAttack() {
        return (this.entityData.get(ATTACK_SWING_INDEX) & 1) == 1;
    }

    public int getAttackSwingIndex() {
        return this.entityData.get(ATTACK_SWING_INDEX);
    }

    public boolean isBerserkActive() {
        return this.entityData.get(BERSERK_ACTIVE);
    }

    public float getAttackAnimationSpeedMultiplier() {
        return isBerserkActive() ? 2.0F : 1.0F;
    }

    public float getWalkAnimationSpeedMultiplier() {
        return 1.0F;
    }

    @Override
    public float getMovementSpeed() {
        return (float) BASE_MOVEMENT_SPEED;
    }

    public void activateBerserk() {
        if (this.level().isClientSide()) {
            return;
        }
        this.berserkTicksLeft = BERSERK_DURATION_TICKS;
        this.entityData.set(BERSERK_ACTIVE, true);
        updateBerserkMovementModifier();
        syncGoalCooldownForBerserk();
    }

    private float getDamageMultiplier() {
        int tier = Mth.clamp(this.entityData.get(DAMAGE_TIER), 0, MAX_DAMAGE_TIER);
        return 1.0F + (0.25F * tier);
    }

    private int getComboExpireTicks() {
        // "1 second without landing hits" should reset combo, cooldown time itself doesn't count.
        return Mth.ceil(getAttackCooldown()) + 20;
    }

    private void resetCombo() {
        this.successfulHits = 0;
        this.comboTargetUuid = null;
        this.lastSuccessfulHitTick = -100000;
        if (this.entityData.get(DAMAGE_TIER) != 0) {
            this.entityData.set(DAMAGE_TIER, 0);
            applyScaledDamageAttribute();
        }
    }

    private void checkComboTimeout() {
        if (this.comboTargetUuid != null &&
                (this.tickCount - this.lastSuccessfulHitTick) >= getComboExpireTicks()) {
            resetCombo();
        }
    }

    private void prepareComboForTarget(LivingEntity target) {
        checkComboTimeout();
        UUID targetUuid = target.getUUID();
        if (this.comboTargetUuid != null && !this.comboTargetUuid.equals(targetUuid)) {
            resetCombo();
        }
        if (this.comboTargetUuid == null) {
            this.comboTargetUuid = targetUuid;
        }
        // First two hits of a combo are x1.25, but idle/default state remains x1.0.
        if (this.successfulHits == 0 && this.entityData.get(DAMAGE_TIER) < 1) {
            this.entityData.set(DAMAGE_TIER, 1);
            applyScaledDamageAttribute();
        }
    }

    private void applyScaledDamageAttribute() {
        AttributeInstance ai = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (ai != null) {
            // Scale only base attack damage. External flat bonuses (eg. Auromancer +3) stay unscaled.
            double newBaseValue = VindicatorUnit.attackDamage * getDamageMultiplier();
            if (Math.abs(ai.getBaseValue() - newBaseValue) > 1.0E-6) {
                ai.setBaseValue(newBaseValue);
            }
        }
    }

    private void onSuccessfulHit(LivingEntity target) {
        UUID targetUuid = target.getUUID();
        if (this.comboTargetUuid == null || !this.comboTargetUuid.equals(targetUuid)) {
            this.successfulHits = 0;
            this.comboTargetUuid = targetUuid;
        }
        this.successfulHits += 1;
        this.lastSuccessfulHitTick = this.tickCount;
        int newTier = Math.min(MAX_DAMAGE_TIER, 1 + (this.successfulHits / 2));
        if (newTier != this.entityData.get(DAMAGE_TIER)) {
            this.entityData.set(DAMAGE_TIER, newTier);
            applyScaledDamageAttribute();
        }
    }

    @Override
    public float getUnitAttackDamage() {
        // Base combo damage only. HUD/events add aura bonus separately.
        return (VindicatorUnit.attackDamage * getDamageMultiplier()) + getSharpnessLevel();
    }

    @Override
    public boolean hasBonusDamage() {
        return getDamageMultiplier() > 1.0F;
    }

    @Override
    public float getAttackCooldown() {
        float cooldown = ATTACK_COOLDOWN_TICKS * getAttackCooldownMultiplier();
        if (isBerserkActive()) {
            cooldown *= BERSERK_ATTACK_COOLDOWN_MULT;
        }
        return cooldown;
    }

    @Override
    public float getAttacksPerSecond() {
        return 20.0F / getAttackCooldown();
    }

    @Override
    public float getBaseAttacksPerSecond() {
        return 20.0F / ATTACK_COOLDOWN_TICKS;
    }

    @Override
    public float getAttackRange() {
        return ELITE_ATTACK_RANGE;
    }

    @Override
    public float getUnitMaxHealth() {
        return ELITE_MAX_HEALTH;
    }

    @Override
    public float getBonusMeleeRange() {
        return BONUS_MELEE_RANGE;
    }

    @Override
    public void setupEquipmentAndUpgradesServer() {
        // Swords are baked into the custom model, so keep hand items empty.
        this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
    }

    private void tickBerserk() {
        if (this.berserkTicksLeft > 0) {
            this.berserkTicksLeft -= 1;
            if (this.berserkTicksLeft <= 0) {
                this.berserkTicksLeft = 0;
                this.entityData.set(BERSERK_ACTIVE, false);
            }
        } else if (this.entityData.get(BERSERK_ACTIVE)) {
            this.entityData.set(BERSERK_ACTIVE, false);
        }
        updateBerserkMovementModifier();
        syncGoalCooldownForBerserk();
    }

    private void clearBerserkRapidBurst() {
        this.queuedBerserkStrikeTargetUuid = null;
        this.queuedBerserkStrikeTicks = 0;
        this.queuedBerserkStrikesRemaining = 0;
    }

    private void startBerserkRapidBurst(LivingEntity target) {
        if (!isBerserkActive() || target == null || !target.isAlive()) {
            return;
        }
        this.queuedBerserkStrikeTargetUuid = target.getUUID();
        this.queuedBerserkStrikesRemaining = Math.max(0, BERSERK_RAPID_HITS_PER_BURST - 1);
        this.queuedBerserkStrikeTicks = BERSERK_RAPID_HIT_DELAY_TICKS;
    }

    private void tickBerserkRapidBurst() {
        if (!isBerserkActive()) {
            clearBerserkRapidBurst();
            return;
        }
        if (this.queuedBerserkStrikesRemaining <= 0 || this.queuedBerserkStrikeTargetUuid == null) {
            return;
        }

        this.queuedBerserkStrikeTicks -= 1;
        if (this.queuedBerserkStrikeTicks > 0) {
            return;
        }

        if (!(this.level() instanceof ServerLevel serverLevel)) {
            clearBerserkRapidBurst();
            return;
        }

        Entity targetEntity = serverLevel.getEntity(this.queuedBerserkStrikeTargetUuid);
        LivingEntity livingTarget = null;
        if (targetEntity instanceof LivingEntity targetLe && targetLe.isAlive()) {
            livingTarget = targetLe;
        } else if (this.getTarget() != null && this.getTarget().isAlive()) {
            livingTarget = this.getTarget();
            this.queuedBerserkStrikeTargetUuid = livingTarget.getUUID();
        } else {
            clearBerserkRapidBurst();
            return;
        }

        int next = this.entityData.get(ATTACK_SWING_INDEX) + 1;
        this.entityData.set(ATTACK_SWING_INDEX, next);

        double dx = livingTarget.getX() - this.getX();
        double dz = livingTarget.getZ() - this.getZ();
        float strikeYaw = (float) (Mth.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
        this.setYRot(strikeYaw);
        this.yBodyRot = strikeYaw;
        this.yHeadRot = strikeYaw;
        this.getLookControl().setLookAt(livingTarget, 30.0F, 30.0F);

        livingTarget.invulnerableTime = 0;
        livingTarget.hurtTime = 0;

        prepareComboForTarget(livingTarget);
        if (super.doHurtTarget(livingTarget)) {
            onSuccessfulHit(livingTarget);
        }

        this.queuedBerserkStrikesRemaining -= 1;
        if (this.queuedBerserkStrikesRemaining > 0) {
            this.queuedBerserkStrikeTicks = BERSERK_RAPID_HIT_DELAY_TICKS;
        } else {
            clearBerserkRapidBurst();
        }
    }

    private void syncGoalCooldownForBerserk() {
        if (MELEE_GOAL_TICKS_UNTIL_NEXT_ATTACK_FIELD == null) {
            return;
        }
        Goal attackGoal = getAttackGoal();
        if (!(attackGoal instanceof AbstractMeleeAttackUnitGoal meleeAttackGoal)) {
            return;
        }
        int maxCooldownTicks = Math.max(0, Mth.floor(getAttackCooldown()));
        try {
            int current = MELEE_GOAL_TICKS_UNTIL_NEXT_ATTACK_FIELD.getInt(meleeAttackGoal);
            if (current > maxCooldownTicks) {
                MELEE_GOAL_TICKS_UNTIL_NEXT_ATTACK_FIELD.setInt(meleeAttackGoal, maxCooldownTicks);
            }
        } catch (IllegalAccessException ignored) {
        }
    }

    private void updateBerserkMovementModifier() {
        AttributeInstance ai = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (ai == null) {
            return;
        }
        if (Math.abs(ai.getBaseValue() - BASE_MOVEMENT_SPEED) > 1.0E-6) {
            ai.setBaseValue(BASE_MOVEMENT_SPEED);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            tickBerserk();
            checkComboTimeout();
            applyScaledDamageAttribute();
            tickEmeraldDrain();
            tickBerserkRapidBurst();
        } else {
            // Keep client-side movement stat in sync instantly with berserk state.
            updateBerserkMovementModifier();
        }
        if (this.level().isClientSide() || this.pendingAttackTicks <= 0) {
            return;
        }
        this.pendingAttackTicks -= 1;
        if (this.pendingAttackTicks > 0 || this.pendingAttackTargetUuid == null) {
            return;
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            LivingEntity livingTarget = null;
            Entity target = serverLevel.getEntity(this.pendingAttackTargetUuid);
            if (target instanceof LivingEntity targetLe && targetLe.isAlive()) {
                livingTarget = targetLe;
            } else if (this.getTarget() != null && this.getTarget().isAlive()) {
                // Fallback to current AI target if original target disappeared.
                livingTarget = this.getTarget();
            }
            if (livingTarget != null) {
                // Force facing target at the strike frame.
                double dx = livingTarget.getX() - this.getX();
                double dz = livingTarget.getZ() - this.getZ();
                float strikeYaw = (float) (Mth.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
                this.setYRot(strikeYaw);
                this.yBodyRot = strikeYaw;
                this.yHeadRot = strikeYaw;
                this.getLookControl().setLookAt(livingTarget, 30.0F, 30.0F);

                // Range was already validated when swing started. Re-checking here caused frequent whiffs
                // on fast swings and made real APS look unchanged.
                if (this.distanceToSqr(livingTarget.getX(), livingTarget.getY(), livingTarget.getZ()) <= 16.0D) {
                    prepareComboForTarget(livingTarget);
                    if (super.doHurtTarget(livingTarget)) {
                        onSuccessfulHit(livingTarget);
                    }
                }
            }
        }
        this.pendingAttackTargetUuid = null;
    }

    private void tickEmeraldDrain() {
        String ownerName = getOwnerName();
        if (ownerName == null || ownerName.isEmpty()) {
            return;
        }

        emeraldDrainProgress += 1;
        // First charge happens only after 10 minutes from spawn.
        if (emeraldDrainProgress < EMERALD_DRAIN_INTERVAL_TICKS) {
            return;
        }
        emeraldDrainProgress = 0;

        if (!tryDrainOwnerEmeraldsInstantly(ownerName, EMERALD_DRAIN_AMOUNT)) {
            this.discard();
        }
    }

    private static boolean tryDrainOwnerEmeraldsInstantly(String ownerName, int amount) {
        if (!ResourcesServerEvents.canAfford(ownerName, ResourceName.EMERALDS, amount)) {
            return false;
        }

        for (Resources resources : ResourcesServerEvents.resourcesList) {
            if (resources.ownerName.equals(ownerName)) {
                resources.changeInstantly(0, 0, 0, -amount);
                ResourcesClientboundPacket.addSubtractResourcesInstantly(new Resources(ownerName, 0, 0, 0, -amount));
                return true;
            }
        }
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("ron_elite_hits", this.successfulHits);
        tag.putInt("ron_elite_tier", this.entityData.get(DAMAGE_TIER));
        tag.putInt(TAG_EMERALD_DRAIN_PROGRESS, emeraldDrainProgress);
        tag.putInt(TAG_BERSERK_TICKS_LEFT, this.berserkTicksLeft);
        if (this.comboTargetUuid != null) {
            tag.putUUID("ron_elite_combo_target", this.comboTargetUuid);
            tag.putInt("ron_elite_last_hit_tick", this.lastSuccessfulHitTick);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.successfulHits = Math.max(0, tag.getInt("ron_elite_hits"));
        int tierFromSave = tag.contains("ron_elite_tier") ? tag.getInt("ron_elite_tier")
                : (this.successfulHits > 0 ? Math.min(MAX_DAMAGE_TIER, 1 + (this.successfulHits / 2)) : 0);
        this.entityData.set(DAMAGE_TIER, Mth.clamp(tierFromSave, 0, MAX_DAMAGE_TIER));
        if (tag.hasUUID("ron_elite_combo_target")) {
            this.comboTargetUuid = tag.getUUID("ron_elite_combo_target");
            this.lastSuccessfulHitTick = tag.getInt("ron_elite_last_hit_tick");
        } else {
            this.comboTargetUuid = null;
            this.lastSuccessfulHitTick = -100000;
        }
        emeraldDrainProgress = Math.max(0, tag.getInt(TAG_EMERALD_DRAIN_PROGRESS));
        this.berserkTicksLeft = Math.max(0, tag.getInt(TAG_BERSERK_TICKS_LEFT));
        this.entityData.set(BERSERK_ACTIVE, this.berserkTicksLeft > 0);
        updateBerserkMovementModifier();
        applyScaledDamageAttribute();
    }

    @Override
    @Nullable
    public ResourceCost getCost() {
        return ResourceCosts.VINDICATOR;
    }
}
