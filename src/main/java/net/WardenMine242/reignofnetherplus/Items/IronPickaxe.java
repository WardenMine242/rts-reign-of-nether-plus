package net.WardenMine242.reignofnetherplus.Items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IronPickaxe extends PickaxeItem {

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
            return Tiers.IRON.getLevel();
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

    public IronPickaxe(Item.Properties properties) {
        super(IRON_DURABILITY_WOOD_STATS, 1, -2.8F, properties);
    }

    @Override
    public void appendHoverText(
        @NotNull ItemStack stack,
        @Nullable Level level,
        @NotNull List<Component> tooltip,
        @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.translatable("item.reignofnether.iron_pickaxe.tooltip1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.reignofnether.iron_pickaxe.tooltip2").withStyle(ChatFormatting.GRAY));
    }
}
