package com.solegendary.siege_of_settlements.building.production;

import com.solegendary.siege_of_settlements.building.BuildingUtils;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import com.solegendary.siege_of_settlements.gamerules.GameruleClient;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.unit.interfaces.HeroUnit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public abstract class HeroProductionItem extends ProductionItem {

    public final ResourceLocation iconRl;
    public final String itemName;

    public HeroProductionItem(ResourceCost cost, String itemName, ResourceLocation iconRl) {
        super(cost);
        this.dupeRule = ProdDupeRule.DISALLOW;
        this.onComplete = (Level level, ProductionPlacement placement) -> {
            if (!level.isClientSide()) {
                int heroesOwned = HeroUnit.getNumHeroesOwnedOrInTraining(false, placement.ownerName);
                if (heroesOwned < GameruleClient.allowedHeroes &&
                    (heroesOwned == 0 || BuildingUtils.castleOwned(false, placement.ownerName))) {
                    placement.produceUnit((ServerLevel) level, getHeroEntityType(), placement.ownerName, true);
                }
            }
        };
        this.itemName = itemName;
        this.iconRl = iconRl;
    }

    public String getItemName() {
        return itemName;
    }

    protected boolean heroOwned(boolean isClientside, String ownerName) {
        String heroName = getHeroEntityType().getDescriptionId();
        return !HeroUnit.getHeroes(isClientside, ownerName, heroName).isEmpty() ||
                HeroUnit.getFallenHero(isClientside, ownerName, heroName) != null;
    }

    // can't make this a member as we can't refer to other registered objects at init time
    protected EntityType<? extends HeroUnit> getHeroEntityType() {
        return null;
    }

    public StopProductionButton getCancelButton(ProductionPlacement prodBuilding, boolean first) {
        return new StopProductionButton(
                getItemName(),
                iconRl,
                prodBuilding,
                this,
                first
        );
    }

    public StartProductionButton getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey, List<FormattedCharSequence> fcs) {
        return new StartProductionButton(
                itemName,
                iconRl,
                hotkey,
                () -> itemIsBeingProduced(prodBuilding.ownerName) ||
                        heroOwned(prodBuilding.level.isClientSide(), prodBuilding.ownerName) ||
                        HeroUnit.getNumHeroesOwnedOrInTraining(true, prodBuilding.ownerName) >= GameruleClient.allowedHeroes,
                () -> HeroUnit.getNumHeroesOwnedOrInTraining(true, prodBuilding.ownerName) == 0 ||
                        BuildingUtils.castleOwned(true, prodBuilding.ownerName),
                fcs,
                this
        );
    }

    protected List<FormattedCharSequence> getAdditionalHeroTooltips() {
        if (GameruleClient.allowedHeroes == 1) {
            return List.of(
                    fcs(""),
                    fcs(I18n.get("units.siege_of_settlements.hero_production.allowed_heroes_1"))
            );
        } else if (GameruleClient.allowedHeroes >= 2) {
            return List.of(
                    fcs(""),
                    fcs(I18n.get("units.siege_of_settlements.hero_production.allowed_heroes_2")),
                    fcs(I18n.get("units.siege_of_settlements.hero_production.unlock_hero_2"))
            );
        } else {
            return List.of();
        }
    }
}
