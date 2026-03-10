package com.example.addon.modules;

import com.example.addon.AddonTemplate;
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
import net.minecraft.class_1799;
import net.minecraft.class_437;
import net.minecraft.class_476;

public class AutoSell extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<List<class_1792>> items;
   private final Setting<Integer> delay;
   private int stage;
   private int timer;

   public AutoSell() {
      super(AddonTemplate.Student, "Auto Sell", "");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.items = this.sgGeneral.add(((Builder)((Builder)(new Builder()).name("items")).description("Item list")).build());
      this.delay = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)(new meteordevelopment.meteorclient.settings.IntSetting.Builder()).name("delay")).description("")).defaultValue(5)).min(1).sliderMax(40).build());
      this.stage = 0;
      this.timer = 0;
   }

   public void onActivate() {
      this.stage = 0;
      this.timer = 0;
      if (this.mc.field_1724 != null) {
         this.mc.field_1724.field_3944.method_45730("sell");
      }
   }

   public void onDeactivate() {
      this.stage = 0;
      this.timer = 0;
   }

   private boolean hasItemsInInventory() {
      if (this.mc.field_1724 == null) {
         return false;
      } else {
         for(int i = 0; i < this.mc.field_1724.method_31548().method_5439(); ++i) {
            class_1799 stack = this.mc.field_1724.method_31548().method_5438(i);
            if (((List)this.items.get()).contains(stack.method_7909())) {
               return true;
            }
         }

         return false;
      }
   }

   @EventHandler
   private void onTick(Post event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (this.timer > 0) {
            --this.timer;
         } else {
            class_437 var3 = this.mc.field_1755;
            if (var3 instanceof class_476) {
               class_476 screen = (class_476)var3;
               class_1707 handler = (class_1707)screen.method_17577();
               if (this.stage == 0) {
                  if (!this.hasItemsInInventory()) {
                     this.mc.field_1724.method_7346();
                     this.toggle();
                     return;
                  }

                  int guiSize = handler.field_7761.size() - this.mc.field_1724.method_31548().method_5439();
                  int emptySlots = 0;

                  for(int i = 0; i < guiSize; ++i) {
                     if (((class_1735)handler.field_7761.get(i)).method_7677().method_7960()) {
                        ++emptySlots;
                     }
                  }

                  if (emptySlots == 0) {
                     this.mc.field_1724.method_7346();
                     this.toggle();
                     return;
                  }

                  boolean moved = false;

                  for(int i = guiSize; i < handler.field_7761.size(); ++i) {
                     class_1735 slot = (class_1735)handler.field_7761.get(i);
                     if (((List)this.items.get()).contains(slot.method_7677().method_7909())) {
                        this.mc.field_1761.method_2906(handler.field_7763, i, 0, class_1713.field_7794, this.mc.field_1724);
                        moved = true;
                        this.timer = (Integer)this.delay.get();
                     }
                  }

                  if (!moved) {
                     this.mc.field_1724.method_7346();
                     this.toggle();
                  }
               }

            }
         }
      }
   }
}
