package com.WardenMine242.siege_of_settlements.ability.abilities;

import com.WardenMine242.siege_of_settlements.unit.units.villagers.ArmoredRavagerUnit;
import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.EnchantEquipAbilityServerboundPacket;
import com.solegendary.siege_of_settlements.ability.EquipAbility;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.buildings.placements.BlacksmithPlacement;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.hud.HudClientEvents;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.registrars.EntityRegistrar;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.resources.Resources;
import com.solegendary.siege_of_settlements.resources.ResourcesServerEvents;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.packets.UnitConvertClientboundPacket;
import com.solegendary.siege_of_settlements.unit.units.villagers.RavagerUnit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class EquipRavagerArmor extends EquipAbility {
    private static final UnitAction EQUIP_ACTION = UnitAction.EQUIP_RAVAGER_ARMOR;
    private static final int REQUIRED_BLACKSMITH_TIER = 2;

    public EquipRavagerArmor() {
        super(EQUIP_ACTION, ResourceCosts.EQUIP_RAVAGER_ARMOR, Items.NETHERITE_CHESTPLATE, EquipmentSlot.CHEST);
        this.defaultHotkey = Keybindings.keyV;
    }

    private static boolean isUnlocked(BuildingPlacement placement) {
        return placement instanceof BlacksmithPlacement blacksmith &&
                blacksmith.getBlacksmithTier() >= REQUIRED_BLACKSMITH_TIER;
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, BuildingPlacement placement) {
        if (!(placement instanceof BlacksmithPlacement blacksmith)) return null;
        return new AbilityButton(
                "Ravager Armor",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/abilities/ravager_armor.png"),
                hotkey,
                () -> CursorClientEvents.getLeftClickAction() == EQUIP_ACTION || blacksmith.autoCastEquip == this,
                () -> !isUnlocked(placement),
                () -> isUnlocked(placement),
                () -> CursorClientEvents.setLeftClickAction(EQUIP_ACTION),
                () -> {
                    EnchantEquipAbilityServerboundPacket.setAutocastEnchantOrEquip(EQUIP_ACTION, blacksmith.originPos);
                    if (blacksmith.autoCastEquip == this)
                        blacksmith.autoCastEquip = null;
                    else
                        blacksmith.autoCastEquip = this;
                },
                List.of(
                        fcs(I18n.get("ability.siege_of_settlements.equip.ravager_armor"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        fcs("", Style.EMPTY),
                        fcs(I18n.get("ability.siege_of_settlements.equip.ravager_armor.tooltip1"), Style.EMPTY),
                        fcs(I18n.get("ability.siege_of_settlements.equip.ravager_armor.tooltip2"), Style.EMPTY),
                        fcs("", Style.EMPTY),
                        fcs(I18n.get("abilities.siege_of_settlements.autocast"), Style.EMPTY)
                ),
                this,
                placement
        );
    }

    @Override
    public boolean isCorrectUnit(LivingEntity entity) {
        return entity.getType() == EntityRegistrar.RAVAGER_UNIT.get();
    }

    @Override
    public boolean hasSameItem(LivingEntity entity) {
        return entity instanceof ArmoredRavagerUnit || entity.getType() == EntityRegistrar.ARMORED_RAVAGER_UNIT.get();
    }

    @Override
    public boolean hasItemInSlot(LivingEntity entity) {
        return hasSameItem(entity);
    }

    @Override
    public void use(Level level, BuildingPlacement buildingUsing, LivingEntity targetEntity) {
        if (!(buildingUsing instanceof BlacksmithPlacement) || !isUnlocked(buildingUsing)) {
            return;
        }

        BlockPos centreBottom = new BlockPos(
                buildingUsing.centrePos.getX(),
                buildingUsing.minCorner.getY(),
                buildingUsing.centrePos.getZ()
        );

        if (!level.isClientSide()) {
            if (isOwnedTarget(targetEntity, buildingUsing) &&
                    isCorrectUnit(targetEntity) &&
                    !hasSameItem(targetEntity) &&
                    canAfford(buildingUsing) &&
                    targetEntity.distanceToSqr(Vec3.atCenterOf(centreBottom)) < RANGE * RANGE &&
                    targetEntity instanceof RavagerUnit ravagerUnit) {
                LivingEntity newEntity = ravagerUnit.convertToUnit(EntityRegistrar.ARMORED_RAVAGER_UNIT.get());
                if (newEntity != null) {
                    ResourcesServerEvents.addSubtractResources(new Resources(
                            buildingUsing.ownerName,
                            -cost.food,
                            -cost.wood,
                            -cost.ore,
                            -cost.emerald
                    ));
                    setToMaxCooldown(buildingUsing);
                    playEffects(level, ravagerUnit);
                    UnitConvertClientboundPacket.syncConvertedUnits(
                            buildingUsing.ownerName,
                            List.of(ravagerUnit.getId()),
                            List.of(newEntity.getId())
                    );
                    ravagerUnit.setShouldDiscard(true);
                }
            }
            return;
        }

        if (!isOwnedTarget(targetEntity, buildingUsing)) {
            HudClientEvents.showTemporaryMessage(I18n.get("ability.siege_of_settlements.equip.error1"));
        } else if (targetEntity.distanceToSqr(Vec3.atCenterOf(centreBottom)) >= RANGE * RANGE) {
            HudClientEvents.showTemporaryMessage(I18n.get("ability.siege_of_settlements.equip.error2"));
        } else if (hasSameItem(targetEntity)) {
            HudClientEvents.showTemporaryMessage(I18n.get("ability.siege_of_settlements.equip.ravager_armor.error2"));
        } else if (!isCorrectUnit(targetEntity)) {
            HudClientEvents.showTemporaryMessage(I18n.get("ability.siege_of_settlements.equip.ravager_armor.error1"));
        } else if (!canAfford(buildingUsing)) {
            HudClientEvents.showTemporaryMessage(I18n.get("ability.siege_of_settlements.equip.error5"));
        } else {
            setToMaxCooldown(buildingUsing);
            playEffects(level, targetEntity);
        }
    }

    private boolean isOwnedTarget(LivingEntity entity, BuildingPlacement buildingUsing) {
        if (entity instanceof Unit unit) {
            return unit.getOwnerName().equals(buildingUsing.ownerName);
        }
        if (entity instanceof Player player) {
            return player.getName().getString().equals(buildingUsing.ownerName);
        }
        return false;
    }

    private void playEffects(Level level, LivingEntity entity) {
        level.playLocalSound(
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                SoundEvents.SMITHING_TABLE_USE,
                entity.getSoundSource(),
                1.0F + entity.getRandom().nextFloat(),
                entity.getRandom().nextFloat() * 0.7F + 0.3F,
                false
        );
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.POOF,
                    entity.getX(),
                    entity.getY() + 1.0D,
                    entity.getZ(),
                    36,
                    0.6D,
                    0.6D,
                    0.6D,
                    0.02D
            );
        }
    }
}
