package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.ItemListSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1707;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_476;

public class DropItemGui extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<List<class_1792>> items;
   private final Setting<Integer> delay;
   private int tickTimer;

   public DropItemGui() {
      super(AddonTemplate.Student, "Drop-Item-Gui", "Auto drop item in GUI");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.items = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("items")).description("Danh sach item se duoc drop")).defaultValue(List.of(class_1802.field_8107, class_1802.field_8606))).build());
      this.delay = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)(new meteordevelopment.meteorclient.settings.IntSetting.Builder()).name("delay-ticks")).description("Do tre giua moi lan drop (ticks)")).defaultValue(2)).min(1).sliderMax(500).build());
      this.tickTimer = 0;
   }

   @EventHandler
   private void onTick(Post event) {
      if (this.mc.field_1724 != null && this.mc.field_1755 != null) {
         if (this.mc.field_1755 instanceof class_476) {
            if (this.tickTimer > 0) {
               --this.tickTimer;
            } else {
               class_476 screen = (class_476)this.mc.field_1755;
               class_1707 handler = (class_1707)screen.method_17577();
               boolean dropped = false;
               Iterator var5 = handler.field_7761.iterator();

               while(var5.hasNext()) {
                  class_1735 slot = (class_1735)var5.next();
                  if (slot.field_7871 != this.mc.field_1724.method_31548() && !slot.method_7677().method_7960()) {
                     class_1792 slotItem = slot.method_7677().method_7909();
                     if (((List)this.items.get()).contains(slotItem)) {
                        this.mc.field_1761.method_2906(handler.field_7763, slot.field_7874, 1, class_1713.field_7795, this.mc.field_1724);
                        dropped = true;
                     }
                  }
               }

               if (dropped) {
                  this.tickTimer = (Integer)this.delay.get();
               }

            }
         }
      }
   }
}
