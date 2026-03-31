package com.solegendary.siege_of_settlements.unit.units.monsters;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.BuildingServerboundPacket;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import com.solegendary.siege_of_settlements.building.production.ProductionItem;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.building.production.StopProductionButton;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.hud.Button;
import com.solegendary.siege_of_settlements.hud.buttons.UnitSpawnButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.registrars.EntityRegistrar;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.research.ResearchServerEvents;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.sandbox.SandboxAction;
import com.solegendary.siege_of_settlements.sandbox.SandboxClientEvents;
import com.solegendary.siege_of_settlements.building.production.StartProductionButton;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;

import java.util.List;

public class ZombieProd extends ProductionItem {

    public final static String itemName = "Zombie";
    public final static ResourceCost cost = ResourceCosts.ZOMBIE;

    public ZombieProd() {
        super(cost);
        this.onComplete = (Level level, ProductionPlacement building) -> {
            if (!level.isClientSide()) {
                if (ResearchServerEvents.playerHasResearch(building.ownerName, ProductionItems.RESEARCH_HUSKS))
                    building.produceUnit((ServerLevel) level, EntityRegistrar.HUSK_UNIT.get(), building.ownerName, true);
                else if (ResearchServerEvents.playerHasResearch(building.ownerName, ProductionItems.RESEARCH_DROWNED))
                    building.produceUnit((ServerLevel) level, EntityRegistrar.DROWNED_UNIT.get(), building.ownerName, true);
                else
                    building.produceUnit((ServerLevel) level, EntityRegistrar.ZOMBIE_UNIT.get(), building.ownerName, true);
            }
        };
    }

    public String getItemName() {
        return ZombieProd.itemName;
    }

    private static ResourceLocation getIcon() {
        if (ResearchClient.hasResearch(ProductionItems.RESEARCH_HUSKS))
            return ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/husk.png");
        else if (ResearchClient.hasResearch(ProductionItems.RESEARCH_DROWNED))
            return ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/drowned.png");
        else
            return ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/zombie.png");
    }

    private static String getCancelName() {
        if (ResearchClient.hasResearch(ProductionItems.RESEARCH_HUSKS))
            return "Husk";
        else if (ResearchClient.hasResearch(ProductionItems.RESEARCH_DROWNED))
            return "Drowned";
        else
            return "Zombie";
    }

    public UnitSpawnButton getPlaceButton() {
        return new UnitSpawnButton(
                ZombieProd.itemName,
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/zombie.png"),
                List.of(
                        FormattedCharSequence.forward(I18n.get("units.monsters.siege_of_settlements.zombie"), Style.EMPTY.withBold(true)),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("units.monsters.siege_of_settlements.zombie.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("units.monsters.siege_of_settlements.zombie.tooltip2"), Style.EMPTY)
                )
        );
    }

    public StartProductionButton getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {
        return new StartProductionButton(
                ZombieProd.itemName,
                getIcon(),
                hotkey,
                () -> ResearchClient.hasResearch(ProductionItems.RESEARCH_HUSKS) || ResearchClient.hasResearch(ProductionItems.RESEARCH_DROWNED),
                () -> true,
                List.of(
                    FormattedCharSequence.forward(I18n.get("units.monsters.siege_of_settlements.zombie"), Style.EMPTY.withBold(true)),
                    ResourceCosts.getFormattedCost(cost),
                    ResourceCosts.getFormattedPopAndTime(cost),
                    FormattedCharSequence.forward("", Style.EMPTY),
                    FormattedCharSequence.forward(I18n.get("units.monsters.siege_of_settlements.zombie.tooltip1"), Style.EMPTY),
                    FormattedCharSequence.forward("", Style.EMPTY),
                    FormattedCharSequence.forward(I18n.get("units.monsters.siege_of_settlements.zombie.tooltip2"), Style.EMPTY)
                ),
                this
        );
    }

    public StopProductionButton getCancelButton(ProductionPlacement prodBuilding, boolean first) {
        return new StopProductionButton(
            getCancelName(),
            getIcon(),
            prodBuilding,
            this,
            first
        );
    }
}
