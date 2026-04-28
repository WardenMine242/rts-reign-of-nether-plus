package com.WardenMine242.siege_of_settlements.unit.units.villagers;

import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.registrars.ItemRegistrar;
import com.solegendary.siege_of_settlements.unit.units.villagers.VindicatorUnit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class ArmoredVindicatorUnit extends VindicatorUnit {

    public static float STAT_ATTACK_DAMAGE = VindicatorUnit.attackDamage;
    public static float STAT_MOVEMENT_SPEED = VindicatorUnit.movementSpeed;
    public static float STAT_ATTACKS_PER_SECOND = VindicatorUnit.attacksPerSecond;
    public static float STAT_MAX_HEALTH = VindicatorUnit.maxHealth;
    public static float STAT_ARMOR = VindicatorUnit.armorValue;
    public static float STAT_ATTACK_RANGE = VindicatorUnit.attackRange;
    public static float STAT_AGGRO_RANGE = VindicatorUnit.aggroRange;
    public static float STAT_RANGED_DAMAGE_RESIST = VindicatorUnit.rangedDamageResist;

    public ArmoredVindicatorUnit(EntityType<? extends Vindicator> entityType, Level level) {
        super(entityType, level);
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
    public float getAttackCooldown() {
        return (20.0F / STAT_ATTACKS_PER_SECOND) * getAttackCooldownMultiplier();
    }

    @Override
    public float getAttacksPerSecond() {
        return STAT_ATTACKS_PER_SECOND / getAttackCooldownMultiplier();
    }

    @Override
    public float getBaseAttacksPerSecond() {
        return STAT_ATTACKS_PER_SECOND;
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
        return STAT_MOVEMENT_SPEED;
    }

    @Override
    public float getUnitAttackDamage() {
        return STAT_ATTACK_DAMAGE + getSharpnessLevel();
    }

    @Override
    public float getUnitMaxHealth() {
        return STAT_MAX_HEALTH;
    }

    @Override
    public double getUnitRangedArmorPercentage() {
        return STAT_RANGED_DAMAGE_RESIST;
    }

    @Override
    public void tick() {
        ensureDoubleAxeEquippedServer();
        super.tick();
    }

    @Override
    public void setupEquipmentAndUpgradesServer() {
        ensureDoubleAxeEquippedServer();
    }

    private void ensureDoubleAxeEquippedServer() {
        if (this.level().isClientSide()) {
            return;
        }
        ItemStack mainHand = this.getItemBySlot(EquipmentSlot.MAINHAND);
        if (mainHand.isEmpty() || mainHand.getItem() != ItemRegistrar.DOUBLE_AXE.get()) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ItemRegistrar.DOUBLE_AXE.get()));
        }
    }
}
