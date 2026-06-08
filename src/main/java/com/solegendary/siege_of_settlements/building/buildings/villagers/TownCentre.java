package com.solegendary.siege_of_settlements.building.buildings.villagers;

import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.ability.abilities.BackToWorkBuilding;
import com.solegendary.siege_of_settlements.ability.abilities.CallToArmsBuilding;
import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlaceButton;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.Buildings;
import com.solegendary.siege_of_settlements.building.buildings.placements.TownCentrePlacement;
import com.solegendary.siege_of_settlements.building.production.ProductionBuilding;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.faction.Faction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;

import java.util.List;

import static com.solegendary.siege_of_settlements.building.BuildingUtils.getAbsoluteBlockData;

public class TownCentre extends ProductionBuilding {

    public final static String buildingName = "Town Centre";
    public final static String structureName = "town_centre";
    public final static String upgradedStructureName = "town_centre_t2";
    public final static ResourceCost cost = ResourceCosts.TOWN_CENTRE;

    // distance you can move away from a town centre before being turned back into a villager
    public static final int MILITIA_RANGE = 60;

    public TownCentre() {
        super(structureName, cost, true);
        this.name = buildingName;
        this.portraitBlock = Blocks.POLISHED_GRANITE;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/polished_granite.png");

        this.buildTimeModifier = 0.331f; // 60s total build time with 3 villagers
        this.canAcceptResources = true;

        this.startingBlockTypes.add(Blocks.STONE_BRICK_STAIRS);
        this.startingBlockTypes.add(Blocks.GRASS_BLOCK);
        this.startingBlockTypes.add(Blocks.POLISHED_ANDESITE_STAIRS);

        Ability callToArms = new CallToArmsBuilding();
        this.abilities.add(callToArms, Keybindings.keyV);
        BackToWorkBuilding backToWork = new BackToWorkBuilding();
        this.abilities.add(backToWork, Keybindings.build);

        this.productions.add(ProductionItems.VILLAGER, Keybindings.keyQ);
        this.productions.add(ProductionItems.RESEARCH_TOWN_CENTRE_T2, Keybindings.keyW);
    }

    public Faction getFaction() {return Faction.VILLAGERS;}

    @Override
    public BuildingPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName) {
        return new TownCentrePlacement(this, level, pos, rotation, ownerName, getAbsoluteBlockData(getRelativeBlockData(level), level, pos, rotation), true, MILITIA_RANGE, true, false);
    }

    @Override
    public int getUpgradeLevel(BuildingPlacement placement) {
        if (placement instanceof TownCentrePlacement townCentre) {
            return townCentre.getUpgradeLevel();
        }
        return 0;
    }

    @Override
    public String getUpgradedStructureName(int upgradeLevel) {
        return upgradeLevel > 0 ? upgradedStructureName : structureName;
    }

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
               name,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/polished_granite.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == Buildings.TOWN_CENTRE,
                () -> false,
                () -> {
                    if (Minecraft.getInstance().player == null) {
                        return true;
                    }
                    String ownerName = BuildingClientEvents.getPlacementOwnerName(this);
                    return !BuildingClientEvents.playerHasAnyBuilding(Buildings.TOWN_CENTRE, ownerName);
                },
                List.of(
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.town_centre"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.town_centre.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.siege_of_settlements.only.one_building"), Style.EMPTY)
                ),
                this
        );
    }
}
