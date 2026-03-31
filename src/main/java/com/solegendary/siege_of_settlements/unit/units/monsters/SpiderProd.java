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

public class SpiderProd extends ProductionItem {

    public final static String itemName = "Spider";
    public final static ResourceCost cost = ResourceCosts.SPIDER;

    public SpiderProd() {
        super(cost);
        this.onComplete = (Level level, ProductionPlacement placement) -> {
            if (!level.isClientSide()) {
                if (ResearchServerEvents.playerHasResearch(placement.ownerName, ProductionItems.RESEARCH_POISON_SPIDERS))
                    placement.produceUnit((ServerLevel) level, EntityRegistrar.POISON_SPIDER_UNIT.get(), placement.ownerName, true);
                else
                    placement.produceUnit((ServerLevel) level, EntityRegistrar.SPIDER_UNIT.get(), placement.ownerName, true);
            }
        };
    }

    private static ResourceLocation getIcon() {
        if (ResearchClient.hasResearch(ProductionItems.RESEARCH_POISON_SPIDERS))
            return ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/cave_spider.png");
        else
            return ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/spider.png");
    }

    private static String getCancelName() {
        if (ResearchClient.hasResearch(ProductionItems.POISON_SPIDER))
            return "Poison Spider";
        else
            return "Spider";
    }

    public String getItemName() {
        return SpiderProd.itemName;
    }

    public UnitSpawnButton getPlaceButton() {
        return new UnitSpawnButton(
                itemName,
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/spider.png"),
                List.of(
                        FormattedCharSequence.forward(I18n.get("units.monsters.siege_of_settlements.spider"), Style.EMPTY.withBold(true)),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("units.monsters.siege_of_settlements.spider.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("units.monsters.siege_of_settlements.spider.tooltip2"), Style.EMPTY),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("units.monsters.siege_of_settlements.spider.tooltip3"), Style.EMPTY)
                )
        );
    }

    public StartProductionButton getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {
        return new StartProductionButton(
            SpiderProd.itemName,
            getIcon(),
            hotkey,
            () -> ResearchClient.hasResearch(ProductionItems.RESEARCH_POISON_SPIDERS),
            () -> true,
            List.of(
                FormattedCharSequence.forward(I18n.get("units.monsters.siege_of_settlements.spider"), Style.EMPTY.withBold(true)),
                ResourceCosts.getFormattedCost(cost),
                ResourceCosts.getFormattedPopAndTime(cost),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("units.monsters.siege_of_settlements.spider.tooltip1"), Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("units.monsters.siege_of_settlements.spider.tooltip2"), Style.EMPTY),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("units.monsters.siege_of_settlements.spider.tooltip3"), Style.EMPTY)
            ),
            this
        );
    }

    public StopProductionButton getCancelButton(ProductionPlacement prodBuilding, boolean first) {
        return new StopProductionButton(
                ResearchClient.hasResearch(ProductionItems.RESEARCH_POISON_SPIDERS) ? "Poison Spider" : "Spider",
                getIcon(),
                prodBuilding,
                this,
                first
        );
    }
}
