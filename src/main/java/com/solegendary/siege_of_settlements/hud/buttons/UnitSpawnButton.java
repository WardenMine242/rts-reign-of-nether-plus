package com.solegendary.siege_of_settlements.hud.buttons;

import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.Button;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.sandbox.SandboxAction;
import com.solegendary.siege_of_settlements.sandbox.SandboxClientEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class UnitSpawnButton extends Button {
    public UnitSpawnButton(String name, ResourceLocation iconRl, @Nullable List<FormattedCharSequence> tooltipLines) {
        super(name, 14, iconRl, (Keybinding) null, () -> SandboxClientEvents.spawnUnitName.equals(name), () -> false, () -> true, () -> {
            CursorClientEvents.setLeftClickSandboxAction(SandboxAction.SPAWN_UNIT);
            SandboxClientEvents.spawnUnitName = name;
        }, null, tooltipLines);
    }

    public UnitSpawnButton(String name, ResourceLocation iconRl, ResourceLocation frameRl, @Nullable List<FormattedCharSequence> tooltipLines) {
        super(name, 14, iconRl, frameRl, null, () -> SandboxClientEvents.spawnUnitName.equals(name), () -> false, () -> true, () -> {
            CursorClientEvents.setLeftClickSandboxAction(SandboxAction.SPAWN_UNIT);
            SandboxClientEvents.spawnUnitName = name;
        }, null, tooltipLines);
    }
}
