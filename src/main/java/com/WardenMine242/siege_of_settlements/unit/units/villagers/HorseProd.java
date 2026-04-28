package com.WardenMine242.siege_of_settlements.unit.units.villagers;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.Buildings;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import com.solegendary.siege_of_settlements.building.production.ProductionItem;
import com.solegendary.siege_of_settlements.building.production.StartProductionButton;
import com.solegendary.siege_of_settlements.building.production.StopProductionButton;
import com.solegendary.siege_of_settlements.hud.buttons.UnitSpawnButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.registrars.EntityRegistrar;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.List;

public class HorseProd extends ProductionItem {

    public static final String itemName = "Horse";
    public static final ResourceCost cost = ResourceCosts.HORSE;
    private static final ResourceLocation HORSE_ICON =
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/horse.png");
    private static final String NAME_KEY = "units.villagers.siege_of_settlements.horse";
    private static final String TOOLTIP1_KEY = "units.villagers.siege_of_settlements.horse.tooltip1";
    private static final String TOOLTIP2_KEY = "units.villagers.siege_of_settlements.horse.tooltip2";

    public HorseProd() {
        super(cost);
        this.onComplete = (Level level, ProductionPlacement placement) -> {
            if (!level.isClientSide()) {
                Entity horse = placement.produceUnit((ServerLevel) level, EntityRegistrar.HORSE_UNIT.get(), placement.ownerName, true);
                if (horse != null && placement.getBuilding() == Buildings.STABLE) {
                    BlockPos target = placement.originPos.offset(new BlockPos(3, 1, 6).rotate(placement.rotation));
                    horse.moveTo(target.getX() + 0.5D, target.getY(), target.getZ() + 0.5D, horse.getYRot(), horse.getXRot());
                }
            }
        };
    }

    @Override
    public String getItemName() {
        return itemName;
    }

    public UnitSpawnButton getPlaceButton() {
        return new UnitSpawnButton(
                itemName,
                HORSE_ICON,
                List.of(
                        FormattedCharSequence.forward(I18n.get(NAME_KEY), Style.EMPTY.withBold(true)),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get(TOOLTIP1_KEY), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get(TOOLTIP2_KEY), Style.EMPTY)
                )
        );
    }

    @Override
    public StartProductionButton getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {
        return new StartProductionButton(
                itemName,
                HORSE_ICON,
                hotkey,
                () -> false,
                () -> true,
                List.of(
                        FormattedCharSequence.forward(I18n.get(NAME_KEY), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        ResourceCosts.getFormattedPopAndTime(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get(TOOLTIP1_KEY), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get(TOOLTIP2_KEY), Style.EMPTY)
                ),
                this
        );
    }

    @Override
    public StopProductionButton getCancelButton(ProductionPlacement prodBuilding, boolean first) {
        return new StopProductionButton(
                itemName,
                HORSE_ICON,
                prodBuilding,
                this,
                first
        );
    }
}
