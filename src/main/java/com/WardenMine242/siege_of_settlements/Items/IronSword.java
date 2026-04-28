package com.WardenMine242.siege_of_settlements.Items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;

public class IronSword extends SwordItem {

    private static final Tier IRON_DURABILITY_WOOD_STATS = new Tier() {
        @Override
        public int getUses() {
            return Tiers.IRON.getUses();
        }

        @Override
        public float getSpeed() {
            return Tiers.WOOD.getSpeed();
        }

        @Override
        public float getAttackDamageBonus() {
            return Tiers.WOOD.getAttackDamageBonus();
        }

        @Override
        public int getLevel() {
            return Tiers.WOOD.getLevel();
        }

        @Override
        public int getEnchantmentValue() {
            return Tiers.WOOD.getEnchantmentValue();
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Tiers.IRON.getRepairIngredient();
        }
    };

    public IronSword(Item.Properties properties) {
        // 4.0 (base player attack speed) + (-3.1) = 0.9 final attack speed
        super(IRON_DURABILITY_WOOD_STATS, 3, -3.1F, properties);
    }
}
