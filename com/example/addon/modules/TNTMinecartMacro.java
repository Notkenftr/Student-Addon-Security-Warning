package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import java.util.Iterator;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.BoolSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1701;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2868;
import net.minecraft.class_2885;
import net.minecraft.class_3965;
import net.minecraft.class_3966;

public class TNTMinecartMacro extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> autoSwapTNT;
   private final Setting<Boolean> autoSwapBow;
   private final Setting<Boolean> autoAim;
   private final Setting<Double> aimRange;
   private int step;
   private class_2338 lastRailPos;
   private int prevRailCount;

   public TNTMinecartMacro() {
      super(AddonTemplate.Student_pvp, "TNT-Minecart-Marco", "");
      this.sgGeneral = this.settings.createGroup("Settings");
      this.autoSwapTNT = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("rail-to-tnt")).description("Auto swap to TNT Minecart when aiming at rail")).defaultValue(true)).build());
      this.autoSwapBow = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("tnt-to-bow")).description("Auto swap to Bow when aiming at TNT Minecart")).defaultValue(true)).build());
      this.autoAim = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("auto-aim")).description("Auto aim at nearest TNT Minecart")).defaultValue(true)).build());
      this.aimRange = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)(new meteordevelopment.meteorclient.settings.DoubleSetting.Builder()).name("aim-range")).description("Range to auto aim at TNT Minecart (blocks)")).defaultValue(10.0D).min(1.0D).sliderMax(30.0D).build());
      this.step = 0;
      this.lastRailPos = null;
      this.prevRailCount = 0;
   }

   public void onActivate() {
      this.step = 0;
      this.lastRailPos = null;
      this.prevRailCount = this.countRailInHand();
   }

   private void switchSlot(int slot) {
      this.mc.field_1724.field_3944.method_52787(new class_2868(slot));
   }

   private boolean isAimingAtRail() {
      class_239 var2 = this.mc.field_1765;
      if (!(var2 instanceof class_3965)) {
         return false;
      } else {
         class_3965 hit = (class_3965)var2;
         class_2248 block = this.mc.field_1687.method_8320(hit.method_17777()).method_26204();
         return block == class_2246.field_10167 || block == class_2246.field_10425 || block == class_2246.field_10025 || block == class_2246.field_10546;
      }
   }

   private boolean isAimingAtTNTMinecart() {
      class_239 var2 = this.mc.field_1765;
      if (var2 instanceof class_3966) {
         class_3966 hit = (class_3966)var2;
         return hit.method_17782() instanceof class_1701;
      } else {
         return false;
      }
   }

   private class_1701 findNearestTNTMinecart() {
      double range = (Double)this.aimRange.get();
      class_1701 nearest = null;
      double nearestDist = Double.MAX_VALUE;
      Iterator var6 = this.mc.field_1687.method_18112().iterator();

      while(var6.hasNext()) {
         class_1297 entity = (class_1297)var6.next();
         if (entity instanceof class_1701) {
            class_1701 tnt = (class_1701)entity;
            double dist = (double)this.mc.field_1724.method_5739(tnt);
            if (dist <= range && dist < nearestDist) {
               nearestDist = dist;
               nearest = tnt;
            }
         }
      }

      return nearest;
   }

   private void aimAt(class_243 target) {
      class_243 eyes = this.mc.field_1724.method_33571();
      class_243 diff = target.method_1020(eyes);
      double yaw = Math.toDegrees(Math.atan2(-diff.field_1352, diff.field_1350));
      double pitch = Math.toDegrees(-Math.atan2(diff.field_1351, Math.sqrt(diff.field_1352 * diff.field_1352 + diff.field_1350 * diff.field_1350)));
      this.mc.field_1724.method_36456((float)yaw);
      this.mc.field_1724.method_36457((float)pitch);
   }

   @EventHandler
   private void onTick(Post event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         class_1792 held;
         boolean isHoldingRail;
         int currentCount;
         if ((Boolean)this.autoSwapTNT.get()) {
            held = this.mc.field_1724.method_6047().method_7909();
            isHoldingRail = held == class_1802.field_8129 || held == class_1802.field_8848 || held == class_1802.field_8211;
            if (isHoldingRail && this.isAimingAtRail()) {
               currentCount = this.findItem(class_1802.field_8069);
               if (currentCount != -1) {
                  this.switchSlot(currentCount);
               }
            }
         }

         int tntSlot;
         if ((Boolean)this.autoSwapBow.get() && this.isAimingAtTNTMinecart()) {
            tntSlot = this.findItem(class_1802.field_8102);
            if (tntSlot != -1) {
               this.switchSlot(tntSlot);
            }
         }

         if ((Boolean)this.autoAim.get()) {
            class_1701 nearest = this.findNearestTNTMinecart();
            if (nearest != null) {
               this.aimAt(nearest.method_19538().method_1031(0.0D, 0.5D, 0.0D));
            }
         }

         if (this.step != 0) {
            if (this.step == 1 && this.lastRailPos != null) {
               tntSlot = this.findItem(class_1802.field_8069);
               if (tntSlot == -1) {
                  this.step = 0;
                  return;
               }

               this.switchSlot(tntSlot);
               class_3965 placeHit = new class_3965(class_243.method_24953(this.lastRailPos).method_1031(0.0D, 0.5D, 0.0D), class_2350.field_11036, this.lastRailPos, false);
               this.mc.field_1724.field_3944.method_52787(new class_2885(class_1268.field_5808, placeHit, 0));
               this.mc.field_1724.method_6104(class_1268.field_5808);
               this.step = 0;
               this.lastRailPos = null;
               this.prevRailCount = this.countRailInHand();
            }

         } else {
            held = this.mc.field_1724.method_6047().method_7909();
            isHoldingRail = held == class_1802.field_8129 || held == class_1802.field_8848 || held == class_1802.field_8211;
            if (!isHoldingRail) {
               this.prevRailCount = 0;
            } else {
               currentCount = this.mc.field_1724.method_6047().method_7947();
               if (this.prevRailCount == 0) {
                  this.prevRailCount = currentCount;
               } else {
                  if (currentCount < this.prevRailCount) {
                     this.prevRailCount = currentCount;
                     class_239 var6 = this.mc.field_1765;
                     if (var6 instanceof class_3965) {
                        class_3965 hit = (class_3965)var6;
                        this.lastRailPos = hit.method_17777();
                        this.step = 1;
                     }
                  }

               }
            }
         }
      }
   }

   private int countRailInHand() {
      if (this.mc.field_1724 == null) {
         return 0;
      } else {
         class_1792 item = this.mc.field_1724.method_6047().method_7909();
         return item != class_1802.field_8129 && item != class_1802.field_8848 && item != class_1802.field_8211 ? 0 : this.mc.field_1724.method_6047().method_7947();
      }
   }

   private int findItem(class_1792 item) {
      for(int i = 0; i < 9; ++i) {
         if (this.mc.field_1724.method_31548().method_5438(i).method_7909() == item) {
            return i;
         }
      }

      return -1;
   }
}
