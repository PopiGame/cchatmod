package net.cchat.cchatmod.core;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static final KeyMapping OPEN_TASK_SCREEN = new KeyMapping(
            "key.cchatmod.opentasks",
            GLFW.GLFW_KEY_M,
            "key.categories.cchatmod.custom"
    );
}
