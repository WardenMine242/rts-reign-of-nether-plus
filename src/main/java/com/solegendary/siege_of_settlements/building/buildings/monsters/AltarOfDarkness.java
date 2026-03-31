package com.solegendary.siege_of_settlements.building.buildings.monsters;

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
import com.solegendary.siege_of_settlements.faction.Faction;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class AltarOfDarkness extends ProductionBuilding {

    public final static String buildingName = "Altar of Darkness";
    public final static String structureName = "altar_of_darkness";
    public final static ResourceCost cost = ResourceCosts.ALTAR_OF_DARKNESS;

    public AltarOfDarkness() {
        super(structureName, cost, false);
        this.name = buildingName;
        this.portraitBlock = Blocks.DARK_PRISMARINE;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/dark_prismarine.png");

        this.startingBlockTypes.add(Blocks.DARK_PRISMARINE);
        this.startingBlockTypes.add(Blocks.TINTED_GLASS);

        this.productions.add(ProductionItems.NECROMANCER, Keybindings.keyQ);
        this.productions.add(ProductionItems.NECROMANCER_REVIVE, Keybindings.keyQ);
        this.productions.add(ProductionItems.WRETCHED_WRAITH, Keybindings.keyW);
        this.productions.add(ProductionItems.WRETCHED_WRAITH_REVIVE, Keybindings.keyW);
    }

    @Override
    public BuildingPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName) {
        ProductionPlacement pp = (ProductionPlacement) super.createBuildingPlacement(level, pos, rotation, ownerName);
        pp.allowProdWhileBuilding = true;
        return pp;
    }

    public Faction getFaction() {return Faction.MONSTERS;}

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
                name,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/dark_prismarine.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == Buildings.ALTAR_OF_DARKNESS,
                () -> !SandboxClientEvents.isSandboxPlayer() && GameruleClient.allowedHeroes <= 0,
                () -> BuildingClientEvents.hasFinishedBuilding(Buildings.MAUSOLEUM) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
                List.of(
                        fcs(I18n.get("buildings.monsters.siege_of_settlements.altar_of_darkness"), true),
                        ResourceCosts.getFormattedCost(cost),
                        fcs(""),
                        fcs(I18n.get("buildings.monsters.siege_of_settlements.altar_of_darkness.tooltip1")),
                        fcs(I18n.get("buildings.monsters.siege_of_settlements.altar_of_darkness.tooltip2"))
                ),
                this
        );
    }
}
