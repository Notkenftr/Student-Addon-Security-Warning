package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.IntSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;

public class ItemSwap extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Integer> swapSlot;
   private int oldSlot;
   private int stage;
   private class_1297 target;

   public ItemSwap() {
      super(AddonTemplate.Student_pvp, "item-swap", "");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.swapSlot = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("swap-slot")).description("Slot to swap")).defaultValue(1)).min(0).max(8).build());
      this.oldSlot = -1;
      this.stage = 0;
   }

   @EventHandler
   private void onTick(Pre event) {
      if (this.mc.field_1724 != null) {
         if (this.mc.field_1755 == null) {
            if (this.stage == 0) {
               if (this.mc.field_1690.field_1886.method_1434()) {
                  this.target = this.mc.field_1692;
                  if (this.target != null) {
                     this.oldSlot = this.mc.field_1724.method_31548().method_67532();
                     int targetSlot = (Integer)this.swapSlot.get();
                     if (this.oldSlot != targetSlot) {
                        this.mc.field_1724.method_31548().method_61496(targetSlot);
                        this.stage = 1;
                     }
                  }
               }
            } else if (this.stage == 1) {
               this.stage = 2;
            } else if (this.stage == 2) {
               if (this.target != null) {
                  this.mc.field_1761.method_2918(this.mc.field_1724, this.target);
                  this.mc.field_1724.method_6104(class_1268.field_5808);
               }

               this.stage = 3;
            } else {
               if (this.stage == 3) {
                  this.mc.field_1724.method_31548().method_61496(this.oldSlot);
                  this.stage = 0;
                  this.target = null;
               }

            }
         }
      }
   }
}
