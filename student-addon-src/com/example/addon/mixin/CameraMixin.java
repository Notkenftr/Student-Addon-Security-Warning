package com.example.addon.mixin;

import com.example.addon.modules.StudentFreeCam;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.class_1297;
import net.minecraft.class_1922;
import net.minecraft.class_4184;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_4184.class})
public abstract class CameraMixin {
   @Shadow
   protected abstract void method_19327(double var1, double var3, double var5);

   @Shadow
   protected abstract void method_19325(float var1, float var2);

   @Inject(
      method = {"update"},
      at = {@At("TAIL")}
   )
   private void onUpdate(class_1922 area, class_1297 focusedEntity, boolean thirdPerson, boolean frontView, float tickDelta, CallbackInfo ci) {
      StudentFreeCam freecam = (StudentFreeCam)Modules.get().get(StudentFreeCam.class);
      if (freecam != null && freecam.isActive()) {
         this.method_19327(freecam.getX(tickDelta), freecam.getY(tickDelta), freecam.getZ(tickDelta));
         this.method_19325(freecam.getYaw(tickDelta), freecam.getPitch(tickDelta));
      }

   }

   @Inject(
      method = {"isThirdPerson"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onIsThirdPerson(CallbackInfoReturnable<Boolean> cir) {
      if (Modules.get().isActive(StudentFreeCam.class)) {
         cir.setReturnValue(true);
      }

   }
}
