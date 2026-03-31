package com.solegendary.siege_of_settlements.unit.units.piglins;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingServerboundPacket;
import com.solegendary.siege_of_settlements.building.Buildings;
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

public class WitherSkeletonProd extends ProductionItem {

    public final static String itemName = "Wither Skeleton";
    public final static ResourceCost cost = ResourceCosts.WITHER_SKELETON;

    public WitherSkeletonProd() {
        super(cost);
        this.onComplete = (Level level, ProductionPlacement placement) -> {
            if (!level.isClientSide())
                placement.produceUnit((ServerLevel) level, EntityRegistrar.WITHER_SKELETON_UNIT.get(), placement.ownerName, true);
        };
    }

    public String getItemName() {
        return WitherSkeletonProd.itemName;
    }

    public UnitSpawnButton getPlaceButton() {
        return new UnitSpawnButton(
                itemName,
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/wither_skeleton.png"),
                List.of(
                        FormattedCharSequence.forward(I18n.get("units.piglins.siege_of_settlements.wither_skeleton"), Style.EMPTY.withBold(true)),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("units.piglins.siege_of_settlements.wither_skeleton.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("units.piglins.siege_of_settlements.wither_skeleton.tooltip2"), Style.EMPTY)
                )
        );
    }

    public StartProductionButton getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {
        List<FormattedCharSequence> tooltipLines = new ArrayList<>(List.of(
                FormattedCharSequence.forward(I18n.get("units.piglins.siege_of_settlements.wither_skeleton"), Style.EMPTY.withBold(true)),
                ResourceCosts.getFormattedCost(cost),
                ResourceCosts.getFormattedPopAndTime(cost),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("units.piglins.siege_of_settlements.wither_skeleton.tooltip1"), Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("units.piglins.siege_of_settlements.wither_skeleton.tooltip2"), Style.EMPTY),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("units.piglins.siege_of_settlements.wither_skeleton.tooltip3"), Style.EMPTY)
        ));

        return new StartProductionButton(
                WitherSkeletonProd.itemName,
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/wither_skeleton.png"),
                hotkey,
                () -> false,
                () -> BuildingClientEvents.hasFinishedBuilding(Buildings.WITHER_SHRINE),
                tooltipLines,
                this
        );
    }

    public StopProductionButton getCancelButton(ProductionPlacement prodBuilding, boolean first) {
        return new StopProductionButton(
                WitherSkeletonProd.itemName,
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/wither_skeleton.png"),
                prodBuilding,
                this,
                first
        );
    }
}
