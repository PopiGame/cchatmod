package net.cchat.cchatmod.mixins;

import net.cchat.cchatmod.gui.chat.CChatModEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.cchat.cchatmod.CChatMod.MOD_ID;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Shadow public ServerPlayer player;

    @Inject(method = "method_45064", at = @At("HEAD"))
    private void onChatMessage(PlayerChatMessage playerChatMessage, Component component, FilteredText filteredText, CallbackInfo ci){
        Minecraft.getInstance().execute(() ->
                CChatModEvents.getInstance().addMessage("[" + player.getGameProfile().getName() + "]: " + component.getString(), ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/default_icon.png"))
        );
    }
}
