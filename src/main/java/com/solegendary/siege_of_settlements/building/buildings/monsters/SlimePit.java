package com.solegendary.siege_of_settlements.building.buildings.monsters;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlaceButton;
import com.solegendary.siege_of_settlements.building.Buildings;
import com.solegendary.siege_of_settlements.building.production.ProductionBuilding;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.faction.Faction;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class SlimePit extends ProductionBuilding {

    public final static String buildingName = "Slime Pit";
    public final static String structureName = "slime_pit";
    public final static ResourceCost cost = ResourceCosts.SLIME_PIT;

    public SlimePit() {
        super(structureName, cost, false);
        this.name = buildingName;
        this.portraitBlock = Blocks.SLIME_BLOCK;
        this.icon = ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/slime.png");

        this.canSetRallyPoint = true;

        this.startingBlockTypes.add(Blocks.POLISHED_DEEPSLATE);
        this.startingBlockTypes.add(Blocks.COBBLED_DEEPSLATE);

        this.explodeChance = 0.2f;

        this.productions.add(ProductionItems.SLIME, Keybindings.keyQ);
    }

    public Faction getFaction() {return Faction.MONSTERS;}

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
            name,
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/slime.png"),
            hotkey,
            () -> BuildingClientEvents.getBuildingToPlace() == Buildings.SLIME_PIT,
            () -> false,
            () -> BuildingClientEvents.hasFinishedBuilding(Buildings.GRAVEYARD) ||
                    ResearchClient.hasCheat("modifythephasevariance"),
            List.of(
                FormattedCharSequence.forward(I18n.get("buildings.monsters.siege_of_settlements.slime_pit"), Style.EMPTY.withBold(true)),
                ResourceCosts.getFormattedCost(cost),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.monsters.siege_of_settlements.slime_pit.tooltip1"), Style.EMPTY),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.monsters.siege_of_settlements.slime_pit.tooltip3"), Style.EMPTY)
            ),
            this
        );
    }

    public BlockPos getDefaultOutdoorSpawnPoint(BlockPos minCorner) {
        return minCorner.offset((int) (-spawnRadiusOffset + 4), 0, (int) (-spawnRadiusOffset + 9));
    }
}
