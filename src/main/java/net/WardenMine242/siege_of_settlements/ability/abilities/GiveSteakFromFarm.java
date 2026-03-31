package net.WardenMine242.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.buildings.placements.FarmPlacement;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.hud.HudClientEvents;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.resources.Resources;
import com.solegendary.siege_of_settlements.resources.ResourcesClientEvents;
import com.solegendary.siege_of_settlements.resources.ResourcesServerEvents;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class GiveSteakFromFarm extends Ability {

    private static final UnitAction GIVE_ACTION = UnitAction.FARM_GIVE_STEAK;
    private static final int STEAK_COUNT = 4;
    private static final ResourceCost COST = ResourceCosts.STEAK;

    public GiveSteakFromFarm() {
        super(GIVE_ACTION, 1, 12, 0, true, true);
        this.defaultHotkey = Keybindings.keyQ;
    }

    private boolean canAfford(BuildingPlacement buildingUsing) {
        Resources res = null;
        if (buildingUsing.getLevel().isClientSide()) {
            res = ResourcesClientEvents.getOwnResources();
        } else {
            for (Resources resources : ResourcesServerEvents.resourcesList) {
                if (resources.ownerName.equals(buildingUsing.ownerName)) {
                    res = resources;
                }
            }
        }
        if (res != null) {
            return (res.food >= COST.food &&
                    res.wood >= COST.wood &&
                    res.ore >= COST.ore);
        }
        return false;
    }

    private void giveSteaks(ServerPlayer player) {
        ItemStack stack = new ItemStack(Items.COOKED_BEEF, STEAK_COUNT);
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
                SoundEvents.ITEM_PICKUP, te.getSoundSource(), 0.9F + te.getRandom().nextFloat(),
                te.getRandom().nextFloat() * 0.7F + 0.3F, false);
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, BuildingPlacement placement) {
        if (!(placement instanceof FarmPlacement)) return null;
        return new AbilityButton(
                "Give Steak",
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/cooked_beef.png"),
                hotkey,
                () -> CursorClientEvents.getLeftClickAction() == GIVE_ACTION,
                () -> false,
                () -> true,
                () -> CursorClientEvents.setLeftClickAction(GIVE_ACTION),
                null,
                List.of(
                        fcs(I18n.get("ability.siege_of_settlements.farm.steak"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(COST),
                        fcs("", Style.EMPTY),
                        fcs(I18n.get("ability.siege_of_settlements.farm.steak.tooltip1"), Style.EMPTY)
                ),
                this,
                placement
        );
    }

    @Override
    public void use(Level level, BuildingPlacement buildingUsing, LivingEntity targetEntity) {
        if (!(buildingUsing instanceof FarmPlacement)) {
            return;
        }

        BlockPos centreBottom = new BlockPos(buildingUsing.centrePos.getX(), buildingUsing.minCorner.getY(), buildingUsing.centrePos.getZ());

        if (!level.isClientSide()) {
            if (targetEntity instanceof ServerPlayer player &&
                player.getName().getString().equals(buildingUsing.ownerName) &&
                canAfford(buildingUsing) &&
                targetEntity.distanceToSqr(Vec3.atCenterOf(centreBottom)) < range * range) {

                giveSteaks(player);
                ResourcesServerEvents.addSubtractResources(new Resources(buildingUsing.ownerName, -COST.food, -COST.wood, -COST.ore));
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
        if (level.isClientSide() || level.getServer() == null) {
            return;
        }
        ServerPlayer player = level.getServer().getPlayerList().getPlayerByName(buildingUsing.ownerName);
        if (player != null) {
            use(level, buildingUsing, player);
        }
    }
}
