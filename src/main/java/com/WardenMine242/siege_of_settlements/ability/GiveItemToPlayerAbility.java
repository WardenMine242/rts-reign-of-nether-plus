package com.WardenMine242.siege_of_settlements.ability;

import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.ability.EquipAbility;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.buildings.placements.BlacksmithPlacement;
import com.solegendary.siege_of_settlements.hud.HudClientEvents;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.Resources;
import com.solegendary.siege_of_settlements.resources.ResourcesClientEvents;
import com.solegendary.siege_of_settlements.resources.ResourcesServerEvents;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public abstract class GiveItemToPlayerAbility extends Ability {
    protected final ResourceCost cost;
    protected final Supplier<Item> itemSupplier;

    protected GiveItemToPlayerAbility(UnitAction action, ResourceCost cost, Supplier<Item> itemSupplier) {
        super(action, EquipAbility.CD_MAX, EquipAbility.RANGE, 0, true, true);
        this.cost = cost;
        this.itemSupplier = itemSupplier;
    }

    protected ResourceCost getCost(BuildingPlacement buildingUsing) {
        return cost;
    }

    protected boolean canAfford(BuildingPlacement buildingUsing) {
        ResourceCost currentCost = getCost(buildingUsing);
        Resources res = null;
        if (buildingUsing.getLevel().isClientSide()) {
            res = ResourcesClientEvents.getOwnResources();
        } else {
            for (Resources resources : ResourcesServerEvents.resourcesList)
                if (resources.ownerName.equals(buildingUsing.ownerName))
                    res = resources;
        }
        if (res != null)
            return (res.food >= currentCost.food &&
                    res.wood >= currentCost.wood &&
                    res.ore >= currentCost.ore &&
                    res.emerald >= currentCost.emerald);
        return false;
    }

    protected void giveItem(ServerPlayer player) {
        giveItemStack(player, new ItemStack(itemSupplier.get()));
    }

    protected void giveItem(ServerPlayer player, BuildingPlacement buildingUsing) {
        giveItem(player);
    }

    protected void giveItemStack(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        } else {
            syncInventory(player);
        }
    }

    protected void giveItemStackReplacingExisting(ServerPlayer player, ItemStack stack, @Nullable Item oldItemToReplace) {
        if (oldItemToReplace != null && replaceFirstInventoryItem(player, oldItemToReplace, stack)) {
            return;
        }
        giveItemStack(player, stack);
    }

    protected boolean replaceFirstInventoryItem(ServerPlayer player, Item oldItem, ItemStack replacement) {
        var inventory = player.getInventory();

        for (int i = 0; i < inventory.items.size(); i++) {
            ItemStack slot = inventory.items.get(i);
            if (!slot.isEmpty() && slot.getItem() == oldItem) {
                inventory.setItem(i, replacement.copy());
                syncInventory(player);
                return true;
            }
        }

        for (int i = 0; i < inventory.offhand.size(); i++) {
            ItemStack slot = inventory.offhand.get(i);
            if (!slot.isEmpty() && slot.getItem() == oldItem) {
                inventory.offhand.set(i, replacement.copy());
                syncInventory(player);
                return true;
            }
        }

        return false;
    }

    protected void syncInventory(ServerPlayer player) {
        player.getInventory().setChanged();
        player.inventoryMenu.broadcastChanges();
        if (player.containerMenu != player.inventoryMenu) {
            player.containerMenu.broadcastChanges();
        }
    }

    private void playSound(Level level, LivingEntity te) {
        level.playLocalSound(te.getX(), te.getY(), te.getZ(),
                SoundEvents.SMITHING_TABLE_USE, te.getSoundSource(), 1.0F + te.getRandom().nextFloat(),
                te.getRandom().nextFloat() * 0.7F + 0.3F, false);
    }

    @Override
    public void use(Level level, BuildingPlacement buildingUsing, LivingEntity targetEntity) {
        if (!(buildingUsing instanceof BlacksmithPlacement))
            return;

        BlockPos centreBottom = new BlockPos(buildingUsing.centrePos.getX(), buildingUsing.minCorner.getY(), buildingUsing.centrePos.getZ());

        if (!level.isClientSide()) {
            ResourceCost currentCost = getCost(buildingUsing);
            if (targetEntity instanceof ServerPlayer player &&
                player.getName().getString().equals(buildingUsing.ownerName) &&
                canAfford(buildingUsing) &&
                targetEntity.distanceToSqr(Vec3.atCenterOf(centreBottom)) < range * range) {

                giveItem(player, buildingUsing);
                ResourcesServerEvents.addSubtractResources(new Resources(
                        buildingUsing.ownerName,
                        -currentCost.food,
                        -currentCost.wood,
                        -currentCost.ore,
                        -currentCost.emerald
                ));
                setToMaxCooldown(buildingUsing);
                playSound(level, targetEntity);
            }
            return;
        }

        if (!(targetEntity instanceof Player player) || !player.getName().getString().equals(buildingUsing.ownerName)) {
            HudClientEvents.showTemporaryMessage(I18n.get("ability.siege_of_settlements.equip.error1"));
        } else if (targetEntity.distanceToSqr(Vec3.atCenterOf(centreBottom)) >= range * range) {
            HudClientEvents.showTemporaryMessage(I18n.get("ability.siege_of_settlements.equip.error2"));
        } else if (!canAfford(buildingUsing)) {
            HudClientEvents.showTemporaryMessage(I18n.get("ability.siege_of_settlements.equip.error5"));
        } else {
            setToMaxCooldown(buildingUsing);
            playSound(level, targetEntity);
        }
    }

    @Override
    public void use(Level level, BuildingPlacement buildingUsing, BlockPos targetBp) {
        if (level.isClientSide()) {
            return;
        }
        if (level.getServer() == null) {
            return;
        }
        ServerPlayer player = level.getServer().getPlayerList().getPlayerByName(buildingUsing.ownerName);
        if (player != null) {
            use(level, buildingUsing, player);
        }
    }
}
