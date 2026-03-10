package com.example.addon.mixin;

import com.example.addon.modules.CustomCrosshair;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.class_329;
import net.minecraft.class_332;
import net.minecraft.class_9779;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_329.class})
public class InGameHudMixin {
   @Inject(
      method = {"renderCrosshair"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRenderCrosshair(class_332 context, class_9779 counter, CallbackInfo ci) {
      CustomCrosshair mod = (CustomCrosshair)Modules.get().get(CustomCrosshair.class);
      if (mod != null && mod.isActive()) {
         mod.renderMixin(context);
         ci.cancel();
      }

   }
}
