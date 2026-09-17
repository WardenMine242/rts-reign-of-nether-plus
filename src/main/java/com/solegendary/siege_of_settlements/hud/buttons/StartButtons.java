package com.solegendary.siege_of_settlements.hud.buttons;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.Button;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.player.PlayerClientEvents;
import com.solegendary.siege_of_settlements.player.PlayerServerboundPacket;
import com.solegendary.siege_of_settlements.tutorial.TutorialClientEvents;
import com.solegendary.siege_of_settlements.tutorial.TutorialStage;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.faction.Faction;
import com.solegendary.siege_of_settlements.util.LanguageUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.GameType;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class StartButtons {

    public static final int ICON_SIZE = 14;

    public static Button sandboxStartButton = new Button(
            "Sandbox",
            ICON_SIZE,
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/tick.png"),
            (Keybinding) null,
            () -> false,
            () -> PlayerClientEvents.isRTSPlayer(),
            () -> {
                Minecraft MC = Minecraft.getInstance();
                if (MC.player == null || MC.gameMode == null) {
                    return false;
                }
                boolean isSurvival = MC.gameMode.getPlayerMode() == GameType.SURVIVAL;
                return !isSurvival || MC.player.hasPermissions(2);
            },
            () -> PlayerServerboundPacket.startRTS(Faction.NONE, 0d,0d,0d),
            null,
            List.of(
                    fcs(LanguageUtil.getTranslation("hud.gamemode.siege_of_settlements.sandbox_confirm"))
            )
    );

    public static Button villagerStartButton = new Button(
        "Villagers",
            ICON_SIZE,
        ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/villager.png"),
        (Keybinding) null,
        () -> CursorClientEvents.getLeftClickAction() == UnitAction.STARTRTS_VILLAGERS,
        () -> !TutorialClientEvents.isAtOrPastStage(TutorialStage.PLACE_WORKERS_B) || !PlayerClientEvents.canStartRTS || PlayerClientEvents.isRTSPlayer(),
        () -> true,
        () -> {
            CursorClientEvents.setLeftClickAction(UnitAction.STARTRTS_VILLAGERS);
        },
        null,
        List.of(
            fcs(LanguageUtil.getTranslation("hud.startbuttons.villagers.siege_of_settlements.first"), true),
            fcs(LanguageUtil.getTranslation("hud.startbuttons.villagers.siege_of_settlements.second"))
        )
    );

    public static Button monsterStartButton = new Button(
        "Monsters",
            ICON_SIZE,
        ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/creeper.png"),
        (Keybinding) null,
        () -> CursorClientEvents.getLeftClickAction() == UnitAction.STARTRTS_MONSTERS,
        () -> true,
        () -> false,
        () -> CursorClientEvents.setLeftClickAction(UnitAction.STARTRTS_MONSTERS),
        null,
        List.of(
            fcs(LanguageUtil.getTranslation("hud.startbuttons.monsters.siege_of_settlements.first"), true),
            fcs(LanguageUtil.getTranslation("hud.startbuttons.monsters.siege_of_settlements.second"))
        )
    );

    public static Button piglinStartButton = new Button(
        "Piglins",
        ICON_SIZE,
        ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/grunt.png"),
        (Keybinding) null,
        () -> CursorClientEvents.getLeftClickAction() == UnitAction.STARTRTS_PIGLINS,
        () -> true,
        () -> false,
        () -> CursorClientEvents.setLeftClickAction(UnitAction.STARTRTS_PIGLINS),
        null,
        List.of(
            fcs(LanguageUtil.getTranslation("hud.startbuttons.piglins.siege_of_settlements.first"), true),
            fcs(LanguageUtil.getTranslation("hud.startbuttons.piglins.siege_of_settlements.second"))
        )
    );
}
