package com.solegendary.siege_of_settlements.mixin;

import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.BuildingUtils;
import com.solegendary.siege_of_settlements.building.buildings.monsters.SpiderLair;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.faction.Faction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WebBlock.class)
public abstract class WebBlockMixin {
    @Inject(
            method = "entityInside",
            at = @At("HEAD"),
            cancellable = true
    )
    private void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity, CallbackInfo ci) {
        BuildingPlacement building = BuildingUtils.findBuilding(pLevel.isClientSide, pPos);
        if (building != null && building.getBuilding() instanceof SpiderLair spiderLair &&
            pEntity instanceof Unit unit && unit.getFaction() == Faction.MONSTERS)
            ci.cancel();
    }
}
