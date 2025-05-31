package net.cchat.cchatmod.mixins;

import net.cchat.cchatmod.gui.chat.CChatModEvents;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(GameConfig gameConfig, CallbackInfo ci){
        CChatModEvents.getInstance();
    }
}
