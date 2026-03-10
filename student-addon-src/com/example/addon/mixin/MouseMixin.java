package com.example.addon.mixin;

import com.example.addon.modules.StudentFreeCam;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.class_312;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_312.class})
public abstract class MouseMixin {
   @Shadow
   private double field_1789;
   @Shadow
   private double field_1787;

   @Inject(
      method = {"updateMouse"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onUpdateMouse(CallbackInfo ci) {
      StudentFreeCam freecam = (StudentFreeCam)Modules.get().get(StudentFreeCam.class);
      if (freecam != null && freecam.isActive()) {
         freecam.changeLookDirection(this.field_1789, this.field_1787);
         this.field_1789 = 0.0D;
         this.field_1787 = 0.0D;
         ci.cancel();
      }

   }
}
