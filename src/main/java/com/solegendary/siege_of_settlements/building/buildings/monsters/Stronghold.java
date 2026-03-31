package com.solegendary.siege_of_settlements.building.buildings.monsters;

import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlaceButton;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.Buildings;
import com.solegendary.siege_of_settlements.building.buildings.placements.StrongholdPlacement;
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
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.solegendary.siege_of_settlements.building.BuildingUtils.getAbsoluteBlockData;

public class Stronghold extends ProductionBuilding {

    public final static String buildingName = "Stronghold";
    public final static String structureName = "stronghold";
    public final static ResourceCost cost = ResourceCosts.STRONGHOLD;
    public final static int nightRange = 60;
    private final Set<BlockPos> nightBorderBps = new HashSet<>();
    public Stronghold() {
        super(structureName, cost, false);
        this.name = buildingName;
        this.portraitBlock = Blocks.REINFORCED_DEEPSLATE;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/reinforced_deepslate_side.png");

        this.buildTimeModifier = 0.5f;

        this.startingBlockTypes.add(Blocks.POLISHED_BLACKSTONE);
        this.startingBlockTypes.add(Blocks.DEEPSLATE_TILE_SLAB);
        this.startingBlockTypes.add(Blocks.POLISHED_BLACKSTONE_WALL);
        this.startingBlockTypes.add(Blocks.DEEPSLATE);

        this.productions.add(ProductionItems.WARDEN, Keybindings.keyQ);
    }

    public Faction getFaction() {
        return Faction.MONSTERS;
    }

    @Override
    public BuildingPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName) {
        return new StrongholdPlacement(this, level, pos, rotation, ownerName, getAbsoluteBlockData(getRelativeBlockData(level), level, pos, rotation), true, nightRange,false, true);
    }

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(name,
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/reinforced_deepslate_side.png"),
            hotkey,
            () -> BuildingClientEvents.getBuildingToPlace() == Buildings.STRONGHOLD,
            () -> false,
            () -> (
                BuildingClientEvents.hasFinishedBuilding(Buildings.LABORATORY)
                    && BuildingClientEvents.hasFinishedBuilding(Buildings.SPIDER_LAIR)
                    && BuildingClientEvents.hasFinishedBuilding(Buildings.DUNGEON)
            ) || ResearchClient.hasCheat("modifythephasevariance"),
            List.of(FormattedCharSequence.forward(I18n.get("buildings.monsters.siege_of_settlements.stronghold"),
                    Style.EMPTY.withBold(true)
                ),
                ResourceCosts.getFormattedCost(cost),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.monsters.siege_of_settlements.stronghold.tooltip1"),
                    Style.EMPTY
                ),
                FormattedCharSequence.forward(I18n.get("buildings.monsters.siege_of_settlements.stronghold.tooltip2",
                    StrongholdPlacement.MAX_OCCUPANTS
                ), Style.EMPTY),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.monsters.siege_of_settlements.stronghold.tooltip3",
                    nightRange
                ), Style.EMPTY),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.monsters.siege_of_settlements.stronghold.tooltip4"),
                    Style.EMPTY
                )
            ),
            this
        );
    }
}
