package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class AutoLogin extends Module {
   private final SettingGroup sg;
   private final Setting<List<String>> accounts;
   private boolean logged;

   public AutoLogin() {
      super(AddonTemplate.Student, "Auto Login", "Auto login when join server.");
      this.sg = this.settings.getDefaultGroup();
      this.accounts = this.sg.add(((Builder)((Builder)((Builder)(new Builder()).name("accounts")).description("name:password")).defaultValue(List.of("name:password"))).build());
      this.logged = false;
   }

   public void onActivate() {
      this.logged = false;
   }

   @EventHandler
   private void onTick(Pre event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (!this.logged) {
            this.logged = true;
            String playerName = this.mc.field_1724.method_5477().getString();
            String password = this.findPassword(playerName);
            if (password != null) {
               this.mc.field_1724.field_3944.method_45730("l " + password);
            }

         }
      }
   }

   private String findPassword(String name) {
      Iterator var2 = ((List)this.accounts.get()).iterator();

      String[] split;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         String entry = (String)var2.next();
         split = entry.trim().split(":", 2);
      } while(split.length != 2 || !split[0].trim().equalsIgnoreCase(name));

      return split[1].trim();
   }
}
