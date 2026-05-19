package com.solegendary.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.ability.AbilityClientboundPacket;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.registrars.EntityRegistrar;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.UnitClientEvents;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.WardenMine242.siege_of_settlements.unit.units.villagers.RedstoneMineUnit;
import com.solegendary.siege_of_settlements.util.MyRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class Minefield extends Ability {

    private static final int CD_MAX_SECONDS = 60;
    private static final int SPAWN_RADIUS = 5;
    private static final int MINES_TO_SPAWN = 8;
    private static final int ATTEMPTS_PER_MINE = 10;

    public Minefield() {
        super(UnitAction.MINEFIELD, CD_MAX_SECONDS * ResourceCost.TICKS_PER_SECOND, 0, SPAWN_RADIUS, false, true);
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        return new AbilityButton(
                "Minefield",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/redstone_mine.png"),
                hotkey,
                () -> false,
                () -> false,
                () -> true,
                () -> UnitClientEvents.sendUnitCommand(UnitAction.MINEFIELD),
                null,
                List.of(
                        FormattedCharSequence.forward(
                                I18n.get("abilities.siege_of_settlements.minefield"),
                                Style.EMPTY.withBold(true)
                        ),
                        FormattedCharSequence.forward(
                                "\uE004  " + CD_MAX_SECONDS + "s  \uE005  " + SPAWN_RADIUS,
                                MyRenderer.iconStyle
                        ),
                        FormattedCharSequence.forward(
                                I18n.get("abilities.siege_of_settlements.minefield.tooltip1", MINES_TO_SPAWN),
                                Style.EMPTY
                        ),
                        FormattedCharSequence.forward(
                                I18n.get("abilities.siege_of_settlements.minefield.tooltip2"),
                                Style.EMPTY
                        )
                ),
                this,
                unit
        );
    }

    @Override
    public void use(Level level, Unit unitUsing, BlockPos targetBp) {
        if (level.isClientSide()) {
            if (this.isOffCooldown(unitUsing)) {
                this.setToMaxCooldown(unitUsing);
            }
            return;
        }

        if (!(level instanceof ServerLevel serverLevel) || !this.isOffCooldown(unitUsing)) {
            return;
        }

        Entity caster = (Entity) unitUsing;
        BlockPos center = caster.blockPosition();
        RandomSource random = serverLevel.getRandom();

        for (int i = 0; i < MINES_TO_SPAWN; i++) {
            BlockPos spawnPos = findMineSpawnPos(serverLevel, center, random);
            if (spawnPos == null) {
                continue;
            }

            RedstoneMineUnit mine = EntityRegistrar.REDSTONE_MINE_UNIT.get().create(serverLevel);
            if (mine == null) {
                continue;
            }

            mine.moveTo(
                    spawnPos.getX() + 0.5D,
                    spawnPos.getY(),
                    spawnPos.getZ() + 0.5D,
                    random.nextFloat() * 360.0F,
                    0.0F
            );
            mine.setOwnerName(unitUsing.getOwnerName());
            serverLevel.addFreshEntity(mine);
        }

        this.setToMaxCooldown(unitUsing);
        AbilityClientboundPacket.sendSetCooldownPacket(caster.getId(), this.action, this.cooldownMax);
    }

    @Nullable
    private BlockPos findMineSpawnPos(ServerLevel level, BlockPos center, RandomSource random) {
        for (int attempt = 0; attempt < ATTEMPTS_PER_MINE; attempt++) {
            double angle = random.nextDouble() * Math.PI * 2.0D;
            double distance = Math.sqrt(random.nextDouble()) * SPAWN_RADIUS;
            int x = Mth.floor(center.getX() + Math.cos(angle) * distance);
            int z = Mth.floor(center.getZ() + Math.sin(angle) * distance);

            int maxY = Math.min(level.getMaxBuildHeight() - 2, center.getY() + 6);
            int minY = Math.max(level.getMinBuildHeight() + 1, center.getY() - 6);
            for (int y = maxY; y >= minY; y--) {
                BlockPos candidate = new BlockPos(x, y, z);
                if (level.getBlockState(candidate.below()).isAir()) {
                    continue;
                }
                if (!level.getFluidState(candidate).isEmpty()) {
                    continue;
                }
                BlockState candidateState = level.getBlockState(candidate);
                boolean hasCollision = !candidateState.getCollisionShape(level, candidate).isEmpty();
                // Let mines spawn through replaceable plants (e.g. grass) instead of treating them as hard blockers.
                if (hasCollision && !candidateState.canBeReplaced()) {
                    continue;
                }
                return candidate;
            }
        }
        return null;
    }
}
