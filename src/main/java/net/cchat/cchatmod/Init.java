package net.cchat.cchatmod;

import net.cchat.cchatmod.commands.CommandTask;
import net.cchat.cchatmod.core.LogInterceptor;
import net.cchat.cchatmod.core.ModKeyBindings;
import net.cchat.cchatmod.gui.chat.CChatModEvents;
import net.cchat.cchatmod.gui.components.NotificationManager;
import net.cchat.cchatmod.gui.screens.CustomChatScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;

import static net.cchat.cchatmod.CChatMod.TASK_MANAGER;

public class Init implements ClientModInitializer {
    private boolean isChatScreenReplaced = false;

    @Override
    public void onInitializeClient() {
        LogInterceptor.init();
        TASK_MANAGER.loadTasks();

        KeyBindingHelper.registerKeyBinding(ModKeyBindings.OPEN_TASK_SCREEN);
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> CommandTask.register(dispatcher));

        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            boolean isChatOpen = minecraft.screen instanceof ChatScreen;
            if (isChatOpen && !isChatScreenReplaced && (minecraft.player == null || !minecraft.player.isSleeping())) {
                minecraft.setScreen(new CustomChatScreen("", CChatModEvents.getInstance()));
                isChatScreenReplaced = true;
            } else if (!isChatOpen) {
                isChatScreenReplaced = false;
            }
        });

        HudRenderCallback.EVENT.register(((guiGraphics, deltaTracker) -> {
            var mc = Minecraft.getInstance();
            NotificationManager.renderNotifications(guiGraphics, mc.font, deltaTracker.getGameTimeDeltaTicks());
        }));
    }
}
