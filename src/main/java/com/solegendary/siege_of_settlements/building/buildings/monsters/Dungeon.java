package com.solegendary.siege_of_settlements.building.buildings.monsters;

import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlaceButton;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.Buildings;
import com.solegendary.siege_of_settlements.building.buildings.placements.DungeonPlacement;
import com.solegendary.siege_of_settlements.building.production.ProductionBuilding;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.research.ResearchClient;
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

public class Dungeon extends ProductionBuilding {

    public final static String buildingName = "Dungeon";
    public final static String structureName = "dungeon";
    public final static ResourceCost cost = ResourceCosts.DUNGEON;

    public Dungeon() {
        super(structureName, cost, false);
        this.name = buildingName;
        this.portraitBlock = Blocks.SPAWNER;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/spawner.png");

        this.startingBlockTypes.add(Blocks.DEEPSLATE_BRICK_STAIRS);

        this.explodeChance = 0.2f;
        this.productions.add(ProductionItems.CREEPER, Keybindings.keyQ);
    }

    public Faction getFaction() {return Faction.MONSTERS;}

    @Override
    public BuildingPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName) {
        return new DungeonPlacement(this, level, pos, rotation, ownerName, getAbsoluteBlockData(getRelativeBlockData(level), level, pos, rotation), false);
    }

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
            name,
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/spawner.png"),
            hotkey,
            () -> BuildingClientEvents.getBuildingToPlace() == Buildings.DUNGEON,
            () -> false,
            () -> BuildingClientEvents.hasFinishedBuilding(Buildings.GRAVEYARD) ||
                    ResearchClient.hasCheat("modifythephasevariance"),
            List.of(
                FormattedCharSequence.forward(I18n.get("buildings.monsters.siege_of_settlements.dungeon"), Style.EMPTY.withBold(true)),
                ResourceCosts.getFormattedCost(cost),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.monsters.siege_of_settlements.dungeon.tooltip1"), Style.EMPTY),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.monsters.siege_of_settlements.dungeon.tooltip2"), Style.EMPTY)
            ),
            this
        );
    }

    @Override
    public BlockPos getIndoorSpawnPoint(ServerLevel level, BlockPos centerPos) {
        return super.getIndoorSpawnPoint(level, centerPos).offset(-1,0,0);
    }
}
