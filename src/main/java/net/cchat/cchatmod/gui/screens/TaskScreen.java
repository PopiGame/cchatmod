package net.cchat.cchatmod.gui.screens;

import net.cchat.cchatmod.data.tasks.Task;
import net.cchat.cchatmod.data.tasks.TaskManager;
import net.cchat.cchatmod.data.tasks.TaskStatus;
import net.cchat.cchatmod.gui.components.AnimatedButton;
import net.cchat.cchatmod.gui.components.NotificationManager;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

import static net.cchat.cchatmod.CChatMod.MOD_ID;

public class TaskScreen extends Screen {
    private final TaskManager taskManager;
    private TaskStatus currentStatus = TaskStatus.ACTIVE;
    private Task selectedTask;

    private static final int COLOR_LEFT_PANEL = 0xBA000000;
    private static final int COLOR_RIGHT_PANEL = 0xAA1E1E1E;
    private static final int COLOR_TEXT = 0xFFFFFFFF;
    private static final int COLOR_ACCENT = 0xFFFFD700;

    private static final int NATIVE_TASK_TEXTURE_WIDTH = 180;
    private static final int NATIVE_TASK_TEXTURE_HEIGHT = 40;
    private static final int PANEL_PADDING = 10;
    private static final int TASK_VERTICAL_SPACING = 5;
    private static final int MAX_VISIBLE_TASKS = 8;
    private static final int MAX_TAB_BUTTON_SIZE = 24;

    private float accentAnimationProgress = 0.0f;
    private float detailAnimationProgress = 0.0f;

    private AnimatedButton activeTabButton;
    private AnimatedButton completedTabButton;
    private AnimatedButton failedTabButton;

    private final ResourceLocation taskPanelTexture;
    private int scrollOffset = 0;

    public TaskScreen(TaskManager taskManager) {
        super(Component.literal("Меню заданий"));
        this.taskManager = taskManager;
        this.taskPanelTexture = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/task.png");
    }

    @Override
    protected void init() {
        int leftPanelWidth = this.width / 4;
        int btnY = PANEL_PADDING;
        int sectionWidth = leftPanelWidth / 3;
        int tabButtonSize = (int) Math.min(sectionWidth * 0.8, MAX_TAB_BUTTON_SIZE);

        int tab1X = (sectionWidth - tabButtonSize) / 2;
        int tab2X = sectionWidth + (sectionWidth - tabButtonSize) / 2;
        int tab3X = 2 * sectionWidth + (sectionWidth - tabButtonSize) / 2;

        activeTabButton = new AnimatedButton(
                tab1X,
                btnY,
                tabButtonSize,
                tabButtonSize,
                Component.literal("★"),
                button -> {
                    currentStatus = TaskStatus.ACTIVE;
                    selectedTask = null;
                    detailAnimationProgress = 0.0f;
                    accentAnimationProgress = 0.0f;
                },
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/button_active.png")
        );
        completedTabButton = new AnimatedButton(
                tab2X,
                btnY,
                tabButtonSize,
                tabButtonSize,
                Component.literal("✔"),
                button -> {
                    currentStatus = TaskStatus.COMPLETED;
                    selectedTask = null;
                    detailAnimationProgress = 0.0f;
                    accentAnimationProgress = 0.0f;
                },
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/button_completed.png")
        );
        failedTabButton = new AnimatedButton(
                tab3X,
                btnY,
                tabButtonSize,
                tabButtonSize,
                Component.literal("✖"),
                button -> {
                    currentStatus = TaskStatus.FAILED;
                    selectedTask = null;
                    detailAnimationProgress = 0.0f;
                    accentAnimationProgress = 0.0f;
                },
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/button_failed.png")
        );

        this.addRenderableWidget(activeTabButton);
        this.addRenderableWidget(completedTabButton);
        this.addRenderableWidget(failedTabButton);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        activeTabButton.setActive(currentStatus == TaskStatus.ACTIVE);
        completedTabButton.setActive(currentStatus == TaskStatus.COMPLETED);
        failedTabButton.setActive(currentStatus == TaskStatus.FAILED);

        int leftPanelWidth = this.width / 4;
        int rightPanelX = leftPanelWidth;

        guiGraphics.fill(0, 0, leftPanelWidth, this.height, COLOR_LEFT_PANEL);
        guiGraphics.fill(rightPanelX, 0, this.width, this.height, COLOR_RIGHT_PANEL);

        int tabButtonSize = (int) Math.min((leftPanelWidth / 3) * 0.8, MAX_TAB_BUTTON_SIZE);
        int listStartY = PANEL_PADDING + tabButtonSize + 10;

        int availableWidth = leftPanelWidth - 2 * PANEL_PADDING;
        float scale = Math.min(1.0f, Math.min((float) availableWidth / NATIVE_TASK_TEXTURE_WIDTH,
                (float) (this.height / 2) / NATIVE_TASK_TEXTURE_HEIGHT));
        int taskDrawWidth = (int) (NATIVE_TASK_TEXTURE_WIDTH * scale);
        int taskDrawHeight = (int) (NATIVE_TASK_TEXTURE_HEIGHT * scale);

        int taskPanelX = PANEL_PADDING + (availableWidth - taskDrawWidth) / 2;

        List<Task> tasks = taskManager.getTasksByStatus(currentStatus);
        int visibleCount = Math.min(tasks.size(), MAX_VISIBLE_TASKS);

        for (int i = 0; i < visibleCount; i++) {
            Task task = tasks.get(i);
            int taskPanelY = listStartY + i * (taskDrawHeight + TASK_VERTICAL_SPACING);

            guiGraphics.blit(RenderType::guiTextured, taskPanelTexture, taskPanelX, taskPanelY, 0, 0, taskDrawWidth, taskDrawHeight, taskDrawWidth, taskDrawHeight);

            int maxTextWidth = taskDrawWidth - 10;
            String title = task.getTitle();
            if (font.width(title) > maxTextWidth) {
                title = font.plainSubstrByWidth(title, maxTextWidth - font.width("...")) + "...";
            }
            String author = "Автор: " + task.getAuthor();
            if (font.width(author) > maxTextWidth) {
                author = font.plainSubstrByWidth(author, maxTextWidth - font.width("...")) + "...";
            }

            int totalTextHeight = font.lineHeight * 2 + 2;
            int textStartY = taskPanelY + (taskDrawHeight - totalTextHeight) / 2;
            int textCenterX = taskPanelX + taskDrawWidth / 2;

            guiGraphics.drawCenteredString(font, title, textCenterX, textStartY, COLOR_TEXT);
            guiGraphics.drawCenteredString(font, author, textCenterX, textStartY + font.lineHeight + 2, 0xFFAAAAAA);

            if (selectedTask == task) {
                accentAnimationProgress = Math.min(1.0f, accentAnimationProgress + 0.05f);
                int borderOffset = (int) (2 * accentAnimationProgress);
                drawRectOutline(guiGraphics,
                        taskPanelX - borderOffset,
                        taskPanelY - borderOffset,
                        taskPanelX + taskDrawWidth + borderOffset,
                        taskPanelY + taskDrawHeight + borderOffset,
                        COLOR_ACCENT);
            }
        }

        if (selectedTask == null) {
            int rightPanelWidth = this.width - rightPanelX;
            guiGraphics.drawCenteredString(font, "Нажмите на задание для просмотра деталей",
                    rightPanelX + rightPanelWidth / 2, this.height / 2, COLOR_TEXT);
        } else {
            if (detailAnimationProgress < 1.0f) {
                detailAnimationProgress = Math.min(1.0f, detailAnimationProgress + 0.02f);
            }
            int offsetY = (int) (this.height * 0.10f);
            int detailHeight = this.height - 2 * offsetY;
            int availableWidthP = this.width - rightPanelX - 2 * PANEL_PADDING;
            int detailWidth = (int) (availableWidthP * 0.8);
            int detailX = rightPanelX + PANEL_PADDING + (availableWidthP - detailWidth) / 2;

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            ResourceLocation chatTop = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/chat_background_up2.png");
            guiGraphics.blit(RenderType::guiTextured, chatTop, detailX, offsetY, 0, 0, detailWidth, 20, detailWidth, 20);

            ResourceLocation chatBottom = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/chat_background_down2.png");
            guiGraphics.blit(RenderType::guiTextured, chatBottom, detailX, offsetY + detailHeight - 20, 0, 0, detailWidth, 20, detailWidth, 20);

            int fillMargin = 10;
            int fillX1 = detailX + fillMargin;
            int fillX2 = detailX + detailWidth - fillMargin;
            int fillWidth = fillX2 - fillX1;
            fillTransparent(guiGraphics, fillX1, offsetY + 20, fillX2, offsetY + detailHeight - 20, 0x70000000);
            RenderSystem.disableBlend();

            int topTextureHeight = 20;
            int bottomTextureHeight = 20;
            int verticalPadding = 5;
            int textAreaTop = offsetY + topTextureHeight + verticalPadding;
            int textAreaBottom = offsetY + detailHeight - bottomTextureHeight - verticalPadding;
            int visibleHeight = textAreaBottom - textAreaTop;
            int maxTextWidth = fillWidth - 20;

            int scaleFactor = (int) minecraft.getWindow().getGuiScale();
            int scissorX = fillX1 * scaleFactor;
            int scissorY = (this.height - textAreaBottom) * scaleFactor;
            int scissorWidth = fillWidth * scaleFactor;
            int scissorHeight = visibleHeight * scaleFactor;
            RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);

            int currentY = textAreaTop - scrollOffset;
            String title = selectedTask.getTitle();
            if (font.width(title) > maxTextWidth) {
                title = font.plainSubstrByWidth(title, maxTextWidth - font.width("...")) + "...";
            }
            guiGraphics.drawCenteredString(font, title, fillX1 + fillWidth / 2, currentY, COLOR_TEXT);
            currentY += font.lineHeight + 5;

            drawStringWithShadow(guiGraphics, font, "Цели:", fillX1 + 10, currentY, COLOR_TEXT);
            currentY += font.lineHeight + 2;
            for (String obj : selectedTask.getObjectives()) {
                List<FormattedCharSequence> wrappedObj = font.split(Component.literal("- " + obj), maxTextWidth);
                for (FormattedCharSequence line : wrappedObj) {
                    drawStringWithShadow(guiGraphics, font, line, fillX1 + 20, currentY, 0xFFCCCCCC);
                    currentY += font.lineHeight + 2;
                }
            }
            currentY += 5;

            drawStringWithShadow(guiGraphics, font, "Описание:", fillX1 + 10, currentY, COLOR_TEXT);
            currentY += font.lineHeight + 2;
            String[] paragraphs = selectedTask.getDescription().split("\n");
            for (String paragraph : paragraphs) {
                List<FormattedCharSequence> wrappedLines = font.split(Component.literal(paragraph), maxTextWidth);
                for (FormattedCharSequence textLine : wrappedLines) {
                    drawStringWithShadow(guiGraphics, font, textLine, fillX1 + 20, currentY, 0xFFCCCCCC);
                    currentY += font.lineHeight + 2;
                }
            }
            RenderSystem.disableScissor();
        }
        NotificationManager.renderNotifications(guiGraphics, font, eventDeltaTime(partialTick));
    }

    private void fillTransparent(GuiGraphics poseStack, int x1, int y1, int x2, int y2, int color) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        poseStack.fill(x1, y1, x2, y2, color);
        RenderSystem.disableBlend();
    }

    private float eventDeltaTime(float partialTick) {
        return 0.05f;
    }

    private void drawRectOutline(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
        graphics.fill(x1, y1, x2, y1 + 1, color);
        graphics.fill(x1, y2 - 1, x2, y2, color);
        graphics.fill(x1, y1, x1 + 1, y2, color);
        graphics.fill(x2 - 1, y1, x2, y2, color);
    }

    private void drawStringWithShadow(GuiGraphics graphics, Font font, String text, int x, int y, int color) {
        graphics.drawString(font, text, x, y, color, true);
    }

    private void drawStringWithShadow(GuiGraphics graphics, Font font, FormattedCharSequence text, int x, int y, int color) {
        graphics.drawString(font, text, x, y, color, true);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int leftPanelWidth = this.width / 4;
        int availableWidth = leftPanelWidth - 2 * PANEL_PADDING;
        float scale = (availableWidth < NATIVE_TASK_TEXTURE_WIDTH) ?
                (float) availableWidth / NATIVE_TASK_TEXTURE_WIDTH : 1.0f;
        int taskDrawWidth = (int) (NATIVE_TASK_TEXTURE_WIDTH * scale);
        int taskDrawHeight = (int) (NATIVE_TASK_TEXTURE_HEIGHT * scale);
        int taskPanelX = PANEL_PADDING + (availableWidth - taskDrawWidth) / 2;
        int tabButtonSize = (int) Math.min((leftPanelWidth / 3) * 0.8, MAX_TAB_BUTTON_SIZE);
        int listStartY = PANEL_PADDING + tabButtonSize + 10;

        if (mouseX >= taskPanelX && mouseX <= taskPanelX + taskDrawWidth) {
            List<Task> tasks = taskManager.getTasksByStatus(currentStatus);
            int visibleCount = Math.min(tasks.size(), MAX_VISIBLE_TASKS);
            for (int i = 0; i < visibleCount; i++) {
                int taskPanelY = listStartY + i * (taskDrawHeight + TASK_VERTICAL_SPACING);
                if (mouseY >= taskPanelY && mouseY <= taskPanelY + taskDrawHeight) {
                    selectedTask = tasks.get(i);
                    detailAnimationProgress = 0.0f;
                    accentAnimationProgress = 0.0f;
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (selectedTask != null) {
            scrollOffset -= (int) (scrollY * 10);
            if (scrollOffset < 0) {
                scrollOffset = 0;
            }
            int offsetY = (int) (this.height * 0.10f);
            int detailHeight = this.height - 2 * offsetY;
            int topTextureHeight = 20;
            int bottomTextureHeight = 20;
            int verticalPadding = 5;
            int textAreaTop = offsetY + topTextureHeight + verticalPadding;
            int textAreaBottom = offsetY + detailHeight - bottomTextureHeight - verticalPadding;
            int visibleHeight = textAreaBottom - textAreaTop;

            int rightPanelX = this.width / 4;
            int availableWidthP = this.width - rightPanelX - 2 * PANEL_PADDING;
            int detailWidth = (int) (availableWidthP * 0.8);
            int detailX = rightPanelX + PANEL_PADDING + (availableWidthP - detailWidth) / 2;
            int fillMargin = 10;
            int fillX1 = detailX + fillMargin;
            int fillX2 = detailX + detailWidth - fillMargin;
            int fillWidth = fillX2 - fillX1;
            int maxTextWidth = fillWidth - 20;

            int simulatedY = textAreaTop;
            simulatedY += font.lineHeight + 5;
            simulatedY += font.lineHeight + 2;
            for (String obj : selectedTask.getObjectives()) {
                List<FormattedCharSequence> wrappedObj = font.split(Component.literal("- " + obj), maxTextWidth);
                simulatedY += wrappedObj.size() * (font.lineHeight + 2);
            }
            simulatedY += 5;
            simulatedY += font.lineHeight + 2;
            for (String paragraph : selectedTask.getDescription().split("\n")) {
                List<FormattedCharSequence> wrappedLines = font.split(Component.literal(paragraph), maxTextWidth);
                simulatedY += wrappedLines.size() * (font.lineHeight + 2);
            }
            int totalTextHeight = simulatedY - textAreaTop;
            if (scrollOffset > Math.max(totalTextHeight - visibleHeight, 0)) {
                scrollOffset = Math.max(totalTextHeight - visibleHeight, 0);
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void onClose() {
        taskManager.saveTasks();
        super.onClose();
    }
}
