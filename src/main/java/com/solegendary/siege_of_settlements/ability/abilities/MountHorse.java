package com.solegendary.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.hud.HudClientEvents;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.UnitClientEvents;
import com.solegendary.siege_of_settlements.unit.goals.MountGoal;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.monsters.SkeletonUnit;
import com.solegendary.siege_of_settlements.unit.units.monsters.StrayUnit;
import com.solegendary.siege_of_settlements.unit.units.piglins.HeadhunterUnit;
import com.solegendary.siege_of_settlements.unit.units.villagers.PillagerUnit;
import net.WardenMine242.siege_of_settlements.unit.units.villagers.HorseUnit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class MountHorse extends Ability {
    private static final String NAME_KEY = "abilities.siege_of_settlements.mount_horse";
    private static final String TOOLTIP1_KEY = "abilities.siege_of_settlements.mount_horse.tooltip1";
    private static final String ERROR_KEY = "abilities.siege_of_settlements.mount_horse.error1";

    public MountHorse() {
        super(UnitAction.MOUNT_HORSE, 0, 0, 0, true);
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        Entity entity = (Entity) unit;
        return new AbilityButton(
                I18n.get(NAME_KEY),
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/saddle.png"),
                hotkey,
                () -> CursorClientEvents.getLeftClickAction() == UnitAction.MOUNT_HORSE,
                entity::isPassenger,
                () -> true,
                () -> CursorClientEvents.setLeftClickAction(UnitAction.MOUNT_HORSE),
                () -> UnitClientEvents.sendUnitCommand(UnitAction.MOUNT_HORSE),
                List.of(
                        FormattedCharSequence.forward(I18n.get(NAME_KEY), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get(TOOLTIP1_KEY), Style.EMPTY)
                ),
                this,
                unit
        );
    }

    private MountGoal getMountGoal(Entity entity) {
        if (entity instanceof PillagerUnit pillagerUnit) {
            return pillagerUnit.getMountGoal();
        }
        if (entity instanceof StrayUnit strayUnit) {
            return strayUnit.getMountGoal();
        }
        if (entity instanceof SkeletonUnit skeletonUnit) {
            return skeletonUnit.getMountGoal();
        }
        if (entity instanceof HeadhunterUnit headhunterUnit) {
            return headhunterUnit.getMountGoal();
        }
        return null;
    }

    @Override
    public void use(Level level, Unit unitUsing, BlockPos targetBp) {
        MountGoal mountGoal = getMountGoal((Entity) unitUsing);
        if (mountGoal != null) {
            mountGoal.setTargetType(MountGoal.TargetType.HORSE);
            mountGoal.autofind = true;
        }
    }

    @Override
    public void use(Level level, Unit unitUsing, LivingEntity targetEntity) {
        MountGoal mountGoal = getMountGoal((Entity) unitUsing);
        if (mountGoal != null && targetEntity instanceof HorseUnit) {
            mountGoal.setTargetType(MountGoal.TargetType.HORSE);
            mountGoal.setTarget(targetEntity);
        } else if (level.isClientSide()) {
            HudClientEvents.showTemporaryMessage(I18n.get(ERROR_KEY));
        }
    }
}
