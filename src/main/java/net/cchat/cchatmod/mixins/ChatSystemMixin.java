package net.cchat.cchatmod.mixins;

import net.cchat.cchatmod.gui.chat.CChatModEvents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static net.cchat.cchatmod.CChatMod.MOD_ID;

@Mixin(ChatComponent.class)
public class ChatSystemMixin {
    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;)V", at = @At("HEAD"), cancellable = true)
    private void onAddMessage(Component message, CallbackInfo ci) {
        CChatModEvents.getInstance().addMessage(message.getString(),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/system_icon.png")
        );
        ci.cancel();
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void removeRender(GuiGraphics guiGraphics, int tickCount, int mouseX, int mouseY, boolean focused, CallbackInfo ci){
        ci.cancel();
    }
}