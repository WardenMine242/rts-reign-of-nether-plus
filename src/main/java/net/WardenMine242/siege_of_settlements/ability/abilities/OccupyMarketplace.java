package net.WardenMine242.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.BuildingUtils;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.UnitServerEvents;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import net.WardenMine242.siege_of_settlements.unit.units.villagers.TraderUnit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.WardenMine242.siege_of_settlements.building.buildings.placements.MarketplacePlacement;

import javax.annotation.Nullable;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class OccupyMarketplace extends Ability {

    public OccupyMarketplace() {
        super(
                UnitAction.OCCUPY_MARKETPLACE,
                0,
                0,
                0,
                false
        );
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        if (!(unit instanceof TraderUnit traderUnit)) {
            return null;
        }

        return new AbilityButton(
                "Occupy Marketplace",
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/emerald.png"),
                hotkey,
                () -> CursorClientEvents.getLeftClickAction() == UnitAction.OCCUPY_MARKETPLACE,
                () -> traderUnit.isOccupyingMarketplace() || traderUnit.isMovingToMarketplace(),
                () -> !traderUnit.isOccupyingMarketplace(),
                () -> CursorClientEvents.setLeftClickAction(UnitAction.OCCUPY_MARKETPLACE),
                null,
                List.of(
                        fcs(I18n.get("abilities.siege_of_settlements.occupy_marketplace"), false)
                ),
                this,
                unit
        );
    }

    @Override
    public void use(Level level, Unit unitUsing, BlockPos targetBp) {
        if (level.isClientSide() || !(unitUsing instanceof TraderUnit traderUnit)) {
            return;
        }

        if (traderUnit.isOccupyingMarketplace()) {
            return;
        }

        MarketplacePlacement marketplace = findTargetMarketplace(level, traderUnit, targetBp);
        if (marketplace == null) {
            return;
        }

        int freeSlot = findFirstFreeSlot(marketplace);
        if (freeSlot < 0) {
            return;
        }

        traderUnit.startMovingToMarketplace(marketplace.originPos, freeSlot);
    }

    @Nullable
    public static MarketplacePlacement findMarketplaceForTrader(Level level, TraderUnit traderUnit) {
        BlockPos marketOrigin = traderUnit.getMarketplaceOrigin();
        if (marketOrigin == null) {
            return null;
        }
        return findMarketplaceByOrigin(level, marketOrigin, traderUnit.getOwnerName());
    }

    @Nullable
    public static MarketplacePlacement findMarketplaceByOrigin(Level level, BlockPos marketOrigin, String ownerName) {
        BuildingPlacement building = BuildingUtils.findBuilding(level.isClientSide(), marketOrigin);
        if (!(building instanceof MarketplacePlacement marketplace)) {
            return null;
        }
        if (!marketplace.ownerName.equals(ownerName) || !marketplace.isBuilt) {
            return null;
        }
        return marketplace;
    }

    private static MarketplacePlacement findTargetMarketplace(Level level, TraderUnit traderUnit, BlockPos targetBp) {
        BuildingPlacement targetedBuilding = BuildingUtils.findBuilding(false, targetBp);
        if (!(targetedBuilding instanceof MarketplacePlacement marketplace)) {
            return null;
        }
        if (!marketplace.ownerName.equals(traderUnit.getOwnerName()) || !marketplace.isBuilt) {
            return null;
        }
        return marketplace;
    }

    public static int findFirstFreeSlot(MarketplacePlacement marketplace) {
        return findFirstFreeSlot(marketplace, null);
    }

    public static int findFirstFreeSlot(MarketplacePlacement marketplace, @Nullable TraderUnit ignoreTrader) {
        for (int slot = 0; slot < marketplace.getTraderSlotCount(); slot++) {
            if (!isSlotTaken(marketplace, slot, ignoreTrader)) {
                return slot;
            }
        }
        return -1;
    }

    public static boolean isSlotTakenByAnother(MarketplacePlacement marketplace, int slotIndex, TraderUnit ignoreTrader) {
        return isSlotTaken(marketplace, slotIndex, ignoreTrader);
    }

    private static boolean isSlotTaken(MarketplacePlacement marketplace, int slotIndex, @Nullable TraderUnit ignoreTrader) {
        for (LivingEntity entity : UnitServerEvents.getAllUnits()) {
            if (entity instanceof TraderUnit trader &&
                (ignoreTrader == null || trader.getId() != ignoreTrader.getId()) &&
                trader.isAlive() &&
                (
                    (trader.isOccupyingMarketplace() &&
                        marketplace.originPos.equals(trader.getMarketplaceOrigin()) &&
                        trader.getMarketplaceSlotIndex() == slotIndex) ||
                    (trader.isMovingToMarketplace() &&
                        marketplace.originPos.equals(trader.getPendingMarketplaceOrigin()) &&
                        trader.getPendingMarketplaceSlotIndex() == slotIndex)
                )) {
                return true;
            }
        }
        return false;
    }
}
