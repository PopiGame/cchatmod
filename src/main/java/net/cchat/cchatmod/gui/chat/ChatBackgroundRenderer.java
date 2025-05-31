package net.cchat.cchatmod.gui.chat;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import static net.cchat.cchatmod.CChatMod.MOD_ID;

public class ChatBackgroundRenderer {
    private static final ResourceLocation CHAT_TOP = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/chat_background_up.png");
    private static final ResourceLocation CHAT_BOTTOM = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/chat_background_down.png");

    public static void drawBackground(GuiGraphics poseStack, int screenWidth, int screenHeight) {
        double ratio = 0.5;
        int offset = 5;
        int backgroundWidth = (int)(screenWidth * ratio);
        int centerX = (screenWidth - backgroundWidth) / 2;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        poseStack.blit(RenderType::guiTextured, CHAT_TOP, centerX, 0, 0, 0, backgroundWidth, 20, backgroundWidth, 20);
        RenderSystem.setShaderTexture(0, CHAT_BOTTOM);
        poseStack.blit(RenderType::guiTextured, CHAT_TOP, centerX, screenHeight - 20, 0, 0, backgroundWidth, 20, backgroundWidth, 20);

        fillTransparent(poseStack, centerX + offset, 20, centerX + backgroundWidth - offset, screenHeight - 20, 0x70000000);

        RenderSystem.disableBlend();
    }

    private static void fillTransparent(GuiGraphics poseStack, int x1, int y1, int x2, int y2, int color) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        poseStack.fill(x1, y1, x2, y2, color);
        RenderSystem.disableBlend();
    }
}
