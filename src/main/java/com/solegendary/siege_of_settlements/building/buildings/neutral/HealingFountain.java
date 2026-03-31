package com.solegendary.siege_of_settlements.building.buildings.neutral;

import com.solegendary.siege_of_settlements.building.Building;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlaceButton;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.buildings.placements.HealingFountainPlacement;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
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

public class HealingFountain extends Building {

    public final static String buildingName = "Healing Fountain";
    public final static String structureName = "healing_fountain";
    public final static ResourceCost cost = ResourceCost.Building(0,0,0,0);
    public HealingFountain() {
        super(structureName, cost, false);
        this.name = buildingName;
        this.portraitBlock = Blocks.ROSE_BUSH;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/rose_bush_bottom.png");

        this.buildTimeModifier = 0.8f;

        this.startingBlockTypes.add(Blocks.POLISHED_ANDESITE);
        this.startingBlockTypes.add(Blocks.STONE_BRICK_STAIRS);
        this.startingBlockTypes.add(Blocks.GRASS_BLOCK);
        this.startingBlockTypes.add(Blocks.SPRUCE_TRAPDOOR);

        this.selfBuilding = true;
        this.capturable = false;
        this.invulnerable = true;
        this.shouldDestroyOnReset = false;
    }

    public Faction getFaction() {return Faction.NONE;}

    @Override
    public BuildingPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName) {
        return new HealingFountainPlacement(this, level, pos, rotation, ownerName, getAbsoluteBlockData(getRelativeBlockData(level), level, pos, rotation), false);
    }

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        return new BuildingPlaceButton(
            HealingFountain.buildingName,
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/rose_bush_bottom.png"),
            hotkey,
            () -> BuildingClientEvents.getBuildingToPlace() == this,
            () -> false,
            () -> true,
            List.of(
                FormattedCharSequence.forward(I18n.get("buildings.neutral.siege_of_settlements.healing_fountain"), Style.EMPTY.withBold(true)),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.neutral.siege_of_settlements.healing_fountain.tooltip1"), Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.neutral.siege_of_settlements.healing_fountain.tooltip2"), Style.EMPTY)
            ),
            this
        );
    }
}
