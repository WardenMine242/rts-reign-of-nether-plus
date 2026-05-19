package com.WardenMine242.siege_of_settlements.Items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DiamondHoe extends IronHoe {

    public DiamondHoe(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(
        @NotNull ItemStack stack,
        @Nullable Level level,
        @NotNull List<Component> tooltip,
        @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.translatable("item.siege_of_settlements.diamond_hoe.tooltip1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.siege_of_settlements.diamond_hoe.tooltip2").withStyle(ChatFormatting.GRAY));
    }
}
