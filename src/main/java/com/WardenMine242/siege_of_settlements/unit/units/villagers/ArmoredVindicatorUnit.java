package com.WardenMine242.siege_of_settlements.unit.units.villagers;

import com.solegendary.siege_of_settlements.ability.Abilities;
import com.solegendary.siege_of_settlements.ability.abilities.UpgradeToElite;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ArmoredVindicatorUnit extends EliteArmoredVindicatorUnit {
    public static float STAT_ATTACK_DAMAGE = 10.0f;
    public static float STAT_MAX_HEALTH = 170.0f;
    public static float STAT_ARMOR = 10.0f;
    public static float STAT_MOVEMENT_SPEED = EliteArmoredVindicatorUnit.STAT_MOVEMENT_SPEED;

    public ArmoredVindicatorUnit(EntityType<? extends Vindicator> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void updateAbilityButtons() {
        super.updateAbilityButtons();
        abilities.add(new UpgradeToElite());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, STAT_MOVEMENT_SPEED)
                .add(Attributes.ATTACK_DAMAGE, STAT_ATTACK_DAMAGE)
                .add(Attributes.ARMOR, STAT_ARMOR)
                .add(Attributes.MAX_HEALTH, STAT_MAX_HEALTH)
                .add(Attributes.FOLLOW_RANGE, com.solegendary.siege_of_settlements.unit.interfaces.Unit.getFollowRange());
    }

    @Override
    @Nullable
    public ResourceCost getCost() {
        return ResourceCosts.ARMORED_VINDICATOR;
    }

    @Override
    public float getMovementSpeed() {
        return STAT_MOVEMENT_SPEED;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            AttributeInstance movementSpeed = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeed != null && Math.abs(movementSpeed.getBaseValue() - STAT_MOVEMENT_SPEED) > 1.0E-6D) {
                movementSpeed.setBaseValue(STAT_MOVEMENT_SPEED);
            }
        }
    }

    @Override
    public float getUnitAttackDamage() {
        return STAT_ATTACK_DAMAGE + getSharpnessLevel();
    }

    @Override
    public float getUnitMaxHealth() {
        return STAT_MAX_HEALTH;
    }
}
