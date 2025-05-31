package net.cchat.cchatmod.mixins;

import com.mojang.blaze3d.platform.InputConstants;
import net.cchat.cchatmod.CChatMod;
import net.cchat.cchatmod.api.TaskAPI;
import net.cchat.cchatmod.core.ModKeyBindings;
import net.cchat.cchatmod.data.tasks.TaskStatus;
import net.cchat.cchatmod.gui.chat.CChatModEvents;
import net.cchat.cchatmod.gui.screens.TaskScreen;
import net.minecraft.Util;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Shadow private long debugCrashKeyTime;

    @Inject(method = "handleDebugKeys", at = @At("TAIL"))
    private void handleDebugKeys(int key, CallbackInfoReturnable<Boolean> cir){
        if(!(this.debugCrashKeyTime > 0L && this.debugCrashKeyTime < Util.getMillis() - 100L)){
            if(key == InputConstants.KEY_D){
                CChatModEvents.getInstance().clearChatHistory();
            }
        }
    }

    @Inject(method = "keyPress", at = @At(value = "FIELD", target="Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;", opcode = Opcodes.GETFIELD, ordinal = 0))
    private void onKey(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci){
        if (ModKeyBindings.OPEN_TASK_SCREEN.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            mc.setScreen(new TaskScreen(CChatMod.TASK_MANAGER));
        }
    }
}
