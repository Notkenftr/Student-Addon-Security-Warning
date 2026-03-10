package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2338;

public class SafeWalk extends Module {
   public SafeWalk() {
      super(AddonTemplate.Student, "Safe Walk", "Sneaks when near edge of block.");
   }

   @EventHandler
   private void onTick(Pre event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (this.mc.field_1724.method_24828()) {
            this.mc.field_1724.method_5660(this.isNearEdge());
            this.mc.field_1690.field_1832.method_23481(this.isNearEdge());
         }
      }
   }

   public void onDeactivate() {
      if (this.mc.field_1724 != null) {
         this.mc.field_1724.method_5660(false);
         this.mc.field_1690.field_1832.method_23481(false);
      }
   }

   private boolean isNearEdge() {
      double x = this.mc.field_1724.method_23317();
      double y = this.mc.field_1724.method_23318();
      double z = this.mc.field_1724.method_23321();
      double[] offsets = new double[]{1.0E-7D, -1.0E-7D};
      double[] var8 = offsets;
      int var9 = offsets.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         double ox = var8[var10];
         double[] var13 = offsets;
         int var14 = offsets.length;

         for(int var15 = 0; var15 < var14; ++var15) {
            double oz = var13[var15];
            class_2338 below = class_2338.method_49637(x + ox, y - 0.5D, z + oz);
            if (this.mc.field_1687.method_8320(below).method_26215()) {
               return true;
            }
         }
      }

      return false;
   }
}
