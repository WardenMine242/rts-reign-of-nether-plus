package com.WardenMine242.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.registrars.EntityRegistrar;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.research.ResearchServerEvents;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.resources.Resources;
import com.solegendary.siege_of_settlements.resources.ResourcesClientEvents;
import com.solegendary.siege_of_settlements.resources.ResourcesServerEvents;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.UnitClientEvents;
import com.solegendary.siege_of_settlements.unit.interfaces.ConvertableUnit;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class UpgradeArcherToElite extends Ability {

    public static final ResourceCost COST = ResourceCosts.UPGRADE_ARCHER_TO_ELITE;

    public UpgradeArcherToElite() {
        super(UnitAction.UPGRADE_ARCHER_TO_ELITE, 20, 0, 0, false, false);
        this.defaultHotkey = Keybindings.keyE;
    }

    private boolean canAfford(Resources resources, ResourceCost cost) {
        if (resources == null) return false;
        return resources.food >= cost.food &&
               resources.wood >= cost.wood &&
               resources.ore >= cost.ore &&
               resources.emerald >= cost.emerald;
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        List<FormattedCharSequence> tooltip = new ArrayList<>();
        tooltip.add(FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.upgrade_to_elite"), Style.EMPTY.withBold(true)));
        tooltip.add(ResourceCosts.getFormattedCost(COST));
        tooltip.add(FormattedCharSequence.forward("", Style.EMPTY));
        tooltip.add(FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.upgrade_to_elite_archer.tooltip1"), Style.EMPTY));

        return new AbilityButton("Upgrade to Elite",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/level_up.png"),
                hotkey,
                () -> false,
                () -> !ResearchClient.hasResearch(ProductionItems.RESEARCH_ELITE_TRAINING),
                () -> canAfford(ResourcesClientEvents.getOwnResources(), COST),
                () -> UnitClientEvents.sendUnitCommand(UnitAction.UPGRADE_ARCHER_TO_ELITE),
                null,
                tooltip,
                this,
                unit
        );
    }

    @Override
    public void use(Level level, Unit unitUsing, BlockPos targetBp) {
        if (level.isClientSide()) return;
        if (!ResearchServerEvents.playerHasResearch(unitUsing.getOwnerName(), ProductionItems.RESEARCH_ELITE_TRAINING)) {
            return;
        }

        LivingEntity entity = (LivingEntity) unitUsing;
        ServerPlayer player = level.getServer().getPlayerList().getPlayerByName(unitUsing.getOwnerName());

        if (player != null) {
            Resources playerRes = null;
            for (Resources res : ResourcesServerEvents.resourcesList) {
                if (res.ownerName.equals(unitUsing.getOwnerName())) {
                    playerRes = res;
                    break;
                }
            }

            if (canAfford(playerRes, COST)) {
                Resources subtraction = new Resources(unitUsing.getOwnerName(), -COST.food, -COST.wood, -COST.ore, -COST.emerald);
                ResourcesServerEvents.addSubtractResources(subtraction);
                if (unitUsing instanceof ConvertableUnit convertable) {
                    convertable.convertToUnit(EntityRegistrar.ELITE_ARCHER_UNIT.get());
                    entity.discard();
                }
            }
        }
    }
}
