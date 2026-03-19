package net.WardenMine242.reignofnetherplus.ability.abilities;

import com.solegendary.reignofnether.ability.Ability;
import com.solegendary.reignofnether.ability.EquipAbility;
import com.solegendary.reignofnether.building.BuildingPlacement;
import com.solegendary.reignofnether.building.buildings.placements.BlacksmithPlacement;
import com.solegendary.reignofnether.hud.HudClientEvents;
import com.solegendary.reignofnether.resources.ResourceCost;
import com.solegendary.reignofnether.resources.Resources;
import com.solegendary.reignofnether.resources.ResourcesClientEvents;
import com.solegendary.reignofnether.resources.ResourcesServerEvents;
import com.solegendary.reignofnether.unit.UnitAction;
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

import java.util.function.Supplier;

public abstract class GiveItemToPlayerAbility extends Ability {
    protected final ResourceCost cost;
    protected final Supplier<Item> itemSupplier;

    protected GiveItemToPlayerAbility(UnitAction action, ResourceCost cost, Supplier<Item> itemSupplier) {
        super(action, EquipAbility.CD_MAX, EquipAbility.RANGE, 0, true, true);
        this.cost = cost;
        this.itemSupplier = itemSupplier;
    }

    protected boolean canAfford(BuildingPlacement buildingUsing) {
        Resources res = null;
        if (buildingUsing.getLevel().isClientSide()) {
            res = ResourcesClientEvents.getOwnResources();
        } else {
            for (Resources resources : ResourcesServerEvents.resourcesList)
                if (resources.ownerName.equals(buildingUsing.ownerName))
                    res = resources;
        }
        if (res != null)
            return (res.food >= cost.food &&
                    res.wood >= cost.wood &&
                    res.ore >= cost.ore);
        return false;
    }

    protected void giveItem(ServerPlayer player) {
        giveItemStack(player, new ItemStack(itemSupplier.get()));
    }

    protected void giveItemStack(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        } else {
            player.getInventory().setChanged();
            player.inventoryMenu.broadcastChanges();
            if (player.containerMenu != player.inventoryMenu) {
                player.containerMenu.broadcastChanges();
            }
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
            if (targetEntity instanceof ServerPlayer player &&
                player.getName().getString().equals(buildingUsing.ownerName) &&
                canAfford(buildingUsing) &&
                targetEntity.distanceToSqr(Vec3.atCenterOf(centreBottom)) < range * range) {

                giveItem(player);
                ResourcesServerEvents.addSubtractResources(new Resources(buildingUsing.ownerName, -cost.food, -cost.wood, -cost.ore));
                setToMaxCooldown(buildingUsing);
                playSound(level, targetEntity);
            }
            return;
        }

        if (!(targetEntity instanceof Player player) || !player.getName().getString().equals(buildingUsing.ownerName)) {
            HudClientEvents.showTemporaryMessage(I18n.get("ability.reignofnether.equip.error1"));
        } else if (targetEntity.distanceToSqr(Vec3.atCenterOf(centreBottom)) >= range * range) {
            HudClientEvents.showTemporaryMessage(I18n.get("ability.reignofnether.equip.error2"));
        } else if (!canAfford(buildingUsing)) {
            HudClientEvents.showTemporaryMessage(I18n.get("ability.reignofnether.equip.error5"));
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
