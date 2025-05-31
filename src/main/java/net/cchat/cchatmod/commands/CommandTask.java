package net.cchat.cchatmod.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.cchat.cchatmod.gui.chat.CChatModEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CommandTask {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("clearchat")
                        .executes(context -> {
                            CChatModEvents.getInstance().clearChatHistory();
                            return 1;
                        })
        );
    }
}
