package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.KeybindSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1792;
import net.minecraft.class_1802;

public class KeyPearl extends Module {
   private final Setting<Keybind> key;
   private boolean pressed;

   public KeyPearl() {
      super(AddonTemplate.Student_pvp, "key-pear", "Throw pearl even if not in hotbar.");
      this.key = this.settings.getDefaultGroup().add(((Builder)((Builder)(new Builder()).name("throw-key")).description("Throw pearl from inventory.")).build());
   }

   @EventHandler
   private void onTick(Pre event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (((Keybind)this.key.get()).isPressed()) {
            if (!this.pressed) {
               this.throwPearl();
               this.pressed = true;
            }
         } else {
            this.pressed = false;
         }

      }
   }

   private void throwPearl() {
      FindItemResult pearl = InvUtils.find(new class_1792[]{class_1802.field_8634});
      if (pearl.found()) {
         int prevSlot = this.mc.field_1724.method_31548().method_67532();
         if (pearl.slot() >= 9) {
            InvUtils.move().from(pearl.slot()).toHotbar(prevSlot);
         } else {
            InvUtils.swap(pearl.slot(), false);
         }

         this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5808);
         InvUtils.swap(prevSlot, false);
      }
   }
}
