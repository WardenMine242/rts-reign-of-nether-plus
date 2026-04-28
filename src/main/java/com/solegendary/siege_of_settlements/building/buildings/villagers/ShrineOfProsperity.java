package com.solegendary.siege_of_settlements.building.buildings.villagers;

import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlaceButton;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.Buildings;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import com.solegendary.siege_of_settlements.building.production.ProductionBuilding;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.gamerules.GameruleClient;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.sandbox.SandboxClientEvents;
import com.solegendary.siege_of_settlements.tutorial.TutorialClientEvents;
import com.solegendary.siege_of_settlements.faction.Faction;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class ShrineOfProsperity extends ProductionBuilding {

    public final static String buildingName = "Shrine of Prosperity";
    public final static String structureName = "shrine_of_prosperity";
    public final static ResourceCost cost = ResourceCosts.SHRINE_OF_PROSPERITY;

    public ShrineOfProsperity() {
        super(structureName, cost, false);
        this.name = buildingName;
        this.portraitBlock = Blocks.ACACIA_LOG;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/acacia_log_top.png");

        this.startingBlockTypes.add(Blocks.COBBLESTONE);

        this.productions.add(ProductionItems.ROYAL_GUARD, Keybindings.keyQ);
        this.productions.add(ProductionItems.ENCHANTER, Keybindings.keyW);
        this.productions.add(ProductionItems.ENCHANTER_REVIVE, Keybindings.keyW);
    }

    @Override
    public BuildingPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName) {
        ProductionPlacement pp = (ProductionPlacement) super.createBuildingPlacement(level, pos, rotation, ownerName);
        pp.allowProdWhileBuilding = true;
        return pp;
    }

    public Faction getFaction() {return Faction.VILLAGERS;}

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
                name,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/acacia_log_top.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == Buildings.SHRINE_OF_PROSPERITY,
                () -> (!SandboxClientEvents.isSandboxPlayer() && GameruleClient.allowedHeroes <= 0) || TutorialClientEvents.isEnabled(),
                () -> BuildingClientEvents.hasFinishedBuilding(Buildings.TOWN_CENTRE) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
                List.of(
                        fcs(I18n.get("buildings.villagers.siege_of_settlements.shrine_of_prosperity"), true),
                        ResourceCosts.getFormattedCost(cost),
                        fcs(""),
                        fcs(I18n.get("buildings.villagers.siege_of_settlements.shrine_of_prosperity.tooltip1")),
                        fcs(I18n.get("buildings.villagers.siege_of_settlements.shrine_of_prosperity.tooltip2"))
                ),
                this
        );
    }
}
