package com.solegendary.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.villagers.WitchUnit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;
import static com.solegendary.siege_of_settlements.util.MiscUtil.fcsIcons;

public class ThrowWaterPotion extends Ability {

    public static final int CD_MAX_SECONDS = 5;

    public final Potion potion = Potions.WATER;

    //TODO Fix potionThrowRange for Witches gathering on a building
    public ThrowWaterPotion(int potionThrowRange) {
        super(
            UnitAction.THROW_WATER_POTION,
            CD_MAX_SECONDS * ResourceCost.TICKS_PER_SECOND,
            potionThrowRange,
            0,
            true,
            true
        );
        this.autocastEnableAction = UnitAction.THROW_WATER_POTION_AUTOCAST_ENABLE;
        this.autocastDisableAction = UnitAction.THROW_WATER_POTION_AUTOCAST_DISABLE;
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        if (!(unit instanceof WitchUnit witchUnit))
            return null;
        return new AbilityButton(
            "Water Potion",
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/items/splash_potion_water.png"),
            hotkey,
            () -> CursorClientEvents.getLeftClickAction() == UnitAction.THROW_WATER_POTION || isAutocasting(unit),
            () -> !ResearchClient.hasResearch(ProductionItems.RESEARCH_WATER_POTIONS),
            () -> true,
            () -> CursorClientEvents.setLeftClickAction(UnitAction.THROW_WATER_POTION),
            () -> toggleAutocast(unit),
            List.of(
                fcs(I18n.get("abilities.siege_of_settlements.water_potion"), true),
                fcsIcons(I18n.get("abilities.siege_of_settlements.water_potion.tooltip1", CD_MAX_SECONDS, witchUnit.getPotionThrowRange())),
                fcs(I18n.get("abilities.siege_of_settlements.water_potion.tooltip2")),
                fcs(I18n.get("abilities.siege_of_settlements.autocast"))
            ),
            this,
            unit
        );
    }

    @Override
    public void use(Level level, Unit unitUsing, BlockPos targetBp) {
        ((WitchUnit) unitUsing).getThrowPotionGoal().setPotion(potion);
        ((WitchUnit) unitUsing).getThrowPotionGoal().setTarget(targetBp);
    }

    @Override
    public void use(Level level, Unit unitUsing, LivingEntity targetEntity) {
        ((WitchUnit) unitUsing).getThrowPotionGoal().setPotion(Potions.WATER);
        ((WitchUnit) unitUsing).getThrowPotionGoal().setTarget(targetEntity);
    }
}
