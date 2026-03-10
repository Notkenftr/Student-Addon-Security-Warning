package com.example.addon.mixin;

import com.example.addon.modules.FreezePlayer;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_746.class})
public class PlayerMixin {
   @Inject(
      method = {"sendMovementPackets"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onSendMovementPackets(CallbackInfo ci) {
      if (Modules.get().isActive(FreezePlayer.class)) {
         ci.cancel();
      }

   }
}
