package com.solegendary.siege_of_settlements.building.buildings.piglins;

import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlaceButton;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.Buildings;
import com.solegendary.siege_of_settlements.building.buildings.placements.CentralPortalPlacement;
import com.solegendary.siege_of_settlements.building.production.ProductionBuilding;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.faction.Faction;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;

import java.util.List;

import static com.solegendary.siege_of_settlements.building.BuildingUtils.getAbsoluteBlockData;

public class CentralPortal extends ProductionBuilding {

    public final static String buildingName = "Central Portal";
    public final static String structureName = "central_portal";
    public final static ResourceCost cost = ResourceCosts.CENTRAL_PORTAL;

    public CentralPortal() {
        super(structureName, cost, true);
        this.name = buildingName;
        this.portraitBlock = Blocks.OBSIDIAN;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/obsidian.png");

        this.buildTimeModifier = 0.32f; // 60s total build time with 3 villagers
        this.canAcceptResources = true;

        this.startingBlockTypes.add(Blocks.NETHER_BRICKS);

        this.productions.add(ProductionItems.GRUNT, Keybindings.keyQ);
    }

    public Faction getFaction() {return Faction.PIGLINS;}

    @Override
    public BuildingPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName) {
        return new CentralPortalPlacement(this, level, pos, rotation, ownerName, getAbsoluteBlockData(getRelativeBlockData(level), level, pos, rotation), true);
    }

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
                name,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/obsidian.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == Buildings.CENTRAL_PORTAL,
                () -> false,
                () -> true,
                List.of(
                        FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.central_portal"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        ResourceCosts.getFormattedPop(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.central_portal.tooltip1"), Style.EMPTY)
                ),
                this
        );
    }

    @Override
    public BlockPos getIndoorSpawnPoint(ServerLevel level, BlockPos originPos) {
        return super.getIndoorSpawnPoint(level, originPos).offset(0,-5,0);
    }
}