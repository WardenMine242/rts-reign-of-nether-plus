package com.solegendary.siege_of_settlements.mixin;

import com.solegendary.siege_of_settlements.alliance.AlliancesClient;
import com.solegendary.siege_of_settlements.alliance.AlliancesServerEvents;
import com.solegendary.siege_of_settlements.blocks.BlockServerEvents;
import com.solegendary.siege_of_settlements.registrars.BlockRegistrar;
import com.solegendary.siege_of_settlements.registrars.MobEffectRegistrar;
import com.solegendary.siege_of_settlements.resources.BlockUtils;
import com.solegendary.siege_of_settlements.unit.units.villagers.EvokerUnit;
import com.solegendary.siege_of_settlements.util.MiscUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

// prevent vexes from charging enemies too far from their parent EvokerUnit
@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {

    protected MobMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(
            method = "setTarget",
            at = @At("HEAD"),
            cancellable = true
    )
    public void setTarget(@Nullable LivingEntity pTarget, CallbackInfo ci) {
        if (pTarget == null || !pTarget.isAlive())
            return;

        Entity entity = pTarget.level().getEntity(this.getId());
        if (entity instanceof Vex vex &&
            vex.getOwner() instanceof EvokerUnit eu) {

            boolean targetIsAlliedPlayer;

            if (level().isClientSide()) {
                targetIsAlliedPlayer = pTarget instanceof Player player &&
                        (AlliancesClient.isAllied(player.getName().getString(), eu.getOwnerName()) ||
                            player.getName().getString().equals(eu.getOwnerName()));
            } else {
                targetIsAlliedPlayer = pTarget instanceof Player player &&
                        (AlliancesServerEvents.isAllied(player.getName().getString(), eu.getOwnerName()) ||
                            player.getName().getString().equals(eu.getOwnerName()));
            }
            boolean outOfRange = eu.distanceTo(pTarget) > eu.getVexTargetRange();
            if (outOfRange || targetIsAlliedPlayer)
                ci.cancel();
        }
    }

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    public void tick(CallbackInfo ci) {
        MobEffectInstance mei = this.getEffect(MobEffectRegistrar.FROST_DAMAGE.get());
        BlockState inBlockState = level().getBlockState(getOnPos().above());
        if (mei != null && mei.getDuration() > 0 && mei.getDuration() % 20 == 0 && onGround()) {
            int layers = BlockUtils.getWraithSnowLayers(inBlockState);
            boolean inIce = inBlockState.getBlock() == Blocks.PACKED_ICE;
            if (layers > 0 || inIce) {
                hurt(damageSources().magic(), layers + (inIce ? 3 : 0));
            }
        }
    }
}
