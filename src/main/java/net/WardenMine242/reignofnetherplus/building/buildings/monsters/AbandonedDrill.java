package net.WardenMine242.reignofnetherplus.building.buildings.monsters;

import com.solegendary.reignofnether.api.ReignOfNetherRegistries;
import com.solegendary.reignofnether.building.Building;
import com.solegendary.reignofnether.building.BuildingClientEvents;
import com.solegendary.reignofnether.building.BuildingPlaceButton;
import com.solegendary.reignofnether.building.Buildings;
import com.solegendary.reignofnether.faction.Faction;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.research.ResearchClient;
import com.solegendary.reignofnether.resources.ResourceCost;
import com.solegendary.reignofnether.resources.ResourceCosts;
import com.solegendary.reignofnether.tutorial.TutorialClientEvents;
import com.solegendary.reignofnether.tutorial.TutorialStage;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class AbandonedDrill extends Building {

    public final static String buildingName = "AbandonedDrill";
    public final static String structureName = "abandoned_drill";
    public final static ResourceCost cost = ResourceCosts.DRILL;

    public AbandonedDrill() {
        super(structureName, cost, false);


        this.name = buildingName;
        this.portraitBlock = Blocks.RAW_IRON_BLOCK;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/raw_iron_block.png");

        this.buildTimeModifier = 1.2f;

        this.startingBlockTypes.add(Blocks.COBBLED_DEEPSLATE);
    }



    public Faction getFaction() {
        return Faction.MONSTERS;
    }

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = ReignOfNetherRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
                name,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/raw_iron_block.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == Buildings.ABANDONED_DRILL,
                () -> !TutorialClientEvents.isAtOrPastStage(TutorialStage.EXPLAIN_BUILDINGS),
                () -> BuildingClientEvents.hasFinishedBuilding(Buildings.STRONGHOLD) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
                List.of(
                        FormattedCharSequence.forward(I18n.get("buildings.monsters.reignofnether.abandoned_drill"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.monsters.reignofnether.abandoned_drill.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.monsters.reignofnether.abandoned_drill.tooltip2"), Style.EMPTY),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.monsters.reignofnether.abandoned_drill.tooltip3"), Style.EMPTY)
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




}