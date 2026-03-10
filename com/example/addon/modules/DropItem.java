package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import java.util.List;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.ItemListSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1792;
import net.minecraft.class_1799;

public class DropItem extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<List<class_1792>> items;

   public DropItem() {
      super(AddonTemplate.Student, "Drop-Item", "Tự động drop toàn bộ item được chọn.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.items = this.sgGeneral.add(((Builder)((Builder)(new Builder()).name("items")).description("Danh sách item sẽ bị drop.")).defaultValue(new class_1792[0]).build());
   }

   @EventHandler
   private void onTick(Post event) {
      if (this.mc.field_1724 != null && this.mc.field_1761 != null) {
         for(int i = 0; i < this.mc.field_1724.method_31548().method_5439(); ++i) {
            class_1799 stack = this.mc.field_1724.method_31548().method_5438(i);
            if (!stack.method_7960() && ((List)this.items.get()).contains(stack.method_7909())) {
               int slotId = -1;

               for(int s = 0; s < this.mc.field_1724.field_7512.field_7761.size(); ++s) {
                  if (((class_1735)this.mc.field_1724.field_7512.field_7761.get(s)).field_7871 == this.mc.field_1724.method_31548() && ((class_1735)this.mc.field_1724.field_7512.field_7761.get(s)).method_34266() == i) {
                     slotId = s;
                     break;
                  }
               }

               if (slotId != -1) {
                  this.mc.field_1761.method_2906(this.mc.field_1724.field_7512.field_7763, slotId, 1, class_1713.field_7795, this.mc.field_1724);
               }
            }
         }

      }
   }
}
