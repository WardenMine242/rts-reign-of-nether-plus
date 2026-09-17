package com.solegendary.siege_of_settlements.building;

import com.solegendary.siege_of_settlements.alliance.AlliancesClient;
import com.solegendary.siege_of_settlements.alliance.AlliancesServerEvents;
import com.solegendary.siege_of_settlements.building.custombuilding.CustomBuilding;
import com.solegendary.siege_of_settlements.registrars.BlockRegistrar;
import com.solegendary.siege_of_settlements.unit.UnitClientEvents;
import com.solegendary.siege_of_settlements.unit.UnitServerEvents;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public interface GarrisonableBuilding {

    // set range for all garrisoned units don't use this for abilities as it may not be balanced
    public int getAttackRange();
    // bonus for units attacking garrisoned units
    public int getExternalAttackRangeBonus();

    // returns the absolute building position units will go to when garrisoning
    @Nullable BlockPos getEntryPosition();
    // returns the absolute building position units will go to when ungarrisoning
    @Nullable BlockPos getExitPosition();

    int getCapacity();

    default boolean isFull() {
        return getNumOccupants((BuildingPlacement) this) >= getCapacity();
    }

    static BlockPos rotatePos(BlockPos pos, Rotation rot) {
        return pos.rotate(rot);
    }

    // will only return actual Units, not any other LivingEntity
    public default List<LivingEntity> getOccupants() {
        if (this instanceof BuildingPlacement building) {
            if (building.level.isClientSide()) {
                List<LivingEntity> list = new ArrayList<>();
                for (LivingEntity le : UnitClientEvents.getAllUnits()) {
                    if (le instanceof Unit u &&
                        GarrisonableBuilding.getGarrison(u) == this) {
                        list.add(le);
                    }
                }
                return list;
            }
            else {
                List<LivingEntity> list = new ArrayList<>();
                for (LivingEntity le : UnitServerEvents.getAllUnits()) {
                    if (le instanceof Unit u &&
                        GarrisonableBuilding.getGarrison(u) == this) {
                        list.add(le);
                    }
                }
                return list;
            }
        }
        return List.of();
    }

    static boolean isUnitAllowedInGarrison(Unit unit, BuildingPlacement building, boolean clientSide) {
        boolean isAllied = clientSide
                ? AlliancesClient.isAllied(unit.getOwnerName(), building.ownerName)
                : AlliancesServerEvents.isAllied(unit.getOwnerName(), building.ownerName);
        return (unit.getOwnerName().equals(building.ownerName) || isAllied ||
                (unit.getOwnerName().isEmpty() && building.ownerName.isEmpty()));
    }

    static boolean isOnGarrisonFloor(Entity entity, BuildingPlacement building) {
        if (building.getBuilding() instanceof CustomBuilding) {
            Block onBlock = entity.level().getBlockState(entity.getOnPos().above()).getBlock();
            return onBlock == BlockRegistrar.GARRISON_ZONE_BLOCK.get() ||
                    onBlock == BlockRegistrar.GARRISON_ENTRY_BLOCK.get();
        }
        return entity.getOnPos().getY() > building.originPos.getY() + 2;
    }

    static boolean isGarrisonableBuilding(Unit unit, BuildingPlacement building, GarrisonableBuilding garrisonableBuilding) {
        Entity entity = (Entity) unit;
        if (garrisonableBuilding.getCapacity() <= 0 || !building.isBuilt) {
            return false;
        }
        if (!isUnitAllowedInGarrison(unit, building, entity.level().isClientSide())) {
            return false;
        }
        if (!building.isPosInsideBuilding(entity.getOnPos())) {
            return false;
        }
        return isOnGarrisonFloor(entity, building);
    }

    @Nullable
    static GarrisonableBuilding getGarrison(Unit unit) {
        List<GarrisonableBuilding> buildings;
        Entity entity = (Entity) unit;
        if (entity.level().isClientSide())
            buildings = BuildingClientEvents.getGarrisonableBuildings();
        else
            buildings = BuildingServerEvents.getGarrisonableBuildings();

        for (GarrisonableBuilding garrisonableBuilding : buildings) {
            if (!(garrisonableBuilding instanceof BuildingPlacement building)) {
                continue;
            }
            if (isGarrisonableBuilding(unit, building, garrisonableBuilding)) {
                return garrisonableBuilding;
            }
        }

        // Fallback when the garrisonable-building list is stale or iteration order misses the unit.
        BuildingPlacement building = BuildingUtils.findBuilding(entity.level().isClientSide(), entity.getOnPos().above());
        if (building == null) {
            building = BuildingUtils.findBuilding(entity.level().isClientSide(), entity.getOnPos());
        }
        if (building instanceof GarrisonableBuilding garrisonableBuilding &&
                isGarrisonableBuilding(unit, building, garrisonableBuilding)) {
            return garrisonableBuilding;
        }
        return null;
    }

    static int getNumOccupants(BuildingPlacement building) {
        List<LivingEntity> entities;
        if (building.getLevel().isClientSide())
            entities = UnitClientEvents.getAllUnits();
        else
            entities = UnitServerEvents.getAllUnits();

        int numOccupants = 0;
        for (LivingEntity entity : entities) {
            if (building.isPosInsideBuilding(entity.getOnPos())) {
                if (building.getBuilding() instanceof CustomBuilding) {
                    Block onBlock = entity.level().getBlockState(entity.getOnPos().above()).getBlock();
                    if (onBlock == BlockRegistrar.GARRISON_ZONE_BLOCK.get() ||
                        onBlock == BlockRegistrar.GARRISON_ENTRY_BLOCK.get())
                        numOccupants += 1;;
                } else {
                    if (entity.getOnPos().getY() > building.originPos.getY() + 2)
                        numOccupants += 1;;
                }
            }
        }

        return numOccupants;
    }
}
