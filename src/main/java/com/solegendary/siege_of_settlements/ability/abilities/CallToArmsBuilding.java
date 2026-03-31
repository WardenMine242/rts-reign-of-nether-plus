package com.solegendary.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.buildings.villagers.TownCentre;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.sounds.SoundAction;
import com.solegendary.siege_of_settlements.sounds.SoundClientboundPacket;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.interfaces.WorkerUnit;
import com.solegendary.siege_of_settlements.unit.units.villagers.VillagerUnit;
import com.solegendary.siege_of_settlements.util.MiscUtil;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.solegendary.siege_of_settlements.unit.UnitClientEvents.sendUnitCommand;

public class CallToArmsBuilding extends Ability {

    private static final int RANGE = TownCentre.MILITIA_RANGE;

    public CallToArmsBuilding() {
        super(
                UnitAction.CALL_TO_ARMS_BUILDING,
                0,
                RANGE,
                0,
                false,
                false
        );
        this.defaultHotkey = Keybindings.keyV;
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, BuildingPlacement placement) {
        return new AbilityButton(
                "Call To Arms (Building)",
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/bell.png"),
                hotkey,
                () -> false,
                () -> false,
                () -> true,
                () -> sendUnitCommand(UnitAction.CALL_TO_ARMS_BUILDING),
                null,
                List.of(
                        FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.call_to_arms_building"), Style.EMPTY.withBold(true)),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.call_to_arms_building.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.call_to_arms_building.tooltip2"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.call_to_arms_building.tooltip3", TownCentre.MILITIA_RANGE), Style.EMPTY)
                ),
                this,
                placement
        );
    }

    @Override
    public void use(Level level, BuildingPlacement buildingUsing, BlockPos targetBp) {
        List<VillagerUnit> units = MiscUtil.getEntitiesWithinRange(
                        new Vector3d(buildingUsing.centrePos.getX(), buildingUsing.centrePos.getY(), buildingUsing.centrePos.getZ()),
                        range, VillagerUnit.class, buildingUsing.getLevel());
        for (VillagerUnit unit : units) {
            if (unit.getOwnerName().equals(buildingUsing.ownerName)) {
                Unit.resetBehaviours(unit);
                WorkerUnit.resetBehaviours(unit);
                unit.callToArmsGoal.setNearestTownCentreAsTarget();
            }
        }

        if (!level.isClientSide()) {
            SoundClientboundPacket.playSoundAtPos(SoundAction.BELL, buildingUsing.centrePos);
            CompletableFuture.delayedExecutor(300, TimeUnit.MILLISECONDS).execute(() -> {
                SoundClientboundPacket.playSoundAtPos(SoundAction.BELL, buildingUsing.centrePos);
            });
        }
    }
}
