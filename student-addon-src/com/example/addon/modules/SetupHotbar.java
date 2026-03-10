package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import java.util.ArrayList;
import java.util.List;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.EnumSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.class_1792;
import net.minecraft.class_1802;

public class SetupHotbar extends Module {
   private final SettingGroup sg;
   private final Setting<SetupHotbar.Profile> selectedProfile;
   private final List<List<Setting<class_1792>>> profiles;

   public SetupHotbar() {
      super(AddonTemplate.Student_pvp, "setup-hotbar", "Fill hotbar with selected profile.");
      this.sg = this.settings.getDefaultGroup();
      this.selectedProfile = this.sg.add(((Builder)((Builder)((Builder)(new Builder()).name("profile")).description("Chọn hotbar profile muốn áp dụng.")).defaultValue(SetupHotbar.Profile.Profile_1)).build());
      this.profiles = new ArrayList();

      for(int p = 0; p < 5; ++p) {
         int profileIndex = p;
         SettingGroup group = this.settings.createGroup("Profile " + (p + 1));
         List<Setting<class_1792>> slots = new ArrayList();

         for(int s = 1; s <= 9; ++s) {
            slots.add(group.add(((meteordevelopment.meteorclient.settings.ItemSetting.Builder)((meteordevelopment.meteorclient.settings.ItemSetting.Builder)((meteordevelopment.meteorclient.settings.ItemSetting.Builder)((meteordevelopment.meteorclient.settings.ItemSetting.Builder)(new meteordevelopment.meteorclient.settings.ItemSetting.Builder()).name("slot-" + s)).description("Item cho slot " + s)).defaultValue(class_1802.field_8162)).visible(() -> {
               return ((SetupHotbar.Profile)this.selectedProfile.get()).ordinal() == profileIndex;
            })).build()));
         }

         this.profiles.add(slots);
      }

   }

   public void onActivate() {
      if (this.mc.field_1724 != null) {
         int idx = ((SetupHotbar.Profile)this.selectedProfile.get()).ordinal();
         List<Setting<class_1792>> chosen = (List)this.profiles.get(idx);

         for(int i = 0; i < 9; ++i) {
            this.fill(i, (class_1792)((Setting)chosen.get(i)).get());
         }

         this.toggle();
      }
   }

   private void fill(int hotbarSlot, class_1792 item) {
      if (item != null && item != class_1802.field_8162) {
         int invSlot = InvUtils.find(new class_1792[]{item}).slot();
         if (invSlot != -1) {
            InvUtils.move().from(invSlot).toHotbar(hotbarSlot);
         }
      }
   }

   public static enum Profile {
      Profile_1,
      Profile_2,
      Profile_3,
      Profile_4,
      Profile_5;

      // $FF: synthetic method
      private static SetupHotbar.Profile[] $values() {
         return new SetupHotbar.Profile[]{Profile_1, Profile_2, Profile_3, Profile_4, Profile_5};
      }
   }
}
