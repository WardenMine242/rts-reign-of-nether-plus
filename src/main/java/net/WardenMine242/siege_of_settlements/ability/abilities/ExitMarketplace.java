package net.WardenMine242.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import net.WardenMine242.siege_of_settlements.unit.units.villagers.TraderUnit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.solegendary.siege_of_settlements.unit.UnitClientEvents.sendUnitCommand;
import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class ExitMarketplace extends Ability {

    public ExitMarketplace() {
        super(
                UnitAction.EXIT_MARKETPLACE,
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
                "Exit Marketplace",
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/emerald.png"),
                hotkey,
                () -> false,
                () -> !traderUnit.isOccupyingMarketplace(),
                () -> traderUnit.isOccupyingMarketplace(),
                () -> sendUnitCommand(UnitAction.EXIT_MARKETPLACE),
                null,
                List.of(
                        fcs(I18n.get("abilities.siege_of_settlements.exit_marketplace"), false)
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
        traderUnit.exitMarketplaceToFront();
    }
}
