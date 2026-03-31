package com.solegendary.siege_of_settlements.unit.units.villagers;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import com.solegendary.siege_of_settlements.building.production.HeroProductionItem;
import com.solegendary.siege_of_settlements.building.production.StartProductionButton;
import com.solegendary.siege_of_settlements.hud.buttons.UnitSpawnButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.registrars.EntityRegistrar;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.unit.interfaces.HeroUnit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class EnchanterProd extends HeroProductionItem {

    public final static String itemName = "Enchanter";
    public final static ResourceCost cost = ResourceCosts.ENCHANTER;

    public EnchanterProd() {
        super(cost, itemName, ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/enchanter.png"));
        this.onComplete = (Level level, ProductionPlacement placement) -> {
            if (!level.isClientSide())
                placement.produceUnit((ServerLevel) level, EntityRegistrar.ENCHANTER_UNIT.get(), placement.ownerName, true);
        };
    }

    @Override
    protected EntityType<? extends HeroUnit> getHeroEntityType() {
        return EntityRegistrar.ENCHANTER_UNIT.get();
    }

    public UnitSpawnButton getPlaceButton() {
        return new UnitSpawnButton(
                itemName,
                iconRl,
                List.of(
                        FormattedCharSequence.forward(
                                I18n.get("units.villagers.siege_of_settlements.enchanter") +
                                " (" + I18n.get("hud.units.siege_of_settlements.hero") + ")",
                                Style.EMPTY.withBold(true)),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("units.villagers.siege_of_settlements.enchanter.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("units.villagers.siege_of_settlements.enchanter.tooltip2"), Style.EMPTY)
                )
        );
    }

    public StartProductionButton getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {
        ArrayList<FormattedCharSequence> tooltips = new ArrayList<>(List.of(
                FormattedCharSequence.forward(
                        I18n.get("units.villagers.siege_of_settlements.enchanter") +
                                " (" + I18n.get("hud.units.siege_of_settlements.hero") + ")",
                        Style.EMPTY.withBold(true)),
                ResourceCosts.getFormattedCost(cost),
                ResourceCosts.getFormattedPopAndTime(cost),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("units.villagers.siege_of_settlements.enchanter.tooltip1"), Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("units.villagers.siege_of_settlements.enchanter.tooltip2"), Style.EMPTY)
        ));
        tooltips.addAll(getAdditionalHeroTooltips());

        return super.getStartButton(
                prodBuilding,
                hotkey,
                tooltips
        );
    }
}
