package com.solegendary.siege_of_settlements.building.buildings.villagers;

import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.ability.abilities.PromoteIllager;
import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.*;
import com.solegendary.siege_of_settlements.building.buildings.placements.CastlePlacement;
import com.solegendary.siege_of_settlements.building.production.ProductionBuilding;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.tutorial.TutorialClientEvents;
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

public class Castle extends ProductionBuilding {

    public final static String buildingName = "Castle";
    public final static String structureName = "castle";
    public final static String upgradedStructureName = "castle_with_flag";
    public final static ResourceCost cost = ResourceCosts.CASTLE;
    public Castle() {
        super(structureName, cost, false);
        this.name = buildingName;
        this.portraitBlock = Blocks.COBBLESTONE;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/cobblestone.png");

        this.buildTimeModifier = 0.5f;

        this.startingBlockTypes.add(Blocks.STONE_BRICKS);
        this.startingBlockTypes.add(Blocks.STONE_BRICK_WALL);
        this.startingBlockTypes.add(Blocks.SPRUCE_SLAB);
        this.startingBlockTypes.add(Blocks.SPRUCE_PLANKS);
        this.startingBlockTypes.add(Blocks.DARK_OAK_PLANKS);

        Ability promoteIllager = new PromoteIllager();
        this.abilities.add(promoteIllager, Keybindings.keyE);

        this.productions.add(ProductionItems.RAVAGER, Keybindings.keyQ);
        this.productions.add(ProductionItems.RESEARCH_RAVAGER_CAVALRY, Keybindings.keyW);
        this.productions.add(ProductionItems.RESEARCH_CASTLE_FLAG, Keybindings.keyE);
    }

    public Faction getFaction() {
        return Faction.VILLAGERS;
    }

    @Override
    public BuildingPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName) {
        return new CastlePlacement(this, level, pos, rotation, ownerName, getAbsoluteBlockData(getRelativeBlockData(level), level, pos, rotation), false);
    }

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(name,
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/cobblestone.png"),
            hotkey,
            () -> BuildingClientEvents.getBuildingToPlace() == Buildings.CASTLE,
            TutorialClientEvents::isEnabled,
            () -> (ResearchClient.hasResearch(ProductionItems.RESEARCH_CASTLE)
                    || ResearchClient.hasCheat("modifythephasevariance")
            ),
            List.of(FormattedCharSequence.forward(
                    I18n.get("buildings.villagers.siege_of_settlements.castle"),
                    Style.EMPTY.withBold(true)
                ),
                ResourceCosts.getFormattedCost(cost),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(
                    I18n.get("buildings.villagers.siege_of_settlements.castle.tooltip1"),
                    Style.EMPTY
                ),
                FormattedCharSequence.forward(I18n.get(
                    "buildings.villagers.siege_of_settlements.castle.tooltip2",
                        CastlePlacement.MAX_OCCUPANTS
                ), Style.EMPTY),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(
                    I18n.get("research.siege_of_settlements.requires.research"),
                    Style.EMPTY
                )
            ),
            this
        );
    }

    // check that the flag is built based on existing placed blocks
    @Override
    public int getUpgradeLevel(BuildingPlacement placement) {
        for (BuildingBlock block : placement.getBlocks())
            if (block.getBlockState().getBlock() == Blocks.WHITE_WOOL
                || block.getBlockState().getBlock() == Blocks.RED_WOOL
                || block.getBlockState().getBlock() == Blocks.LIGHT_GRAY_WOOL) {
                return 1;
            }
        return 0;
    }

    @Override
    public String getUpgradedStructureName(int upgradeLevel) {
        return upgradeLevel > 0 ? upgradedStructureName : structureName;
    }
}
