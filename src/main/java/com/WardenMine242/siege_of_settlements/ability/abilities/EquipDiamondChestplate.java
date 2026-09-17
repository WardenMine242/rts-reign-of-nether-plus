package com.WardenMine242.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.ability.EquipAbility;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.buildings.placements.BlacksmithPlacement;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.registrars.EntityRegistrar;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class EquipDiamondChestplate extends EquipAbility {

    private static final UnitAction EQUIP_ACTION = UnitAction.EQUIP_DIAMOND_ARMOR;
    private static final int REQUIRED_BLACKSMITH_TIER = 2;

    public EquipDiamondChestplate() {
        super(EQUIP_ACTION, ResourceCosts.EQUIP_DIAMOND_ARMOR, Items.DIAMOND_CHESTPLATE, EquipmentSlot.CHEST);
        this.defaultHotkey = Keybindings.keyG;
    }

    private static boolean isUnlocked(BuildingPlacement placement) {
        return placement instanceof BlacksmithPlacement blacksmith &&
                blacksmith.getBlacksmithTier() >= REQUIRED_BLACKSMITH_TIER;
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, BuildingPlacement placement) {
        if (!(placement instanceof BlacksmithPlacement)) return null;
        return new AbilityButton(
                "Diamond Chestplate",
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/diamond_chestplate.png"),
                hotkey,
                () -> CursorClientEvents.getLeftClickAction() == EQUIP_ACTION || placement.autocast == this,
                () -> !isUnlocked(placement),
                () -> isUnlocked(placement),
                () -> CursorClientEvents.setLeftClickAction(EQUIP_ACTION),
                null,
                List.of(
                        fcs(I18n.get("ability.siege_of_settlements.equip.diamond_chestplate"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        fcs("", Style.EMPTY),
                        fcs(I18n.get("ability.siege_of_settlements.equip.diamond_chestplate.tooltip1"), Style.EMPTY),
                        fcs("", Style.EMPTY),
                        fcs(I18n.get("ability.siege_of_settlements.equip.only.player_or_unit"), Style.EMPTY)
                ),
                this,
                placement
        );
    }

    @Override
    public void use(Level level, BuildingPlacement buildingUsing, LivingEntity targetEntity) {
        if (!isUnlocked(buildingUsing)) {
            return;
        }
        super.use(level, buildingUsing, targetEntity);
    }

    @Override
    public boolean isCorrectUnit(LivingEntity entity) {
        if (entity instanceof Player) {
            return true;
        }
        return List.of(
                EntityRegistrar.MILITIA_UNIT.get(),
                EntityRegistrar.VINDICATOR_UNIT.get(),
                EntityRegistrar.PILLAGER_UNIT.get(),
                EntityRegistrar.ELITE_ARMORED_PILLAGER_UNIT.get(),
                EntityRegistrar.EVOKER_UNIT.get()
        ).contains(entity.getType());
    }
}
