package com.solegendary.siege_of_settlements.unit.goals;

import com.solegendary.siege_of_settlements.hud.HudClientEvents;
import com.solegendary.siege_of_settlements.unit.UnitServerEvents;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.monsters.PoisonSpiderUnit;
import com.solegendary.siege_of_settlements.unit.units.monsters.SkeletonUnit;
import com.solegendary.siege_of_settlements.unit.units.monsters.SpiderUnit;
import com.solegendary.siege_of_settlements.unit.units.monsters.StrayUnit;
import com.solegendary.siege_of_settlements.unit.units.piglins.HeadhunterUnit;
import com.solegendary.siege_of_settlements.unit.units.piglins.HoglinUnit;
import com.solegendary.siege_of_settlements.unit.units.villagers.PillagerUnit;
import com.solegendary.siege_of_settlements.unit.units.villagers.RavagerUnit;
import com.solegendary.siege_of_settlements.util.MiscUtil;
import com.solegendary.siege_of_settlements.util.MyMath;
import net.WardenMine242.siege_of_settlements.unit.units.villagers.HorseUnit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.joml.Vector3d;

import java.util.List;

// allows a unit to be able to mount any target as long as their ability allows them to
public class MountGoal extends MoveToTargetBlockGoal {

    public enum TargetType {
        AUTO,
        RAVAGER,
        HORSE,
        SPIDER,
        HOGLIN
    }

    public static final float SEARCH_RANGE = 30;
    public static final float RANGE = 2;
    private LivingEntity targetEntity = null;
    public boolean autofind = false;
    private TargetType targetType = TargetType.AUTO;

    public MountGoal(Mob mob) {
        super(mob, false, 0);
    }

    public void setTarget(LivingEntity entity) {
        this.targetEntity = entity;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType == null ? TargetType.AUTO : targetType;
    }

    private boolean isTypeAllowed(Mob m) {
        return switch (this.targetType) {
            case AUTO -> true;
            case RAVAGER -> m instanceof RavagerUnit;
            case HORSE -> m instanceof HorseUnit;
            case SPIDER -> m instanceof SpiderUnit || m instanceof PoisonSpiderUnit;
            case HOGLIN -> m instanceof HoglinUnit;
        };
    }

    private boolean isMountableUnit(Mob m) {
        if (!(m instanceof Unit unit))
            return false;

        if (m.isVehicle())
            return false;
        else if (!unit.getOwnerName().equals(((Unit) this.mob).getOwnerName()))
            return false;
        else if (!isTypeAllowed(m))
            return false;
        else return (this.mob instanceof PillagerUnit && (m instanceof RavagerUnit || m instanceof HorseUnit)) ||
                    (this.mob instanceof SkeletonUnit && (m instanceof SpiderUnit || m instanceof PoisonSpiderUnit)) ||
                    (this.mob instanceof StrayUnit && (m instanceof SpiderUnit || m instanceof PoisonSpiderUnit)) ||
                    (this.mob instanceof HeadhunterUnit && m instanceof HoglinUnit);
    }

    public void setNearestTarget() {
        if (this.mob.level().isClientSide())
            return;

        for (LivingEntity entity : UnitServerEvents.getAllUnits()) {
            if (this.mob instanceof Unit) {
                // find the closest mob
                double closestDist = Double.MAX_VALUE;
                Mob closestMob = null;
                for (Mob pfMob : MiscUtil.getEntitiesWithinRange(
                        new Vector3d(this.mob.position().x, this.mob.position().y, this.mob.position().z),
                        SEARCH_RANGE, Mob.class, this.mob.level())) {
                    if (isMountableUnit(pfMob)) {
                        double dist = mob.position().distanceTo(pfMob.position());
                        if (dist < closestDist) {
                            closestDist = mob.position().distanceTo(pfMob.position());
                            closestMob = pfMob;
                        }
                    }
                }
                if (closestMob != null)
                    setTarget(closestMob);
                else
                    stop();
            }
        }
    }

    @Override
    public void tick() {
        if (autofind)
            setNearestTarget();

        if (this.targetEntity != null)
            this.setMoveTarget(this.targetEntity.getOnPos());

        if (moveTarget != null &&
            this.mob instanceof Unit unit1 &&
            this.targetEntity instanceof Unit unit2) {
            if (MyMath.distance(
                    this.mob.getX(), this.mob.getZ(),
                    moveTarget.getX(), moveTarget.getZ()) <= RANGE &&
                unit1.getOwnerName().equals(unit2.getOwnerName())) {
                Unit.resetBehaviours(unit1);
                this.mob.startRiding(targetEntity);

                if (this.mob.level().isClientSide())
                    HudClientEvents.removeFromControlGroups(this.mob.getId());

                this.stop();
            }
        }
    }

    @Override
    public void stop() {
        this.autofind = false;
        this.stopMoving();
        this.setTarget(null);
        this.setTargetType(TargetType.AUTO);
    }
}
