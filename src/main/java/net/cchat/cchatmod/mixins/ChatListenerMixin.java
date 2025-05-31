package net.cchat.cchatmod.mixins;

import net.cchat.cchatmod.gui.chat.CChatModEvents;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.cchat.cchatmod.CChatMod.MOD_ID;

@Mixin(ChatListener.class)
public class ChatListenerMixin {
    @Inject(method = "handleSystemMessage", at = @At("HEAD"), cancellable = true)
    private void onHandleSystemMessage(Component message, boolean overlay, CallbackInfo ci) {
        if (!overlay) {
            CChatModEvents.getInstance().addMessage(message.getString(),
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/system_icon.png"));
            ci.cancel();
        }
    }
}