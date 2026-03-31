package com.solegendary.siege_of_settlements.resources;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class ResourcesSaveData extends SavedData {

    public final ArrayList<Resources> resources = new ArrayList<>();

    private static ResourcesSaveData create() {
        return new ResourcesSaveData();
    }

    @Nonnull
    public static ResourcesSaveData getInstance(LevelAccessor level) {
        MinecraftServer server = level.getServer();
        if (server == null) {
            return create();
        }
        return server.overworld()
            .getDataStorage()
            .computeIfAbsent(ResourcesSaveData::load, ResourcesSaveData::create, "saved-resources-data");
    }

    public static ResourcesSaveData load(CompoundTag tag) {
        SiegeOfSettlements.LOGGER.info("ResourcesSaveData.load");

        ResourcesSaveData data = create();
        ListTag ltag = (ListTag) tag.get("resources");

        if (ltag != null) {
            for (Tag ctag : ltag) {
                CompoundTag ptag = (CompoundTag) ctag;

                String ownerName = ptag.getString("ownerName");
                int food = ptag.getInt("food");
                int wood = ptag.getInt("wood");
                int ore = ptag.getInt("ore");
                int emerald = ptag.getInt("emerald");

                data.resources.add(new Resources(ownerName, food, wood, ore, emerald));

                SiegeOfSettlements.LOGGER.info("ResourcesSaveData.load: " + ownerName + "|" + food + "|" + wood + "|" + ore + "|" + emerald);
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        //SiegeOfSettlements.LOGGER.info("ResourcesSaveData.save");

        ListTag list = new ListTag();
        this.resources.forEach(r -> {
            CompoundTag cTag = new CompoundTag();
            cTag.putString("ownerName", r.ownerName);
            cTag.putInt("food", r.food);
            cTag.putInt("wood", r.wood);
            cTag.putInt("ore", r.ore);
            cTag.putInt("emerald", r.emerald);
            list.add(cTag);

            SiegeOfSettlements.LOGGER.info(
                "ResourcesSaveData.save: " + r.ownerName + "|" + r.food + "|" + r.wood + "|" + r.ore + "|" + r.emerald);
        });
        tag.put("resources", list);
        return tag;
    }

    public void save() {
        this.setDirty();
    }
}
