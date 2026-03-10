package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;

public class AutoMine extends Module {
   private class_2338 lastPos = null;
   private class_2350 lastSide = null;
   private boolean mining = false;
   private int ticksMining = 0;
   private int requiredTicks = 0;

   public AutoMine() {
      super(AddonTemplate.Student, "Auto Mine", "Simulates holding left click to mine blocks.");
   }

   private int calcRequiredTicks(class_2338 pos) {
      class_2680 state = this.mc.field_1687.method_8320(pos);
      float hardness = state.method_26214(this.mc.field_1687, pos);
      if (hardness < 0.0F) {
         return 9999;
      } else if (hardness == 0.0F) {
         return 1;
      } else {
         float miningSpeed = this.mc.field_1724.method_6047().method_7924(state);
         boolean correctTool = this.mc.field_1724.method_6047().method_7951(state);
         float damage = miningSpeed / hardness / (correctTool ? 30.0F : 100.0F);
         int efficiency = this.mc.field_1724.method_6047().method_58657().method_57534().stream().filter((e) -> {
            return e.method_55840().contains("efficiency");
         }).mapToInt((e) -> {
            return this.mc.field_1724.method_6047().method_58657().method_57536(e);
         }).findFirst().orElse(0);
         if (efficiency > 0 && correctTool) {
            float bonus = (float)(efficiency * efficiency + 1);
            damage = (miningSpeed + bonus) / hardness / 30.0F;
         }

         return damage >= 1.0F ? 1 : (int)Math.ceil((double)(1.0F / damage));
      }
   }

   @EventHandler
   private void onTick(Pre event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (this.mc.field_1765 != null && this.mc.field_1765.method_17783() == class_240.field_1332) {
            class_3965 hit = (class_3965)this.mc.field_1765;
            class_2338 pos = hit.method_17777();
            class_2350 side = hit.method_17780();
            if (!pos.equals(this.lastPos) || side != this.lastSide) {
               this.stopMining();
               this.lastPos = pos;
               this.lastSide = side;
               this.requiredTicks = this.calcRequiredTicks(pos);
               this.ticksMining = 0;
               this.mining = false;
            }

            if (!this.mining) {
               boolean started = this.mc.field_1761.method_2910(pos, side);
               if (started) {
                  this.mc.field_1724.method_6104(class_1268.field_5808);
                  this.mining = true;
                  this.ticksMining = 1;
               }
            } else {
               ++this.ticksMining;
               this.mc.field_1761.method_2902(pos, side);
               if (this.ticksMining % 4 == 0) {
                  this.mc.field_1724.method_6104(class_1268.field_5808);
               }

               if (this.ticksMining >= this.requiredTicks + 2) {
                  this.mining = false;
                  this.ticksMining = 0;
                  this.lastPos = null;
                  this.lastSide = null;
               }
            }

         } else {
            this.stopMining();
         }
      }
   }

   private void stopMining() {
      if (this.mining) {
         this.mc.field_1761.method_2925();
      }

      this.lastPos = null;
      this.lastSide = null;
      this.mining = false;
      this.ticksMining = 0;
      this.requiredTicks = 0;
   }

   public void onDeactivate() {
      this.stopMining();
      if (this.mc.field_1690 != null) {
         this.mc.field_1690.field_1886.method_23481(false);
      }

   }
}
