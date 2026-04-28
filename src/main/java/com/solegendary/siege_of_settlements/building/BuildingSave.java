package com.solegendary.siege_of_settlements.building;

import com.solegendary.siege_of_settlements.building.buildings.placements.PortalPlacement;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;

// contains all necessary data for a restarted server to recreate all buildings in the level
public class BuildingSave {

    public BlockPos originPos;
    public Level level;
    public Building building;
    public String ownerName;
    public Rotation rotation;
    public BlockPos rallyPoint;
    public boolean isBuilt;
    public boolean isDiagonalBridge;
    public int upgradeLevel; // castle flag, lab rod, etc.
    /** For {@link com.WardenMine242.siege_of_settlements.building.buildings.villagers.VillagerDrill}: 1..6; 0 = unset (legacy saves). */
    public int drillTier;
    PortalPlacement.PortalType portalType;
    public BlockPos portalDestination;

    public BuildingSave(BlockPos originPos, Level level, Building building, String ownerName, Rotation rotation, BlockPos rallyPoint,
                        boolean isDiagonalBridge, boolean isBuilt, int upgradeLevel, int drillTier, PortalPlacement.PortalType portalType, BlockPos portalDestination) {
        this.originPos = originPos;
        this.level = level;
        this.building = building;
        this.ownerName = ownerName;
        this.rotation = rotation;
        this.rallyPoint = rallyPoint;
        this.isDiagonalBridge = isDiagonalBridge;
        this.isBuilt = isBuilt;
        this.upgradeLevel = upgradeLevel;
        this.drillTier = drillTier;
        this.portalType = portalType;
        this.portalDestination = portalDestination;
    }
}
