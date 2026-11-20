package com.solegendary.siege_of_settlements.building.production;

public class ActiveProduction {
    public boolean completed;
    public float ticksLeft;
    public float totalTicks; // Store initial total for progress calculation
    public ProductionItem item;
    public ActiveProduction(ProductionItem item, boolean isClientside, String ownerName) {
        this.item = item;
        this.ticksLeft = item.getCost(isClientside, ownerName).ticks;
        this.totalTicks = this.ticksLeft;
    }

    public ActiveProduction(ProductionItem item, boolean isClientside, String ownerName, float customTicks) {
        this.item = item;
        this.ticksLeft = customTicks;
        this.totalTicks = customTicks;
    }

    public ActiveProduction(ProductionItem item, float ticksLeft, boolean completed) {
        this.item = item;
        this.ticksLeft = Math.max(0, ticksLeft);
        this.totalTicks = ticksLeft;
        this.completed = completed;
    }

    public ActiveProduction(ProductionItem item, float ticksLeft, float totalTicks, boolean completed) {
        this.item = item;
        this.ticksLeft = Math.max(0, ticksLeft);
        this.totalTicks = totalTicks;
        this.completed = completed;
    }
}
