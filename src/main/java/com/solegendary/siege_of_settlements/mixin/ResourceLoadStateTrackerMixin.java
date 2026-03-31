package com.solegendary.siege_of_settlements.mixin;

import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.hud.buttons.ActionButtons;
import com.solegendary.siege_of_settlements.hud.buttons.HelperButtons;
import com.solegendary.siege_of_settlements.sandbox.SandboxActionButtons;
import com.solegendary.siege_of_settlements.unit.UnitClientEvents;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import net.minecraft.client.ResourceLoadStateTracker;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResourceLoadStateTracker.class)
public abstract class ResourceLoadStateTrackerMixin {

    // update all building and unit buttons when the language is changed
    @Inject(
            method = "finishReload",
            at = @At("TAIL")
    )
    protected void onDone(CallbackInfo ci) {
        for (LivingEntity le : UnitClientEvents.getAllUnits())
            if (le instanceof Unit unit)
                unit.updateAbilityButtons();

        for (BuildingPlacement building : BuildingClientEvents.getBuildings())
            building.updateButtons();

        ActionButtons.updateButtons();
        SandboxActionButtons.updateButtons();
        HelperButtons.updateButtons();
    }
}
