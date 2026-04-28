package com.WardenMine242.siege_of_settlements.Items;

import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.Buildings;
import com.solegendary.siege_of_settlements.building.BuildingUtils;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.resources.ResourceName;
import com.solegendary.siege_of_settlements.resources.Resources;
import com.solegendary.siege_of_settlements.resources.ResourcesServerEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IronHoe extends HoeItem {

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

    public IronHoe(Item.Properties properties) {
        // -1.0 (item modifier) + 1.0 (player base) = 0.0 final attack damage
        super(IRON_DURABILITY_WOOD_STATS, -1, 0.0F, properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null || player.isSpectator()) {
            return InteractionResult.PASS;
        }

        BlockPos pos = context.getClickedPos();
        if (level.getBlockState(pos).is(Blocks.FARMLAND) && level.getBlockState(pos.above()).isAir()) {
            if (!level.isClientSide) {
                if (!player.isCreative() &&
                    player instanceof ServerPlayer serverPlayer &&
                    serverPlayer.gameMode.getGameModeForPlayer() == GameType.SURVIVAL) {
                    BuildingPlacement building = BuildingUtils.findBuilding(false, pos.above());
                    if (building != null && building.getBuilding() == Buildings.WHEAT_FARM) {
                        String playerName = player.getName().getString();
                        if (!ResourcesServerEvents.canAfford(playerName, ResourceName.WOOD, ResourceCosts.REPLANT_WOOD_COST)) {
                            return InteractionResult.FAIL;
                        }
                        ResourcesServerEvents.addSubtractResources(new Resources(playerName, 0, -ResourceCosts.REPLANT_WOOD_COST, 0));
                    }
                }

                level.setBlockAndUpdate(pos.above(), Blocks.WHEAT.defaultBlockState());
                level.gameEvent(player, GameEvent.BLOCK_PLACE, pos.above());

                if (!player.isCreative()) {
                    context.getItemInHand().hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.useOn(context);
    }

    @Override
    public void appendHoverText(
        @NotNull ItemStack stack,
        @Nullable Level level,
        @NotNull List<Component> tooltip,
        @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.translatable("item.siege_of_settlements.iron_hoe.tooltip1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.siege_of_settlements.iron_hoe.tooltip2").withStyle(ChatFormatting.GRAY));
    }
}
