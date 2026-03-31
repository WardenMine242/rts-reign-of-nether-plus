package com.solegendary.siege_of_settlements.building.buildings.monsters;

import com.solegendary.siege_of_settlements.ability.abilities.Sacrifice;
import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.*;
import com.solegendary.siege_of_settlements.building.buildings.placements.SculkCatalystPlacement;
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

import java.util.List;

import static com.solegendary.siege_of_settlements.building.BuildingUtils.getAbsoluteBlockData;

public class SculkCatalyst extends Building {

    public final static String buildingName = "Sculk Catalyst";
    public final static String structureName = "sculk_catalyst";
    public final static ResourceCost cost = ResourceCosts.SCULK_CATALYST;

    // vanilla logic determines the actual range, but this is what we're guessing it to be for the range limiter
    // mixin and the dirt path fix
    public final static int ESTIMATED_RANGE = 10;
    public final static int nightRangeMin = 25;
    public final static int nightRangeMax = 50;

    public SculkCatalyst() {
        super(structureName, cost, false);
        this.name = buildingName;
        this.portraitBlock = Blocks.SCULK_CATALYST;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/sculk_catalyst_side.png");

        this.buildTimeModifier = 2.5f;

        this.startingBlockTypes.add(Blocks.POLISHED_BLACKSTONE);

        this.abilities.add(new Sacrifice(), Keybindings.keyQ);
    }

    public Faction getFaction() {
        return Faction.MONSTERS;
    }

    @Override
    public BuildingPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName) {
        return new SculkCatalystPlacement(this, level, pos, rotation, ownerName, getAbsoluteBlockData(getRelativeBlockData(level), level, pos, rotation), false);
    }

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(name,
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/sculk_catalyst_side.png"),
            hotkey,
            () -> BuildingClientEvents.getBuildingToPlace() == Buildings.SCULK_CATALYST,
            () -> false,
            () -> BuildingClientEvents.hasFinishedBuilding(Buildings.MAUSOLEUM) || ResearchClient.hasCheat(
                "modifythephasevariance"),
            List.of(FormattedCharSequence.forward(
                    I18n.get("buildings.monsters.siege_of_settlements.sculk_catalyst"),
                    Style.EMPTY.withBold(true)
                ),
                ResourceCosts.getFormattedCost(cost),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(
                    I18n.get("buildings.monsters.siege_of_settlements.sculk_catalyst.tooltip1"),
                    Style.EMPTY
                ),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get(
                    "buildings.monsters.siege_of_settlements.sculk_catalyst.tooltip2",
                    nightRangeMin
                ), Style.EMPTY),
                FormattedCharSequence.forward(I18n.get(
                    "buildings.monsters.siege_of_settlements.sculk_catalyst.tooltip3",
                    nightRangeMax
                ), Style.EMPTY),
                FormattedCharSequence.forward(
                    I18n.get("buildings.monsters.siege_of_settlements.sculk_catalyst.tooltip4"),
                    Style.EMPTY
                )
            ),
            this
        );
    }
}
