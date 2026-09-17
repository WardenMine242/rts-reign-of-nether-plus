package com.solegendary.siege_of_settlements.unit.units.villagers;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.BuildingServerboundPacket;
import com.solegendary.siege_of_settlements.building.buildings.placements.BarracksPlacement;
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

import java.util.ArrayList;
import java.util.List;

public class PillagerProd extends ProductionItem {

    public final static String itemName = "Pillager";
    public final static ResourceCost cost = ResourceCosts.PILLAGER;

    public PillagerProd() {
        super(cost);
        this.onComplete = (Level level, ProductionPlacement placement) -> {
            if (!level.isClientSide())
                placement.produceUnit((ServerLevel) level, EntityRegistrar.PILLAGER_UNIT.get(), placement.ownerName, true);
        };
    }

    public String getItemName() {
        return PillagerProd.itemName;
    }

    public UnitSpawnButton getPlaceButton() {
        return new UnitSpawnButton(
                itemName,
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/pillager.png"),
                List.of(
                        FormattedCharSequence.forward(I18n.get("units.villagers.siege_of_settlements.pillager"), Style.EMPTY.withBold(true)),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("units.villagers.siege_of_settlements.pillager.tooltip1"), Style.EMPTY)
                )
        );
    }

    public StartProductionButton getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {
        List<FormattedCharSequence> tooltipLines = new ArrayList<>(List.of(
            FormattedCharSequence.forward(I18n.get("units.villagers.siege_of_settlements.pillager"), Style.EMPTY.withBold(true)),
            ResourceCosts.getFormattedCost(cost),
            ResourceCosts.getFormattedPopAndTime(cost),
            FormattedCharSequence.forward("", Style.EMPTY),
            FormattedCharSequence.forward(I18n.get("units.villagers.siege_of_settlements.pillager.tooltip1"), Style.EMPTY)
        ));
        return new StartProductionButton(
            PillagerProd.itemName,
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/pillager.png"),
            hotkey,
            () -> prodBuilding instanceof BarracksPlacement && prodBuilding.getUpgradeLevel() < 1,
            () -> !(prodBuilding instanceof BarracksPlacement) || prodBuilding.getUpgradeLevel() >= 1,
            tooltipLines,
            this
        );
    }

    public StopProductionButton getCancelButton(ProductionPlacement prodBuilding, boolean first) {
        return new StopProductionButton(
            PillagerProd.itemName,
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/pillager.png"),
            prodBuilding,
            this,
            first
        );
    }
}
