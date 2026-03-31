package com.solegendary.siege_of_settlements.survival;

import com.solegendary.siege_of_settlements.building.buildings.placements.PortalPlacement;
import com.solegendary.siege_of_settlements.registrars.EntityRegistrar;
import com.solegendary.siege_of_settlements.survival.spawners.PiglinWaveSpawner;
import com.solegendary.siege_of_settlements.unit.UnitServerEvents;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.piglins.GhastUnit;
import com.solegendary.siege_of_settlements.unit.units.piglins.HoglinUnit;
import com.solegendary.siege_of_settlements.unit.units.piglins.MagmaCubeUnit;
import com.solegendary.siege_of_settlements.player.PlayerServerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.solegendary.siege_of_settlements.survival.SurvivalServerEvents.ENEMY_OWNER_NAME;
import static com.solegendary.siege_of_settlements.survival.spawners.PiglinWaveSpawner.checkAndApplyArmour;
import static com.solegendary.siege_of_settlements.survival.spawners.WaveSpawner.getModifiedPopCost;

public class WavePortal {

    private static final int SPAWN_TICKS_MAX = 600;
    private static int spawnTicks = 0;

    public final PortalPlacement portal;
    public final Wave wave;
    private int initialSpawnPop;

    private BlockPos lastOnPos;

    public WavePortal(PortalPlacement portal, Wave wave) {
        this.portal = portal;
        this.portal.selfBuilding = true;
        this.wave = wave;
        this.initialSpawnPop = (wave.population / wave.getNumPortals()) / 2;
    }

    public PortalPlacement getPortal() {
        return portal;
    }

    public void tick(long ticksToAdd) {
        if (!portal.isBuilt)
            return;

        if (initialSpawnPop > 0) {
            doSpawn();
        } else {
            int pop = SurvivalServerEvents.getTotalEnemyPopulation();
            int playerCount = Math.max(1, PlayerServerEvents.rtsPlayers.size());
            if (pop > wave.population * 2.0f * playerCount) {
                return;
            }
            else if (spawnTicks >= SPAWN_TICKS_MAX) {
                spawnTicks = 0;
                doSpawn();
            } else {
                spawnTicks += ticksToAdd;
            }
        }
    }

    public void doSpawn() {
        Random random = new Random();

        EntityType<? extends Unit> mobType = (EntityType<? extends Unit>) PiglinWaveSpawner.getRandomUnitOfTier(wave.highestUnitTier);

        ServerLevel level = (ServerLevel) portal.getLevel();

        // produceUnit spawns them before applying the ownerName, meaning they aren't registered as WaveEnemies automatically
        Entity entity = portal.produceUnit(level, mobType, ENEMY_OWNER_NAME, true);

        if (entity instanceof GhastUnit ghastUnit)
            ghastUnit.move(MoverType.SELF, new Vec3(0,10,0));

        if (entity instanceof Unit unit) {
            checkAndApplyArmour((LivingEntity) unit, wave.highestUnitTier);

            if (wave.highestUnitTier >= 3 && random.nextBoolean() && entity instanceof HoglinUnit hoglinUnit) {
                Entity entityPassenger = UnitServerEvents.spawnMob(EntityRegistrar.HEADHUNTER_UNIT.get(),
                        level, hoglinUnit.getOnPos(), ENEMY_OWNER_NAME);
                if (entityPassenger instanceof Unit pUnit) {
                    entityPassenger.startRiding(hoglinUnit);
                    if (initialSpawnPop > 0)
                        initialSpawnPop -= getModifiedPopCost(unit);
                }
            }

            if (unit instanceof MagmaCubeUnit magmaCubeUnit)
                magmaCubeUnit.setSize(wave.highestUnitTier, true);

            List<Unit> enemies = new ArrayList<>();
            for (WaveEnemy e : SurvivalServerEvents.getCurrentEnemies()) {
                Unit unit1 = e.unit;
                if (unit1.equals(unit)) continue;
                SurvivalServerEvents.getCurrentEnemies().add(new WaveEnemy(unit));
                break;
            }
            if (initialSpawnPop > 0)
                initialSpawnPop -= getModifiedPopCost(unit);
        }
    }
}
