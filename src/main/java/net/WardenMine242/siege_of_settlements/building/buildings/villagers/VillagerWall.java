package net.WardenMine242.siege_of_settlements.building.buildings.villagers;

import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.*;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.faction.Faction;
import com.solegendary.siege_of_settlements.tutorial.TutorialClientEvents;
import com.solegendary.siege_of_settlements.tutorial.TutorialStage;
import net.WardenMine242.siege_of_settlements.building.buildings.placements.WallPlacement;
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

public class VillagerWall extends Building {

    public final static String buildingName = "VillagerWall";
    public final static String structureName = "villager_wall";
    public final static ResourceCost cost = ResourceCosts.WALL;

    public VillagerWall() {
        super(structureName, cost, false);


        this.name = buildingName;
        this.portraitBlock = Blocks.CHISELED_STONE_BRICKS;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/chiseled_stone_bricks.png");

        this.buildTimeModifier = 0.3f;

        this.fireThreshold = 0f;
        this.explodeChance = 0f;

        this.startingBlockTypes.add(Blocks.OAK_LOG);
    }


    public Faction getFaction() {
        return Faction.VILLAGERS;
    }

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
                name,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/chiseled_stone_bricks.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == Buildings.VILLAGER_WALL,
                () -> !TutorialClientEvents.isAtOrPastStage(TutorialStage.EXPLAIN_BUILDINGS),
                () -> BuildingClientEvents.hasFinishedBuilding(Buildings.TOWN_CENTRE) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
                List.of(
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.villager_wall"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.villager_wall.tooltip1"), Style.EMPTY)
                ),
                this
        );
    }

    @Override
    public BuildingPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName) {
        return new WallPlacement(
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
