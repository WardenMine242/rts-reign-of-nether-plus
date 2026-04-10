package net.WardenMine242.siege_of_settlements.building.buildings.villagers;

import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlaceButton;
import com.solegendary.siege_of_settlements.building.Buildings;
import com.solegendary.siege_of_settlements.building.production.ProductionBuilding;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.faction.Faction;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.tutorial.TutorialClientEvents;
import com.solegendary.siege_of_settlements.tutorial.TutorialStage;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class Stable extends ProductionBuilding {
    public final static String buildingName = "Stable";
    public final static String structureName = "stable";
    public final static ResourceCost cost = ResourceCosts.STABLE;
    private static final String NAME_KEY = "buildings.villagers.siege_of_settlements.stable";
    private static final String TOOLTIP1_KEY = "buildings.villagers.siege_of_settlements.stable.tooltip1";

    public Stable() {
        super(structureName, cost, false);


        this.name = buildingName;
        this.portraitBlock = Blocks.BOOKSHELF;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/oak_planks.png");

        this.buildTimeModifier = 0.5f;

        this.startingBlockTypes.add(Blocks.BEEHIVE);
        this.startingBlockTypes.add(Blocks.STRIPPED_OAK_WOOD);
        this.startingBlockTypes.add(Blocks.OAK_PLANKS);

        this.productions.add(ProductionItems.HORSE, Keybindings.keyQ);
    }


    public Faction getFaction() {
        return Faction.VILLAGERS;
    }

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
                name,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/oak_planks.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == Buildings.STABLE,
                () -> !TutorialClientEvents.isAtOrPastStage(TutorialStage.EXPLAIN_BUILDINGS),
                () -> BuildingClientEvents.hasFinishedBuilding(Buildings.BLACKSMITH) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
                List.of(
                        FormattedCharSequence.forward(I18n.get(NAME_KEY), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get(TOOLTIP1_KEY), Style.EMPTY)
                ),
                this
        );
    }
}
