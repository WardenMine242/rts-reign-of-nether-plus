package com.WardenMine242.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.resources.ResourcesAction;
import com.solegendary.siege_of_settlements.resources.ResourcesServerEvents;
import com.solegendary.siege_of_settlements.resources.ResourcesServerboundPacket;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.WardenMine242.siege_of_settlements.building.buildings.placements.MarketplacePlacement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class MarketplaceTradeGUI extends Ability {

    private static final int CD_TICKS = 20; // 1 second

    public MarketplaceTradeGUI() {
        super(UnitAction.NONE, CD_TICKS, 0, 0, false, false);
        this.defaultHotkey = Keybindings.keyQ;
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, BuildingPlacement placement) {
        if (!(placement instanceof MarketplacePlacement)) {
            return null;
        }

        return new AbilityButton(
                I18n.get("ability.siege_of_settlements.marketplace.trade_gui"),
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/emerald.png"),
                hotkey,
                () -> false,
                () -> false,
                () -> true,
                () -> {
                    Minecraft mc = Minecraft.getInstance();
                    if (mc.player == null) {
                        return;
                    }
                    mc.setScreen(new MarketplaceTradeScreen());
                    setToMaxCooldown(placement);
                },
                null,
                List.of(
                        fcs(I18n.get("ability.siege_of_settlements.marketplace.trade_gui"), Style.EMPTY.withBold(true)),
                        fcs(I18n.get("ability.siege_of_settlements.marketplace.trade_gui.tooltip1"), Style.EMPTY)
                ),
                this,
                placement
        );
    }

    private static class MarketplaceTradeScreen extends Screen {

        private static final int BUTTON_SIZE = 28;
        private static final int LABEL_GAP = 4;
        private static final int ROW_GAP = 12;
        private static final float TRADE_TEXT_SCALE = 0.65f;

        private final List<TradeButtonMeta> tradeButtons = new ArrayList<>();

        private MarketplaceTradeScreen() {
            super(Component.empty());
        }

        @Override
        protected void init() {
            super.init();
            tradeButtons.clear();

            int columnSpacing = Math.min(170, Math.max(95, this.width / 4));
            int[] columnCenters = new int[] {
                    this.width / 2 - columnSpacing,
                    this.width / 2,
                    this.width / 2 + columnSpacing
            };

            int topButtonY = this.height / 2 - 70;
            int topLabelY = topButtonY + BUTTON_SIZE + LABEL_GAP;
            int bottomButtonY = topLabelY + this.font.lineHeight + ROW_GAP;
            int bottomLabelY = bottomButtonY + BUTTON_SIZE + LABEL_GAP;
            int exitButtonY = bottomLabelY + this.font.lineHeight + 10;

            String foodText = I18n.get("resources.siege_of_settlements.food");
            String woodText = I18n.get("resources.siege_of_settlements.wood");
            String oreText = I18n.get("resources.siege_of_settlements.ore");
            String emeraldText = I18n.get("resources.siege_of_settlements.emeralds");

            // Top row: sell 100 resources for emeralds
            addTradeButton(
                    columnCenters[0] - BUTTON_SIZE / 2,
                    topButtonY,
                    topLabelY,
                    new ItemStack(Items.WHEAT),
                    ResourcesAction.MARKET_SELL_FOOD,
                    Component.translatable(
                            "screen.siege_of_settlements.marketplace_trade.sell_food",
                            ResourcesServerEvents.MARKET_TRADE_RESOURCE_AMOUNT,
                            ResourcesServerEvents.MARKET_SELL_FOOD_EMERALDS
                    ),
                    ResourcesServerEvents.MARKET_TRADE_RESOURCE_AMOUNT + " " + foodText + " -> " +
                            ResourcesServerEvents.MARKET_SELL_FOOD_EMERALDS + " " + emeraldText
            );
            addTradeButton(
                    columnCenters[1] - BUTTON_SIZE / 2,
                    topButtonY,
                    topLabelY,
                    new ItemStack(Items.OAK_LOG),
                    ResourcesAction.MARKET_SELL_WOOD,
                    Component.translatable(
                            "screen.siege_of_settlements.marketplace_trade.sell_wood",
                            ResourcesServerEvents.MARKET_TRADE_RESOURCE_AMOUNT,
                            ResourcesServerEvents.MARKET_SELL_WOOD_EMERALDS
                    ),
                    ResourcesServerEvents.MARKET_TRADE_RESOURCE_AMOUNT + " " + woodText + " -> " +
                            ResourcesServerEvents.MARKET_SELL_WOOD_EMERALDS + " " + emeraldText
            );
            addTradeButton(
                    columnCenters[2] - BUTTON_SIZE / 2,
                    topButtonY,
                    topLabelY,
                    new ItemStack(Items.RAW_IRON),
                    ResourcesAction.MARKET_SELL_ORE,
                    Component.translatable(
                            "screen.siege_of_settlements.marketplace_trade.sell_ore",
                            ResourcesServerEvents.MARKET_TRADE_RESOURCE_AMOUNT,
                            ResourcesServerEvents.MARKET_SELL_ORE_EMERALDS
                    ),
                    ResourcesServerEvents.MARKET_TRADE_RESOURCE_AMOUNT + " " + oreText + " -> " +
                            ResourcesServerEvents.MARKET_SELL_ORE_EMERALDS + " " + emeraldText
            );

            // Bottom row: buy 100 resources for emeralds
            addTradeButton(
                    columnCenters[0] - BUTTON_SIZE / 2,
                    bottomButtonY,
                    bottomLabelY,
                    new ItemStack(Items.WHEAT),
                    ResourcesAction.MARKET_BUY_FOOD,
                    Component.translatable(
                            "screen.siege_of_settlements.marketplace_trade.buy_food",
                            ResourcesServerEvents.MARKET_TRADE_RESOURCE_AMOUNT,
                            ResourcesServerEvents.MARKET_BUY_FOOD_EMERALDS
                    ),
                    ResourcesServerEvents.MARKET_BUY_FOOD_EMERALDS + " " + emeraldText + " -> " +
                            ResourcesServerEvents.MARKET_TRADE_RESOURCE_AMOUNT + " " + foodText
            );
            addTradeButton(
                    columnCenters[1] - BUTTON_SIZE / 2,
                    bottomButtonY,
                    bottomLabelY,
                    new ItemStack(Items.OAK_LOG),
                    ResourcesAction.MARKET_BUY_WOOD,
                    Component.translatable(
                            "screen.siege_of_settlements.marketplace_trade.buy_wood",
                            ResourcesServerEvents.MARKET_TRADE_RESOURCE_AMOUNT,
                            ResourcesServerEvents.MARKET_BUY_WOOD_EMERALDS
                    ),
                    ResourcesServerEvents.MARKET_BUY_WOOD_EMERALDS + " " + emeraldText + " -> " +
                            ResourcesServerEvents.MARKET_TRADE_RESOURCE_AMOUNT + " " + woodText
            );
            addTradeButton(
                    columnCenters[2] - BUTTON_SIZE / 2,
                    bottomButtonY,
                    bottomLabelY,
                    new ItemStack(Items.RAW_IRON),
                    ResourcesAction.MARKET_BUY_ORE,
                    Component.translatable(
                            "screen.siege_of_settlements.marketplace_trade.buy_ore",
                            ResourcesServerEvents.MARKET_TRADE_RESOURCE_AMOUNT,
                            ResourcesServerEvents.MARKET_BUY_ORE_EMERALDS
                    ),
                    ResourcesServerEvents.MARKET_BUY_ORE_EMERALDS + " " + emeraldText + " -> " +
                            ResourcesServerEvents.MARKET_TRADE_RESOURCE_AMOUNT + " " + oreText
            );

            addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> onClose())
                    .pos(this.width / 2 - 40, exitButtonY)
                    .size(80, 20)
                    .build());
        }

        private void addTradeButton(
                int x,
                int y,
                int textY,
                ItemStack icon,
                ResourcesAction action,
                Component tooltip,
                String tradeLine
        ) {
            Button button = Button.builder(Component.empty(), b -> sendTrade(action))
                    .pos(x, y)
                    .size(BUTTON_SIZE, BUTTON_SIZE)
                    .build();

            addRenderableWidget(button);
            tradeButtons.add(new TradeButtonMeta(button, icon, tooltip, tradeLine, textY));
        }

        private void sendTrade(ResourcesAction action) {
            if (Minecraft.getInstance().player == null) {
                return;
            }
            ResourcesServerboundPacket.sendMarketplaceTrade(action, Minecraft.getInstance().player.getName().getString());
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            renderBackground(guiGraphics);
            super.render(guiGraphics, mouseX, mouseY, partialTick);

            for (TradeButtonMeta meta : tradeButtons) {
                int iconX = meta.button.getX() + (BUTTON_SIZE - 16) / 2;
                int iconY = meta.button.getY() + (BUTTON_SIZE - 16) / 2;
                guiGraphics.renderItem(meta.icon, iconX, iconY);
                guiGraphics.pose().pushPose();
                guiGraphics.pose().scale(TRADE_TEXT_SCALE, TRADE_TEXT_SCALE, 1.0f);
                int scaledCenterX = Math.round((meta.button.getX() + BUTTON_SIZE / 2) / TRADE_TEXT_SCALE);
                int scaledY = Math.round(meta.textY / TRADE_TEXT_SCALE);
                int textWidth = this.font.width(meta.tradeLine);
                guiGraphics.drawString(
                        this.font,
                        meta.tradeLine,
                        scaledCenterX - textWidth / 2,
                        scaledY,
                        0xD0D0D0,
                        false
                );
                guiGraphics.pose().popPose();

                if (isHovering(meta.button, mouseX, mouseY)) {
                    guiGraphics.renderTooltip(this.font, meta.tooltip, mouseX, mouseY);
                }
            }
        }

        private static boolean isHovering(Button button, int mouseX, int mouseY) {
            return mouseX >= button.getX() && mouseX < button.getX() + button.getWidth()
                    && mouseY >= button.getY() && mouseY < button.getY() + button.getHeight();
        }

        @Override
        public boolean isPauseScreen() {
            return false;
        }

        private record TradeButtonMeta(Button button, ItemStack icon, Component tooltip, String tradeLine, int textY) {
        }
    }
}
