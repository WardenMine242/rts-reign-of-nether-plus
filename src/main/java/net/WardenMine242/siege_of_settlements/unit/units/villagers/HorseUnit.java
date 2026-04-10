package net.WardenMine242.siege_of_settlements.unit.units.villagers;

import com.solegendary.siege_of_settlements.ability.Abilities;
import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.ability.abilities.Eject;
import com.solegendary.siege_of_settlements.faction.Faction;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.unit.Checkpoint;
import com.solegendary.siege_of_settlements.unit.goals.GarrisonGoal;
import com.solegendary.siege_of_settlements.unit.goals.MoveToTargetBlockGoal;
import com.solegendary.siege_of_settlements.unit.goals.RandomLookAroundUnitGoal;
import com.solegendary.siege_of_settlements.unit.goals.ReturnResourcesGoal;
import com.solegendary.siege_of_settlements.unit.goals.SelectedTargetGoal;
import com.solegendary.siege_of_settlements.unit.goals.UsePortalGoal;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class HorseUnit extends Horse implements Unit {
    public static final Abilities ABILITIES = new Abilities();
    static {
        ABILITIES.add(new Eject(), Keybindings.keyQ);
    }

    @Override
    public void updateAbilityButtons() {
        abilities = ABILITIES.clone();
    }

    Object2ObjectArrayMap<Ability, Float> cooldowns = Unit.createCooldownMap();
    Object2ObjectArrayMap<Ability, Integer> charges = new Object2ObjectArrayMap<>();

    @Override
    public Object2ObjectArrayMap<Ability, Float> getCooldowns() {
        return cooldowns;
    }

    @Override
    public boolean hasAutocast(Ability ability) {
        return autocast == ability;
    }

    @Override
    public void setAutocast(Ability autocast) {
        this.autocast = autocast;
    }

    @Override
    public Object2ObjectArrayMap<Ability, Integer> getCharges() {
        return charges;
    }

    Ability autocast;

    private int eatingTicksLeft = 0;

    @Override
    public void setEatingTicksLeft(int amount) {
        eatingTicksLeft = amount;
    }

    @Override
    public int getEatingTicksLeft() {
        return eatingTicksLeft;
    }

    private BlockPos anchorPos = new BlockPos(0, 0, 0);

    @Override
    public void setAnchor(BlockPos bp) {
        anchorPos = bp;
    }

    @Override
    public BlockPos getAnchor() {
        return anchorPos;
    }

    private final ArrayList<Checkpoint> checkpoints = new ArrayList<>();

    @Override
    public ArrayList<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    GarrisonGoal garrisonGoal;

    @Override
    public GarrisonGoal getGarrisonGoal() {
        return garrisonGoal;
    }

    @Override
    public boolean canGarrison() {
        return false;
    }

    UsePortalGoal usePortalGoal;

    @Override
    public UsePortalGoal getUsePortalGoal() {
        return usePortalGoal;
    }

    @Override
    public boolean canUsePortal() {
        return getUsePortalGoal() != null;
    }

    @Override
    public Faction getFaction() {
        return Faction.VILLAGERS;
    }

    @Override
    public Abilities getAbilities() {
        return abilities;
    }

    @Override
    public List<ItemStack> getItems() {
        return items;
    }

    @Override
    public MoveToTargetBlockGoal getMoveGoal() {
        return moveGoal;
    }

    @Override
    public SelectedTargetGoal<? extends LivingEntity> getTargetGoal() {
        return targetGoal;
    }

    @Override
    public ReturnResourcesGoal getReturnResourcesGoal() {
        return returnResourcesGoal;
    }

    @Override
    public int getMaxResources() {
        return maxResources;
    }

    private MoveToTargetBlockGoal moveGoal;
    private SelectedTargetGoal<? extends LivingEntity> targetGoal;
    private ReturnResourcesGoal returnResourcesGoal;

    @Override
    public LivingEntity getFollowTarget() {
        return followTarget;
    }

    @Override
    public boolean getHoldPosition() {
        return holdPosition;
    }

    @Override
    public void setHoldPosition(boolean holdPosition) {
        this.holdPosition = holdPosition;
    }

    private LivingEntity followTarget = null;
    private boolean holdPosition = false;

    @Override
    public void setFollowTarget(@Nullable LivingEntity target) {
        this.followTarget = target;
    }

    public static final EntityDataAccessor<String> ownerDataAccessor =
            SynchedEntityData.defineId(HorseUnit.class, EntityDataSerializers.STRING);

    @Override
    public String getOwnerName() {
        return this.entityData.get(ownerDataAccessor);
    }

    @Override
    public void setOwnerName(String name) {
        this.entityData.set(ownerDataAccessor, name);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ownerDataAccessor, "");
    }

    @Override
    public float getMovementSpeed() {
        return movementSpeed;
    }

    @Override
    public float getUnitMaxHealth() {
        return maxHealth;
    }

    @Nullable
    @Override
    public ResourceCost getCost() {
        return ResourceCosts.LLAMA;
    }

    public static final float maxHealth = 60.0f;
    public static final float armorValue = 0.0f;
    public static final float movementSpeed = 0.32f;
    private static final float PLAYER_RIDDEN_SPEED_MULT = 0.65f;
    public int maxResources = 100;

    private Abilities abilities = ABILITIES.clone();
    private final List<ItemStack> items = new ArrayList<>();

    public HorseUnit(EntityType<? extends Horse> entityType, Level level) {
        super(entityType, level);
        updateAbilityButtons();
    }

    @Override
    public boolean removeWhenFarAway(double d) {
        return false;
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        // Shift + RMB should do nothing.
        if (player.isSecondaryUseActive()) {
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        // Enemy players cannot ride this RTS horse.
        String ownerName = this.getOwnerName();
        if (!ownerName.isBlank() && !ownerName.equals(player.getName().getString())) {
            return InteractionResult.FAIL;
        }

        if (!this.isVehicle()) {
            player.startRiding(this);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        return InteractionResult.PASS;
    }

    @Override
    public void openCustomInventoryScreen(@NotNull Player player) {
        // Disable horse inventory opening.
    }

    @Override
    protected float getRiddenSpeed(@NotNull Player player) {
        // Riding speed is intentionally reduced to match RTS unit pacing.
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * PLAYER_RIDDEN_SPEED_MULT;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Horse.createBaseHorseAttributes()
                .add(Attributes.MOVEMENT_SPEED, HorseUnit.movementSpeed)
                .add(Attributes.MAX_HEALTH, HorseUnit.maxHealth)
                .add(Attributes.FOLLOW_RANGE, Unit.getFollowRange())
                .add(Attributes.ARMOR, HorseUnit.armorValue);
    }

    @Override
    protected void pickUpItem(@NotNull ItemEntity pItemEntity) {
        // Units use custom pickup logic from Unit.tick().
    }

    @Override
    protected void customServerAiStep() {
        // Disable vanilla horse behaviour that conflicts with RTS commands.
    }

    @Override
    public LivingEntity getTarget() {
        return this.targetGoal.getTarget();
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity pEntity) {
        return false;
    }

    @Override
    public void tick() {
        this.setCanPickUpLoot(false);
        super.tick();
        keepHorseState();
        Unit.tick(this);
    }

    private void keepHorseState() {
        if (this.level().isClientSide()) {
            return;
        }

        if (!this.isTamed()) {
            this.setTamed(true);
        }
        if (!this.isSaddled()) {
            this.equipSaddle(null);
        }

        // RTS horse must keep moving and never enter vanilla grazing/standing idle states.
        this.setEating(false);
        this.setStanding(false);

        if (this.getAttribute(Attributes.MOVEMENT_SPEED) != null &&
                Math.abs(this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() - movementSpeed) > 1.0E-6D) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(movementSpeed);
        }
    }

    @Override
    public double getPassengersRidingOffset() {
        // Keep riders seated in a stable RTS-like position (closer to ravager seat style).
        return this.getBbHeight() * 0.82D;
    }

    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull Entity.MoveFunction moveFunction) {
        if (!this.hasPassenger(passenger)) {
            return;
        }

        double riderY = this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset();
        moveFunction.accept(passenger, this.getX(), riderY, this.getZ());

        if (passenger instanceof LivingEntity living && !(passenger instanceof Player)) {
            // Non-player riders should keep a clean forward-facing seated look.
            float mountYaw = this.getYRot();
            living.setYBodyRot(mountYaw);
            living.setYHeadRot(mountYaw);
            living.yBodyRotO = mountYaw;
            living.yHeadRotO = mountYaw;
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        this.addUnitSaveData(pCompound);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.readUnitSaveData(pCompound);
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason,
                                        @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData spawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.setTamed(true);
        this.equipSaddle(null);
        if (this.getAttribute(Attributes.MAX_HEALTH) != null) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
        }
        if (this.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(movementSpeed);
        }
        this.setHealth(this.getMaxHealth());
        return spawnData;
    }

    public void initialiseGoals() {
        this.usePortalGoal = new UsePortalGoal(this);
        this.moveGoal = new MoveToTargetBlockGoal(this, false, 0);
        this.targetGoal = new SelectedTargetGoal<>(this, true, true);
        this.returnResourcesGoal = new ReturnResourcesGoal(this);
    }

    @Override
    protected void registerGoals() {
        initialiseGoals();
        this.goalSelector.addGoal(2, usePortalGoal);
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, returnResourcesGoal);
        this.targetSelector.addGoal(2, targetGoal);
        this.goalSelector.addGoal(3, moveGoal);
        this.goalSelector.addGoal(4, new RandomLookAroundUnitGoal(this));
    }
}
