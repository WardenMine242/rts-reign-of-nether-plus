package com.WardenMine242.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.research.ResearchServerEvents;
import com.solegendary.siege_of_settlements.registrars.EntityRegistrar;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.resources.Resources;
import com.solegendary.siege_of_settlements.resources.ResourcesClientEvents;
import com.solegendary.siege_of_settlements.resources.ResourcesServerEvents;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.packets.UnitConvertClientboundPacket;
import com.solegendary.siege_of_settlements.unit.units.villagers.VillagerUnit;
import com.solegendary.siege_of_settlements.unit.units.villagers.VillagerUnitProfession;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.solegendary.siege_of_settlements.unit.UnitClientEvents.sendUnitCommand;
import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class VillagerBecomeTrader extends Ability {
    private static final ResourceCost COST = ResourceCosts.BECOME_TRADER;

    public VillagerBecomeTrader() {
        super(
                UnitAction.BECOME_TRADER,
                0,
                0,
                0,
                false
        );
    }

    private boolean canAfford(String ownerName, boolean isClientSide) {
        Resources res = null;
        if (isClientSide) {
            res = ResourcesClientEvents.getOwnResources();
        } else {
            for (Resources resources : ResourcesServerEvents.resourcesList) {
                if (resources.ownerName.equals(ownerName)) {
                    res = resources;
                    break;
                }
            }
        }

        return res != null &&
                res.food >= COST.food &&
                res.wood >= COST.wood &&
                res.ore >= COST.ore &&
                res.emerald >= COST.emerald;
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        if (!(unit instanceof VillagerUnit villagerUnit)) {
            return null;
        }

        return new AbilityButton(
                "Become Trader",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/trader.png"),
                null,
                () -> false,
                () -> !ResearchClient.hasResearch(ProductionItems.RESEARCH_TRADER) || villagerUnit.getUnitProfession() != VillagerUnitProfession.NONE,
                () -> canAfford(villagerUnit.getOwnerName(), true),
                () -> sendUnitCommand(UnitAction.BECOME_TRADER),
                null,
                List.of(
                        fcs(I18n.get("abilities.siege_of_settlements.become_trader"), true),
                        ResourceCosts.getFormattedCost(COST),
                        fcs(""),
                        fcs(I18n.get("abilities.siege_of_settlements.become_trader.tooltip1")),
                        fcs(I18n.get("abilities.siege_of_settlements.become_trader.tooltip2"))
                ),
                this,
                unit
        );
    }

    @Override
    public void use(Level level, Unit unitUsing, BlockPos targetBp) {
        if (level.isClientSide() || !(unitUsing instanceof VillagerUnit villagerUnit)) {
            return;
        }
        if (!ResearchServerEvents.playerHasResearch(villagerUnit.getOwnerName(), ProductionItems.RESEARCH_TRADER)) {
            return;
        }
        if (villagerUnit.converted) {
            return;
        }
        if (!canAfford(villagerUnit.getOwnerName(), false)) {
            return;
        }

        LivingEntity newEntity = villagerUnit.convertToUnit(EntityRegistrar.TRADER_UNIT.get());
        if (newEntity != null) {
            ResourcesServerEvents.addSubtractResources(new Resources(
                    villagerUnit.getOwnerName(),
                    -COST.food,
                    -COST.wood,
                    -COST.ore,
                    -COST.emerald
            ));

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        ParticleTypes.POOF,
                        villagerUnit.getX(),
                        villagerUnit.getY() + 1.0D,
                        villagerUnit.getZ(),
                        36,
                        0.6D,
                        0.6D,
                        0.6D,
                        0.02D
                );
            }
            UnitConvertClientboundPacket.syncConvertedUnits(
                    villagerUnit.getOwnerName(),
                    List.of(villagerUnit.getId()),
                    List.of(newEntity.getId())
            );
            villagerUnit.converted = true;
        }
    }
}
