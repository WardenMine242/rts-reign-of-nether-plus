package com.solegendary.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.ability.AbilityClientboundPacket;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.piglins.BlazeUnit;
import com.solegendary.siege_of_settlements.util.MyRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class FirewallShot extends Ability {

    public static final int CD_MAX_SECONDS = 20;
    public static final int RANGE = 15;

    public FirewallShot() {
        super(UnitAction.SHOOT_FIREWALL, CD_MAX_SECONDS * ResourceCost.TICKS_PER_SECOND, RANGE, 0, true, true);
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        return new AbilityButton("Fire Wall Shot",
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/blocks/fire.png"),
            hotkey,
            () -> CursorClientEvents.getLeftClickAction() == UnitAction.SHOOT_FIREWALL,
            () -> !ResearchClient.hasResearch(ProductionItems.RESEARCH_BLAZE_FIREWALL),
            () -> true,
            () -> CursorClientEvents.setLeftClickAction(UnitAction.SHOOT_FIREWALL),
            null,
            List.of(
                FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.fire_wall_shot"),
                    Style.EMPTY.withBold(true)
                ),
                FormattedCharSequence.forward(
                    I18n.get("abilities.siege_of_settlements.fire_wall_shot.tooltip1", CD_MAX_SECONDS) + RANGE,
                    MyRenderer.iconStyle
                ),
                FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.fire_wall_shot.tooltip2"), Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.fire_wall_shot.tooltip3"), Style.EMPTY)
            ),
            this,
            unit
        );
    }

    @Override
    public void use(Level level, Unit unitUsing, LivingEntity targetEntity) {
        BlockPos bp = targetEntity.getOnPos();
        use(level, unitUsing, bp);
    }

    @Override
    public void use(Level level, Unit unitUsing, BlockPos targetBp) {
        BlazeUnit blazeUnit = (BlazeUnit) unitUsing;
        blazeUnit.shootFirewallShot(targetBp);
        this.setToMaxCooldown(unitUsing);
        if (!level.isClientSide()) {
            AbilityClientboundPacket.sendSetCooldownPacket(blazeUnit.getId(), this.action, this.cooldownMax);
        }
    }
}
