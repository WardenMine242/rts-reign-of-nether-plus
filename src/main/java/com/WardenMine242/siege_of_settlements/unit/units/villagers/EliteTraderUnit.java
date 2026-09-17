package com.WardenMine242.siege_of_settlements.unit.units.villagers;

import com.WardenMine242.siege_of_settlements.ability.abilities.UpgradeTraderToElite;
import com.solegendary.siege_of_settlements.ability.Abilities;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EliteTraderUnit extends TraderUnit implements GeoEntity {

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public EliteTraderUnit(EntityType<? extends Vindicator> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void updateAbilityButtons() {
        super.updateAbilityButtons();
        this.getAbilities().remove(UpgradeTraderToElite.class);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "elite_trader_controller", 0, this::animationPredicate));
    }

    private PlayState animationPredicate(AnimationState<EliteTraderUnit> state) {
        if (state.isMoving()) {
            state.getController().setAnimationSpeed(1.0D);
            state.setAndContinue(WALK_ANIM);
            return PlayState.CONTINUE;
        }
        state.getController().setAnimationSpeed(1.0D);
        state.setAndContinue(IDLE_ANIM);
        return PlayState.CONTINUE;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TraderUnit.createAttributes();
    }

    @Override
    public int getMarketplaceEmeraldsPerMinute() {
        return 20;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
