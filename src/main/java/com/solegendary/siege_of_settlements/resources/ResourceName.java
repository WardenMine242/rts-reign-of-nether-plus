package com.solegendary.siege_of_settlements.resources;

// actions that server can take to clients
public enum ResourceName {
    FOOD,
    WOOD,
    ORE,
    EMERALDS,
    NONE;

    public String langKey() {
        return "resources.siege_of_settlements." + this.toString().toLowerCase();
    }
}
