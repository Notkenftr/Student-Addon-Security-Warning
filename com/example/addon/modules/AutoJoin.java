package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.IntSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1707;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2868;
import net.minecraft.class_437;
import net.minecraft.class_476;

public class AutoJoin extends Module {
   private final SettingGroup sg;
   private final Setting<Integer> targetSlot;
   private int timer;
   private int stage;
   private int limeClickCount;

   public AutoJoin() {
      super(AddonTemplate.Student, "Auto-Join", ".");
      this.sg = this.settings.getDefaultGroup();
      this.targetSlot = this.sg.add(((Builder)((Builder)((Builder)(new Builder()).name("target-slot")).description("Slot")).defaultValue(22)).min(0).sliderMax(53).build());
      this.timer = 0;
      this.stage = 0;
      this.limeClickCount = 0;
   }

   public void onActivate() {
      this.timer = 100;
      this.stage = 0;
      this.limeClickCount = 0;
   }

   @EventHandler
   private void onTick(Pre event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (this.timer > 0) {
            --this.timer;
         } else if (this.stage != 0) {
            class_476 screen;
            class_437 var9;
            class_1707 handler;
            if (this.stage == 1) {
               var9 = this.mc.field_1755;
               if (var9 instanceof class_476) {
                  screen = (class_476)var9;
                  handler = (class_1707)screen.method_17577();
                  boolean found = false;

                  for(int i = 0; i < handler.field_7761.size(); ++i) {
                     if (((class_1735)handler.field_7761.get(i)).method_7677().method_31574(class_1802.field_8581)) {
                        this.mc.field_1761.method_2906(handler.field_7763, i, 0, class_1713.field_7790, this.mc.field_1724);
                        found = true;
                        this.timer = 2;
                        return;
                     }
                  }

                  if (!found) {
                     ++this.limeClickCount;
                     if (this.limeClickCount < 3) {
                        this.timer = 10;
                        return;
                     }

                     this.stage = 2;
                     this.timer = 2;
                     this.limeClickCount = 0;
                  }

               }
            } else {
               if (this.stage == 2) {
                  var9 = this.mc.field_1755;
                  if (!(var9 instanceof class_476)) {
                     return;
                  }

                  screen = (class_476)var9;
                  handler = (class_1707)screen.method_17577();
                  this.mc.field_1761.method_2906(handler.field_7763, (Integer)this.targetSlot.get(), 0, class_1713.field_7790, this.mc.field_1724);
                  this.stage = 0;
                  this.timer = 100;
                  this.limeClickCount = 0;
               }

            }
         } else {
            int clockSlot = -1;

            int prev;
            for(prev = 0; prev < 9; ++prev) {
               if (this.mc.field_1724.method_31548().method_5438(prev).method_31574(class_1802.field_8557)) {
                  clockSlot = prev;
                  break;
               }
            }

            if (clockSlot == -1) {
               FindItemResult result = InvUtils.find(new class_1792[]{class_1802.field_8557});
               if (result.found()) {
                  InvUtils.move().from(result.slot()).toHotbar(0);
                  int clockSlot = false;
                  this.timer = 2;
               }
            } else {
               prev = this.mc.field_1724.method_31548().method_67532();
               this.mc.field_1724.method_31548().method_61496(clockSlot);
               this.mc.field_1724.field_3944.method_52787(new class_2868(clockSlot));
               this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5808);
               this.mc.field_1724.method_6104(class_1268.field_5808);
               this.mc.field_1724.method_31548().method_61496(prev);
               this.mc.field_1724.field_3944.method_52787(new class_2868(prev));
               this.stage = 1;
               this.timer = 3;
            }
         }
      }
   }
}
