package net.cchat.cchatmod.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class ChatEnterMixin {
    @Shadow(remap = true)
    protected EditBox input;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.isSleeping()) {
            return;
        }
        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        int customWidth = 150;
        int customHeight = 22;
        int posX = (screenWidth - customWidth) / 2;
        int posY = screenHeight - 50;

        ci.cancel();
        if (Minecraft.getInstance() == null || this.input == null) {
            return;
        }
        this.input.setWidth(customWidth - 10);
        this.input.setX(posX + 3);
        this.input.setY(posY + 7);
        guiGraphics.fill(posX, posY, posX + customWidth, posY + customHeight, 0x70000000);
        this.input.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}