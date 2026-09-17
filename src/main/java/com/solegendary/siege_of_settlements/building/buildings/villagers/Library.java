package com.solegendary.siege_of_settlements.building.buildings.villagers;

import com.solegendary.siege_of_settlements.ability.abilities.*;
import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.*;
import com.solegendary.siege_of_settlements.building.buildings.placements.LibraryPlacement;
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

public class Library extends ProductionBuilding {

    public final static String buildingName = "Library";
    public final static String structureName = "library";
    public final static String upgradedStructureName = "library_grand";
    public final static ResourceCost cost = ResourceCosts.LIBRARY;

    public Library() {
        super(structureName, cost, false);
        this.name = buildingName;
        this.portraitBlock = Blocks.ENCHANTING_TABLE;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/enchanting_table_top.png");

        this.buildTimeModifier = 1.1f;

        this.canSetRallyPoint = false;

        this.startingBlockTypes.add(Blocks.OAK_LOG);
        this.startingBlockTypes.add(Blocks.SPRUCE_STAIRS);

        this.explodeChance = 0.2f;

        this.abilities.add(new EnchantMaiming(), Keybindings.keyQ);
        this.abilities.add(new EnchantQuickCharge(), Keybindings.keyW);
        this.abilities.add(new EnchantSharpness(), Keybindings.keyE);
        this.abilities.add(new EnchantMultishot(), Keybindings.keyR);
        this.abilities.add(new EnchantVigor(), Keybindings.keyT);

        this.productions.add(ProductionItems.RESEARCH_GRAND_LIBRARY, Keybindings.keyY);
    }

    public Faction getFaction() {
        return Faction.VILLAGERS;
    }

    @Override
    public BuildingPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName) {
        return new LibraryPlacement(this, level, pos, rotation, ownerName, getAbsoluteBlockData(getRelativeBlockData(level), level, pos, rotation), false);
    }

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(name,
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/enchanting_table_top.png"),
            hotkey,
            () -> BuildingClientEvents.getBuildingToPlace() == Buildings.LIBRARY,
            TutorialClientEvents::isEnabled,
            () -> BuildingClientEvents.hasFinishedBuilding(Buildings.BARRACKS) ||
                    ResearchClient.hasCheat("modifythephasevariance"),
            List.of(FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.library"),
                    Style.EMPTY.withBold(true)
                ),
                ResourceCosts.getFormattedCost(cost),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.library.tooltip1"),
                    Style.EMPTY
                ),
                FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.library.tooltip2"),
                    Style.EMPTY
                ),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.library.tooltip3"),
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
            if (block.getBlockState().getBlock() == Blocks.GLOWSTONE) {
                return 1;
            }
        return 0;
    }

    @Override
    public String getUpgradedStructureName(int upgradeLevel) {
        return upgradeLevel > 0 ? upgradedStructureName : structureName;
    }
}
