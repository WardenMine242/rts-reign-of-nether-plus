package com.WardenMine242.siege_of_settlements.unit.units.villagers;

import com.solegendary.siege_of_settlements.ability.Abilities;
import com.solegendary.siege_of_settlements.ability.abilities.MountRavager;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.villagers.PillagerUnit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class EliteArmoredPillagerUnit extends PillagerUnit {
    public static float STAT_ATTACK_DAMAGE = 16.0f;
    public static float STAT_ATTACKS_PER_SECOND = PillagerUnit.attacksPerSecond;
    public static float STAT_MAX_HEALTH = 240.0f;
    public static float STAT_ARMOR = 10.0f;
    public static float STAT_MOVEMENT_SPEED = PillagerUnit.movementSpeed;
    public static float STAT_ATTACK_RANGE = PillagerUnit.attackRange;
    public static float STAT_AGGRO_RANGE = PillagerUnit.aggroRange;

    public EliteArmoredPillagerUnit(EntityType<? extends Pillager> entityType, Level level) {
        super(entityType, level);
        updateAbilityButtons();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, STAT_MOVEMENT_SPEED)
                .add(Attributes.MAX_HEALTH, STAT_MAX_HEALTH)
                .add(Attributes.FOLLOW_RANGE, Unit.getFollowRange())
                .add(Attributes.ARMOR, STAT_ARMOR);
    }

    @Override
    @Nullable
    public ResourceCost getCost() {
        return ResourceCosts.ELITE_ARMORED_PILLAGER;
    }

    @Override
    public float getAttackCooldown() {
        return ((20.0F / STAT_ATTACKS_PER_SECOND) * getAttackCooldownMultiplier());
    }

    @Override
    public float getAttacksPerSecond() {
        ItemStack itemStack = this.getItemBySlot(EquipmentSlot.MAINHAND);
        return 20f / (getAttackCooldown() + (CrossbowItem.getChargeDuration(itemStack)));
    }

    @Override
    public float getBaseAttacksPerSecond() {
        return 20f / (getAttackCooldown() + 35);
    }

    @Override
    public float getAggroRange() {
        return STAT_AGGRO_RANGE;
    }

    @Override
    public float getAttackRange() {
        return STAT_ATTACK_RANGE;
    }

    @Override
    public float getMovementSpeed() {
        if (this.isPassenger() && this.getVehicle() instanceof Unit mountedUnit) {
            return mountedUnit.getMovementSpeed();
        }
        return STAT_MOVEMENT_SPEED;
    }

    @Override
    public float getUnitAttackDamage() {
        return STAT_ATTACK_DAMAGE + getTowerDamageResearchBonus();
    }

    @Override
    public float getUnitMaxHealth() {
        return STAT_MAX_HEALTH;
    }
}
