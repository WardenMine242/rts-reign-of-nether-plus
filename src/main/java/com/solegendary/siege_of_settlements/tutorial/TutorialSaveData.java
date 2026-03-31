package com.solegendary.siege_of_settlements.tutorial;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;

public class TutorialSaveData extends SavedData {

    public TutorialStage stage;

    private static TutorialSaveData create() {
        return new TutorialSaveData();
    }

    @Nonnull
    public static TutorialSaveData getInstance(LevelAccessor level) {
        MinecraftServer server = level.getServer();
        if (server == null) {
            return create();
        }
        return server.overworld()
            .getDataStorage()
            .computeIfAbsent(TutorialSaveData::load, TutorialSaveData::create, "saved-tutorial-data");
    }

    public static TutorialSaveData load(CompoundTag tag) {
        TutorialSaveData data = create();
        data.stage = TutorialStage.valueOf(tag.getString("stage"));
        SiegeOfSettlements.LOGGER.info("TutorialSaveData.load: " + data.stage);
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        //SiegeOfSettlements.LOGGER.info("TutorialSaveData.save: " + stage);
        tag.putString("stage", this.stage.name());
        return tag;
    }

    public void save() {
        this.setDirty();
    }
}
