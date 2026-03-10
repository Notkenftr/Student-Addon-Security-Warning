package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.ItemSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1713;
import net.minecraft.class_1792;
import net.minecraft.class_1802;

public class DropHotbar extends Module {
   private final SettingGroup sg;
   private final Setting<class_1792> item;
   private final Setting<Boolean> autoRefill;

   public DropHotbar() {
      super(AddonTemplate.Student, "Drop Hotbar", "Auto switch & drop selected item.");
      this.sg = this.settings.getDefaultGroup();
      this.item = this.sg.add(((Builder)((Builder)((Builder)(new Builder()).name("item")).description("Item to drop.")).defaultValue(class_1802.field_8831)).build());
      this.autoRefill = this.sg.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("auto-refill")).description("Automatically refill from inventory.")).defaultValue(true)).build());
   }

   @EventHandler
   private void onTick(Pre event) {
      if (this.mc.field_1724 != null) {
         class_1792 target = (class_1792)this.item.get();
         if (target != null && target != class_1802.field_8162) {
            int hotbarSlot = this.findItemInHotbar(target);
            if (hotbarSlot == -1 && (Boolean)this.autoRefill.get()) {
               int invSlot = this.findItemInInventory(target);
               if (invSlot != -1) {
                  this.mc.field_1761.method_2906(this.mc.field_1724.field_7512.field_7763, invSlot, 0, class_1713.field_7794, this.mc.field_1724);
                  return;
               }
            }

            if (hotbarSlot != -1) {
               this.mc.field_1724.method_31548().method_61496(hotbarSlot);
               if (!this.mc.field_1724.method_6047().method_7960()) {
                  this.mc.field_1724.method_7290(true);
               }
            }

         }
      }
   }

   private int findItemInHotbar(class_1792 item) {
      for(int i = 0; i < 9; ++i) {
         if (this.mc.field_1724.method_31548().method_5438(i).method_31574(item)) {
            return i;
         }
      }

      return -1;
   }

   private int findItemInInventory(class_1792 item) {
      for(int i = 9; i < this.mc.field_1724.method_31548().method_5439(); ++i) {
         if (this.mc.field_1724.method_31548().method_5438(i).method_31574(item)) {
            return i;
         }
      }

      return -1;
   }
}
