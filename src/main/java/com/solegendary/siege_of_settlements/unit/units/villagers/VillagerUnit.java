package com.solegendary.siege_of_settlements.unit.units.villagers;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.Abilities;
import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.ability.abilities.CallToArmsUnit;
import com.solegendary.siege_of_settlements.building.BuildingPlaceButton;
import com.solegendary.siege_of_settlements.building.custombuilding.CustomBuildingClientEvents;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.faction.FactionRegistries;
import com.solegendary.siege_of_settlements.hud.Button;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.registrars.EnchantmentRegistrar;
import com.solegendary.siege_of_settlements.registrars.EntityRegistrar;
import com.solegendary.siege_of_settlements.registrars.MobEffectRegistrar;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.research.ResearchServerEvents;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.unit.Checkpoint;
import com.solegendary.siege_of_settlements.unit.goals.*;
import com.solegendary.siege_of_settlements.unit.interfaces.*;
import com.solegendary.siege_of_settlements.unit.packets.UnitConvertClientboundPacket;
import com.solegendary.siege_of_settlements.unit.packets.UnitSyncClientboundPacket;
import com.solegendary.siege_of_settlements.faction.Faction;
import com.WardenMine242.siege_of_settlements.ability.abilities.VillagerBecomeTrader;
import com.WardenMine242.siege_of_settlements.ability.abilities.VillagerBecomeFarmer;
import com.WardenMine242.siege_of_settlements.ability.abilities.VillagerBecomeLumberjack;
import com.WardenMine242.siege_of_settlements.ability.abilities.VillagerBecomeMiner;
import com.WardenMine242.siege_of_settlements.ability.abilities.VillagerBecomeMason;
import com.WardenMine242.siege_of_settlements.ability.abilities.VillagerBecomeHunter;
import com.WardenMine242.siege_of_settlements.ability.abilities.VillagerBecomeBlacksmith;
import com.WardenMine242.siege_of_settlements.ability.abilities.VillagerOccupyBlacksmith;
import com.WardenMine242.siege_of_settlements.ability.abilities.ExitBlacksmith;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.client.resources.language.I18n;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.UUID;

import static com.solegendary.siege_of_settlements.unit.units.villagers.VillagerUnitProfession.*;
import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class VillagerUnit extends Vindicator implements Unit, WorkerUnit, AttackerUnit, ArmSwingingUnit, VillagerDataHolder, ConvertableUnit, GeoEntity {
    public static final Abilities ABILITIES = new Abilities();
    static {
        ABILITIES.add(new CallToArmsUnit(), Keybindings.keyQ);
        ABILITIES.add(new VillagerBecomeHunter(), null);
        ABILITIES.add(new VillagerBecomeFarmer(), null);
        ABILITIES.add(new VillagerBecomeLumberjack(), null);
        ABILITIES.add(new VillagerBecomeMiner(), null);
        ABILITIES.add(new VillagerBecomeMason(), null);
        ABILITIES.add(new VillagerBecomeBlacksmith(), null);
        ABILITIES.add(new VillagerOccupyBlacksmith(), Keybindings.keyZ);
        ABILITIES.add(new ExitBlacksmith(), Keybindings.keyZ);
        ABILITIES.add(new VillagerBecomeTrader(), Keybindings.keyX);
    }

    //region
    @Override
    public void updateAbilityButtons() {
        // Only filter abilities on client side
        if (level().isClientSide()) {
            abilities = ABILITIES.clone();
            // Remove blacksmith occupation ability if not a blacksmith
            if (getUnitProfession() != VillagerUnitProfession.BLACKSMITH) {
                abilities.remove(VillagerOccupyBlacksmith.class);
                abilities.remove(ExitBlacksmith.class);
            }
        }
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
    public GarrisonGoal getGarrisonGoal() { return garrisonGoal; }
    public boolean canGarrison() { return getGarrisonGoal() != null; }

    UsePortalGoal usePortalGoal;
    public UsePortalGoal getUsePortalGoal() { return usePortalGoal; }
    public boolean canUsePortal() { return getUsePortalGoal() != null; }

    public Faction getFaction() {return Faction.VILLAGERS;}
    public Abilities getAbilities() {return abilities;}
    public List<ItemStack> getItems() {return items;};
    public MoveToTargetBlockGoal getMoveGoal() {return moveGoal;}
    public SelectedTargetGoal<? extends LivingEntity> getTargetGoal() {return targetGoal;}
    public BuildRepairGoal getBuildRepairGoal() {return buildRepairGoal;}
    public GatherResourcesGoal getGatherResourceGoal() {return gatherResourcesGoal;}
    public ReturnResourcesGoal getReturnResourcesGoal() {return returnResourcesGoal;}
    public int getMaxResources() {return maxResources;}

    private MoveToTargetBlockGoal moveGoal;
    private SelectedTargetGoal<? extends LivingEntity> targetGoal;
    public BuildRepairGoal buildRepairGoal;
    public GatherResourcesGoal gatherResourcesGoal;
    private ReturnResourcesGoal returnResourcesGoal;
    private AbstractMeleeAttackUnitGoal attackGoal;
    public CallToArmsGoal callToArmsGoal;

    public LivingEntity getFollowTarget() { return followTarget; }
    public boolean getHoldPosition() { return holdPosition; }
    public void setHoldPosition(boolean holdPosition) { this.holdPosition = holdPosition; }

    // if true causes moveGoal and attackGoal to work together to allow attack moving
    // moves to a block but will chase/attack nearby monsters in range up to a certain distance away
    private LivingEntity followTarget = null; // if nonnull, continuously moves to the target
    private boolean holdPosition = false;
    private BlockPos attackMoveTarget = null;

    // which player owns this unit? this format ensures its synched to client without having to use packets
    public String getOwnerName() { return this.entityData.get(ownerDataAccessor); }
    public void setOwnerName(String name) { this.entityData.set(ownerDataAccessor, name); }
    public static final EntityDataAccessor<String> ownerDataAccessor =
            SynchedEntityData.defineId(VillagerUnit.class, EntityDataSerializers.STRING);

    // combat stats
    public float getMovementSpeed() {return movementSpeed;}
    public float getUnitMaxHealth() {return maxHealth;}

    @Nullable
    public ResourceCost getCost() {return ResourceCosts.VILLAGER;}
    public boolean getWillRetaliate() {return willRetaliate;}
    public float getAttackCooldown() {return ((20 / attacksPerSecond) * getAttackCooldownMultiplier());}
    public float getAttacksPerSecond() {return 20f / getAttackCooldown();}
    public float getBaseAttacksPerSecond() {return attacksPerSecond;}
    public float getAggroRange() {return aggroRange;}
    public boolean getAggressiveWhenIdle() {return aggressiveWhenIdle && !isVehicle();}
    public float getAttackRange() {return attackRange;}
    public float getUnitAttackDamage() {return attackDamage;}
    public BlockPos getAttackMoveTarget() { return attackMoveTarget; }
    public boolean canAttackBuildings() {return getAttackBuildingGoal() != null;}
    public Goal getAttackGoal() { return attackGoal; }
    public Goal getAttackBuildingGoal() { return null; }
    public void setAttackMoveTarget(@Nullable BlockPos bp) { this.attackMoveTarget = bp; }
    public void setFollowTarget(@Nullable LivingEntity target) { this.followTarget = target; }

    // ConvertableUnit
    public boolean converted = false;
    private boolean shouldDiscard = false;
    public boolean shouldDiscard() { return shouldDiscard; }
    public void setShouldDiscard(boolean discard) { this.shouldDiscard = discard; }

    // endregion

    public BlockState getReplantBlockState() {
        if (getUnitProfession() == FARMER && !isVeteran)
            return Blocks.CARROTS.defaultBlockState();
        else if (getUnitProfession() == FARMER && isVeteran)
            return Blocks.POTATOES.defaultBlockState();
        else
            return Blocks.WHEAT.defaultBlockState();
    }

    final static public float attackDamage = 1.0f;
    final static public float attacksPerSecond = 0.5f;
    final static public float attackRange = 2; // only used by ranged units or melee building attackers
    final static public float aggroRange = 0;
    final static public boolean willRetaliate = false; // will attack when hurt by an enemy
    final static public boolean aggressiveWhenIdle = false;

    final static public float maxHealth = 25.0f;
    final static public float armorValue = 0.0f;
    final static public float movementSpeed = 0.25f;
    public int maxResources = 100;

    // Geckolib animation
    private static final RawAnimation BLACKSMITH_WORKING_ANIM = RawAnimation.begin().thenLoop("blacksmith_work");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    // Blacksmith occupation data
    private static final EntityDataAccessor<Boolean> IN_BLACKSMITH =
            SynchedEntityData.defineId(VillagerUnit.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> BLACKSMITH_SLOT =
            SynchedEntityData.defineId(VillagerUnit.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<BlockPos>> BLACKSMITH_ORIGIN =
            SynchedEntityData.defineId(VillagerUnit.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Integer> PENDING_BLACKSMITH_SLOT =
            SynchedEntityData.defineId(VillagerUnit.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<BlockPos>> PENDING_BLACKSMITH_ORIGIN =
            SynchedEntityData.defineId(VillagerUnit.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);

    public VillagerUnitProfession getUnitProfession() {
        VillagerProfession profession = getProfession();
        if (VillagerProfession.FARMER.equals(profession)) {
            return FARMER;
        } else if (VillagerProfession.FLETCHER.equals(profession)) {
            return VillagerUnitProfession.LUMBERJACK;
        } else if (VillagerProfession.TOOLSMITH.equals(profession)) {
            return VillagerUnitProfession.MINER;
        } else if (VillagerProfession.MASON.equals(profession)) {
            return VillagerUnitProfession.MASON;
        } else if (VillagerProfession.WEAPONSMITH.equals(profession)) {
            return VillagerUnitProfession.HUNTER;
        } else if (VillagerProfession.ARMORER.equals(profession)) {
            return VillagerUnitProfession.BLACKSMITH;
        }
        return VillagerUnitProfession.NONE;
    }

    public boolean isVeteran = false;
    public boolean isVeteran() { return isVeteran; }
    public void makeVeteran() {
        isVeteran = true;
        UnitSyncClientboundPacket.makeVillagerVeteran(this);
    }

    public boolean hasSpeedCheat() {
        return !this.level().isClientSide() && ResearchServerEvents.playerHasCheat(getOwnerName(), "operationcwal");
    }

    // equal to 4 full farm clears
    // bonus == plants carrots instead of wheat (+25% food), potatoes for veteran (+50% food)
    final static public int FARMER_EXP_REQ = 80;
    public int farmerExp = 0; // farm food blocks gathered
    public void incrementFarmerExp() {
        farmerExp += hasSpeedCheat() ? 10 : 1;
        if (farmerExp >= FARMER_EXP_REQ && !isVeteran && getUnitProfession() == FARMER)
            makeVeteran();
    }

    // equal to ~4mins of log chopping, excludes leaves
    final static public float LUMBERJACK_SPEED_MULT = 1.25f;
    final static public float LUMBERJACK_SPEED_MULT_VETERAN = 1.5f;
    final static public int LUMBERJACK_EXP_REQ = 30;
    public int lumberjackExp = 0;
    public void incrementLumberjackExp() {
        lumberjackExp += hasSpeedCheat() ? 10 : 1;
        if (lumberjackExp >= LUMBERJACK_EXP_REQ && !isVeteran && getUnitProfession() == LUMBERJACK)
            makeVeteran();
    }

    // ~5mins of gathering
    final static public float MINER_SPEED_MULT = 0.625f;
    final static public float MINER_SPEED_MULT_VETERAN = 0.666f;
    final static public int MINER_EXP_REQ = 6;
    public int minerExp = 0; // ore blocks gathered
    public void incrementMinerExp() {
        minerExp += hasSpeedCheat() ? 10 : 1;
        if (minerExp >= MINER_EXP_REQ && !isVeteran && getUnitProfession() == MINER)
            makeVeteran();
    }

    // blocks built or repaired, excluding first capitol
    // ~5mins of building
    // counted as +1 worker when building/repairing (+2 for veteran)
    final static public int MASON_EXP_REQ = 600;
    public int masonExp = 0;
    public void incrementMasonExp() {
        masonExp += hasSpeedCheat() ? 10 : 1;
        if (masonExp >= MASON_EXP_REQ && !isVeteran && getUnitProfession() == MASON)
            makeVeteran();
    }

    // chickens only worth 1, other animals worth 2
    // does 2 damage to huntable animals (3 for veteran)
    final static public int HUNTER_EXP_REQ = 8;
    public int hunterExp = 0;
    public void incrementHunterExp() {
        hunterExp += hasSpeedCheat() ? 10 : 1;
        if (hunterExp >= (HUNTER_EXP_REQ / 2) && !hasUnitProfession())
            setProfession(VillagerProfession.WEAPONSMITH);
        else if (hunterExp >= HUNTER_EXP_REQ && !isVeteran && getUnitProfession() == HUNTER)
            makeVeteran();
    }

    // ~10 items crafted
    final static public int BLACKSMITH_EXP_REQ = 10;
    public int blacksmithExp = 0;
    public void incrementBlacksmithExp() {
        blacksmithExp += hasSpeedCheat() ? 10 : 1;
        if (blacksmithExp >= BLACKSMITH_EXP_REQ && !isVeteran && getUnitProfession() == BLACKSMITH)
            makeVeteran();
    }

    public Item chestplate = Items.AIR;
    public boolean chestplateEnchanted = false;
    public boolean swordEnchanted = false;
    public boolean bowEnchanted = false;

    private Abilities abilities = ABILITIES.clone();
    private final List<ItemStack> items = new ArrayList<>();

    private boolean isSwingingArmOnce = false;
    private int swingTime = 0;

    public int getSwingTime() {
        return swingTime;
    }

    public void setSwingTime(int time) {
        this.swingTime = time;
    }

    public boolean isSwingingArmOnce() { return isSwingingArmOnce; }

    public void setSwingingArmOnce(boolean swing) {
        isSwingingArmOnce = swing;
    }

    public boolean isSwingingArmRepeatedly() {
        return ((this.getGatherResourceGoal() != null && this.getGatherResourceGoal().isGathering()) ||
                (this.getBuildRepairGoal() != null && this.getBuildRepairGoal().isBuilding()));
    }

    public static List<BuildingPlaceButton> getBuildingButtons() {
        List<BuildingPlaceButton> buttons = new ArrayList<>();
        buttons.addAll(FactionRegistries.VILLAGERS.getBuildingButtons());

        // Custom buildings are collected dynamically as they are registered.
        CustomBuildingClientEvents.customBuildings.forEach(cb -> {
            if (cb.buildableByVillagers)
                buttons.add(cb.getWorkerBuildButton(null));
        });

        return buttons;
    }

    public VillagerUnit(EntityType<? extends Vindicator> entityType, Level level) {
        super(entityType, level);

        updateAbilityButtons();
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double d) { return false; }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, VillagerUnit.attackDamage)
                .add(Attributes.MOVEMENT_SPEED, VillagerUnit.movementSpeed)
                .add(Attributes.MAX_HEALTH, VillagerUnit.maxHealth)
                .add(Attributes.FOLLOW_RANGE, Unit.getFollowRange())
                .add(Attributes.ARMOR, VillagerUnit.armorValue);
    }

    @Override
    protected SoundEvent getAmbientSound() { return SoundEvents.VILLAGER_AMBIENT;}
    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.VILLAGER_DEATH; }
    @Override
    protected SoundEvent getHurtSound(DamageSource p_34103_) { return SoundEvents.VILLAGER_HURT; }
    @Override
    public boolean isLeftHanded() { return false; }
    @Override // prevent vanilla logic for picking up items
    protected void pickUpItem(ItemEntity pItemEntity) { }

    public void tick() {
        if (shouldDiscard)
            this.discard();
        else {
            this.setCanPickUpLoot(true);
            super.tick();
            Unit.tick(this);
            AttackerUnit.tick(this);
            WorkerUnit.tick(this);
            this.callToArmsGoal.tick();

            if (tickCount % 20 == 0) {
                if (getMainHandItem().getAllEnchantments().containsKey(Enchantments.BLOCK_EFFICIENCY) &&
                    !hasEffectWithDuration(MobEffectRegistrar.TEMPORARY_EFFICIENCY.get())) {
                    EnchantmentHelper.setEnchantments(new HashMap<>(), getMainHandItem());
                }
            }

            // Handle blacksmith occupation
            if (isMovingToBlacksmith()) {
                handleMoveToBlacksmith();
            } else {
                forceBlacksmithPosition();
            }
        }
    }

    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        DataResult<Tag> var10000 = VillagerData.CODEC.encodeStart(NbtOps.INSTANCE, this.getVillagerData());
        var10000.resultOrPartial((err) -> SiegeOfSettlements.LOGGER.error("Failed to save villager data"))
                .ifPresent((tag) -> pCompound.put("VillagerData", tag));
        pCompound.putInt("farmerExp", this.farmerExp);
        pCompound.putInt("lumberjackExp", this.lumberjackExp);
        pCompound.putInt("minerExp", this.minerExp);
        pCompound.putInt("masonExp", this.masonExp);
        pCompound.putInt("hunterExp", this.hunterExp);
        pCompound.putInt("blacksmithExp", this.blacksmithExp);
        pCompound.putBoolean("isVeteran", this.isVeteran);
        pCompound.putInt("chestplateId", Item.getId(chestplate));
        pCompound.putBoolean("chestplateEnchanted", chestplateEnchanted);
        pCompound.putBoolean("swordEnchanted", swordEnchanted);
        pCompound.putBoolean("bowEnchanted", bowEnchanted);
        pCompound.putBoolean("ronPlusInBlacksmith", isOccupyingBlacksmith());
        pCompound.putInt("ronPlusBlacksmithSlot", getBlacksmithSlotIndex());
        BlockPos blacksmithOrigin = getBlacksmithOrigin();
        if (blacksmithOrigin != null) {
            pCompound.putInt("ronPlusBlacksmithX", blacksmithOrigin.getX());
            pCompound.putInt("ronPlusBlacksmithY", blacksmithOrigin.getY());
            pCompound.putInt("ronPlusBlacksmithZ", blacksmithOrigin.getZ());
        }
        pCompound.putBoolean("ronPlusPendingBlacksmith", isMovingToBlacksmith());
        pCompound.putInt("ronPlusPendingBlacksmithSlot", getPendingBlacksmithSlotIndex());
        BlockPos pendingOrigin = getPendingBlacksmithOrigin();
        if (pendingOrigin != null) {
            pCompound.putInt("ronPlusPendingBlacksmithX", pendingOrigin.getX());
            pCompound.putInt("ronPlusPendingBlacksmithY", pendingOrigin.getY());
            pCompound.putInt("ronPlusPendingBlacksmithZ", pendingOrigin.getZ());
        }
        this.addUnitSaveData(pCompound);
    }

    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("VillagerData", 10)) {
            DataResult<VillagerData> dataresult = VillagerData.CODEC.parse(new Dynamic(NbtOps.INSTANCE, pCompound.get("VillagerData")));
            dataresult.resultOrPartial((err) -> SiegeOfSettlements.LOGGER.error("Failed to load villager data"))
                    .ifPresent(this::setVillagerData);
        }
        this.farmerExp = pCompound.getInt("farmerExp");
        this.lumberjackExp = pCompound.getInt("lumberjackExp");
        this.minerExp = pCompound.getInt("minerExp");
        this.masonExp = pCompound.getInt("masonExp");
        this.hunterExp = pCompound.getInt("hunterExp");
        this.blacksmithExp = pCompound.getInt("blacksmithExp");
        if (pCompound.contains("chestplateId"))
            this.chestplate = Item.byId(pCompound.getInt("chestplateId"));
        if (pCompound.contains("chestplateEnchanted"))
            this.chestplateEnchanted = pCompound.getBoolean("chestplateEnchanted");
        if (pCompound.contains("swordEnchanted"))
            this.swordEnchanted = pCompound.getBoolean("swordEnchanted");
        if (pCompound.contains("bowEnchanted"))
            this.bowEnchanted = pCompound.getBoolean("bowEnchanted");
        if (!level().isClientSide() && pCompound.getBoolean("isVeteran"))
            makeVeteran();
        
        // Load blacksmith occupation data
        if (pCompound.getBoolean("ronPlusInBlacksmith")) {
            int slot = pCompound.getInt("ronPlusBlacksmithSlot");
            BlockPos origin = new BlockPos(
                    pCompound.getInt("ronPlusBlacksmithX"),
                    pCompound.getInt("ronPlusBlacksmithY"),
                    pCompound.getInt("ronPlusBlacksmithZ")
            );
            if (slot >= 0 && !origin.equals(BlockPos.ZERO)) {
                occupyBlacksmith(origin, slot);
            } else {
                leaveBlacksmith();
            }
        } else {
            leaveBlacksmith();
        }

        if (!isOccupyingBlacksmith() && pCompound.getBoolean("ronPlusPendingBlacksmith")) {
            int pendingSlot = pCompound.getInt("ronPlusPendingBlacksmithSlot");
            BlockPos pendingOrigin = new BlockPos(
                    pCompound.getInt("ronPlusPendingBlacksmithX"),
                    pCompound.getInt("ronPlusPendingBlacksmithY"),
                    pCompound.getInt("ronPlusPendingBlacksmithZ")
            );
            if (pendingSlot >= 0 && !pendingOrigin.equals(BlockPos.ZERO)) {
                startMovingToBlacksmith(pendingOrigin, pendingSlot);
            }
        }
        
        this.readUnitSaveData(pCompound);
    }

    public void convertToMilitia() {
        if (!converted) {
            LivingEntity newEntity = this.convertToUnit(EntityRegistrar.MILITIA_UNIT.get());
            if (newEntity instanceof MilitiaUnit mUnit) {
                mUnit.resourcesSaveData = this.gatherResourcesGoal.permSaveData;
                mUnit.profession = this.getProfession();
                mUnit.isVeteran = this.isVeteran;
                mUnit.farmerExp = this.farmerExp;
                mUnit.lumberjackExp = this.lumberjackExp;
                mUnit.minerExp = this.minerExp;
                mUnit.masonExp = this.masonExp;
                mUnit.hunterExp = this.hunterExp;
                mUnit.blacksmithExp = this.blacksmithExp;
                ItemStack chest = new ItemStack(this.chestplate);
                if (chestplateEnchanted && chest.getItem() != Items.AIR) {
                    chest.enchant(EnchantmentRegistrar.FORTYIFYING.get(), 1);
                }
                mUnit.setItemSlot(EquipmentSlot.CHEST, chest);
                mUnit.swordEnchanted = swordEnchanted;
                mUnit.bowEnchanted = bowEnchanted;
                mUnit.swapWeapons(mUnit.isUsingBow());

                UnitConvertClientboundPacket.syncConvertedUnits(getOwnerName(), List.of(getId()), List.of(newEntity.getId()));
                converted = true;
            }
        }
    }

    @Override
    public void resetBehaviours() {
        this.callToArmsGoal.stop();
    }

    public void initialiseGoals() {
        this.usePortalGoal = new UsePortalGoal(this);
        this.moveGoal = new MoveToTargetBlockGoal(this, false, 0);
        this.targetGoal = new SelectedTargetGoal<>(this, true, true);
        this.garrisonGoal = new GarrisonGoal(this);
        this.attackGoal = new MeleeAttackUnitGoal(this, true);
        this.buildRepairGoal = new BuildRepairGoal(this);
        this.gatherResourcesGoal = new GatherResourcesGoal(this);
        this.returnResourcesGoal = new ReturnResourcesGoal(this);
        this.callToArmsGoal = new CallToArmsGoal(this);
    }

    @Override
    protected void registerGoals() {
        initialiseGoals();
        this.goalSelector.addGoal(2, usePortalGoal);

        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, attackGoal);
        this.goalSelector.addGoal(2, buildRepairGoal);
        this.goalSelector.addGoal(2, gatherResourcesGoal);
        this.goalSelector.addGoal(2, returnResourcesGoal);
        this.goalSelector.addGoal(2, garrisonGoal);
        this.targetSelector.addGoal(2, targetGoal);
        this.goalSelector.addGoal(3, moveGoal);
        this.goalSelector.addGoal(4, new RandomLookAroundUnitGoal(this));
    }

    @Override
    public void setupEquipmentAndUpgradesClient() {
        if (ResearchClient.hasResearch(ProductionItems.RESEARCH_RESOURCE_CAPACITY))
            this.maxResources = 200;
    }

    @Override
    public void setupEquipmentAndUpgradesServer() {
        if (ResearchServerEvents.playerHasResearch(this.getOwnerName(), ProductionItems.RESEARCH_RESOURCE_CAPACITY))
            this.maxResources = 200;

        if (this.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof BannerItem)
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.AIR));
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        return pSpawnData;
    }

    @Override
    public List<FormattedCharSequence> getAttackDamageStatTooltip() {
        if (getUnitProfession() == HUNTER) {
            return List.of(
                    fcs(I18n.get("unitstats.siege_of_settlements.attack_damage"), true),
                    fcs(I18n.get("unitstats.siege_of_settlements.attack_damage_bonus_animals", isVeteran() ? "100%" : "50%"))
            );
        } else {
            return List.of(fcs(I18n.get("unitstats.siege_of_settlements.attack_damage"), true));
        }
    }
    @Override
    public boolean hasBonusDamage() {
        return getUnitProfession() == HUNTER;
    }

    private static final EntityDataAccessor<VillagerData> VILLAGER_DATA;

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ownerDataAccessor, "");
        this.entityData.define(VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
        this.entityData.define(IN_BLACKSMITH, false);
        this.entityData.define(BLACKSMITH_SLOT, -1);
        this.entityData.define(BLACKSMITH_ORIGIN, Optional.empty());
        this.entityData.define(PENDING_BLACKSMITH_SLOT, -1);
        this.entityData.define(PENDING_BLACKSMITH_ORIGIN, Optional.empty());
    }

    @Override
    public VillagerData getVillagerData() {
        return this.entityData.get(VILLAGER_DATA);
    }

    @Override
    public void setVillagerData(@NotNull VillagerData data) {
        VillagerData villagerdata = this.getVillagerData();
        this.entityData.set(VILLAGER_DATA, data);
    }

    public void setProfession(VillagerProfession profession) {
        this.setVillagerData(this.getVillagerData().setProfession(profession));
        if (profession == VillagerProfession.FARMER && getItemBySlot(EquipmentSlot.HEAD).getItem() == Items.CARVED_PUMPKIN)
            setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.AIR));
    }
    public VillagerProfession getProfession() {
        return this.getVillagerData().getProfession();
    }
    public boolean hasUnitProfession() {
        return this.getUnitProfession() != VillagerUnitProfession.NONE;
    }

    @Override
    public List<Button> getAbilityButtons() {
        List<Button> abilities = new ArrayList<>(getAbilities().getButtons(this));
        if (FMLEnvironment.dist == Dist.CLIENT) {
            abilities.addAll(getBuildingButtons());
        }
        return abilities;
    }

    @Override
    public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {
        if (pStack.getItem() != Items.AIR && pSlot == EquipmentSlot.MAINHAND &&
            this.hasEffectWithDuration(MobEffectRegistrar.TEMPORARY_EFFICIENCY.get())) {
            pStack.enchant(Enchantments.BLOCK_EFFICIENCY, 1);
        }
        super.setItemSlot(pSlot, pStack);
    }

    static {
        VILLAGER_DATA = SynchedEntityData.defineId(VillagerUnit.class, EntityDataSerializers.VILLAGER_DATA);
    }

    // Geckolib GeoEntity implementation
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "villager_controller", 0, this::animationPredicate));
    }

    private PlayState animationPredicate(AnimationState<VillagerUnit> state) {
        if (isOccupyingBlacksmith() && getUnitProfession() == VillagerUnitProfession.BLACKSMITH) {
            state.getController().setAnimationSpeed(1.0D);
            state.setAndContinue(BLACKSMITH_WORKING_ANIM);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    // Blacksmith occupation methods
    public boolean isOccupyingBlacksmith() {
        return this.entityData.get(IN_BLACKSMITH) && this.entityData.get(BLACKSMITH_ORIGIN).isPresent() &&
                this.entityData.get(BLACKSMITH_SLOT) >= 0;
    }

    @Nullable
    public BlockPos getBlacksmithOrigin() {
        return this.entityData.get(BLACKSMITH_ORIGIN).orElse(null);
    }

    public int getBlacksmithSlotIndex() {
        return this.entityData.get(BLACKSMITH_SLOT);
    }

    public boolean isMovingToBlacksmith() {
        return !isOccupyingBlacksmith() &&
                this.entityData.get(PENDING_BLACKSMITH_ORIGIN).isPresent() &&
                this.entityData.get(PENDING_BLACKSMITH_SLOT) >= 0;
    }

    @Nullable
    public BlockPos getPendingBlacksmithOrigin() {
        return this.entityData.get(PENDING_BLACKSMITH_ORIGIN).orElse(null);
    }

    public int getPendingBlacksmithSlotIndex() {
        return this.entityData.get(PENDING_BLACKSMITH_SLOT);
    }

    public void startMovingToBlacksmith(BlockPos blacksmithOrigin, int slotIndex) {
        leaveBlacksmith();
        this.entityData.set(PENDING_BLACKSMITH_ORIGIN, Optional.of(blacksmithOrigin.immutable()));
        this.entityData.set(PENDING_BLACKSMITH_SLOT, slotIndex);
        this.setNoAi(false);

        if (!this.level().isClientSide()) {
            Unit.fullResetBehaviours(this);
            this.getNavigation().stop();
            com.solegendary.siege_of_settlements.building.buildings.placements.BlacksmithPlacement blacksmith = 
                com.WardenMine242.siege_of_settlements.ability.abilities.VillagerOccupyBlacksmith.findBlacksmithByOrigin(this.level(), blacksmithOrigin, this.getOwnerName());
            if (blacksmith != null) {
                BlockPos slotFront = com.solegendary.siege_of_settlements.building.buildings.placements.BlacksmithPlacement.getBlacksmithSlotFrontWorldPos(blacksmith, slotIndex);
                this.setMoveTarget(slotFront);
            }
        }
    }

    private void clearPendingBlacksmith() {
        this.entityData.set(PENDING_BLACKSMITH_ORIGIN, Optional.empty());
        this.entityData.set(PENDING_BLACKSMITH_SLOT, -1);
    }

    public void occupyBlacksmith(BlockPos blacksmithOrigin, int slotIndex) {
        this.entityData.set(IN_BLACKSMITH, true);
        this.entityData.set(BLACKSMITH_ORIGIN, Optional.of(blacksmithOrigin.immutable()));
        this.entityData.set(BLACKSMITH_SLOT, slotIndex);
        clearPendingBlacksmith();

        if (!this.level().isClientSide()) {
            Unit.fullResetBehaviours(this);
            this.getNavigation().stop();
            this.setNoAi(true);
        }
    }

    public void leaveBlacksmith() {
        this.entityData.set(IN_BLACKSMITH, false);
        this.entityData.set(BLACKSMITH_ORIGIN, Optional.empty());
        this.entityData.set(BLACKSMITH_SLOT, -1);
        clearPendingBlacksmith();
        this.setNoAi(false);

        if (!this.level().isClientSide()) {
            Unit.fullResetBehaviours(this);
            this.getNavigation().stop();
        }
    }

    public static void forceLeaveBlacksmith(com.solegendary.siege_of_settlements.building.buildings.placements.BlacksmithPlacement blacksmith) {
        if (blacksmith.level.isClientSide) {
            return;
        }
        for (VillagerUnit villager : blacksmith.level.getEntitiesOfClass(VillagerUnit.class,
                new AABB(blacksmith.minCorner).inflate(10))) {
            if (villager.isOccupyingBlacksmith() && blacksmith.originPos.equals(villager.getBlacksmithOrigin())) {
                villager.exitBlacksmithToFront();
            }
        }
    }

    public void exitBlacksmithToFront() {
        BlockPos blacksmithOrigin = getBlacksmithOrigin();
        int slotIndex = getBlacksmithSlotIndex();
        com.solegendary.siege_of_settlements.building.buildings.placements.BlacksmithPlacement blacksmith = null;
        if (blacksmithOrigin != null) {
            blacksmith = com.WardenMine242.siege_of_settlements.ability.abilities.VillagerOccupyBlacksmith.findBlacksmithByOrigin(this.level(), blacksmithOrigin, this.getOwnerName());
        }

        BlockPos tpPos = null;
        float yaw = this.getYRot();
        if (blacksmith != null && slotIndex >= 0 && slotIndex < blacksmith.getBlacksmithSlotCount()) {
            tpPos = com.solegendary.siege_of_settlements.building.buildings.placements.BlacksmithPlacement.getBlacksmithSlotExitWorldPos(blacksmith, slotIndex);
            yaw = com.solegendary.siege_of_settlements.building.buildings.placements.BlacksmithPlacement.getBlacksmithSlotFacing(blacksmith, slotIndex).getOpposite().toYRot();
        }

        leaveBlacksmith();

        if (tpPos != null) {
            this.teleportTo(tpPos.getX() + 0.5D, tpPos.getY(), tpPos.getZ() + 0.5D);
            this.setDeltaMovement(0.0D, 0.0D, 0.0D);
            this.setYRot(yaw);
            this.setYHeadRot(yaw);
            this.setYBodyRot(yaw);
            this.setXRot(0.0F);
            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();
            this.yHeadRotO = this.getYHeadRot();
            if (!this.level().isClientSide()) {
                this.hurtMarked = true;
            }
        }
    }

    private void handleMoveToBlacksmith() {
        if (this.level().isClientSide()) {
            return;
        }

        BlockPos pendingOrigin = getPendingBlacksmithOrigin();
        if (pendingOrigin == null) {
            clearPendingBlacksmith();
            return;
        }

        com.solegendary.siege_of_settlements.building.buildings.placements.BlacksmithPlacement blacksmith = 
            com.WardenMine242.siege_of_settlements.ability.abilities.VillagerOccupyBlacksmith.findBlacksmithByOrigin(this.level(), pendingOrigin, this.getOwnerName());
        if (blacksmith == null) {
            clearPendingBlacksmith();
            return;
        }

        int slot = getPendingBlacksmithSlotIndex();
        if (slot < 0 || slot >= blacksmith.getBlacksmithSlotCount() ||
                com.WardenMine242.siege_of_settlements.ability.abilities.VillagerOccupyBlacksmith.isSlotTakenByAnother(blacksmith, slot, this)) {
            slot = com.WardenMine242.siege_of_settlements.ability.abilities.VillagerOccupyBlacksmith.findFirstFreeSlot(blacksmith, this);
            if (slot >= 0) {
                this.entityData.set(PENDING_BLACKSMITH_SLOT, slot);
            }
        }
        if (slot < 0) {
            clearPendingBlacksmith();
            return;
        }

        BlockPos slotFront = com.solegendary.siege_of_settlements.building.buildings.placements.BlacksmithPlacement.getBlacksmithSlotFrontWorldPos(blacksmith, slot);

        if (this.tickCount % 10 == 0 || this.getMoveGoal().getMoveTarget() == null ||
                !slotFront.equals(this.getMoveGoal().getMoveTarget())) {
            this.setMoveTarget(slotFront);
        }

        boolean closeToSlotFront = this.distanceToSqr(slotFront.getCenter()) <= 4.0D;
        boolean insideBuilding = blacksmith.isPosInsideBuilding(this.getOnPos()) || blacksmith.isPosInsideBuilding(this.getOnPos().above());
        boolean closeToCentre = this.distanceToSqr(blacksmith.centrePos.getCenter()) <= 49.0D;
        if (!insideBuilding && !closeToSlotFront && !closeToCentre) {
            return;
        }

        occupyBlacksmith(blacksmith.originPos, slot);
        forceBlacksmithPosition();
    }

    public void forceBlacksmithPosition() {
        if (!isOccupyingBlacksmith()) {
            if (this.isNoAi() && !isMovingToBlacksmith()) {
                this.setNoAi(false);
            }
            return;
        }

        if (!this.level().isClientSide()) {
            this.setNoAi(true);
            this.getNavigation().stop();
        }

        com.solegendary.siege_of_settlements.building.buildings.placements.BlacksmithPlacement blacksmith = 
            com.WardenMine242.siege_of_settlements.ability.abilities.VillagerOccupyBlacksmith.findBlacksmithForVillager(this.level(), this);
        if (blacksmith == null || getBlacksmithSlotIndex() >= blacksmith.getBlacksmithSlotCount()) {
            if (!this.level().isClientSide() && this.tickCount > 40) {
                leaveBlacksmith();
            }
            return;
        }

        BlockPos slotPos = com.solegendary.siege_of_settlements.building.buildings.placements.BlacksmithPlacement.getBlacksmithSlotWorldPos(blacksmith, getBlacksmithSlotIndex());
        Direction lookDir = com.solegendary.siege_of_settlements.building.buildings.placements.BlacksmithPlacement.getBlacksmithSlotFacing(blacksmith, getBlacksmithSlotIndex());

        this.setPos(slotPos.getX() + 0.5D, slotPos.getY(), slotPos.getZ() + 0.5D);
        this.setDeltaMovement(0.0D, 0.0D, 0.0D);

        float yaw = lookDir.toYRot();
        this.setYRot(yaw);
        this.setYHeadRot(yaw);
        this.setYBodyRot(yaw);
        this.setXRot(0.0F);

        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        this.yHeadRotO = this.getYHeadRot();

        if (!this.level().isClientSide()) {
            this.getNavigation().stop();
            this.setNoAi(true);
            this.hurtMarked = true;
        }
    }
}
