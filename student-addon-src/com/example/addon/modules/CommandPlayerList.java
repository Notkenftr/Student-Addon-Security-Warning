package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.EnumSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_640;

public class CommandPlayerList extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<CommandPlayerList.Mode> mode;
   private final Setting<String> baseCommand;
   private final Setting<String> suffix;
   private final Setting<Integer> delay;
   private List<String> players;
   private int index;
   private long lastSend;

   public CommandPlayerList() {
      super(AddonTemplate.Student, "command-player-list", "Send command/chat to all players with suffix and random order");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.mode = this.sgGeneral.add(((Builder)((Builder)(new Builder()).name("mode")).defaultValue(CommandPlayerList.Mode.Command)).build());
      this.baseCommand = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.StringSetting.Builder)((meteordevelopment.meteorclient.settings.StringSetting.Builder)(new meteordevelopment.meteorclient.settings.StringSetting.Builder()).name("base-command")).defaultValue("tpa ")).build());
      this.suffix = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.StringSetting.Builder)((meteordevelopment.meteorclient.settings.StringSetting.Builder)((meteordevelopment.meteorclient.settings.StringSetting.Builder)(new meteordevelopment.meteorclient.settings.StringSetting.Builder()).name("suffix")).description("Text added after player name")).defaultValue("")).build());
      this.delay = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)(new meteordevelopment.meteorclient.settings.IntSetting.Builder()).name("delay-ms")).defaultValue(1500)).min(500).sliderMax(5000).build());
   }

   public void onActivate() {
      this.players = new ArrayList();
      this.index = 0;
      this.lastSend = 0L;
      if (this.mc.field_1724 != null && this.mc.method_1562() != null) {
         Iterator var1 = this.mc.method_1562().method_2880().iterator();

         while(var1.hasNext()) {
            class_640 entry = (class_640)var1.next();
            String name = entry.method_2966().getName();
            if (!name.equals(this.mc.field_1724.method_7334().getName())) {
               this.players.add(name);
            }
         }

         if (this.players.isEmpty()) {
            this.error("No players found.", new Object[0]);
            this.toggle();
         } else {
            Collections.shuffle(this.players);
         }
      } else {
         this.toggle();
      }
   }

   @EventHandler
   private void onTick(Post event) {
      if (this.mc.field_1724 != null && !this.players.isEmpty()) {
         long now = System.currentTimeMillis();
         if (now - this.lastSend >= (long)(Integer)this.delay.get()) {
            if (this.index >= this.players.size()) {
               this.index = 0;
            }

            String player = (String)this.players.get(this.index);
            String extra = ((String)this.suffix.get()).isEmpty() ? "" : " " + (String)this.suffix.get();
            String msg = (String)this.baseCommand.get() + player + extra;
            switch(((CommandPlayerList.Mode)this.mode.get()).ordinal()) {
            case 0:
               this.mc.field_1724.field_3944.method_45729("/" + msg.trim());
               break;
            case 1:
               this.mc.method_1562().method_45730(msg.trim());
            }

            this.lastSend = now;
            ++this.index;
         }
      }
   }

   public static enum Mode {
      Chat,
      Command;

      // $FF: synthetic method
      private static CommandPlayerList.Mode[] $values() {
         return new CommandPlayerList.Mode[]{Chat, Command};
      }
   }
}
