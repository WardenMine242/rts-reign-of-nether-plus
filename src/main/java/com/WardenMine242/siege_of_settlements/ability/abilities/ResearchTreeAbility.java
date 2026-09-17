package com.WardenMine242.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.WardenMine242.siege_of_settlements.guiscreen.ResearchTreeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class ResearchTreeAbility extends Ability {

    public ResearchTreeAbility() {
        super(UnitAction.NONE, 20, 0, 0, false, false);
        this.defaultHotkey = Keybindings.keyQ;
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, BuildingPlacement placement) {
        return new AbilityButton(
                I18n.get("ability.siege_of_settlements.research_tree"),
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/bookshelf.png"),
                hotkey,
                () -> false,
                () -> false,
                () -> true,
                () -> {
                    Minecraft mc = Minecraft.getInstance();
                    if (mc.player != null) {
                        mc.setScreen(new ResearchTreeScreen());
                        setToMaxCooldown(placement);
                    }
                },
                null,
                List.of(
                        fcs(I18n.get("ability.siege_of_settlements.research_tree"), Style.EMPTY.withBold(true))
                ),
                this,
                placement
        );
    }
}
