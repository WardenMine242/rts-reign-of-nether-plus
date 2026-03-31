package com.solegendary.siege_of_settlements.hud.buttons;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.Button;
import com.solegendary.siege_of_settlements.hud.HudClientEvents;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.resources.ResourceName;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.UnitClientEvents;
import com.solegendary.siege_of_settlements.unit.goals.BuildRepairGoal;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.interfaces.WorkerUnit;
import com.solegendary.siege_of_settlements.util.LanguageUtil;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

import static com.solegendary.siege_of_settlements.unit.UnitClientEvents.sendUnitCommand;

// static list of generic unit actions (build, attack, move, stop, etc.)
public class ActionButtons {

    public static Button buildRepair;
    public static Button gather;
    public static Button attack;
    public static Button stop;
    public static Button hold;
    public static Button move;
    public static Button garrison;
    public static Button ungarrison;

    public static void updateButtons() {
        buildRepair = new Button(
                "Build/Repair",
                Button.itemIconSize,
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/items/shovel.png"),
                Keybindings.build,
                () -> CursorClientEvents.getLeftClickAction() == UnitAction.BUILD_REPAIR ||
                        HudClientEvents.hudSelectedEntity instanceof WorkerUnit workerUnit &&
                                workerUnit.getBuildRepairGoal() != null &&
                                workerUnit.getBuildRepairGoal().autocastRepair,
                () -> false,
                () -> true,
                () -> CursorClientEvents.setLeftClickAction(UnitAction.BUILD_REPAIR),
                () -> {
                    LivingEntity hudEntity = HudClientEvents.hudSelectedEntity;
                    if (hudEntity instanceof WorkerUnit workerUnit) {
                        BuildRepairGoal goal = workerUnit.getBuildRepairGoal();
                        if (goal != null) {
                            if (goal.autocastRepair)
                                UnitClientEvents.sendUnitCommand(UnitAction.DISABLE_AUTOCAST_BUILD_REPAIR);
                            else
                                UnitClientEvents.sendUnitCommand(UnitAction.ENABLE_AUTOCAST_BUILD_REPAIR);
                        }
                    }
                },
                List.of(
                        FormattedCharSequence.forward(LanguageUtil.getTranslation("hud.actionbuttons.siege_of_settlements.build_repair"), Style.EMPTY),
                        FormattedCharSequence.forward(LanguageUtil.getTranslation("hud.actionbuttons.siege_of_settlements.build_repair_autocast"), Style.EMPTY)
                )
        );
        gather = new Button(
                "Gather",
                Button.itemIconSize,
                null, // changes depending on the gather target
                Keybindings.gather,
                () -> UnitClientEvents.getSelectedUnitResourceTarget() != ResourceName.NONE,
                () -> false,
                () -> true,
                () -> sendUnitCommand(UnitAction.TOGGLE_GATHER_TARGET),
                null,
                null
        );
        attack = new Button(
                "Attack",
                Button.itemIconSize,
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/items/sword.png"),
                Keybindings.attack,
                () -> CursorClientEvents.getLeftClickAction() == UnitAction.ATTACK,
                () -> false,
                () -> true,
                () -> CursorClientEvents.setLeftClickAction(UnitAction.ATTACK),
                null,
                List.of(FormattedCharSequence.forward(LanguageUtil.getTranslation("hud.actionbuttons.siege_of_settlements.attack"), Style.EMPTY))
        );
        stop = new Button(
                "Stop",
                Button.itemIconSize,
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/items/barrier.png"),
                Keybindings.stop,
                () -> false, // except if currently clicked on
                () -> false,
                () -> true,
                () -> sendUnitCommand(UnitAction.STOP),
                null,
                List.of(FormattedCharSequence.forward(LanguageUtil.getTranslation("hud.actionbuttons.siege_of_settlements.stop"), Style.EMPTY))
        );
        hold = new Button(
                "Hold Position",
                Button.itemIconSize,
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/items/chestplate.png"),
                Keybindings.hold,
                () -> {
                    LivingEntity entity = HudClientEvents.hudSelectedEntity;
                    return entity instanceof Unit unit && unit.getHoldPosition();
                },
                () -> false,
                () -> true,
                () -> sendUnitCommand(UnitAction.HOLD),
                null,
                List.of(FormattedCharSequence.forward(LanguageUtil.getTranslation("hud.actionbuttons.siege_of_settlements.hold_position"), Style.EMPTY))
        );
        move = new Button(
                "Move",
                Button.itemIconSize,
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/items/boots.png"),
                Keybindings.move,
                () -> CursorClientEvents.getLeftClickAction() == UnitAction.MOVE,
                () -> false,
                () -> true,
                () -> CursorClientEvents.setLeftClickAction(UnitAction.MOVE),
                null,
                List.of(FormattedCharSequence.forward(LanguageUtil.getTranslation("hud.actionbuttons.siege_of_settlements.move"), Style.EMPTY))
        );
        garrison = new Button(
                "Garrison",
                Button.itemIconSize,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/ladder.png"),
                Keybindings.garrison,
                () -> CursorClientEvents.getLeftClickAction() == UnitAction.GARRISON,
                () -> false,
                () -> true,
                () -> CursorClientEvents.setLeftClickAction(UnitAction.GARRISON),
                null,
                List.of(FormattedCharSequence.forward(LanguageUtil.getTranslation("hud.actionbuttons.siege_of_settlements.garrison"), Style.EMPTY))
        );
        ungarrison = new Button(
                "Ungarrison",
                Button.itemIconSize,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/oak_trapdoor.png"),
                Keybindings.garrison,
                () -> false,
                () -> false,
                () -> true,
                () -> sendUnitCommand(UnitAction.UNGARRISON),
                null,
                List.of(FormattedCharSequence.forward(LanguageUtil.getTranslation("hud.actionbuttons.siege_of_settlements.ungarrison"), Style.EMPTY))
        );
    }

    static {
        updateButtons();
    }
}
