package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.ItemSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2868;
import net.minecraft.class_634;

public class AHSeller extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<class_1792> item;
   private final Setting<Integer> price;
   private final Setting<Integer> amount;
   private final Setting<Integer> delay;
   private int tickDelay;
   private int stage;

   public AHSeller() {
      super(AddonTemplate.Student, "ah seller", "Auto sell item on ah");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.item = this.sgGeneral.add(((Builder)((Builder)(new Builder()).name("item")).defaultValue(class_1802.field_8477)).build());
      this.price = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)(new meteordevelopment.meteorclient.settings.IntSetting.Builder()).name("price")).defaultValue(1000)).min(1).sliderMax(10000000).build());
      this.amount = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)(new meteordevelopment.meteorclient.settings.IntSetting.Builder()).name("amount")).defaultValue(64)).min(1).sliderMax(64).build());
      this.delay = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)(new meteordevelopment.meteorclient.settings.IntSetting.Builder()).name("delay")).defaultValue(40)).min(0).sliderMax(200).build());
      this.tickDelay = 0;
      this.stage = 0;
   }

   public void onActivate() {
      this.tickDelay = 0;
      this.stage = 0;
   }

   @EventHandler
   private void onTick(Post event) {
      if (this.mc.field_1724 != null) {
         if (this.tickDelay > 0) {
            --this.tickDelay;
         } else if (this.stage == 0) {
            FindItemResult result = InvUtils.find(new class_1792[]{(class_1792)this.item.get()});
            if (result.found()) {
               InvUtils.move().from(result.slot()).toHotbar(0);
               this.mc.field_1724.field_3944.method_52787(new class_2868(0));
               this.stage = 1;
               this.tickDelay = 5;
            }
         } else if (this.stage == 1) {
            class_634 var10000 = this.mc.field_1724.field_3944;
            String var10001 = String.valueOf(this.price.get());
            var10000.method_45730("ah sell " + var10001 + " " + String.valueOf(this.amount.get()));
            this.stage = 2;
            this.tickDelay = 10;
         } else {
            if (this.stage == 2) {
               if (this.mc.field_1724.field_7512 == null) {
                  return;
               }

               class_1703 handler = this.mc.field_1724.field_7512;

               for(int i = 0; i < handler.field_7761.size(); ++i) {
                  class_1799 stack = handler.method_7611(i).method_7677();
                  if (!stack.method_7960() && stack.method_7909() == class_1802.field_8581) {
                     this.mc.field_1761.method_2906(handler.field_7763, i, 0, class_1713.field_7794, this.mc.field_1724);
                     this.stage = 0;
                     this.tickDelay = (Integer)this.delay.get();
                     return;
                  }
               }
            }

         }
      }
   }
}
