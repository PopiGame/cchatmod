package net.cchat.cchatmod.gui.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;

import static net.cchat.cchatmod.CChatMod.MOD_ID;

public class CChatModEvents {
    public static final ResourceLocation DEFAULT_ICON = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/default_icon.png");

    private static final int DISPLAY_TIME_TICKS = 200;
    private static final int MAX_HISTORY_SIZE = 50;
    private static final float CHAT_WIDTH_RATIO = 0.4f;
    private static final int Y_OFFSET = -70;

    private final ChatHistoryManager chatHistoryManager;
    private final ChatRenderer chatRenderer;

    private static CChatModEvents instance;

    public static synchronized CChatModEvents getInstance() {
        if (instance == null) {
            instance = new CChatModEvents(Minecraft.getInstance());
        }
        return instance;
    }

    private CChatModEvents(Minecraft minecraft) {
        this.chatHistoryManager = new ChatHistoryManager(MAX_HISTORY_SIZE, DISPLAY_TIME_TICKS, minecraft);
        this.chatRenderer = new ChatRenderer(minecraft, minecraft.font, CHAT_WIDTH_RATIO, Y_OFFSET);
    }

    public void addMessage(String text, ResourceLocation icon) {
        chatHistoryManager.addMessage(Component.literal(text), icon != null ? icon : DEFAULT_ICON);
    }

    public void addMessage(Component message, ResourceLocation icon) {
        chatHistoryManager.addMessage(message, icon != null ? icon : DEFAULT_ICON);
    }

    public void addMessage(String hex, String name, String message, ResourceLocation icon) {
        Component comp = createFormattedMessage(hex, name, message, null);
        addMessage(comp, icon);
    }

    public void addMessage(String hex, String name, String hex2, String message, ResourceLocation icon) {
        Component comp = createFormattedMessage(hex, name, message, hex2);
        addMessage(comp, icon);
    }

    private Component createFormattedMessage(String hex, String name, String message, String hex2) {
        int color1 = parseHex(hex);
        Integer color2 = hex2 != null ? parseHex(hex2) : null;

        MutableComponent bracketLeft = Component.literal("[").withStyle(style -> style.withColor(TextColor.fromRgb(color1)));
        MutableComponent nameComponent = Component.literal(name).withStyle(style -> style.withColor(TextColor.fromRgb(color1)));
        MutableComponent bracketRight = Component.literal("]").withStyle(style -> style.withColor(TextColor.fromRgb(color1)));
        MutableComponent prefix = Component.literal("")
                .append(bracketLeft)
                .append(nameComponent)
                .append(bracketRight);
        MutableComponent separator = Component.literal(": ").withStyle(Style.EMPTY);

        MutableComponent msgComponent = (color2 != null)
                ? Component.literal(message).withStyle(style -> style.withColor(TextColor.fromRgb(color2)))
                : Component.literal(message).withStyle(Style.EMPTY);

        return prefix.append(separator).append(msgComponent);
    }

    private int parseHex(String hex) {
        try {
            if (hex.startsWith("#")) {
                hex = hex.substring(1);
            }
            return Integer.parseInt(hex, 16);
        } catch (NumberFormatException e) {
            return 0xFFFFFF;
        }
    }

    public void clearChatHistory() {
        chatHistoryManager.clearHistory();
    }

    public void render(GuiGraphics guiGraphics, int screenWidth, int screenHeight, boolean isChatOpen) {
        chatRenderer.render(guiGraphics, screenWidth, screenHeight, isChatOpen, chatHistoryManager);
    }

    public void adjustScrollOffset(int delta) {
        chatHistoryManager.adjustScrollOffset(delta);
    }
}