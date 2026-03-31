package com.solegendary.siege_of_settlements.config;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.hud.Button;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.tutorial.TutorialClientEvents;
import com.solegendary.siege_of_settlements.util.MyRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class ConfigClientEvents {

    private static ArrayList<FormattedCharSequence> tooltipLines = new ArrayList<>();
    public static boolean showDiffsButton = false;

    static {
        resetDiffsTooltips();
    }

    public static void resetDiffsTooltips() {
        tooltipLines.clear();
        tooltipLines.add(FormattedCharSequence.forward(I18n.get("config.siege_of_settlements.warn_config_change"), Style.EMPTY.withBold(true)));
        tooltipLines.add(FormattedCharSequence.forward(I18n.get("config.siege_of_settlements.hide_config_change"), Style.EMPTY));
        tooltipLines.add(FormattedCharSequence.forward("", Style.EMPTY));
        showDiffsButton = false;
    }

    /*
    @SubscribeEvent
    public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn evt) {
        // LOG IN TO SERVER WORLD ONLY
        SiegeOfSettlements.LOGGER.info("Resetting client config diffs");
        resetDiffsTooltips();
    }
     */

    public static Button getDiffsButton() {
        return new Button(
                "Configs Changed Warning",
                Button.itemIconSize,
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/warning.png"),
                (Keybinding) null,
                () -> false,
                () -> !showDiffsButton || TutorialClientEvents.isEnabled(),
                () -> true,
                () -> showDiffsButton = false,
                null,
                tooltipLines
        );
    }

    private static final Minecraft MC = Minecraft.getInstance();
    //Load config data from server
    public static void loadConfigData(ClientboundSyncResourceCostPacket msg, Supplier<NetworkEvent.Context> ctx) {
        String key = msg.getId();
        if(ResourceCost.ENTRIES.containsKey(key)) {
            ResourceCost rescost = ResourceCost.ENTRIES.get(key);
            //jank, but this is how we rebake using the values sent from the packet currently
            //we can clean this up later

            int woodDiff = msg.getWood() - rescost.wood;
            int foodDiff = msg.getFood() - rescost.food;
            int oreDiff = msg.getOre() - rescost.ore;
            int emeraldDiff = msg.getEmerald() - rescost.emerald;
            int ticksDiff = msg.getTicks() - rescost.ticks;
            int popDiff = msg.getPopulation() - rescost.population;

            rescost.wood = msg.getWood();
            rescost.food = msg.getFood();
            rescost.ore = msg.getOre();
            rescost.emerald = msg.getEmerald();
            rescost.ticks = msg.getTicks();
            rescost.population = msg.getPopulation();

            if (foodDiff != 0 ||
                woodDiff != 0 ||
                oreDiff != 0 ||
                emeraldDiff != 0 ||
                ticksDiff != 0 ||
                popDiff != 0) {

                String text = rescost.id.replace("siege_of_settlements.", "")
                        .replace("_","    ")
                        .toLowerCase(Locale.ENGLISH);
                text = text.substring(0, 1).toUpperCase() + text.substring(1);
                text += ":";

                if (foodDiff != 0)
                    text += (foodDiff > 0 ? "    +" : "    ") + foodDiff + " \uE000";
                if (woodDiff != 0)
                    text += (woodDiff > 0 ? "    +" : "    ") + woodDiff + " \uE001";
                if (oreDiff != 0)
                    text += (oreDiff > 0 ? "    +" : "    ") + oreDiff + " \uE002";
                if (emeraldDiff != 0)
                    text += (emeraldDiff > 0 ? "    +" : "    ") + emeraldDiff + " \uE00A";
                if (ticksDiff != 0)
                    text += (ticksDiff > 0 ? "    +" : "    ") + (ticksDiff / 20) + " \uE004";
                if (popDiff != 0)
                    text += (popDiff > 0 ? "    +" : "    ") +  popDiff + " \uE003";

                showDiffsButton = true;

                tooltipLines.add(FormattedCharSequence.forward(text, MyRenderer.iconStyle));

                SiegeOfSettlements.LOGGER.info("Received different config from server: " + text);
            }
        }
    }
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent evt) {
        SiegeOfSettlements.LOGGER.info("onPlayerJoined fired from ClientConfigEvents");
        //If we own this singleplayer world
        if (evt.getEntity().getServer().isSingleplayerOwner(evt.getEntity().getGameProfile())) {
            resetDiffsTooltips();
            //rebake from clientsideside configs
            SiegeOfSettlements.LOGGER.info("Attempting to rebake from client..");
            for(ResourceCostConfigEntry entry : ResourceCostConfigEntry.ENTRIES) {
                String key = entry.id;
                if(ResourceCost.ENTRIES.containsKey(key)) {
                    ResourceCost rescost = ResourceCost.ENTRIES.get(key);
                    SiegeOfSettlements.LOGGER.info("ID found: " + key + ", replacing resourcecost " + ResourceCost.ENTRIES.get(key));
                    rescost.bakeValues(entry);
                }
            }
            ResourceCosts.deferredLoadResourceCosts();
        }
    }
}
