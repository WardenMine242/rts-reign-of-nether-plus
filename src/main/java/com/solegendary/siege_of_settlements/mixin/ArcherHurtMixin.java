package com.solegendary.siege_of_settlements.mixin;

import com.WardenMine242.siege_of_settlements.unit.units.villagers.ArcherUnit;
import com.WardenMine242.siege_of_settlements.unit.units.villagers.EliteArcherUnit;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class ArcherHurtMixin {

    @Redirect(
            method = "hurt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;stopUsingItem()V")
    )
    private void siege_of_settlements$preventArcherStopUsingItem(LivingEntity instance) {
        if (!(instance instanceof ArcherUnit) && !(instance instanceof EliteArcherUnit)) {
            instance.stopUsingItem();
        }
    }
}
