package net.WardenMine242.reignofnetherplus.ability.abilities;

import com.solegendary.reignofnether.ability.EquipAbility;
import com.solegendary.reignofnether.building.BuildingPlacement;
import com.solegendary.reignofnether.building.buildings.placements.BlacksmithPlacement;
import com.solegendary.reignofnether.cursor.CursorClientEvents;
import com.solegendary.reignofnether.hud.AbilityButton;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.keybinds.Keybindings;
import com.solegendary.reignofnether.resources.ResourceCosts;
import com.solegendary.reignofnether.unit.UnitAction;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

import java.util.List;

import static com.solegendary.reignofnether.util.MiscUtil.fcs;

public class EquipIronChestplate extends EquipAbility {

    private static final UnitAction EQUIP_ACTION = UnitAction.EQUIP_IRON_ARMOR;

    public EquipIronChestplate() {
        super(EQUIP_ACTION, ResourceCosts.EQUIP_IRON_ARMOR, Items.IRON_CHESTPLATE, EquipmentSlot.CHEST);
        this.defaultHotkey = Keybindings.keyL;
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, BuildingPlacement placement) {
        if (!(placement instanceof BlacksmithPlacement)) return null;
        return new AbilityButton(
                "Iron Chestplate",
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/iron_chestplate.png"),
                hotkey,
                () -> CursorClientEvents.getLeftClickAction() == EQUIP_ACTION || placement.autocast == this,
                () -> false,
                () -> true,
                () -> CursorClientEvents.setLeftClickAction(EQUIP_ACTION),
                null,
                List.of(
                        fcs(I18n.get("ability.reignofnetherplus.equip.iron_chestplate"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        fcs("", Style.EMPTY),
                        fcs(I18n.get("ability.reignofnetherplus.equip.iron_chestplate.tooltip1"), Style.EMPTY),
                        fcs("", Style.EMPTY),
                        fcs(I18n.get("ability.reignofnetherplus.equip.only.player"), Style.EMPTY)
                ),
                this,
                placement
        );
    }

    @Override
    public boolean isCorrectUnit(LivingEntity entity) {
        return entity instanceof Player;
    }
}
