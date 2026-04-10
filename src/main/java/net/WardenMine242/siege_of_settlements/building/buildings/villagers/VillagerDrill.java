package net.WardenMine242.siege_of_settlements.building.buildings.villagers;

import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.*;
import com.solegendary.siege_of_settlements.building.production.ProductionBuilding;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.faction.Faction;
import com.solegendary.siege_of_settlements.tutorial.TutorialClientEvents;
import com.solegendary.siege_of_settlements.tutorial.TutorialStage;
import net.WardenMine242.siege_of_settlements.building.buildings.placements.VillageDrillPlacement;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static com.solegendary.siege_of_settlements.building.BuildingUtils.getAbsoluteBlockData;

public class VillagerDrill extends ProductionBuilding {

    public final static String buildingName = "VillagerDrill";
    public final static String structureName = "villager_drill";
    public final static String structureNameT1 = "drill_t2";
    public final static String structureNameT2 = "drill_t3";
    public final static ResourceCost cost = ResourceCosts.DRILL;

    public final static int MAX_UPGRADE_LEVEL = 2;

    public VillagerDrill() {
        super(structureName, cost, false);




        this.name = buildingName;
        this.portraitBlock = Blocks.RAW_IRON_BLOCK;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/raw_iron_block.png");

        this.buildTimeModifier = 1.0f;

        this.startingBlockTypes.add(Blocks.COBBLESTONE);

        this.productions.add(ProductionItems.DRILL_T2, Keybindings.keyQ);
        this.productions.add(ProductionItems.DRILL_T3, Keybindings.keyQ);
    }



    public Faction getFaction() {
        return Faction.VILLAGERS;
    }

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
                name,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/raw_iron_block.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == Buildings.VILLAGER_DRILL,
                () -> !TutorialClientEvents.isAtOrPastStage(TutorialStage.EXPLAIN_BUILDINGS),
                () -> BuildingClientEvents.hasFinishedBuilding(Buildings.TOWN_CENTRE) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
                List.of(
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.villager_drill"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.villager_drill.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.villager_drill.tooltip2"), Style.EMPTY)
                ),
                this
        );
    }

    public static boolean isBuildingPlacementValid(Level level, BlockPos originPos) {

        if (level == null || originPos == null)
            return false;

        int size = 9;
        int half = size / 2;

        int baseY = originPos.getY();

        for (int x = -half; x <= half; x++) {
            for (int z = -half; z <= half; z++) {

                BlockPos checkPos = originPos.offset(x, 0, z);

                BlockState groundState = level.getBlockState(checkPos);


                if (groundState.isAir())
                    return false;


                if (checkPos.getY() != baseY)
                    return false;


                if (!level.getBlockState(checkPos.above()).isAir())
                    return false;
            }
        }


        BlockState center = level.getBlockState(originPos);
        return center.is(Blocks.RAW_IRON_BLOCK);
    }

    @Override
    public BuildingPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName) {
        return new VillageDrillPlacement(
                this,
                level,
                pos,
                rotation,
                ownerName,
                getAbsoluteBlockData(getRelativeBlockData(level), level, pos, rotation),
                false
        );
    }
}
