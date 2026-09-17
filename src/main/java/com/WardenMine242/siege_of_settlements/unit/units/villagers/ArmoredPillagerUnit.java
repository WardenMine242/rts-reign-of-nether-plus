package com.WardenMine242.siege_of_settlements.unit.units.villagers;

import com.solegendary.siege_of_settlements.ability.Abilities;
import com.solegendary.siege_of_settlements.ability.abilities.UpgradePillagerToElite;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ArmoredPillagerUnit extends EliteArmoredPillagerUnit {
    public static float STAT_ATTACK_DAMAGE = 12.0f;
    public static float STAT_MAX_HEALTH = 140.0f;
    public static float STAT_ARMOR = 10.0f;

    public ArmoredPillagerUnit(EntityType<? extends Pillager> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void updateAbilityButtons() {
        super.updateAbilityButtons();
        abilities.add(new UpgradePillagerToElite());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, EliteArmoredPillagerUnit.STAT_MOVEMENT_SPEED)
                .add(Attributes.ATTACK_DAMAGE, STAT_ATTACK_DAMAGE)
                .add(Attributes.ARMOR, STAT_ARMOR)
                .add(Attributes.MAX_HEALTH, STAT_MAX_HEALTH)
                .add(Attributes.FOLLOW_RANGE, Unit.getFollowRange());
    }

    @Override
    @Nullable
    public ResourceCost getCost() {
        return ResourceCosts.ARMORED_PILLAGER;
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
