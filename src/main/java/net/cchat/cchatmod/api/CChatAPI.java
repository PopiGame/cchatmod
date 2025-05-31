package net.cchat.cchatmod.api;

import net.cchat.cchatmod.gui.chat.CChatModEvents;
import net.minecraft.resources.ResourceLocation;

public class CChatAPI {
    public static void sendMessage(String text, ResourceLocation icon) {
        CChatModEvents.getInstance().addMessage(text, icon);
    }

    public static void sendMessage(String hex, String name, String message, ResourceLocation icon) {
        CChatModEvents.getInstance().addMessage(hex, name, message, icon);
    }

    public static void sendMessage(String hex, String name, String hex2, String message, ResourceLocation icon) {
        CChatModEvents.getInstance().addMessage(hex, name, hex2, message, icon);
    }
}
