package net.cchat.cchatmod.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class Notification {
    private final String message;
    private final float duration;
    private final long creationTime;

    private static final int START_Y = 10;
    private static final int END_Y = 150;
    private static final int SPACING = 5;

    private static final int FADE_Y = 150;
    private static final int FADE_RANGE = 20;

    public Notification(String message, float duration) {
        this.message = message;
        this.duration = duration;
        this.creationTime = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public float getProgress() {
        long now = System.currentTimeMillis();
        return Math.min((now - creationTime) / (duration * 1000f), 1.0f);
    }

    public boolean isExpired() {
        return getProgress() >= 1.0f;
    }

    public void render(GuiGraphics poseStack, Font font, int index) {
        float progress = getProgress();
        int currentY = START_Y + (int) ((END_Y - START_Y) * progress) + index * (font.lineHeight + SPACING);

        int textHeight = font.lineHeight;
        int bottomY = currentY + textHeight;

        int alpha = 255;
        if (bottomY > FADE_Y) {
            int overlap = bottomY - FADE_Y;
            float fadeFactor = Math.min(1.0f, (float) overlap / FADE_RANGE);
            alpha = (int) (255 * (1.0f - fadeFactor));
        }

        int textColor = (alpha << 24) | 0xFFFFFF;
        poseStack.drawString(font, message, 10, currentY, textColor,true);
    }
}