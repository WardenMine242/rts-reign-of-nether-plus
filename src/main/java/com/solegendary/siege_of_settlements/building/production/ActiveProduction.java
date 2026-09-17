package com.solegendary.siege_of_settlements.building.production;

public class ActiveProduction {
    public boolean completed;
    public float ticksLeft;
    public ProductionItem item;
    public ActiveProduction(ProductionItem item, boolean isClientside, String ownerName) {
        this.item = item;
        this.ticksLeft = item.getCost(isClientside, ownerName).ticks;
    }

    public ActiveProduction(ProductionItem item, float ticksLeft, boolean completed) {
        this.item = item;
        this.ticksLeft = Math.max(0, ticksLeft);
        this.completed = completed;
    }
}
