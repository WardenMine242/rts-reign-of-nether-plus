package com.solegendary.siege_of_settlements.hud.passives;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.abilities.EnchantMaiming;
import com.solegendary.siege_of_settlements.ability.abilities.EnchantVigor;
import com.solegendary.siege_of_settlements.registrars.EnchantmentRegistrar;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;



public class PassiveIcons {

    private static ItemStack getEnchantedItemStack(Item item) {
        ItemStack itemStack = new ItemStack(item);
        itemStack.enchant(Enchantments.UNBREAKING, 1);
        return itemStack;
    }

    public static final EnchantmentIcon MULTISHOT = new EnchantmentIcon(
            Enchantments.MULTISHOT,
            EquipmentSlot.MAINHAND,
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/abilities/multishot.png"),
            List.of(
                    fcs(I18n.get("hud.enchant.siege_of_settlements.multishot"), true),
                    fcs(I18n.get("hud.enchant.siege_of_settlements.multishot.tooltip"))
            )
    );
    public static final EnchantmentIcon QUICK_CHARGE = new EnchantmentIcon(
            Enchantments.QUICK_CHARGE,
            EquipmentSlot.MAINHAND,
            getEnchantedItemStack(Items.CROSSBOW),
            List.of(
                    fcs(I18n.get("hud.enchant.siege_of_settlements.quickshot"), true),
                    fcs(I18n.get("hud.enchant.siege_of_settlements.quickshot.tooltip"))
            )
    );
    public static final EnchantmentIcon MAIMING = new EnchantmentIcon(
            EnchantmentRegistrar.MAIMING.get(),
            EquipmentSlot.MAINHAND,
            getEnchantedItemStack(Items.IRON_AXE),
            List.of(
                    fcs(I18n.get("hud.enchant.siege_of_settlements.maiming"), true),
                    fcs(I18n.get("hud.enchant.siege_of_settlements.maiming.tooltip"))
            )
    );
    public static final EnchantmentIcon SHARPNESS = new EnchantmentIcon(
            Enchantments.SHARPNESS,
            EquipmentSlot.MAINHAND,
            getEnchantedItemStack(Items.IRON_SWORD),
            List.of(
                    fcs(I18n.get("hud.enchant.siege_of_settlements.sharpness"), true),
                    fcs(I18n.get("hud.enchant.siege_of_settlements.sharpness.tooltip"))
            )
    );
    public static final EnchantmentIcon VIGOR = new EnchantmentIcon(
            EnchantmentRegistrar.VIGOR.get(),
            EquipmentSlot.MAINHAND,
            getEnchantedItemStack(Items.STICK),
            List.of(
                    fcs(I18n.get("hud.enchant.siege_of_settlements.vigor"), true),
                    fcs(I18n.get("hud.enchant.siege_of_settlements.vigor.tooltip"))
            )
    );
    public static final EnchantmentIcon EFFICIENCY = new EnchantmentIcon(
            Enchantments.BLOCK_EFFICIENCY,
            EquipmentSlot.MAINHAND,
            getEnchantedItemStack(Items.IRON_PICKAXE),
            List.of(
                    fcs(I18n.get("hud.enchant.siege_of_settlements.efficiency"), true),
                    fcs(I18n.get("hud.enchant.siege_of_settlements.efficiency.tooltip"))
            )
    );
    public static final EnchantmentIcon FORTIFYING = new EnchantmentIcon(
            EnchantmentRegistrar.FORTYIFYING.get(),
            EquipmentSlot.CHEST,
            getEnchantedItemStack(Items.IRON_CHESTPLATE),
            List.of(
                    fcs(I18n.get("hud.enchant.siege_of_settlements.fortifying"), true),
                    fcs(I18n.get("hud.enchant.siege_of_settlements.fortifying.tooltip"))
            )
    );
    public static final EnchantmentIcon POWER = new EnchantmentIcon(
            Enchantments.POWER_ARROWS,
            EquipmentSlot.MAINHAND,
            getEnchantedItemStack(Items.BOW),
            List.of(
                    fcs(I18n.get("hud.enchant.siege_of_settlements.power"), true),
                    fcs(I18n.get("hud.enchant.siege_of_settlements.power.tooltip"))
            )
    );
    public static final EnchantmentIcon ZEAL = new EnchantmentIcon(
            EnchantmentRegistrar.ZEAL.get(),
            EquipmentSlot.MAINHAND,
            getEnchantedItemStack(Items.NETHER_STAR),
            List.of(
                    fcs(I18n.get("hud.enchant.siege_of_settlements.zeal"), true),
                    fcs(I18n.get("hud.enchant.siege_of_settlements.zeal.tooltip"))
            )
    );
    public static final EnchantmentIcon PIERCING = new EnchantmentIcon(
            Enchantments.PIERCING,
            EquipmentSlot.MAINHAND,
            getEnchantedItemStack(Items.ARROW),
            List.of(
                    fcs(I18n.get("hud.enchant.siege_of_settlements.piercing"), true),
                    fcs(I18n.get("hud.enchant.siege_of_settlements.piercing.tooltip"))
            )
    );
    public static final EnchantmentIcon BREACHING = new EnchantmentIcon(
            EnchantmentRegistrar.BREACHING.get(),
            EquipmentSlot.MAINHAND,
            getEnchantedItemStack(Items.DIAMOND_AXE),
            List.of(
                    fcs(I18n.get("hud.enchant.siege_of_settlements.breaching"), true),
                    fcs(I18n.get("hud.enchant.siege_of_settlements.breaching.tooltip"))
            )
    );
    public static final EnchantmentIcon THORNS = new EnchantmentIcon(
            Enchantments.THORNS,
            EquipmentSlot.CHEST,
            getEnchantedItemStack(Items.CHAINMAIL_CHESTPLATE),
            List.of(
                    fcs(I18n.get("hud.enchant.siege_of_settlements.thorns"), true),
                    fcs(I18n.get("hud.enchant.siege_of_settlements.thorns.tooltip"))
            )
    );
    public static final EnchantmentIcon FIRE_ASPECT = new EnchantmentIcon(
            Enchantments.FIRE_ASPECT,
            EquipmentSlot.MAINHAND,
            getEnchantedItemStack(Items.NETHERITE_SWORD),
            List.of(
                    fcs(I18n.get("hud.enchant.siege_of_settlements.fire_aspect"), true),
                    fcs(I18n.get("hud.enchant.siege_of_settlements.fire_aspect.tooltip"))
            )
    );
    public static final EnchantmentIcon FLAME = new EnchantmentIcon(
            Enchantments.FLAMING_ARROWS,
            EquipmentSlot.MAINHAND,
            getEnchantedItemStack(Items.TRIDENT),
            List.of(
                    fcs(I18n.get("hud.enchant.siege_of_settlements.flame"), true),
                    fcs(I18n.get("hud.enchant.siege_of_settlements.flame.tooltip"))
            )
    );

    public static final EnchantmentIcon ENCHANTMENT_AMPLIFIER = new EnchantmentIcon(
            null,
            EquipmentSlot.MAINHAND,
            getEnchantedItemStack(Items.ENCHANTED_BOOK),
            List.of(
                    fcs(I18n.get("hud.enchant.siege_of_settlements.march_of_progress"), true),
                    fcs(I18n.get("hud.enchant.siege_of_settlements.march_of_progress.tooltip"))
            )
    );

    public static final List<EnchantmentIcon> ENCHANTMENT_ICONS = List.of(
            MULTISHOT,
            QUICK_CHARGE,
            MAIMING,
            SHARPNESS,
            VIGOR,
            ZEAL,
            //EFFICIENCY,
            FORTIFYING,
            POWER,
            PIERCING,
            BREACHING,
            THORNS,
            FIRE_ASPECT,
            FLAME
    );
}
