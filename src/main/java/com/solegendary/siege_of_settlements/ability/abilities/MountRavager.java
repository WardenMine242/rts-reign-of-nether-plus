package com.solegendary.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.hud.HudClientEvents;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.UnitClientEvents;
import com.solegendary.siege_of_settlements.unit.goals.MountGoal;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.monsters.SkeletonUnit;
import com.solegendary.siege_of_settlements.unit.units.monsters.StrayUnit;
import com.solegendary.siege_of_settlements.unit.units.piglins.HeadhunterUnit;
import com.solegendary.siege_of_settlements.unit.units.villagers.PillagerUnit;
import com.solegendary.siege_of_settlements.unit.units.villagers.RavagerUnit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class MountRavager extends Ability {
    public MountRavager() {
        super(UnitAction.MOUNT_RAVAGER, 0, 0, 0, true);
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        Entity entity = (Entity) unit;
        return new AbilityButton("Mount Ravager",
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/ravager.png"),
            hotkey,
            () -> CursorClientEvents.getLeftClickAction() == UnitAction.MOUNT_RAVAGER,
            () -> entity.isPassenger() || !ResearchClient.hasResearch(ProductionItems.RESEARCH_RAVAGER_CAVALRY),
            () -> true,
            () -> CursorClientEvents.setLeftClickAction(UnitAction.MOUNT_RAVAGER),
            () -> UnitClientEvents.sendUnitCommand(UnitAction.MOUNT_RAVAGER),
            List.of(
                FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.mount_ravager"), Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.mount_ravager.tooltip1"), Style.EMPTY)
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

    // right click
    @Override
    public void use(Level level, Unit unitUsing, BlockPos targetBp) {
        MountGoal mountGoal = getMountGoal((Entity) unitUsing);
        if (mountGoal != null)
            mountGoal.autofind = true;
    }

    @Override
    public void use(Level level, Unit unitUsing, LivingEntity targetEntity) {
        MountGoal mountGoal = getMountGoal((Entity) unitUsing);
        if (mountGoal != null && targetEntity instanceof RavagerUnit) {
            mountGoal.setTarget(targetEntity);
        } else if (level.isClientSide()) {
            HudClientEvents.showTemporaryMessage(I18n.get("abilities.siege_of_settlements.mount_ravager.error1"));
        }
    }
}
