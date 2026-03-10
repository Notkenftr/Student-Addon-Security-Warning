package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class AutoWalk extends Module {
   public AutoWalk() {
      super(AddonTemplate.Student, "Auto Walk", "Automatically walks forward.");
   }

   @EventHandler
   private void onTick(Pre event) {
      if (this.mc.field_1724 != null) {
         this.mc.field_1690.field_1894.method_23481(true);
      }
   }

   public void onDeactivate() {
      this.mc.field_1690.field_1894.method_23481(false);
   }
}
