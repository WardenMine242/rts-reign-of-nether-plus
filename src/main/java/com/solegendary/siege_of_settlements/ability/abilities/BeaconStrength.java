package com.solegendary.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.ability.BeaconAbility;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.buildings.placements.BeaconPlacement;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.UnitClientEvents;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class BeaconStrength extends BeaconAbility {

    public final static MobEffect AURA_EFFECT = MobEffects.DAMAGE_BOOST;

    public BeaconStrength() {
        super(UnitAction.BEACON_STRENGTH, AURA_EFFECT);
        this.defaultHotkey = Keybindings.keyT;
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, BuildingPlacement placement) {
        if (!(placement instanceof BeaconPlacement beacon)) return null;

        return new AbilityButton(
                "Strength Aura",
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/mob_effect/strength.png"),
                hotkey,
                () -> beacon.getAuraEffect() == AURA_EFFECT,
                () -> false,
                () -> beacon.getUpgradeLevel() >= 5,
                () -> UnitClientEvents.sendUnitCommand(UnitAction.BEACON_STRENGTH),
                null,
                List.of(
                        fcs(I18n.get("ability.siege_of_settlements.beacon_aura.strength"), true),
                        fcs(""),
                        fcs(I18n.get("ability.siege_of_settlements.beacon_aura.strength.tooltip1")),
                        fcs(I18n.get("ability.siege_of_settlements.beacon_aura.one_aura"))
                ),
                this,
                placement
        );
    }
}