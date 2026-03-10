package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import java.util.List;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.IntSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1713;
import net.minecraft.class_1792;
import net.minecraft.class_1802;

public class AutoFillItem extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Integer> hotbarSlot;
   private final Setting<List<class_1792>> items;

   public AutoFillItem() {
      super(AddonTemplate.Student_pvp, "item-fill", "Fill");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.hotbarSlot = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("hotbar-slot")).description("Fill item into hotbar")).defaultValue(1)).min(1).max(9).build());
      this.items = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.ItemListSetting.Builder)((meteordevelopment.meteorclient.settings.ItemListSetting.Builder)((meteordevelopment.meteorclient.settings.ItemListSetting.Builder)(new meteordevelopment.meteorclient.settings.ItemListSetting.Builder()).name("items")).description("Fill item.")).defaultValue(List.of(class_1802.field_8831))).build());
   }

   @EventHandler
   private void onTick(Post event) {
      if (this.mc.field_1724 != null && this.mc.field_1761 != null) {
         int hbSlot = (Integer)this.hotbarSlot.get() - 1;
         class_1792 current = this.mc.field_1724.method_31548().method_5438(hbSlot).method_7909();
         if (!((List)this.items.get()).contains(current)) {
            for(int i = 9; i < this.mc.field_1724.method_31548().method_5439(); ++i) {
               class_1792 item = this.mc.field_1724.method_31548().method_5438(i).method_7909();
               if (((List)this.items.get()).contains(item)) {
                  this.mc.field_1761.method_2906(this.mc.field_1724.field_7512.field_7763, i, hbSlot, class_1713.field_7791, this.mc.field_1724);
                  break;
               }
            }

         }
      }
   }
}
