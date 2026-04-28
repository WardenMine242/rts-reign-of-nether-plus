package com.WardenMine242.siege_of_settlements.unit.units.villagers;

import com.solegendary.siege_of_settlements.ability.Abilities;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.resources.Resources;
import com.solegendary.siege_of_settlements.resources.ResourcesServerEvents;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.villagers.VindicatorUnit;
import com.WardenMine242.siege_of_settlements.ability.abilities.ExitMarketplace;
import com.WardenMine242.siege_of_settlements.ability.abilities.OccupyMarketplace;
import com.WardenMine242.siege_of_settlements.building.buildings.placements.MarketplacePlacement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;

public class TraderUnit extends VindicatorUnit {
    // Income while the trader is occupying a marketplace stall.
    public static final int MARKETPLACE_EMERALDS_PER_MINUTE = 10;
    private static final int TICKS_PER_MINUTE = 20 * 60;

    private static final EntityDataAccessor<Boolean> IN_MARKETPLACE =
            SynchedEntityData.defineId(TraderUnit.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> MARKET_SLOT =
            SynchedEntityData.defineId(TraderUnit.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<BlockPos>> MARKET_ORIGIN =
            SynchedEntityData.defineId(TraderUnit.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Integer> PENDING_MARKET_SLOT =
            SynchedEntityData.defineId(TraderUnit.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<BlockPos>> PENDING_MARKET_ORIGIN =
            SynchedEntityData.defineId(TraderUnit.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);

    public static final Abilities ABILITIES = new Abilities();
    static {
        ABILITIES.add(new OccupyMarketplace(), Keybindings.keyQ);
        ABILITIES.add(new ExitMarketplace(), Keybindings.keyQ);
    }

    private Abilities abilities = ABILITIES.clone();
    private int marketplaceIncomeProgress = 0;

    public TraderUnit(EntityType<? extends Vindicator> entityType, Level level) {
        super(entityType, level);
        updateAbilityButtons();
    }

    @Override
    public void updateAbilityButtons() {
        abilities = ABILITIES.clone();
    }

    @Override
    public Abilities getAbilities() {
        return abilities;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IN_MARKETPLACE, false);
        this.entityData.define(MARKET_SLOT, -1);
        this.entityData.define(MARKET_ORIGIN, Optional.empty());
        this.entityData.define(PENDING_MARKET_SLOT, -1);
        this.entityData.define(PENDING_MARKET_ORIGIN, Optional.empty());
    }

    public boolean isOccupyingMarketplace() {
        return this.entityData.get(IN_MARKETPLACE) && this.entityData.get(MARKET_ORIGIN).isPresent() &&
                this.entityData.get(MARKET_SLOT) >= 0;
    }

    @Nullable
    public BlockPos getMarketplaceOrigin() {
        return this.entityData.get(MARKET_ORIGIN).orElse(null);
    }

    public int getMarketplaceSlotIndex() {
        return this.entityData.get(MARKET_SLOT);
    }

    public boolean isMovingToMarketplace() {
        return !isOccupyingMarketplace() &&
                this.entityData.get(PENDING_MARKET_ORIGIN).isPresent() &&
                this.entityData.get(PENDING_MARKET_SLOT) >= 0;
    }

    @Nullable
    public BlockPos getPendingMarketplaceOrigin() {
        return this.entityData.get(PENDING_MARKET_ORIGIN).orElse(null);
    }

    public int getPendingMarketplaceSlotIndex() {
        return this.entityData.get(PENDING_MARKET_SLOT);
    }

    public void startMovingToMarketplace(BlockPos marketOrigin, int slotIndex) {
        leaveMarketplace();
        this.entityData.set(PENDING_MARKET_ORIGIN, Optional.of(marketOrigin.immutable()));
        this.entityData.set(PENDING_MARKET_SLOT, slotIndex);
        this.setNoAi(false);

        if (!this.level().isClientSide()) {
            Unit.fullResetBehaviours(this);
            this.getNavigation().stop();
            MarketplacePlacement marketplace = OccupyMarketplace.findMarketplaceByOrigin(this.level(), marketOrigin, this.getOwnerName());
            if (marketplace != null) {
                BlockPos slotFront = MarketplacePlacement.getTraderSlotFrontWorldPos(marketplace, slotIndex);
                this.setMoveTarget(slotFront);
            }
        }
    }

    private void clearPendingMarketplace() {
        this.entityData.set(PENDING_MARKET_ORIGIN, Optional.empty());
        this.entityData.set(PENDING_MARKET_SLOT, -1);
    }

    public void occupyMarketplace(BlockPos marketOrigin, int slotIndex) {
        this.entityData.set(IN_MARKETPLACE, true);
        this.entityData.set(MARKET_ORIGIN, Optional.of(marketOrigin.immutable()));
        this.entityData.set(MARKET_SLOT, slotIndex);
        clearPendingMarketplace();

        if (!this.level().isClientSide()) {
            Unit.fullResetBehaviours(this);
            this.getNavigation().stop();
            this.setNoAi(true);
        }
    }

    public void leaveMarketplace() {
        this.entityData.set(IN_MARKETPLACE, false);
        this.entityData.set(MARKET_ORIGIN, Optional.empty());
        this.entityData.set(MARKET_SLOT, -1);
        clearPendingMarketplace();
        this.setNoAi(false);

        if (!this.level().isClientSide()) {
            Unit.fullResetBehaviours(this);
            this.getNavigation().stop();
        }
    }

    public void exitMarketplaceToFront() {
        BlockPos marketOrigin = getMarketplaceOrigin();
        int slotIndex = getMarketplaceSlotIndex();
        MarketplacePlacement marketplace = null;
        if (marketOrigin != null) {
            marketplace = OccupyMarketplace.findMarketplaceByOrigin(this.level(), marketOrigin, this.getOwnerName());
        }

        BlockPos tpPos = null;
        float yaw = this.getYRot();
        if (marketplace != null && slotIndex >= 0 && slotIndex < marketplace.getTraderSlotCount()) {
            tpPos = MarketplacePlacement.getTraderSlotExitWorldPos(marketplace, slotIndex);
            yaw = MarketplacePlacement.getTraderSlotFacing(marketplace, slotIndex).getOpposite().toYRot();
        }

        leaveMarketplace();

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

    @Override
    public void tick() {
        super.tick();
        tickMarketplaceIncome();
        if (isMovingToMarketplace()) {
            handleMoveToMarketplace();
        } else {
            forceMarketStallPosition();
        }
    }

    private void tickMarketplaceIncome() {
        if (this.level().isClientSide() || !isOccupyingMarketplace()) {
            return;
        }

        // Give income as a single payout once per minute (not gradually).
        marketplaceIncomeProgress += 1;
        if (marketplaceIncomeProgress < TICKS_PER_MINUTE) {
            return;
        }

        marketplaceIncomeProgress %= TICKS_PER_MINUTE;
        ResourcesServerEvents.addSubtractResources(
                new Resources(getOwnerName(), 0, 0, 0, MARKETPLACE_EMERALDS_PER_MINUTE)
        );
    }

    private void handleMoveToMarketplace() {
        if (this.level().isClientSide()) {
            return;
        }

        BlockPos pendingOrigin = getPendingMarketplaceOrigin();
        if (pendingOrigin == null) {
            clearPendingMarketplace();
            return;
        }

        MarketplacePlacement marketplace = OccupyMarketplace.findMarketplaceByOrigin(this.level(), pendingOrigin, this.getOwnerName());
        if (marketplace == null) {
            clearPendingMarketplace();
            return;
        }

        int slot = getPendingMarketplaceSlotIndex();
        if (slot < 0 || slot >= marketplace.getTraderSlotCount() ||
                OccupyMarketplace.isSlotTakenByAnother(marketplace, slot, this)) {
            slot = OccupyMarketplace.findFirstFreeSlot(marketplace, this);
            if (slot >= 0) {
                this.entityData.set(PENDING_MARKET_SLOT, slot);
            }
        }
        if (slot < 0) {
            clearPendingMarketplace();
            return;
        }

        BlockPos slotFront = MarketplacePlacement.getTraderSlotFrontWorldPos(marketplace, slot);

        if (this.tickCount % 10 == 0 || this.getMoveGoal().getMoveTarget() == null ||
                !slotFront.equals(this.getMoveGoal().getMoveTarget())) {
            this.setMoveTarget(slotFront);
        }

        boolean closeToSlotFront = this.distanceToSqr(slotFront.getCenter()) <= 4.0D;
        boolean insideBuilding = marketplace.isPosInsideBuilding(this.getOnPos()) || marketplace.isPosInsideBuilding(this.getOnPos().above());
        boolean closeToCentre = this.distanceToSqr(marketplace.centrePos.getCenter()) <= 49.0D;
        if (!insideBuilding && !closeToSlotFront && !closeToCentre) {
            return;
        }

        occupyMarketplace(marketplace.originPos, slot);
        forceMarketStallPosition();
    }

    public void forceMarketStallPosition() {
        if (!isOccupyingMarketplace()) {
            if (this.isNoAi() && !isMovingToMarketplace()) {
                this.setNoAi(false);
            }
            return;
        }

        if (!this.level().isClientSide()) {
            this.setNoAi(true);
            this.getNavigation().stop();
        }

        MarketplacePlacement marketplace = OccupyMarketplace.findMarketplaceForTrader(this.level(), this);
        if (marketplace == null || getMarketplaceSlotIndex() >= marketplace.getTraderSlotCount()) {
            if (!this.level().isClientSide() && this.tickCount > 40) {
                leaveMarketplace();
            }
            return;
        }

        BlockPos slotPos = MarketplacePlacement.getTraderSlotWorldPos(marketplace, getMarketplaceSlotIndex());
        Direction lookDir = MarketplacePlacement.getTraderSlotFacing(marketplace, getMarketplaceSlotIndex());

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

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("ronPlusInMarketplace", isOccupyingMarketplace());
        pCompound.putInt("ronPlusMarketSlot", getMarketplaceSlotIndex());
        BlockPos marketOrigin = getMarketplaceOrigin();
        if (marketOrigin != null) {
            pCompound.putInt("ronPlusMarketX", marketOrigin.getX());
            pCompound.putInt("ronPlusMarketY", marketOrigin.getY());
            pCompound.putInt("ronPlusMarketZ", marketOrigin.getZ());
        }
        pCompound.putBoolean("ronPlusPendingMarket", isMovingToMarketplace());
        pCompound.putInt("ronPlusPendingMarketSlot", getPendingMarketplaceSlotIndex());
        BlockPos pendingOrigin = getPendingMarketplaceOrigin();
        if (pendingOrigin != null) {
            pCompound.putInt("ronPlusPendingMarketX", pendingOrigin.getX());
            pCompound.putInt("ronPlusPendingMarketY", pendingOrigin.getY());
            pCompound.putInt("ronPlusPendingMarketZ", pendingOrigin.getZ());
        }
        pCompound.putInt("ronPlusMarketIncomeProgress", marketplaceIncomeProgress);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.getBoolean("ronPlusInMarketplace")) {
            int slot = pCompound.getInt("ronPlusMarketSlot");
            BlockPos origin = new BlockPos(
                    pCompound.getInt("ronPlusMarketX"),
                    pCompound.getInt("ronPlusMarketY"),
                    pCompound.getInt("ronPlusMarketZ")
            );
            if (slot >= 0 && !origin.equals(BlockPos.ZERO)) {
                occupyMarketplace(origin, slot);
            } else {
                leaveMarketplace();
            }
        } else {
            leaveMarketplace();
        }

        if (!isOccupyingMarketplace() && pCompound.getBoolean("ronPlusPendingMarket")) {
            int pendingSlot = pCompound.getInt("ronPlusPendingMarketSlot");
            BlockPos pendingOrigin = new BlockPos(
                    pCompound.getInt("ronPlusPendingMarketX"),
                    pCompound.getInt("ronPlusPendingMarketY"),
                    pCompound.getInt("ronPlusPendingMarketZ")
            );
            if (pendingSlot >= 0 && !pendingOrigin.equals(BlockPos.ZERO)) {
                startMovingToMarketplace(pendingOrigin, pendingSlot);
            }
        }

        marketplaceIncomeProgress = Math.max(0, pCompound.getInt("ronPlusMarketIncomeProgress"));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.25f)
                .add(Attributes.ATTACK_DAMAGE, 0.0f)
                .add(Attributes.ARMOR, 0.0f)
                .add(Attributes.MAX_HEALTH, 25.0f)
                .add(Attributes.FOLLOW_RANGE, Unit.getFollowRange());
    }

    @Override
    @Nullable
    public ResourceCost getCost() {
        return ResourceCosts.VILLAGER;
    }

    @Override
    public float getUnitAttackDamage() {
        return 0.0f;
    }

    @Override
    public float getMovementSpeed() {
        return 0.25f;
    }

    @Override
    public double getUnitPhysicalArmorPercentage() {
        return 0.0d;
    }

    @Override
    public boolean getWillRetaliate() {
        return false;
    }

    @Override
    public boolean getAggressiveWhenIdle() {
        return false;
    }

    @Override
    public boolean canAttackBuildings() {
        return false;
    }

    @Override
    public void setupEquipmentAndUpgradesServer() {
        this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WANDERING_TRADER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WANDERING_TRADER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return SoundEvents.WANDERING_TRADER_HURT;
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity pEntity) {
        return false;
    }
}
