package com.solegendary.siege_of_settlements.fogofwar;

public enum FrozenChunkAction {
    SET_BUILDING_DESTROYED,
    SET_BUILDING_BUILT,
    FREEZE_CHUNK_MANUALLY, // generally used for resources, as buildings are handled in Building classes
    UNMUTE
}
