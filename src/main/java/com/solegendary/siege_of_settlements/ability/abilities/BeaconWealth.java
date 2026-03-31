package com.solegendary.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.ability.BeaconAbility;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.buildings.placements.BeaconPlacement;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.UnitClientEvents;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class BeaconWealth extends BeaconAbility {

    public final static MobEffect AURA_EFFECT = MobEffects.LUCK;

    public BeaconWealth() {
        super(UnitAction.BEACON_WEALTH, AURA_EFFECT);
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, BuildingPlacement placement) {
        if (!(placement instanceof BeaconPlacement beacon)) return null;

        return new AbilityButton(
                "Wealth Aura",
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/mob_effect/hero_of_the_village.png"),
                hotkey,
                () -> beacon.getAuraEffect() == AURA_EFFECT,
                () -> false,
                () -> beacon.getUpgradeLevel() >= 1,
                () -> UnitClientEvents.sendUnitCommand(UnitAction.BEACON_WEALTH),
                null,
                List.of(
                        fcs(I18n.get("ability.siege_of_settlements.beacon_aura.wealth"), true),
                        fcs(""),
                        fcs(I18n.get("ability.siege_of_settlements.beacon_aura.wealth.tooltip1")),
                        fcs(I18n.get("ability.siege_of_settlements.beacon_aura.one_aura"))
                ),
                this,
                placement
        );
    }
}
