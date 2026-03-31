package net.WardenMine242.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.resources.Resources;
import com.solegendary.siege_of_settlements.resources.ResourcesClientEvents;
import com.solegendary.siege_of_settlements.resources.ResourcesServerEvents;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import net.WardenMine242.siege_of_settlements.unit.units.villagers.EliteVindicatorUnit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.solegendary.siege_of_settlements.unit.UnitClientEvents.sendUnitCommand;
import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class EliteVindicatorBerserk extends Ability {
    private static final int DURATION_SECONDS = 10;
    private static final ResourceCost COST = ResourceCosts.BERSERK;

    public EliteVindicatorBerserk() {
        super(UnitAction.BERSERK, DURATION_SECONDS * 20, 0, 0, false);
    }

    private boolean canAfford(String ownerName, boolean isClientSide) {
        Resources res = null;
        if (isClientSide) {
            res = ResourcesClientEvents.getOwnResources();
        } else {
            for (Resources resources : ResourcesServerEvents.resourcesList) {
                if (resources.ownerName.equals(ownerName)) {
                    res = resources;
                    break;
                }
            }
        }

        return res != null &&
                res.food >= COST.food &&
                res.wood >= COST.wood &&
                res.ore >= COST.ore &&
                res.emerald >= COST.emerald;
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        if (!(unit instanceof EliteVindicatorUnit eliteVindicatorUnit)) {
            return null;
        }

        return new AbilityButton(
                "Berserk",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/abilities/bloodlust.png"),
                hotkey,
                eliteVindicatorUnit::isBerserkActive,
                () -> false,
                () -> !eliteVindicatorUnit.isBerserkActive() && canAfford(eliteVindicatorUnit.getOwnerName(), true),
                () -> sendUnitCommand(UnitAction.BERSERK),
                null,
                List.of(
                        fcs(I18n.get("abilities.siege_of_settlements.berserk"), true),
                        ResourceCosts.getFormattedCost(COST),
                        fcs(""),
                        fcs(I18n.get("abilities.siege_of_settlements.berserk.tooltip1", DURATION_SECONDS)),
                        fcs(I18n.get("abilities.siege_of_settlements.berserk.tooltip2"))
                ),
                this,
                unit
        );
    }

    @Override
    public void use(Level level, Unit unitUsing, BlockPos targetBp) {
        if (level.isClientSide() || !(unitUsing instanceof EliteVindicatorUnit eliteVindicatorUnit)) {
            return;
        }
        if (eliteVindicatorUnit.isBerserkActive()) {
            return;
        }
        if (!canAfford(eliteVindicatorUnit.getOwnerName(), false)) {
            return;
        }

        if (COST.food > 0 || COST.wood > 0 || COST.ore > 0 || COST.emerald > 0) {
            ResourcesServerEvents.addSubtractResources(new Resources(
                    eliteVindicatorUnit.getOwnerName(),
                    -COST.food,
                    -COST.wood,
                    -COST.ore,
                    -COST.emerald
            ));
        }

        eliteVindicatorUnit.activateBerserk();
        setToMaxCooldown(unitUsing);
    }

    @Override
    public boolean shouldResetBehaviours() {
        return false;
    }
}
