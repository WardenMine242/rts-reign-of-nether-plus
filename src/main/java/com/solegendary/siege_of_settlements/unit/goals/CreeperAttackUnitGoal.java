package com.solegendary.siege_of_settlements.unit.goals;

import com.solegendary.siege_of_settlements.unit.units.monsters.CreeperUnit;

public class CreeperAttackUnitGoal extends AbstractMeleeAttackUnitGoal {
    private final CreeperUnit creeperUnit;

    public CreeperAttackUnitGoal(CreeperUnit creeperUnit, int attackInterval, boolean followingTargetEvenIfNotSeen) {
        super(creeperUnit, followingTargetEvenIfNotSeen);
        this.creeperUnit = creeperUnit;
    }

    public void tick() {
        if (creeperUnit.canExplodeOnTarget())
            creeperUnit.setSwellDir(1);
    }
}
