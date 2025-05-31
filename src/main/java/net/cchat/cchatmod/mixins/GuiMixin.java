package net.cchat.cchatmod.mixins;

import net.cchat.cchatmod.gui.chat.CChatModEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Shadow @Final private LayeredDraw layers;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initGui(Minecraft minecraft, CallbackInfo ci){
        LayeredDraw cchat = new LayeredDraw();
        cchat.add((guiGraphics, deltaTracker) -> {
            boolean isChatOpen = minecraft.screen instanceof ChatScreen;
            CChatModEvents.getInstance().render(guiGraphics, minecraft.getWindow().getGuiScaledWidth(),
                    minecraft.getWindow().getGuiScaledHeight(), isChatOpen);
        });
        this.layers.add(cchat, () -> !minecraft.options.hideGui);
    }
}
