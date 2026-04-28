package com.solegendary.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.buildings.placements.CastlePlacement;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.hud.HudClientEvents;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.villagers.*;
import com.solegendary.siege_of_settlements.util.MiscUtil;
import com.solegendary.siege_of_settlements.util.MyRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

import java.util.List;

public class PromoteIllager extends Ability {

    private static final int CD_MAX = 120 * ResourceCost.TICKS_PER_SECOND;
    private static final int RANGE = 20;
    private static final int BUFF_RANGE = 10;

    private static boolean hasActiveCaptain(CastlePlacement castle) {
        return castle.promotedIllager != null
                && castle.promotedIllager.isAlive()
                && castle.promotedIllager.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof BannerItem;
    }

    public PromoteIllager() {
        super(UnitAction.PROMOTE_ILLAGER, CD_MAX, RANGE, 0, true, true);
        this.defaultHotkey = Keybindings.keyW;
    }

    // checks that the unit has a banner and applies the speed buff to nearby friendly units if it is
    public static void checkAndApplyBuff(LivingEntity entity) {
        if (!entity.level().isClientSide() && entity instanceof Unit captainUnit
            && entity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof BannerItem) {
            List<Mob> nearbyMobs = MiscUtil.getEntitiesWithinRange(new Vector3d(entity.position().x,
                    entity.position().y,
                    entity.position().z
                ),
                BUFF_RANGE,
                Mob.class,
                entity.level()
            );

            for (Mob mob : nearbyMobs)
                if (mob instanceof Unit unit && unit.getOwnerName().equals(captainUnit.getOwnerName()) &&
                    !(mob instanceof RavagerUnit))
                    mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2, 0));
        }
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, BuildingPlacement placement) {
        return new AbilityButton("Promote Illager",
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/items/ominous_banner.png"),
            hotkey,
            () -> false,
            () -> placement instanceof CastlePlacement castle && hasActiveCaptain(castle),
            () -> true,
            () -> CursorClientEvents.setLeftClickAction(UnitAction.PROMOTE_ILLAGER),
            null,
            List.of(
                FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.promote_illager"),
                    Style.EMPTY.withBold(true)
                ),
                FormattedCharSequence.forward(
                    I18n.get("abilities.siege_of_settlements.promote_illager.tooltip1", CD_MAX / 20) + RANGE,
                    MyRenderer.iconStyle
                ),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.promote_illager.tooltip2"),
                    Style.EMPTY
                ),
                FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.promote_illager.tooltip3", BUFF_RANGE),
                    Style.EMPTY
                ),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.promote_illager.tooltip4"), Style.EMPTY)
            ),
            this,
            placement
        );
    }

    @Override
    public float getCooldown(BuildingPlacement placement) {
        if (placement instanceof CastlePlacement castle && !hasActiveCaptain(castle)) {
            return 0;
        }
        return super.getCooldown(placement);
    }

    @Override
    public void use(Level level, BuildingPlacement buildingUsing, LivingEntity targetEntity) {
        if (!(buildingUsing instanceof CastlePlacement))
            return;
        CastlePlacement castle = (CastlePlacement) buildingUsing;

        Vec3 pos = targetEntity.getEyePosition();
        if (buildingUsing.centrePos.distToCenterSqr(pos.x, pos.y, pos.z) > RANGE * RANGE) {
            if (level.isClientSide()) {
                HudClientEvents.showTemporaryMessage(I18n.get("abilities.siege_of_settlements.promote_illager.error1"));
            }
        } else if (targetEntity instanceof VindicatorUnit || targetEntity instanceof PillagerUnit ||
                   targetEntity instanceof EvokerUnit || targetEntity instanceof MilitiaUnit) {

            Unit unit = (Unit) targetEntity;

            if (targetEntity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof BannerItem) {
                if (level.isClientSide()) {
                    HudClientEvents.showTemporaryMessage(I18n.get("abilities.siege_of_settlements.promote_illager.error2"));
                }
                return;
            }
            if (!unit.getOwnerName().equals(buildingUsing.ownerName)) {
                if (level.isClientSide()) {
                    HudClientEvents.showTemporaryMessage(I18n.get("abilities.siege_of_settlements.promote_illager.error3"));
                }
                return;
            }
            // only once promotedIllager allowed at a time
            if (castle.promotedIllager != null && castle.promotedIllager.isAlive() &&
                castle.promotedIllager.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof BannerItem) {
                castle.promotedIllager.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.AIR));
            }
            castle.promotedIllager = targetEntity;
            castle.promotedIllager.setItemSlot(EquipmentSlot.HEAD, Raid.getLeaderBannerInstance());

            // spawn a firework
            if (!level.isClientSide()) {
                MiscUtil.shootFirework(level, castle.promotedIllager.getEyePosition());
            }
            this.setToMaxCooldown(buildingUsing);
        } else {
            if (level.isClientSide()) {
                HudClientEvents.showTemporaryMessage(I18n.get("abilities.siege_of_settlements.promote_illager.error4"));
            }
        }
    }
}
