package com.solegendary.siege_of_settlements.player;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.faction.Faction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;

import net.minecraft.world.level.GameType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RTSPlayerSaveData extends SavedData {

    public final ArrayList<RTSPlayer> rtsPlayers = new ArrayList<>();
    public final Map<String, GameType> playerDefaultGameModes = new HashMap<>();

    private static RTSPlayerSaveData create() {
        return new RTSPlayerSaveData();
    }

    @Nonnull
    public static RTSPlayerSaveData getInstance(LevelAccessor level) {
        MinecraftServer server = level.getServer();
        if (server == null) {
            return create();
        }
        return server.overworld()
            .getDataStorage()
            .computeIfAbsent(RTSPlayerSaveData::load, RTSPlayerSaveData::create, "saved-rtsplayer-data");
    }

    public static RTSPlayerSaveData load(CompoundTag tag) {
        SiegeOfSettlements.LOGGER.info("RTSPlayerSaveData.load");

        RTSPlayerSaveData data = create();
        ListTag ltag = (ListTag) tag.get("rtsplayers");

        if (ltag != null) {
            for (Tag ctag : ltag) {
                CompoundTag ptag = (CompoundTag) ctag;

                String name = ptag.getString("name");
                int id = ptag.getInt("id");
                int ticksWithoutCapitol = ptag.getInt("ticksWithoutCapitol");
                int beaconOwnerTicks = ptag.getInt("beaconOwnerTicks");
                int startPosColorId = ptag.getInt("startPosColorId");
                Faction faction = Faction.valueOf(ptag.getString("faction"));
                int[] scores = ptag.contains("scores") ? ptag.getIntArray("scores") : new RTSPlayerScores().getScoreListAsArray();

                data.rtsPlayers.add(RTSPlayer.getFromSave(name, id, ticksWithoutCapitol, faction, beaconOwnerTicks, scores, startPosColorId));

                SiegeOfSettlements.LOGGER.info("RTSPlayerSaveData.load: " + name + "|" + id + "|" + faction);
            }
        }

        CompoundTag modesTag = tag.getCompound("playerDefaultGameModes");
        for (String name : modesTag.getAllKeys()) {
            try {
                GameType mode = GameType.byName(modesTag.getString(name));
                data.playerDefaultGameModes.put(name, mode);
            } catch (Exception e) {
                SiegeOfSettlements.LOGGER.warn("Failed to load default game mode for player " + name);
            }
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        //SiegeOfSettlements.LOGGER.info("RTSPlayerSaveData.save");

        ListTag list = new ListTag();
        this.rtsPlayers.forEach(p -> {
            CompoundTag cTag = new CompoundTag();
            cTag.putString("name", p.name);
            cTag.putInt("id", p.id);
            cTag.putInt("ticksWithoutCapitol", p.ticksWithoutCapitol);
            cTag.putInt("beaconOwnerTicks", p.beaconOwnerTicks);
            cTag.putInt("startPosColorId", p.startPosColorId);
            cTag.putString("faction", p.faction.name());
            cTag.putIntArray("scores", p.scores.getScoreListAsArray());
            list.add(cTag);

            //SiegeOfSettlements.LOGGER.info("RTSPlayerSaveData.save: " + p.name + "|" + p.id + "|" + p.faction);
        });
        tag.put("rtsplayers", list);

        CompoundTag modesTag = new CompoundTag();
        this.playerDefaultGameModes.forEach((name, mode) -> {
            modesTag.putString(name, mode.getName());
        });
        tag.put("playerDefaultGameModes", modesTag);

        return tag;
    }

    public void save() {
        this.setDirty();
    }
}
