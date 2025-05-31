package net.cchat.cchatmod.gui.screens;

import net.cchat.cchatmod.gui.chat.CChatModEvents;
import net.minecraft.client.gui.screens.ChatScreen;

public class CustomChatScreen extends ChatScreen {
    private final CChatModEvents chatModEvents;

    public CustomChatScreen(String initial, CChatModEvents chatModEvents) {
        super(initial);
        this.chatModEvents = chatModEvents;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY != 0) {
            chatModEvents.adjustScrollOffset((int) scrollY);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}