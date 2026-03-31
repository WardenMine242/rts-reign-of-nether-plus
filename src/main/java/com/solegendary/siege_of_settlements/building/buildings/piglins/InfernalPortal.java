package com.solegendary.siege_of_settlements.building.buildings.piglins;

import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlaceButton;
import com.solegendary.siege_of_settlements.building.Buildings;
import com.solegendary.siege_of_settlements.building.buildings.placements.PortalPlacement;
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

import static com.solegendary.siege_of_settlements.building.BuildingUtils.getAbsoluteBlockData;
import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class InfernalPortal extends ProductionBuilding {

    public final static String buildingName = "Infernal Portal";
    public final static String structureName = "infernal_portal";
    public final static ResourceCost cost = ResourceCosts.INFERNAL_PORTAL;

    public InfernalPortal() {
        super(structureName, cost, false);
        this.name = buildingName;
        this.portraitBlock = Blocks.CRYING_OBSIDIAN;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/crying_obsidian.png");

        this.startingBlockTypes.add(Blocks.NETHER_BRICKS);

        this.productions.add(ProductionItems.PIGLIN_MERCHANT, Keybindings.keyQ);
        this.productions.add(ProductionItems.PIGLIN_MERCHANT_REVIVE, Keybindings.keyQ);
        //this.productions.add(ProductionItems.WILDFIRE, Keybindings.keyW);
        //this.productions.add(ProductionItems.WILDFIRE_REVIVE, Keybindings.keyW);
    }

    public Faction getFaction() {return Faction.PIGLINS;}

    @Override
    public PortalPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName) {
        PortalPlacement pp = new PortalPlacement(this, level, pos, rotation, ownerName, getAbsoluteBlockData(getRelativeBlockData(level), level, pos, rotation), false);
        pp.allowProdWhileBuilding = true;
        return pp;
    }

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
                name,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/crying_obsidian.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == Buildings.INFERNAL_PORTAL,
                () -> !SandboxClientEvents.isSandboxPlayer() && GameruleClient.allowedHeroes <= 0,
                () -> BuildingClientEvents.hasFinishedBuilding(Buildings.CENTRAL_PORTAL) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
                List.of(
                        fcs(I18n.get("buildings.piglins.siege_of_settlements.infernal_portal"), true),
                        ResourceCosts.getFormattedCost(cost),
                        fcs(""),
                        fcs(I18n.get("buildings.piglins.siege_of_settlements.infernal_portal.tooltip1")),
                        fcs(I18n.get("buildings.piglins.siege_of_settlements.infernal_portal.tooltip2"))
                ),
                this
        );
    }
}