package net.cchat.cchatmod.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class AnimatedButton extends AbstractWidget {
    private final Minecraft minecraft = Minecraft.getInstance();
    private float hoverOffset = 0.0f;
    private static final float HOVER_SPEED = 0.2f;
    private static final int MAX_HOVER_OFFSET = 3;
    private boolean active = false;
    private final OnPress onPress;
    private final ResourceLocation backgroundTexture;

    public AnimatedButton(int x, int y, int width, int height, Component title, OnPress onPress, ResourceLocation backgroundTexture) {
        super(x, y, width, height, title);
        this.onPress = onPress;
        this.backgroundTexture = backgroundTexture;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        this.playDownSound(minecraft.getSoundManager());
        this.onPress.onPress(this);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        boolean hovered = this.isHoveredOrFocused();
        if (hovered || active) {
            hoverOffset = Math.min(MAX_HOVER_OFFSET, hoverOffset + HOVER_SPEED);
        } else {
            hoverOffset = Math.max(0, hoverOffset - HOVER_SPEED);
        }

        int drawY = this.getY() - (int) hoverOffset;

        if (active) {
            RenderSystem.setShaderColor(1.2f, 1.2f, 1.2f, 1.0f);
        } else {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }

        guiGraphics.blit(RenderType::guiTextured, backgroundTexture, this.getX(), drawY, 0, 0, this.width, this.height, this.width, this.height);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @FunctionalInterface
    public interface OnPress {
        void onPress(AnimatedButton button);
    }
}
