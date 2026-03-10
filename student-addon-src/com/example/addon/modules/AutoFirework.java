package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import java.lang.reflect.Field;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.IntSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2868;

public class AutoFirework extends Module {
   private final SettingGroup sg;
   private final Setting<Integer> delay;
   private final Setting<Integer> hotbarSlot;
   private int tickCounter;

   public AutoFirework() {
      super(AddonTemplate.Student, "Auto Firework", "Auto refill and use firework rockets.");
      this.sg = this.settings.getDefaultGroup();
      this.delay = this.sg.add(((Builder)((Builder)((Builder)(new Builder()).name("delay-ticks")).description("Thời gian giữa mỗi lần dùng firework (ticks).")).defaultValue(20)).min(1).sliderMax(5000).build());
      this.hotbarSlot = this.sg.add(((Builder)((Builder)((Builder)(new Builder()).name("hotbar-slot")).description("Slot hotbar để giữ firework (1-9).")).defaultValue(1)).min(1).max(9).build());
      this.tickCounter = 0;
   }

   public void onActivate() {
      this.tickCounter = 0;
   }

   private void setSelectedSlot(int slot) {
      try {
         Field f = this.mc.field_1724.method_31548().getClass().getDeclaredField("selectedSlot");
         f.setAccessible(true);
         f.set(this.mc.field_1724.method_31548(), slot);
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   @EventHandler
   private void onTick(Pre event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (this.mc.field_1724.method_6128()) {
            int slot = (Integer)this.hotbarSlot.get() - 1;
            if (!this.mc.field_1724.method_31548().method_5438(slot).method_31574(class_1802.field_8639)) {
               FindItemResult result = InvUtils.find(new class_1792[]{class_1802.field_8639});
               if (result.found()) {
                  InvUtils.move().from(result.slot()).toHotbar(slot);
               }
            } else {
               ++this.tickCounter;
               if (this.tickCounter >= (Integer)this.delay.get()) {
                  this.tickCounter = 0;
                  int prevSlot = this.mc.field_1724.method_31548().method_67532();
                  this.setSelectedSlot(slot);
                  this.mc.field_1724.field_3944.method_52787(new class_2868(slot));
                  this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5808);
                  this.mc.field_1724.method_6104(class_1268.field_5808);
                  this.setSelectedSlot(prevSlot);
                  this.mc.field_1724.field_3944.method_52787(new class_2868(prevSlot));
               }
            }
         }
      }
   }
}
