package com.WardenMine242.siege_of_settlements.guiscreen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.production.ProductionItem;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.building.production.StartProductionButton;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.WardenMine242.siege_of_settlements.network.ResearchTreeServerboundPacket;
import com.WardenMine242.siege_of_settlements.research.ResearchTreePrerequisites;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class ResearchTreeScreen extends Screen {

    private static final ResourceLocation TICK_TEXTURE = ResourceLocation.fromNamespaceAndPath("siege_of_settlements", "textures/hud/tick.png");
    private static final ResourceLocation FRAME_TEXTURE = ResourceLocation.fromNamespaceAndPath("siege_of_settlements", "textures/hud/icon_frame.png");
    private static final ResourceLocation FRAME_SELECTED_TEXTURE = ResourceLocation.fromNamespaceAndPath("siege_of_settlements", "textures/hud/icon_frame_selected.png");

    private static final int NODE_SIZE = 32;
    private final List<ResearchNode> nodes = new ArrayList<>();

    private double panX = 0;
    private double panY = 0;

    public ResearchTreeScreen() {
        super(Component.translatable("screen.siege_of_settlements.research_tree"));
    }

    @Override
    protected void init() {
        super.init();
        nodes.clear();
        panX = 0;
        panY = 0;

        int centerX = width / 2;
        int centerY = height / 2;

        // Center: Marketplace (Start)
        ResearchNode marketplace = addNode(centerX, centerY, ProductionItems.RESEARCH_MARKETPLACE);

        // Center Up: Tower Damage -> Fire Walls -> Arcane Tower
        ResearchNode towerDamage = addNode(centerX, centerY - 40, ProductionItems.RESEARCH_TOWER_DAMAGE_1, ProductionItems.RESEARCH_TOWER_DAMAGE_2).addPrerequisite(marketplace);
        ResearchNode fireWalls = addNode(centerX, centerY - 80, ProductionItems.RESEARCH_FIRE_WALLS, ProductionItems.RESEARCH_FIRE_WALLS2).addPrerequisite(towerDamage);
        ResearchNode arcaneTower = addNode(centerX, centerY - 120, ProductionItems.RESEARCH_POWERFUL_ARCANE_TOWER).addPrerequisite(fireWalls);

        // Left: Water potions chain, then drill
        ResearchNode drill = addNode(centerX - 110, centerY, ProductionItems.RESEARCH_UPGRADED_DRILL, ProductionItems.RESEARCH_ADVANCED_DRILL).addPrerequisite(marketplace);
        ResearchNode waterPotion = addNode(centerX - 55, centerY, ProductionItems.RESEARCH_WATER_POTIONS).addPrerequisite(marketplace);
        ResearchNode lingeringPotion = addNode(centerX - 110, centerY - 40, ProductionItems.RESEARCH_LINGERING_POTIONS).addPrerequisite(waterPotion);
        ResearchNode healingPotion = addNode(centerX - 160, centerY - 40, ProductionItems.RESEARCH_HEALING_POTIONS).addPrerequisite(lingeringPotion);
        ResearchNode evokerVexes = addNode(centerX - 160, centerY - 80, ProductionItems.RESEARCH_EVOKER_VEXES).addPrerequisite(healingPotion);



        // Bottom Left: Trader -> Elite Trader -> Elite Training
        ResearchNode trader = addNode(centerX - 60, centerY + 50, ProductionItems.RESEARCH_TRADER).addPrerequisite(marketplace);
        ResearchNode eliteTrader = addNode(centerX - 100, centerY + 90, ProductionItems.RESEARCH_ELITE_TRADER).addPrerequisite(trader);
        ResearchNode eliteTraining = addNode(centerX - 160, centerY + 90, ProductionItems.RESEARCH_ELITE_TRAINING).addPrerequisite(eliteTrader);

        // Right: Upgraded Barracks -> Castle -> Improved Blacksmith
        ResearchNode upgradedBarracks = addNode(centerX + 60, centerY, ProductionItems.RESEARCH_UPGRADED_BARRACKS).addPrerequisite(marketplace);
        ResearchNode militiaBows = addNode(centerX + 110, centerY - 40, ProductionItems.RESEARCH_MILITIA_BOWS).addPrerequisite(upgradedBarracks);
        ResearchNode golemSmithing = addNode(centerX + 160, centerY - 40, ProductionItems.RESEARCH_GOLEM_SMITHING).addPrerequisite(militiaBows);
        ResearchNode castle = addNode(centerX + 110, centerY, ProductionItems.RESEARCH_CASTLE).addPrerequisite(upgradedBarracks);
        ResearchNode ravagerCavalry = addNode(centerX + 110, centerY + 40, ProductionItems.RESEARCH_RAVAGER_CAVALRY).addPrerequisite(castle);
        ResearchNode blacksmith = addNode(centerX + 160, centerY, ProductionItems.RESEARCH_ADVANCED_BLACKSMITH).addPrerequisite(castle);

        // Bottom Right: Professions -> Resource Capacity -> Big House (Diagonal)
        // Bottom Left: Professions -> Blacksmiths (separate branch on other side)
        ResearchNode professions = addNode(centerX + 60, centerY + 50, ProductionItems.RESEARCH_PROFESSIONS).addPrerequisite(marketplace);
        ResearchNode resourceCap = addNode(centerX + 100, centerY + 90, ProductionItems.RESEARCH_RESOURCE_CAPACITY).addPrerequisite(professions);
        ResearchNode blacksmiths = addNode(centerX + 20, centerY + 90, ProductionItems.RESEARCH_BLACKSMITHS).addPrerequisite(professions);
        ResearchNode bigHouse = addNode(centerX + 100, centerY + 130, ProductionItems.RESEARCH_BIG_HOUSE).addPrerequisite(resourceCap);

        addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> onClose())
                .pos(this.width / 2 - 40, this.height - 30)
                .size(80, 20)
                .build());
    }

    private ResearchNode addNode(int x, int y, ProductionItem... items) {
        ResearchNode node = new ResearchNode(x - NODE_SIZE / 2, y - NODE_SIZE / 2, items);
        nodes.add(node);
        return node;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(panX, panY, 0);

        // Draw lines first
        for (ResearchNode node : nodes) {
            for (ResearchNode prereq : node.prerequisites) {
                drawLine(guiGraphics, prereq.x + NODE_SIZE / 2, prereq.y + NODE_SIZE / 2, node.x + NODE_SIZE / 2, node.y + NODE_SIZE / 2);
            }
        }

        // Draw nodes
        ResearchNode hoveredNode = null;
        for (ResearchNode node : nodes) {
            ProductionItem currentItem = node.getCurrentItem();
            boolean allCompleted = node.isAllCompleted();
            boolean unlocked = node.isUnlocked();
            boolean hovering = isHovering(node, mouseX, mouseY);
            if (hovering) hoveredNode = node;

            // Frame
            guiGraphics.blit(hovering ? FRAME_SELECTED_TEXTURE : FRAME_TEXTURE, node.x, node.y, 0, 0, NODE_SIZE, NODE_SIZE, NODE_SIZE, NODE_SIZE);

            // Icon
            RenderSystem.setShaderColor(unlocked ? 1.0f : 0.3f, unlocked ? 1.0f : 0.3f, unlocked ? 1.0f : 0.3f, 1.0f);
            StartProductionButton btn = currentItem.getStartButton(null, null);
            ResourceLocation icon = btn != null ? btn.iconResource : ResourceLocation.fromNamespaceAndPath("minecraft", "textures/misc/unknown_pack.png");
            guiGraphics.blit(icon, node.x + 4, node.y + 4, 0, 0, NODE_SIZE - 8, NODE_SIZE - 8, NODE_SIZE - 8, NODE_SIZE - 8);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

            // Tick
            if (allCompleted) {
                guiGraphics.blit(TICK_TEXTURE, node.x + NODE_SIZE - 12, node.y + NODE_SIZE - 12, 0, 0, 12, 12, 12, 12);
            }
        }
        guiGraphics.pose().popPose();

        if (hoveredNode != null) {
            renderNodeTooltip(guiGraphics, hoveredNode, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0) { // Left mouse button
            boolean overNode = false;
            for (ResearchNode node : nodes) {
                if (isHovering(node, (int) mouseX, (int) mouseY)) {
                    overNode = true;
                    break;
                }
            }
            if (!overNode) {
                panX += dragX;
                panY += dragY;
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    private void drawLine(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2) {
        int color = 0xFFA0A0A0;
        // Draw L-shaped line instead of a rectangle to avoid "big squares"
        if (x1 == x2) {
            guiGraphics.fill(x1 - 1, Math.min(y1, y2), x1 + 1, Math.max(y1, y2), color);
        } else if (y1 == y2) {
            guiGraphics.fill(Math.min(x1, x2), y1 - 1, Math.max(x1, x2), y1 + 1, color);
        } else {
            // Vertical segment from (x1, y1) to (x1, y2)
            guiGraphics.fill(x1 - 1, Math.min(y1, y2), x1 + 1, Math.max(y1, y2), color);
            // Horizontal segment from (x1, y2) to (x2, y2)
            guiGraphics.fill(Math.min(x1, x2), y2 - 1, Math.max(x1, x2), y2 + 1, color);
        }
    }

    private void renderNodeTooltip(GuiGraphics guiGraphics, ResearchNode node, int mouseX, int mouseY) {
        ProductionItem item = node.getCurrentItem();
        StartProductionButton btn = item.getStartButton(null, null);
        List<FormattedCharSequence> lines = btn != null ? btn.tooltipLines : new ArrayList<>();
        
        List<FormattedCharSequence> tooltip = new ArrayList<>(lines);

        if (node.isAllCompleted()) {
            tooltip.add(FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.completed"), Style.EMPTY.withColor(0x55FF55)));
        } else if (!node.isUnlocked()) {
            tooltip.add(FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.requires_previous"), Style.EMPTY.withColor(0xFF5555)));
        }

        guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (ResearchNode node : nodes) {
            if (isHovering(node, (int) mouseX, (int) mouseY)) {
                if (node.isUnlocked() && !node.isAllCompleted()) {
                    ResearchTreeServerboundPacket.sendResearchRequest(com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries.PRODUCTION_ITEM.getKey(node.getCurrentItem()));
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isHovering(ResearchNode node, int mouseX, int mouseY) {
        double actualX = node.x + panX;
        double actualY = node.y + panY;
        return mouseX >= actualX && mouseX < actualX + NODE_SIZE && mouseY >= actualY && mouseY < actualY + NODE_SIZE;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static class ResearchNode {
        public final ProductionItem[] items;
        public final int x, y;
        public final List<ResearchNode> prerequisites = new ArrayList<>();

        public ResearchNode(int x, int y, ProductionItem... items) {
            this.items = items;
            this.x = x;
            this.y = y;
        }

        public ResearchNode addPrerequisite(ResearchNode node) {
            prerequisites.add(node);
            return this;
        }

        public ProductionItem getCurrentItem() {
            for (ProductionItem item : items) {
                if (!ResearchClient.hasResearch(item)) {
                    return item;
                }
            }
            return items[items.length - 1];
        }

        public boolean isAllCompleted() {
            for (ProductionItem item : items) {
                if (!ResearchClient.hasResearch(item)) {
                    return false;
                }
            }
            return true;
        }

        public boolean isUnlocked() {
            return ResearchTreePrerequisites.clientMeetsPrerequisites(getCurrentItem());
        }
    }
}
