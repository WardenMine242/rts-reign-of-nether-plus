package com.solegendary.siege_of_settlements.unit.goals;

import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.BuildingUtils;
import com.solegendary.siege_of_settlements.building.buildings.neutral.NeutralTransportPortal;
import com.solegendary.siege_of_settlements.building.buildings.placements.PortalPlacement;
import com.solegendary.siege_of_settlements.hud.HudClientEvents;
import com.solegendary.siege_of_settlements.sounds.SoundAction;
import com.solegendary.siege_of_settlements.sounds.SoundClientboundPacket;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.util.LanguageUtil;
import com.solegendary.siege_of_settlements.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public class UsePortalGoal extends MoveToTargetBlockGoal {

    private BuildingPlacement buildingTarget;

    public UsePortalGoal(Mob mob) {
        super(mob, true, 0);
    }

    public void tick() {
        calcMoveTarget();
        if (buildingTarget instanceof PortalPlacement portal && moveTarget != null) {
            if (buildingTarget.getBlocksPlaced() <= 0) {
                stopUsingPortal();
            }
            if (this.mob.distanceToSqr(new Vec3(moveTarget.getX() + 0.5f,
                moveTarget.getY() + 0.5f,
                moveTarget.getZ() + 0.5f
            )) <= 3f) {
                // teleport to destination
                if (portal.hasDestination()) {
                    BlockPos bp = portal.destination;
                    SoundClientboundPacket.playSoundAtPos(SoundAction.USE_PORTAL, bp);
                    mob.teleportTo(bp.getX() + 0.5f, bp.getY() + 0.5f, bp.getZ() + 0.5f);
                    SoundClientboundPacket.playSoundAtPos(SoundAction.USE_PORTAL, portal.destination);
                }
                this.stopUsingPortal();
            }
        } else {
            this.moveTarget = null;
        }
    }

    private void calcMoveTarget() {
        if (this.buildingTarget instanceof PortalPlacement) {
            this.moveTarget = this.buildingTarget.centrePos;
        }
    }

    public void setBuildingTarget(BlockPos blockPos) {
        if (blockPos != null) {
            if (this.mob.level().isClientSide()) {
                this.buildingTarget = BuildingUtils.findBuilding(true, blockPos);
                if (this.buildingTarget instanceof PortalPlacement portal &&
                    (buildingTarget.ownerName.equals(((Unit) mob).getOwnerName()) ||
                    portal.getBuilding() instanceof NeutralTransportPortal)) {

                    if (portal.hasDestination()) {
                        MiscUtil.addUnitCheckpoint(((Unit) mob), new BlockPos(
                            buildingTarget.centrePos.getX(),
                            buildingTarget.originPos.getY() + 1,
                            buildingTarget.centrePos.getZ()
                        ), true);
                    } else {
                        HudClientEvents.showTemporaryMessage(LanguageUtil.getTranslation("hud.siege_of_settlements.no_destination"));
                    }
                }
            } else {
                this.buildingTarget = BuildingUtils.findBuilding(false, blockPos);
            }
            calcMoveTarget();
            this.start();
        }
    }

    public BuildingPlacement getBuildingTarget() {
        return buildingTarget;
    }

    // if we override stop() it for some reason is called after start() and we can never begin this goal...
    public void stopUsingPortal() {
        buildingTarget = null;
        super.stopMoving();
    }
}
