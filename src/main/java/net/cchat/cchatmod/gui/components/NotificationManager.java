package net.cchat.cchatmod.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class NotificationManager {
    private static final Queue<Notification> notificationQueue = new LinkedList<>();

    public static void addNotification(String message) {
        notificationQueue.offer(new Notification(message, 3.0f));
    }

    public static void renderNotifications(GuiGraphics graphics, Font font, float deltaTime) {
        int index = 0;
        for (Notification n : notificationQueue) {
            n.render(graphics, font, index);
            index++;
        }

        while (!notificationQueue.isEmpty() && notificationQueue.peek().isExpired()) {
            notificationQueue.poll();
        }
    }
}