package com.solegendary.siege_of_settlements.unit.units.villagers;

import com.solegendary.siege_of_settlements.ability.Abilities;
import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.ability.abilities.Eject;
import com.solegendary.siege_of_settlements.ability.abilities.Roar;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.BuildingUtils;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.unit.Checkpoint;
import com.solegendary.siege_of_settlements.unit.Relationship;
import com.solegendary.siege_of_settlements.unit.UnitServerEvents;
import com.solegendary.siege_of_settlements.unit.goals.*;
import com.solegendary.siege_of_settlements.unit.interfaces.AttackerUnit;
import com.solegendary.siege_of_settlements.unit.interfaces.ConvertableUnit;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.faction.Faction;
import com.solegendary.siege_of_settlements.util.MiscUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class RavagerUnit extends Ravager implements Unit, AttackerUnit, ConvertableUnit {
    public static final Abilities ABILITIES = new Abilities();
    static {
        ABILITIES.add(new Roar(), Keybindings.keyQ);
        ABILITIES.add(new Eject(), Keybindings.keyW);
    }

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

    private boolean shouldDiscard = false;
    public boolean shouldDiscard() { return shouldDiscard; }
    public void setShouldDiscard(boolean discard) { this.shouldDiscard = discard; }

    private int eatingTicksLeft = 0;
    public void setEatingTicksLeft(int amount) { eatingTicksLeft = amount; }
    public int getEatingTicksLeft() { return eatingTicksLeft; }
    private BlockPos anchorPos = new BlockPos(0,0,0);
    public void setAnchor(BlockPos bp) { anchorPos = bp; }
    public BlockPos getAnchor() { return anchorPos; }

    private final ArrayList<Checkpoint> checkpoints = new ArrayList<>();
    public ArrayList<Checkpoint> getCheckpoints() { return checkpoints; };

    GarrisonGoal garrisonGoal;
    public GarrisonGoal getGarrisonGoal() { return garrisonGoal; }
    public boolean canGarrison() { return getGarrisonGoal() != null; }

    UsePortalGoal usePortalGoal;
    public UsePortalGoal getUsePortalGoal() { return usePortalGoal; }
    public boolean canUsePortal() { return getUsePortalGoal() != null; }

    public Faction getFaction() {return Faction.VILLAGERS;}
    public Abilities getAbilities() {return abilities;};
    public List<ItemStack> getItems() {return items;};
    public MoveToTargetBlockGoal getMoveGoal() {return moveGoal;}
    public SelectedTargetGoal<? extends LivingEntity> getTargetGoal() {return targetGoal;}
    public Goal getAttackBuildingGoal() {return attackBuildingGoal;}
    public Goal getAttackGoal() {return attackGoal;}
    public ReturnResourcesGoal getReturnResourcesGoal() {return returnResourcesGoal;}
    public int getMaxResources() {return maxResources;}

    private MoveToTargetBlockGoal moveGoal;
    private SelectedTargetGoal<? extends LivingEntity> targetGoal;
    private ReturnResourcesGoal returnResourcesGoal;

    public BlockPos getAttackMoveTarget() { return attackMoveTarget; }
    public LivingEntity getFollowTarget() { return followTarget; }
    public boolean getHoldPosition() { return holdPosition; }
    public void setHoldPosition(boolean holdPosition) { this.holdPosition = holdPosition; }

    // if true causes moveGoal and attackGoal to work together to allow attack moving
    // moves to a block but will chase/attack nearby monsters in range up to a certain distance away
    private BlockPos attackMoveTarget = null;
    private LivingEntity followTarget = null; // if nonnull, continuously moves to the target
    private boolean holdPosition = false;

    // which player owns this unit? this format ensures its synched to client without having to use packets
    public String getOwnerName() { return this.entityData.get(ownerDataAccessor); }
    public void setOwnerName(String name) { this.entityData.set(ownerDataAccessor, name); }
    public static final EntityDataAccessor<String> ownerDataAccessor =
            SynchedEntityData.defineId(RavagerUnit.class, EntityDataSerializers.STRING);

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ownerDataAccessor, "");
    }

    // combat stats
    public boolean getWillRetaliate() {return willRetaliate;}
    public float getAttackCooldown() {return ((20 / attacksPerSecond) * getAttackCooldownMultiplier());}
    public float getAttacksPerSecond() {return 20f / getAttackCooldown();}
    public float getBaseAttacksPerSecond() {return attacksPerSecond;}
    public float getAggroRange() {return aggroRange;}
    public boolean getAggressiveWhenIdle() {
        if (!aggressiveWhenIdle) {
            return false;
        }
        if (!isVehicle()) {
            return true;
        }
        // Keep autonomous attacks while a player is directly riding the ravager.
        return this.getFirstPassenger() instanceof Player;
    }
    public float getAttackRange() {return attackRange;}
    public float getMovementSpeed() {return movementSpeed;}
    public float getUnitAttackDamage() {return attackDamage;}
    public float getUnitMaxHealth() {return maxHealth;}

    @Nullable
    public ResourceCost getCost() {return ResourceCosts.RAVAGER;}
    public boolean canAttackBuildings() {return getAttackBuildingGoal() != null;}

    public void setAttackMoveTarget(@Nullable BlockPos bp) { this.attackMoveTarget = bp; }
    public void setFollowTarget(@Nullable LivingEntity target) { this.followTarget = target; }

    // endregion

    final static public float attackDamage = 22.0f;
    final static public float attacksPerSecond = 0.6f;
    final static public float maxHealth = 450.0f;
    final static public float armorValue = 0.0f;
    final static public float movementSpeed = 0.25f;
    final static public float attackRange = 2; // only used by ranged units or melee building attackers
    final static public float aggroRange = 10;
    final static public boolean willRetaliate = true; // will attack when hurt by an enemy
    final static public boolean aggressiveWhenIdle = true;

    public int maxResources = 100;

    private AbstractMeleeAttackUnitGoal attackGoal;
    private MeleeAttackBuildingGoal attackBuildingGoal;

    private Abilities abilities = ABILITIES.clone();
    private final List<ItemStack> items = new ArrayList<>();
    private boolean wasPlayerControlledLastTick = false;
    private float boatLikeYaw = 0.0F;
    private float boatLikeDeltaRotation = 0.0F;
    private static final float PLAYER_RIDER_SPEED_MULT = 0.35F;
    private static final float BOAT_TURN_DEGREES_PER_TICK = 3.8F;
    private static final float BOAT_FORWARD_FORCE = 0.04F;
    private static final float BOAT_BACKWARD_FORCE = 0.005F;
    private static final float BOAT_TURN_ONLY_FORCE = 0.0F;

    public final static float ROAR_DAMAGE = 8.0f;
    public final static float ROAR_RANGE = 4.0f;
    public final static float ROAR_KNOCKBACK = 6f;
    public final static int ROAR_SLOW_DURATION = 10 * ResourceCost.TICKS_PER_SECOND;

    private static final Predicate<Entity> NO_RAVAGER_AND_ALIVE = e -> e.isAlive() && !(e instanceof Ravager);

    public RavagerUnit(EntityType<? extends Ravager> entityType, Level level) {
        super(entityType, level);

        updateAbilityButtons();
    }

    // prevents attack commands applying only to passengers
    @Override
    protected void updateControlFlags() {}

    @Override
    public boolean removeWhenFarAway(double d) { return false; }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        Entity passenger = this.getFirstPassenger();
        return passenger instanceof LivingEntity living ? living : null;
    }

    public boolean canBeControlledByRider() {
        return this.getControllingPassenger() instanceof Player;
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isAlive() && this.isVehicle() && this.getControllingPassenger() instanceof Player player) {
            // Adapted from vanilla Boat.controlBoat() for boat-like turn feel.
            boolean inputLeft = player.xxa > 0.0F;
            boolean inputRight = player.xxa < 0.0F;
            boolean inputUp = player.zza > 0.0F;
            boolean inputDown = player.zza < 0.0F;

            float moveForce = 0.0F;
            if (inputLeft && !inputRight) {
                boatLikeDeltaRotation = -1.0F;
            } else if (inputRight && !inputLeft) {
                boatLikeDeltaRotation = 1.0F;
            } else {
                boatLikeDeltaRotation = 0.0F;
            }
            if (inputRight != inputLeft && !inputUp && !inputDown) {
                moveForce += BOAT_TURN_ONLY_FORCE;
            }
            boatLikeYaw = Mth.wrapDegrees(boatLikeYaw + boatLikeDeltaRotation * BOAT_TURN_DEGREES_PER_TICK);

            if (inputUp) {
                moveForce += BOAT_FORWARD_FORCE;
            }
            if (inputDown) {
                moveForce -= BOAT_BACKWARD_FORCE;
            }

            float forwardInput = Mth.clamp(moveForce / BOAT_FORWARD_FORCE, -1.0F, 1.0F);

            applyPlayerControlledRotation();

            float baseSpeed = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
            this.setSpeed(baseSpeed * PLAYER_RIDER_SPEED_MULT);
            super.travel(new Vec3(0.0D, travelVector.y, forwardInput));
            if (inputUp || inputDown) {
                // No side skid while turning: keep horizontal velocity only along current facing.
                Vec3 vel = this.getDeltaMovement();
                double horizontal = Math.sqrt(vel.x * vel.x + vel.z * vel.z);
                if (horizontal > 1.0E-5D) {
                    float yawRad = this.getYRot() * ((float) Math.PI / 180.0F);
                    double dirX = -Mth.sin(yawRad);
                    double dirZ = Mth.cos(yawRad);
                    double sign = Math.signum(forwardInput);
                    this.setDeltaMovement(dirX * horizontal * sign, vel.y, dirZ * horizontal * sign);
                }
            } else {
                // No sliding: stop horizontal movement immediately when throttle is released.
                Vec3 vel = this.getDeltaMovement();
                this.setDeltaMovement(0.0D, vel.y, 0.0D);
            }
            return;
        }

        super.travel(travelVector);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, RavagerUnit.maxHealth)
                .add(Attributes.MOVEMENT_SPEED, RavagerUnit.movementSpeed)
                .add(Attributes.ATTACK_DAMAGE, RavagerUnit.attackDamage)
                .add(Attributes.ARMOR, RavagerUnit.armorValue)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75)
                .add(Attributes.FOLLOW_RANGE, Unit.getFollowRange());
    }

    // prevent shield blocks from stunning and triggering a roar
    @Override
    protected void blockedByShield(LivingEntity pEntity) { }

    public void tick() {
        if (shouldDiscard) {
            this.discard();
            return;
        }
        this.setCanPickUpLoot(false);

        boolean playerControlledNow = this.getFirstPassenger() instanceof Player;
        if (playerControlledNow && !wasPlayerControlledLastTick) {
            // Drop old queued orders so the ravager doesn't snap back after a ride.
            Unit.fullResetBehaviours(this);
            this.getNavigation().stop();
            boatLikeYaw = this.getYRot();
            boatLikeDeltaRotation = 0.0F;
        } else if (!playerControlledNow && wasPlayerControlledLastTick) {
            Unit.fullResetBehaviours(this);
            this.getNavigation().stop();
            this.setTarget(null);
            boatLikeDeltaRotation = 0.0F;
        }
        if (playerControlledNow) {
            // Prevent AI navigation jitter while the player is steering.
            this.getNavigation().stop();
        }
        wasPlayerControlledLastTick = playerControlledNow;

        super.tick();
        Unit.tick(this);
        AttackerUnit.tick(this);
        if (playerControlledNow) {
            // Keep final orientation stable after ALL AI/look processing this tick.
            applyPlayerControlledRotation();
        }
    }

    private void applyPlayerControlledRotation() {
        this.setYRot(boatLikeYaw);
        this.setYBodyRot(boatLikeYaw);
        this.setYHeadRot(boatLikeYaw);
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

    public void initialiseGoals() {
        this.usePortalGoal = new UsePortalGoal(this);
        this.moveGoal = new MoveToTargetBlockGoal(this, false, 0);
        this.targetGoal = new SelectedTargetGoal<>(this, true, true);
        this.attackGoal = new MeleeAttackUnitGoal(this, false);
        this.attackBuildingGoal = new MeleeAttackBuildingGoal(this);
    }

    @Override
    protected void registerGoals() {
        initialiseGoals();
        this.goalSelector.addGoal(2, usePortalGoal);

        // movegoal must be lower priority than attacks so that attack-moving works correctly
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, attackGoal);
        this.goalSelector.addGoal(2, attackBuildingGoal);
        this.targetSelector.addGoal(2, targetGoal);
        this.goalSelector.addGoal(3, moveGoal);
        this.goalSelector.addGoal(4, new RandomLookAroundUnitGoal(this));
    }

    private void strongKnockback(Entity pEntity) {
        double d0 = pEntity.getX() - this.getX();
        double d1 = pEntity.getZ() - this.getZ();
        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
        pEntity.push(d0 / d2 * ROAR_KNOCKBACK, 0.2, d1 / d2 * ROAR_KNOCKBACK);
    }

    public void startToRoar() {
        this.playSound(SoundEvents.RAVAGER_ROAR, 3.0F, 1.0F);
        this.roarTick = 40;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isAlive()) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(movementSpeed);
        }
    }

    @Override
    public void roar() {
        if (this.isAlive()) {
            if (!level().isClientSide()) {
                LivingEntity livingentity;

                List<Mob> nearbyMobs = MiscUtil.getEntitiesWithinRange(
                        new Vector3d(this.position().x, this.position().y, this.position().z),
                        ROAR_RANGE,
                        Mob.class,
                        this.level());

                for (Mob mob : nearbyMobs) {
                    if (mob instanceof Unit unit && UnitServerEvents.getUnitToEntityRelationship(this, mob) != Relationship.FRIENDLY) {
                        this.strongKnockback(mob);
                        mob.hurt(damageSources().mobAttack(this), ROAR_DAMAGE);
                        mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, ROAR_SLOW_DURATION, 1));
                    }
                }

                Set<BuildingPlacement> affectedBuildings = new HashSet<>();
                for (double x = this.position().x - ROAR_RANGE; x < this.position().x + ROAR_RANGE; x++) {
                    for (double y = this.position().y - ROAR_RANGE; y < this.position().y + ROAR_RANGE; y++) {
                        for (double z = this.position().z - ROAR_RANGE; z < this.position().z + ROAR_RANGE; z++) {
                            BlockPos bp = new BlockPos((int) x, (int) y, (int) z);
                            BuildingPlacement building = BuildingUtils.findBuilding(false, bp);
                            if (building != null && !building.ownerName.equals(this.getOwnerName()))
                                affectedBuildings.add(building);
                        }
                    }
                }
                for (BuildingPlacement building : affectedBuildings)
                    building.destroyRandomBlocks((int) ROAR_DAMAGE);

                Vec3 vec3 = this.getBoundingBox().getCenter();

                this.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 1.0F);
                this.gameEvent(GameEvent.ENTITY_ROAR);
                this.level().explode(null, null, null,
                        vec3.x, vec3.y, vec3.z,
                        2.0f,
                        false,
                        Level.ExplosionInteraction.NONE);
            }
            else {
                Vec3 vec3 = this.getBoundingBox().getCenter();

                for(int i = 0; i < 80; ++i) {
                    double d0 = this.random.nextGaussian() * 0.2;
                    double d1 = this.random.nextGaussian() * 0.2;
                    double d2 = this.random.nextGaussian() * 0.2;
                    this.level().addParticle(ParticleTypes.POOF, vec3.x, vec3.y, vec3.z, d0, d1, d2);
                }
                this.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 1.0F);
            }
        }
    }




}
