package com.WardenMine242.siege_of_settlements.unit.units.villagers;

import com.WardenMine242.siege_of_settlements.unit.goals.IllusionerBowAttackGoal;
import com.solegendary.siege_of_settlements.ability.Abilities;
import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.registrars.EntityRegistrar;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.unit.Checkpoint;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.goals.*;
import com.solegendary.siege_of_settlements.unit.interfaces.AttackerUnit;
import com.solegendary.siege_of_settlements.unit.interfaces.RangedAttackerUnit;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.faction.Faction;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IllusionerUnit extends Illusioner implements Unit, AttackerUnit, RangedAttackerUnit {
    public static final Abilities ABILITIES = new Abilities();
    private static final int CLONE_COUNT = 8;
    private static final int CLONE_RESPAWN_COOLDOWN_TICKS = 20 * 20;
    private static final int CLONE_IDLE_DISCARD_TICKS = 10 * 20;
    private static final float CLONE_MAX_HEALTH_MULTIPLIER = 0.5F;
    private static final float CLONE_SUMMON_CHANCE_PER_SECOND = 0.35F;
    private static final int CLONE_SUMMON_POOF_PARTICLES = 30;
    private static final double CLONE_SUMMON_POOF_SPEED = 0.22D;
    public static final String CLONE_NAME_TRANSLATION_KEY = "entity.siege_of_settlements.illusioner_clone";
    private static final String TAG_IS_CLONE = "sos_illusioner_is_clone";
    private static final String TAG_CLONE_RESPAWN_COOLDOWN_TICKS = "sos_illusioner_clone_respawn_cd";
    private static final String TAG_CLONE_OWNER_UUID = "sos_illusioner_clone_owner_uuid";
    private static final String TAG_CLONE_WAVE_ACTIVE = "sos_illusioner_clone_wave_active";
    private static final ResourceCost CLONE_COST = ResourceCost.Unit(0, 0, 0, 0, 0);

    //region
    @Override
    public void updateAbilityButtons() {
        abilities = ABILITIES.clone();
    }
    Object2ObjectArrayMap<Ability, Float> cooldowns = Unit.createCooldownMap();
    Object2ObjectArrayMap<Ability, Integer> charges = new Object2ObjectArrayMap<>();
    @Override public Object2ObjectArrayMap<Ability, Float> getCooldowns() { return cooldowns; }
    @Override public boolean hasAutocast(Ability ability) { return autocast == ability; }
    @Override public void setAutocast(Ability autocast) { this.autocast = autocast; }
    @Override public Object2ObjectArrayMap<Ability, Integer> getCharges() { return charges; }

    Ability autocast;

    private int eatingTicksLeft = 0;
    public void setEatingTicksLeft(int amount) { eatingTicksLeft = amount; }
    public int getEatingTicksLeft() { return eatingTicksLeft; }
    private BlockPos anchorPos = new BlockPos(0,0,0);
    public void setAnchor(BlockPos bp) { anchorPos = bp; }
    public BlockPos getAnchor() { return anchorPos; }

    private final ArrayList<Checkpoint> checkpoints = new ArrayList<>();
    public ArrayList<Checkpoint> getCheckpoints() { return checkpoints; };

    GarrisonGoal garrisonGoal;
    public GarrisonGoal getGarrisonGoal() { return null; }
    public boolean canGarrison() { return false; }

    UsePortalGoal usePortalGoal;
    public UsePortalGoal getUsePortalGoal() { return usePortalGoal; }
    public boolean canUsePortal() { return getUsePortalGoal() != null; }

    public Faction getFaction() {return Faction.VILLAGERS;}
    public Abilities getAbilities() {return abilities;}
    public List<ItemStack> getItems() {return items;}
    public MoveToTargetBlockGoal getMoveGoal() {return moveGoal;}
    public SelectedTargetGoal<? extends IllusionerUnit> getTargetGoal() {return targetGoal;}
    public Goal getAttackGoal() {return attackGoal;}
    public Goal getAttackBuildingGoal() {return attackBuildingGoal;}

    public ReturnResourcesGoal getReturnResourcesGoal() {return returnResourcesGoal;}
    public int getMaxResources() {return maxResources;}

    private MoveToTargetBlockGoal moveGoal;
    private SelectedTargetGoal<? extends IllusionerUnit> targetGoal;
    public BuildRepairGoal buildRepairGoal;
    public GatherResourcesGoal gatherResourcesGoal;
    private ReturnResourcesGoal returnResourcesGoal;

    public BlockPos getAttackMoveTarget() { return attackMoveTarget; }
    public LivingEntity getFollowTarget() { return followTarget; }
    public boolean getHoldPosition() { return holdPosition; }
    public void setHoldPosition(boolean holdPosition) { this.holdPosition = holdPosition; }

    private BlockPos attackMoveTarget = null;
    private LivingEntity followTarget = null;
    private boolean holdPosition = false;

    public String getOwnerName() { return this.entityData.get(ownerDataAccessor); }
    public void setOwnerName(String name) { this.entityData.set(ownerDataAccessor, name); }
    public static final EntityDataAccessor<String> ownerDataAccessor =
            SynchedEntityData.defineId(IllusionerUnit.class, EntityDataSerializers.STRING);

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ownerDataAccessor, "");
    }

    public boolean getWillRetaliate() {return willRetaliate;}
    public float getAttackCooldown() {return (20f / attacksPerSecond) * getAttackCooldownMultiplier();}
    public float getAttacksPerSecond() {return 20f / getAttackCooldown();}
    public float getBaseAttacksPerSecond() { return attacksPerSecond; }
    public float getAggroRange() {return aggroRange;}
    public boolean getAggressiveWhenIdle() {return aggressiveWhenIdle && !isVehicle();}
    public float getAttackRange() {return attackRange;}
    public float getUnitAttackDamage() {return attackDamage;}
    public boolean canAttackBuildings() {return getAttackBuildingGoal() != null;}

    public float getMovementSpeed() {return movementSpeed;}
    public float getUnitMaxHealth() {return maxHealth;}

    @Nullable
    public ResourceCost getCost() { return isSummonedClone ? CLONE_COST : ResourceCosts.ILLUSIONER; }

    public void setAttackMoveTarget(@Nullable BlockPos bp) { this.attackMoveTarget = bp; }
    public void setFollowTarget(@Nullable LivingEntity target) { this.followTarget = target; }

    // endregion

    final static public float attackDamage = 14f;
    final static public float attacksPerSecond = 1.25f;
    final static public float aggroRange = 16f;
    final static public boolean willRetaliate = true;
    final static public boolean aggressiveWhenIdle = true;
    final static public float attackRange = 16f;

    final static public float maxHealth = 120.0f;
    final static public float armorValue = 0.0f;
    final static public float movementSpeed = 0.28f;

    public int maxResources = 100;

    public int fogRevealDuration = 0;
    public int getFogRevealDuration() { return fogRevealDuration; }
    public void setFogRevealDuration(int duration) { fogRevealDuration = duration; }

    private boolean isSummonedClone = false;
    private int cloneSummonCooldownTicks = 0;
    private boolean cloneWaveActive = false;
    private int cloneWaveOutOfCombatTicks = 0;
    private UUID cloneOwnerUuid = null;
    private final ArrayList<UUID> activeCloneIds = new ArrayList<>();

    private IllusionerBowAttackGoal<? extends IllusionerUnit> attackGoal;
    private MeleeAttackBuildingGoal attackBuildingGoal;

    private Abilities abilities = ABILITIES.clone();
    private final List<ItemStack> items = new ArrayList<>();

    public IllusionerUnit(EntityType<? extends Illusioner> entityType, Level level) {
        super(entityType, level);
        updateAbilityButtons();
    }

    @Override
    public boolean removeWhenFarAway(double d) { return false; }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, IllusionerUnit.movementSpeed)
                .add(Attributes.MAX_HEALTH, IllusionerUnit.maxHealth)
                .add(Attributes.FOLLOW_RANGE, Unit.getFollowRange())
                .add(Attributes.ARMOR, IllusionerUnit.armorValue)
                .add(Attributes.ATTACK_DAMAGE, IllusionerUnit.attackDamage);
    }

    @Override
    public void resetBehaviours() {
        // No abilities to reset yet
    }

    public void tick() {
        this.setCanPickUpLoot(false);
        super.tick();
        Unit.tick(this);
        AttackerUnit.tick(this);

        if (this.level().isClientSide()) {
            return;
        }

        if (isSummonedClone) {
            tickSummonedClone();
            return;
        }

        if (this.cloneSummonCooldownTicks > 0) {
            cloneSummonCooldownTicks--;
        }
        refreshActiveClones();
        syncCloneTargets((ServerLevel) this.level());
        tickCloneWaveTimeout();
        maybeSummonClones();
    }

    @Override
    public boolean wantsToPickUp(ItemStack itemStack) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        this.addUnitSaveData(pCompound);
        pCompound.putBoolean(TAG_IS_CLONE, this.isSummonedClone);
        pCompound.putInt(TAG_CLONE_RESPAWN_COOLDOWN_TICKS, this.cloneSummonCooldownTicks);
        pCompound.putBoolean(TAG_CLONE_WAVE_ACTIVE, this.cloneWaveActive);
        if (this.cloneOwnerUuid != null) {
            pCompound.putUUID(TAG_CLONE_OWNER_UUID, this.cloneOwnerUuid);
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.readUnitSaveData(pCompound);
        this.isSummonedClone = pCompound.getBoolean(TAG_IS_CLONE);
        this.cloneSummonCooldownTicks = Math.max(0, pCompound.getInt(TAG_CLONE_RESPAWN_COOLDOWN_TICKS));
        this.cloneWaveActive = pCompound.getBoolean(TAG_CLONE_WAVE_ACTIVE);
        this.cloneOwnerUuid = pCompound.hasUUID(TAG_CLONE_OWNER_UUID) ? pCompound.getUUID(TAG_CLONE_OWNER_UUID) : null;
        if (this.isSummonedClone) {
            // Clone name is code-defined, not by nametag.
            this.setCustomName(null);
        }
    }

    public void initialiseGoals() {
        this.usePortalGoal = new UsePortalGoal(this);
        this.moveGoal = new MoveToTargetBlockGoal(this, false, 0);
        this.targetGoal = new SelectedTargetGoal<>(this, true, true);
        this.garrisonGoal = null;
        this.attackGoal = new IllusionerBowAttackGoal<>(this);
        this.returnResourcesGoal = new ReturnResourcesGoal(this);
    }

    @Override
    protected void registerGoals() {
        initialiseGoals();
        this.goalSelector.addGoal(2, usePortalGoal);
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, attackGoal);
        this.goalSelector.addGoal(2, returnResourcesGoal);
        this.targetSelector.addGoal(2, targetGoal);
        this.goalSelector.addGoal(3, moveGoal);
        this.goalSelector.addGoal(4, new RandomLookAroundUnitGoal(this));
    }

    @Override
    public AbstractIllager.IllagerArmPose getArmPose() {
        if (this.isCastingSpell()) {
            return AbstractIllager.IllagerArmPose.SPELLCASTING;
        }
        if (this.isUsingItem() || this.hasLivingTarget() || AttackerUnit.isAttackingBuilding(this)) {
            return AbstractIllager.IllagerArmPose.BOW_AND_ARROW;
        }
        return AbstractIllager.IllagerArmPose.CROSSED;
    }

    @Override
    public Component getName() {
        if (this.isSummonedClone) {
            return Component.translatable(CLONE_NAME_TRANSLATION_KEY);
        }
        return super.getName();
    }

    @Override
    public void performUnitRangedAttack(net.minecraft.world.entity.LivingEntity pTarget, float velocity) {
        ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this,
                (item) -> item instanceof BowItem
        )));
        AbstractArrow abstractarrow = ProjectileUtil.getMobArrow(this, itemstack, velocity);
        if (this.getMainHandItem().getItem() instanceof BowItem) {
            abstractarrow = ((BowItem)this.getMainHandItem().getItem()).customArrow(abstractarrow);
        }
        double d0 = pTarget.getX() - this.getX();
        double d1 = pTarget.getY(0.3333333333333333) - abstractarrow.getY();
        double d2 = pTarget.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);

        if (pTarget.getEyeHeight() <= 1.0f)
            d1 -= (1.0f - pTarget.getEyeHeight());

        abstractarrow.setBaseDamage(this.getUnitAttackDamage());
        abstractarrow.shoot(d0, d1 + d3 * 0.20000000298023224, d2, 1.6F, 0);
        this.playSound(SoundEvents.SKELETON_SHOOT, 3.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(abstractarrow);
        this.getMainHandItem().setDamageValue(0);
    }

    @Override
    public void setupEquipmentAndUpgradesClient() {
    }

    @Override
    public void setupEquipmentAndUpgradesServer() {
        this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        return pSpawnData;
    }

    @Override
    public void die(@NotNull DamageSource pDamageSource) {
        if (!this.level().isClientSide() && !this.isSummonedClone && this.level() instanceof ServerLevel) {
            discardOwnedClones();
        }
        super.die(pDamageSource);
    }

    private void tickSummonedClone() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        IllusionerUnit owner = getCloneOwner(serverLevel);
        if (owner == null || !owner.isAlive()) {
            this.discard();
        }
    }

    @Nullable
    private IllusionerUnit getCloneOwner(ServerLevel serverLevel) {
        if (this.cloneOwnerUuid == null) {
            return null;
        }
        Entity ownerEntity = serverLevel.getEntity(this.cloneOwnerUuid);
        if (ownerEntity instanceof IllusionerUnit owner && !owner.isSummonedClone) {
            return owner;
        }
        return null;
    }

    private void refreshActiveClones() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (this.activeCloneIds.isEmpty()) {
            for (IllusionerUnit clone : serverLevel.getEntitiesOfClass(
                    IllusionerUnit.class,
                    this.getBoundingBox().inflate(192.0D),
                    clone -> clone.isSummonedClone
                            && clone.cloneOwnerUuid != null
                            && this.getUUID().equals(clone.cloneOwnerUuid)
            )) {
                this.activeCloneIds.add(clone.getUUID());
            }
        }
        this.activeCloneIds.removeIf(uuid -> {
            Entity entity = serverLevel.getEntity(uuid);
            if (!(entity instanceof IllusionerUnit clone)) {
                return true;
            }
            return !clone.isAlive()
                    || !clone.isSummonedClone
                    || clone.cloneOwnerUuid == null
                    || !this.getUUID().equals(clone.cloneOwnerUuid);
        });

        if (this.cloneWaveActive && this.activeCloneIds.isEmpty()) {
            this.cloneWaveActive = false;
            this.cloneWaveOutOfCombatTicks = 0;
            this.cloneSummonCooldownTicks = Math.max(this.cloneSummonCooldownTicks, CLONE_RESPAWN_COOLDOWN_TICKS);
        }
    }

    private void tickCloneWaveTimeout() {
        if (!(this.level() instanceof ServerLevel serverLevel) || this.activeCloneIds.isEmpty()) {
            this.cloneWaveOutOfCombatTicks = 0;
            return;
        }

        boolean anyCloneInCombat = this.isInCombat();
        for (UUID cloneId : this.activeCloneIds) {
            Entity entity = serverLevel.getEntity(cloneId);
            if (entity instanceof IllusionerUnit clone &&
                    clone.isAlive() &&
                    clone.isSummonedClone &&
                    (clone.hasLivingTarget() || AttackerUnit.isAttackingBuilding(clone))) {
                anyCloneInCombat = true;
                break;
            }
        }

        if (anyCloneInCombat) {
            this.cloneWaveOutOfCombatTicks = 0;
            return;
        }

        this.cloneWaveOutOfCombatTicks++;
        if (this.cloneWaveOutOfCombatTicks >= CLONE_IDLE_DISCARD_TICKS) {
            discardOwnedClones();
            this.cloneWaveActive = false;
            this.cloneWaveOutOfCombatTicks = 0;
            this.cloneSummonCooldownTicks = 0;
        }
    }

    private void syncCloneTargets(ServerLevel serverLevel) {
        if (this.activeCloneIds.isEmpty()) {
            return;
        }

        LivingEntity sharedTarget = null;
        if (this.hasLivingTarget()) {
            sharedTarget = this.getTarget();
        } else {
            for (UUID cloneId : this.activeCloneIds) {
                Entity entity = serverLevel.getEntity(cloneId);
                if (entity instanceof IllusionerUnit clone && clone.isAlive() && clone.hasLivingTarget()) {
                    sharedTarget = clone.getTarget();
                    break;
                }
            }
        }

        if (sharedTarget == null || !sharedTarget.isAlive()) {
            return;
        }

        for (UUID cloneId : this.activeCloneIds) {
            Entity entity = serverLevel.getEntity(cloneId);
            if (!(entity instanceof IllusionerUnit clone) || !clone.isAlive() || !clone.isSummonedClone) {
                continue;
            }
            if (clone.getTarget() != sharedTarget) {
                clone.setTarget(sharedTarget);
                if (clone.getTargetGoal() != null) {
                    clone.getTargetGoal().setTarget(sharedTarget);
                }
            }
        }
    }

    private boolean isInCombat() {
        return this.hasLivingTarget() || AttackerUnit.isAttackingBuilding(this);
    }

    private void maybeSummonClones() {
        if (this.cloneSummonCooldownTicks > 0 || !isInCombat() || this.cloneWaveActive || !this.activeCloneIds.isEmpty()) {
            return;
        }
        // "By own decision": evaluate roughly once per second with a chance gate.
        if (this.tickCount % 20 != 0 || this.random.nextFloat() >= CLONE_SUMMON_CHANCE_PER_SECOND) {
            return;
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            summonClones(serverLevel);
            this.cloneWaveActive = !this.activeCloneIds.isEmpty();
            this.cloneWaveOutOfCombatTicks = 0;
        }
    }

    private void summonClones(ServerLevel serverLevel) {
        LivingEntity currentTarget = this.getTarget();
        double startAngle = this.random.nextDouble() * (Math.PI * 2.0D);
        double cloneMaxHealth = Math.max(1.0D, this.getMaxHealth() * CLONE_MAX_HEALTH_MULTIPLIER);
        this.activeCloneIds.clear();

        for (int i = 0; i < CLONE_COUNT; i++) {
            IllusionerUnit clone = EntityRegistrar.ILLUSIONER_UNIT.get().create(serverLevel);
            if (clone == null) {
                continue;
            }

            double angle = startAngle + ((Math.PI * 2.0D) * i / CLONE_COUNT);
            double radius = 2.6D + (this.random.nextDouble() * 0.8D);
            double spawnX = this.getX() + Math.cos(angle) * radius;
            double spawnZ = this.getZ() + Math.sin(angle) * radius;

            clone.moveTo(spawnX, this.getY(), spawnZ, this.getYRot(), this.getXRot());
            clone.setYHeadRot(this.getYHeadRot());
            clone.setOwnerName(this.getOwnerName());
            clone.setAnchor(this.getAnchor());
            clone.setupEquipmentAndUpgradesServer();
            clone.markAsSummonedClone(this.getUUID());

            AttributeInstance cloneMaxHealthAttr = clone.getAttribute(Attributes.MAX_HEALTH);
            if (cloneMaxHealthAttr != null) {
                cloneMaxHealthAttr.setBaseValue(cloneMaxHealth);
            }
            clone.setHealth((float) cloneMaxHealth);

            if (currentTarget != null && currentTarget.isAlive()) {
                clone.setTarget(currentTarget);
                if (clone.getTargetGoal() != null) {
                    clone.getTargetGoal().setTarget(currentTarget);
                }
            }

            clone.setHoldPosition(this.getHoldPosition());

            serverLevel.addFreshEntity(clone);
            spawnCloneSummonParticles(serverLevel, clone);
            this.activeCloneIds.add(clone.getUUID());
        }
    }

    private void spawnCloneSummonParticles(ServerLevel serverLevel, IllusionerUnit clone) {
        double particleX = clone.getX();
        double particleY = clone.getY(0.5D);
        double particleZ = clone.getZ();

        for (int i = 0; i < CLONE_SUMMON_POOF_PARTICLES; i++) {
            double motionX = this.random.nextGaussian() * CLONE_SUMMON_POOF_SPEED;
            double motionY = this.random.nextGaussian() * CLONE_SUMMON_POOF_SPEED;
            double motionZ = this.random.nextGaussian() * CLONE_SUMMON_POOF_SPEED;
            serverLevel.sendParticles(
                    ParticleTypes.POOF,
                    particleX,
                    particleY,
                    particleZ,
                    0,
                    motionX,
                    motionY,
                    motionZ,
                    1.0D
            );
        }
    }

    private void markAsSummonedClone(UUID ownerUuid) {
        this.isSummonedClone = true;
        this.cloneOwnerUuid = ownerUuid;
        this.cloneSummonCooldownTicks = 0;
        this.cloneWaveActive = false;
        this.cloneWaveOutOfCombatTicks = 0;
        this.activeCloneIds.clear();
        this.setCustomName(null);
        this.setCustomNameVisible(false);
    }

    private void discardOwnedClones() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        for (UUID cloneId : new ArrayList<>(this.activeCloneIds)) {
            Entity entity = serverLevel.getEntity(cloneId);
            if (entity instanceof IllusionerUnit clone && clone.isSummonedClone) {
                clone.discard();
            }
        }
        for (IllusionerUnit clone : serverLevel.getEntitiesOfClass(
                IllusionerUnit.class,
                this.getBoundingBox().inflate(192.0D),
                clone -> clone.isSummonedClone
                        && clone.cloneOwnerUuid != null
                        && this.getUUID().equals(clone.cloneOwnerUuid)
        )) {
            clone.discard();
        }
        this.activeCloneIds.clear();
    }
}
