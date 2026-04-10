package com.solegendary.siege_of_settlements.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.hud.TitleClientEvents;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.gui.TitleScreenModUpdateIndicator;
import net.minecraftforge.internal.BrandingControl;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.net.URI;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    private static final ResourceLocation MINECRAFT_LOGO =
            ResourceLocation.parse("textures/gui/title/minecraft.png");
    private static final ResourceLocation DISCORD_TEXTURE =
            ResourceLocation.parse( "textures/gui/title/discord.png");


    @Shadow @Final private PanoramaRenderer panorama;
    @Shadow @Final private boolean fading;
    @Shadow private long fadeInStart;
    @Nullable @Shadow(remap = false) private TitleScreenModUpdateIndicator modUpdateNotification;
    private AbstractWidget lilypadButton;
    private AbstractWidget discordButton;

    protected TitleScreenMixin(Component pTitle) {
        super(pTitle);
    }

    private boolean textureExists(ResourceLocation resource) {
        try {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            return resourceManager.getResource(resource).isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        // Promo buttons disabled.
    }


    private void openLink(String url) {
        try {
            URI uri = new URI(url);
            Util.getPlatform().openUri(uri);
        } catch (Exception e) {
            System.err.println("Failed to open URL: " + e.getMessage());
        }
    }

    private static final int titleWidth = 300;
    private static final int titleHeight = 106;
    private static final int titleTextureWidth = 1080;
    private static final int titleTextureHeight = 380;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY,
                        float pPartialTick, CallbackInfo ci) {
        ci.cancel();
        PoseStack pPoseStack = guiGraphics.pose();
        // Handle fade-in effect
        if (this.fadeInStart == 0L && this.fading) {
            this.fadeInStart = Util.getMillis();
        }
        float fadeProgress = this.fading
                ? (float) (Util.getMillis() - this.fadeInStart) / 1000.0F
                : 1.0F;

        TitleClientEvents.getPanorama().render(pPartialTick, Mth.clamp(fadeProgress, 0.0F, 1.0F));
        int logoX = this.width / 2 - (titleWidth / 2);
        int singleplayerTopY = this.height / 4 + 48;
        int titleY = (singleplayerTopY - titleHeight) / 2;

        float alpha = this.fading ? Mth.clamp(fadeProgress - 1.0F, 0.0F, 1.0F) : 1.0F;
        int alphaMask = Mth.ceil(alpha * 255.0F) << 24;

        if ((alphaMask & -67108864) != 0) {
            // Ensure proper blending and color state
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);

            // Render Minecraft logo
            RenderSystem.setShaderTexture(0, MINECRAFT_LOGO);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
            pPoseStack.pushPose();
            pPoseStack.translate(logoX, titleY, 0.0F);
            pPoseStack.scale(
                    (float) titleWidth / (float) titleTextureWidth,
                    (float) titleHeight / (float) titleTextureHeight,
                    1.0F
            );
            guiGraphics.blit(MINECRAFT_LOGO, 0, 0, 0.0F, 0.0F,
                    titleTextureWidth, titleTextureHeight,
                    titleTextureWidth, titleTextureHeight
            );
            pPoseStack.popPose();

            // Render main menu elements and splash text
            ForgeHooksClient.renderMainMenu((TitleScreen) Minecraft.getInstance().screen,
                    guiGraphics, this.font, this.width, this.height, alphaMask);

            if (TitleClientEvents.splash != null) {
                pPoseStack.pushPose();
                pPoseStack.translate(this.width / 2 + 90, 82.0, 0.0);
                pPoseStack.mulPose(Axis.ZP.rotationDegrees(-20.0F));
                float scale = 1.8F - Mth.abs(Mth.sin((float) (Util.getMillis() % 1000L) / 1000.0F * 6.2831855F) * 0.1F);
                scale = scale * 100.0F / (float) (this.font.width(TitleClientEvents.splash) + 32);
                pPoseStack.scale(scale, scale, scale);
                int splashX = Math.max(0, this.font.width(TitleClientEvents.splash) / 5);
                guiGraphics.drawCenteredString(this.font, TitleClientEvents.splash, splashX + 14, splashX - 2, 16776960 | alphaMask);
                pPoseStack.popPose();
            }

            // Render branding lines
            BrandingControl.forEachLine(true, true, (line, text) -> {
                if (line == 1) {
                    text = "Siege of Settlements " + SiegeOfSettlements.VERSION_STRING;
                }
                guiGraphics.drawString(this.font, text, 2, this.height - (10 + line * (9 + 1)), 16777215 | alphaMask);
            });

            //BrandingControl.forEachAboveCopyrightLine((line, text) -> {
            //    int xPos = this.width - this.font.width(text);
            //    drawString(pPoseStack, this.font, text, xPos, this.height - (10 + (line + 1) * (9 + 1)), 16777215 | alphaMask);
            //});

            // Adjust widget transparency
            for (GuiEventListener child : this.children()) {
                if (child instanceof AbstractWidget widget) {
                    widget.setAlpha(alpha);
                }
            }

            // Call the superclass render
            super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

            // Render mod update notification
            if (alpha >= 1.0F && this.modUpdateNotification != null) {
                this.modUpdateNotification.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
            }

            // Disable blending after rendering
            RenderSystem.disableBlend();
        }
    }

    /*
    @Shadow private Component getMultiplayerDisabledReason() { return null; }
    @Shadow private void realmsButtonClicked() { }

    private final int menuOffsetY = 20;

    @Inject(method = "createNormalMenuOptions", at = @At("HEAD"), cancellable = true)
    private void createNormalMenuOptions(int pY, int pRowHeight, CallbackInfo ci) {
        ci.cancel();
        this.addRenderableWidget(Button.builder(Component.translatable("menu.singleplayer"), (p_280833_) -> {
            this.minecraft.setScreen(new SelectWorldScreen(this));
        }).bounds(this.width / 2 - 100, pY + menuOffsetY, 200, 20).build());
        Component component = this.getMultiplayerDisabledReason();
        boolean flag = component == null;
        Tooltip tooltip = component != null ? Tooltip.create(component) : null;
        ((Button)this.addRenderableWidget(Button.builder(Component.translatable("menu.multiplayer"), (p_210872_) -> {
            Screen screen = this.minecraft.options.skipMultiplayerWarning ? new JoinMultiplayerScreen(this) : new SafetyScreen(this);
            this.minecraft.setScreen((Screen)screen);
        }).bounds(this.width / 2 - 100, pY + menuOffsetY + pRowHeight * 1, 200, 20).tooltip(tooltip).build())).active = flag;
        ((Button)this.addRenderableWidget(Button.builder(Component.translatable("menu.online"), (p_210872_) -> {
            this.realmsButtonClicked();
        }).bounds(this.width / 2 + 2, pY + menuOffsetY + pRowHeight * 2, 98, 20).tooltip(tooltip).build())).active = flag;
    }
     */
}
