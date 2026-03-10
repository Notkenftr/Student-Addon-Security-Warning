package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class FreezePlayer extends Module {
   private double savedX;
   private double savedY;
   private double savedZ;

   public FreezePlayer() {
      super(AddonTemplate.Student_pvp, "Freeze", "Đóng băng nhân vật tại chỗ.");
   }

   public void onActivate() {
      if (this.mc.field_1724 != null) {
         this.savedX = this.mc.field_1724.method_23317();
         this.savedY = this.mc.field_1724.method_23318();
         this.savedZ = this.mc.field_1724.method_23321();
      }
   }

   public void onDeactivate() {
      if (this.mc.field_1724 != null) {
         this.mc.field_1724.method_18800(0.0D, 0.0D, 0.0D);
      }
   }

   @EventHandler
   private void onTick(Pre event) {
      if (this.mc.field_1724 != null) {
         this.mc.field_1724.method_18800(0.0D, 0.0D, 0.0D);
         this.mc.field_1724.method_5814(this.savedX, this.savedY, this.savedZ);
      }
   }
}
