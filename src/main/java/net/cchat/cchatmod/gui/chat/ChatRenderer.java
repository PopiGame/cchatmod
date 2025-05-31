package net.cchat.cchatmod.gui.chat;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.cchat.cchatmod.gui.chat.ChatHistoryManager.ChatMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.List;

public class ChatRenderer {
    private final Minecraft minecraft;
    private final Font font;
    private final float chatWidthRatio;
    private final int yOffset;
    private final int fadeHeight = 20;

    public ChatRenderer(Minecraft minecraft, Font font, float chatWidthRatio, int yOffset) {
        this.minecraft = minecraft;
        this.font = font;
        this.chatWidthRatio = chatWidthRatio;
        this.yOffset = yOffset;
    }

    public void render(GuiGraphics poseStack, int screenWidth, int screenHeight, boolean isChatOpen, ChatHistoryManager chatHistoryManager) {
        if (minecraft.player != null && minecraft.player.isSleeping()) {
            return;
        }

        if (isChatOpen) {
            poseStack.pose().pushPose();
            ChatBackgroundRenderer.drawBackground(poseStack, screenWidth, screenHeight);
            poseStack.pose().popPose();
        }

        poseStack.pose().pushPose();
        poseStack.pose().translate(0, 0, 100);
        long currentTick = chatHistoryManager.getCurrentTick();
        int maxWidth = (int) (screenWidth * chatWidthRatio);
        if (isChatOpen) {
            renderChatHistory(poseStack, screenWidth, screenHeight, maxWidth, chatHistoryManager);
        } else if (chatHistoryManager.getCurrentMessage() != null && currentTick < chatHistoryManager.getMessageEndTick()) {
            renderMessage(poseStack, chatHistoryManager.getCurrentMessage(), screenWidth, screenHeight + yOffset, maxWidth);
        }
        poseStack.pose().popPose();
    }

    private void renderChatHistory(GuiGraphics poseStack, int screenWidth, int screenHeight, int maxWidth, ChatHistoryManager chatHistoryManager) {
        int topBoundary = (int)(screenHeight * 0.12f);
        int bottomY = screenHeight + yOffset;
        int extraBottomMargin = 5;
        int effectiveBottomY = bottomY + extraBottomMargin;

        int visibleHeight = bottomY - topBoundary;

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        int scaleFactor = (int)minecraft.getWindow().getGuiScale();
        int scissorX = 0;
        int scissorY = screenHeight - effectiveBottomY;
        int scissorWidth = screenWidth;
        int scissorHeight = effectiveBottomY - topBoundary;
        GL11.glScissor(scissorX * scaleFactor, scissorY * scaleFactor, scissorWidth * scaleFactor, scissorHeight * scaleFactor);

        LinkedList<ChatMessage> history = chatHistoryManager.getChatHistory();
        int totalContentHeight = 0;
        for (ChatMessage msg : history) {
            int msgHeight = calculateMessageHeight(msg, maxWidth);
            totalContentHeight += msgHeight + 10;
        }
        if (totalContentHeight > 0) {
            totalContentHeight -= 10;
        }

        int maxScroll = Math.max(0, totalContentHeight - visibleHeight);
        int scrollOffset = chatHistoryManager.getScrollPixelOffset();
        if (scrollOffset > maxScroll) {
            scrollOffset = maxScroll;
            chatHistoryManager.setScrollPixelOffset(maxScroll);
        }
        if (scrollOffset < 0) {
            scrollOffset = 0;
            chatHistoryManager.setScrollPixelOffset(0);
        }

        int startY = bottomY - totalContentHeight + scrollOffset;
        int currentY = startY;
        for (ChatMessage message : history) {
            int msgHeight = calculateMessageHeight(message, maxWidth);
            int messageTop = currentY;
            int messageBottom = currentY + msgHeight;
            if (messageBottom >= topBoundary && messageTop <= bottomY) {
                renderMessage(poseStack, message, screenWidth, currentY + msgHeight, maxWidth, topBoundary);
            }
            currentY += msgHeight + 10;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }


    private void renderMessage(GuiGraphics poseStack, ChatMessage message, int screenWidth, int yPosition, int maxWidth, int topBoundary) {
        List<FormattedCharSequence> lines = font.split(message.getText(), maxWidth);
        int panelWidth = calculatePanelWidth(lines);
        int panelHeight = calculatePanelHeight(lines);
        int xPosition = (screenWidth - panelWidth) / 2;

        drawBackground(poseStack, xPosition, yPosition, panelWidth, panelHeight);
        drawIcon(poseStack, message.getIcon(), xPosition, yPosition, panelHeight);
        drawText(poseStack, lines, xPosition, yPosition, panelHeight);

        int messageTop = yPosition - panelHeight;
        if (messageTop < topBoundary) {
            int fadeRegionHeight = topBoundary - messageTop;
            fadeRegionHeight = Math.min(fadeRegionHeight, fadeHeight);
            drawFadeOverlay(poseStack, xPosition, messageTop, panelWidth, topBoundary, fadeRegionHeight);
        }
    }

    private void renderMessage(GuiGraphics poseStack, ChatMessage message, int screenWidth, int yPosition, int maxWidth) {
        List<FormattedCharSequence> lines = font.split(message.getText(), maxWidth);
        int panelWidth = calculatePanelWidth(lines);
        int panelHeight = calculatePanelHeight(lines);
        int xPosition = (screenWidth - panelWidth) / 2;

        drawBackground(poseStack, xPosition, yPosition, panelWidth, panelHeight);
        drawIcon(poseStack, message.getIcon(), xPosition, yPosition, panelHeight);
        drawText(poseStack, lines, xPosition, yPosition, panelHeight);
    }

    private void drawBackground(GuiGraphics poseStack, int xPosition, int yPosition, int width, int height) {
        poseStack.fill(xPosition - 2, yPosition - height - 2, xPosition + width + 2, yPosition + 2, 0x80000000);
    }

    private void drawIcon(GuiGraphics poseStack, ResourceLocation icon, int xPosition, int yPosition, int panelHeight) {
        int iconX = xPosition + 5;
        int iconY = yPosition - panelHeight + (panelHeight - 16) / 2;
        poseStack.blit(RenderType::guiTextured, icon, iconX, iconY, 0, 0, 16, 16, 16, 16);
    }

    private void drawText(GuiGraphics poseStack, List<FormattedCharSequence> lines, int xPosition, int yPosition, int panelHeight) {
        int textX = xPosition + 27;
        int totalTextHeight = lines.size() * 12;
        int textY = yPosition - panelHeight + (panelHeight - totalTextHeight) / 2 + 2;
        for (FormattedCharSequence line : lines) {
            poseStack.drawString(font, line, textX, textY, -1);
            textY += 12;
        }
    }

    private int calculatePanelWidth(List<FormattedCharSequence> lines) {
        return lines.stream().mapToInt(font::width).max().orElse(0) + 30;
    }

    private int calculatePanelHeight(List<FormattedCharSequence> lines) {
        return lines.size() * 12 + 10;
    }

    private int calculateMessageHeight(ChatMessage message, int maxWidth) {
        List<FormattedCharSequence> lines = font.split(message.getText(), maxWidth);
        return calculatePanelHeight(lines);
    }

    private void drawFadeOverlay(GuiGraphics graphics, int x, int messageTop, int width, int topBoundary, int fadeRegionHeight) {
        RenderSystem.enableBlend();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        float maxAlpha = 0.8f;
        float yTop = messageTop;
        float yBottom = messageTop + fadeRegionHeight;

        buffer.addVertex(x, yTop, 0).setColor(0, 0, 0, maxAlpha);
        buffer.addVertex(x + width, yTop, 0).setColor(0, 0, 0, maxAlpha);
        buffer.addVertex(x + width, yBottom, 0).setColor(0, 0, 0, 0f);
        buffer.addVertex(x, yBottom, 0).setColor(0, 0, 0, 0f);
        RenderSystem.disableBlend();

        RenderSystem.setShader(CoreShaders.POSITION_COLOR);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        BufferUploader.draw(buffer.buildOrThrow());
    }
}