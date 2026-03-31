package com.solegendary.siege_of_settlements.building;

import net.minecraft.core.BlockPos;

import java.util.Set;

public interface RangeIndicator {
    public void updateBorderBps();
    public Set<BlockPos> getBorderBps();
    public boolean showOnlyWhenSelected();
}