package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_266;
import net.minecraft.class_268;
import net.minecraft.class_269;
import net.minecraft.class_274;
import net.minecraft.class_5250;
import net.minecraft.class_5251;
import net.minecraft.class_8646;
import net.minecraft.class_9014;
import net.minecraft.class_9015;
import net.minecraft.class_9020;
import net.minecraft.class_274.class_275;

public class FakeScoreboard extends Module {
   private static final String SCOREBOARD_NAME = "Student_custom";
   private class_266 customObjective;
   private class_266 originalObjective;
   private final List<String> teamNames = new ArrayList();
   private long keyStartTime = 0L;
   private long keyInitialSeconds = 0L;
   private long lastScoreboardUpdate = 0L;
   private final SettingGroup sgGeneral;
   private final Setting<String> title;
   private final Setting<String> money;
   private final Setting<String> shards;
   private final Setting<String> kills;
   private final Setting<String> deaths;
   private final Setting<String> playtime;
   private final Setting<String> team;

   public FakeScoreboard() {
      super(AddonTemplate.Student_esp, "Fake-Scoreboard", "Fake scoreboard overlay.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.title = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("title")).defaultValue("KingSMP")).onChanged((s) -> {
         this.safeUpdate();
      })).build());
      this.money = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("money")).defaultValue("36")).onChanged((s) -> {
         this.safeUpdate();
      })).build());
      this.shards = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("shards")).defaultValue("36")).onChanged((s) -> {
         this.safeUpdate();
      })).build());
      this.kills = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("kills")).defaultValue("36")).onChanged((s) -> {
         this.safeUpdate();
      })).build());
      this.deaths = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("deaths")).defaultValue("36")).onChanged((s) -> {
         this.safeUpdate();
      })).build());
      this.playtime = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("playtime")).defaultValue("3d 6h")).onChanged((s) -> {
         this.safeUpdate();
      })).build());
      this.team = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("team")).defaultValue("StudentAddon")).onChanged((s) -> {
         this.safeUpdate();
      })).build());
   }

   private void safeUpdate() {
      if (this.isActive() && this.mc.field_1687 != null && this.mc.field_1724 != null) {
         this.updateScoreboard();
      }

   }

   public void onActivate() {
      if (this.mc.field_1687 != null && this.mc.field_1724 != null) {
         class_269 scoreboard = this.mc.field_1687.method_8428();
         this.originalObjective = scoreboard.method_1189(class_8646.field_45157);
         this.keyStartTime = System.currentTimeMillis();
         this.keyInitialSeconds = 3599L;
         this.updateScoreboard();
      }
   }

   public void onDeactivate() {
      if (this.mc.field_1687 != null) {
         try {
            class_269 scoreboard = this.mc.field_1687.method_8428();
            this.cleanupTeams(scoreboard);
            if (this.customObjective != null) {
               try {
                  scoreboard.method_1194(this.customObjective);
               } catch (Exception var4) {
               }

               this.customObjective = null;
            }

            if (this.originalObjective != null) {
               try {
                  scoreboard.method_1158(class_8646.field_45157, this.originalObjective);
               } catch (Exception var3) {
                  scoreboard.method_1158(class_8646.field_45157, (class_266)null);
               }
            } else {
               scoreboard.method_1158(class_8646.field_45157, (class_266)null);
            }

            this.originalObjective = null;
         } catch (Exception var5) {
         }

      }
   }

   @EventHandler
   private void onTick(Post event) {
      if (this.mc.field_1687 != null && this.mc.field_1724 != null) {
         long now = System.currentTimeMillis();
         if (now - this.lastScoreboardUpdate >= 1000L) {
            this.updateScoreboard();
            this.lastScoreboardUpdate = now;
         }

      }
   }

   private void cleanupTeams(class_269 scoreboard) {
      Iterator var2 = this.teamNames.iterator();

      while(var2.hasNext()) {
         String teamName = (String)var2.next();

         try {
            class_268 t = scoreboard.method_1153(teamName);
            if (t != null) {
               scoreboard.method_1191(t);
            }
         } catch (Exception var5) {
         }
      }

      this.teamNames.clear();
   }

   private void updateScoreboard() {
      if (this.mc.field_1687 != null && this.mc.field_1724 != null) {
         class_269 scoreboard = this.mc.field_1687.method_8428();
         this.cleanupTeams(scoreboard);
         if (this.customObjective != null) {
            try {
               scoreboard.method_1194(this.customObjective);
            } catch (Exception var9) {
            }
         }

         this.customObjective = scoreboard.method_1168("Student_custom", class_274.field_1468, this.gradientTitle((String)this.title.get()), class_275.field_1472, false, class_9020.field_47557);
         scoreboard.method_1158(class_8646.field_45157, this.customObjective);
         List<class_5250> entries = this.generateEntries();

         for(int i = 0; i < entries.size(); ++i) {
            String teamName = "Student_team_" + i;
            this.teamNames.add(teamName);
            class_268 t = scoreboard.method_1153(teamName);
            if (t != null) {
               scoreboard.method_1191(t);
            }

            t = scoreboard.method_1171(teamName);
            t.method_1138((class_2561)entries.get(i));
            String holderName = "§" + Integer.toHexString(i);
            class_9015 holder = class_9015.method_55422(holderName);
            scoreboard.method_1155(holder, this.customObjective);
            class_9014 score = scoreboard.method_1180(holder, this.customObjective);
            score.method_55410(entries.size() - i);
            scoreboard.method_1172(holderName, t);
         }

      }
   }

   private List<class_5250> generateEntries() {
      return List.of(this.text(" "), this.colored("$ ", 64512).method_10852(this.colored("ᴍᴏɴᴇʏ ", 16777215)).method_10852(this.colored((String)this.money.get(), 64512)), this.colored("★ ", 10683385).method_10852(this.colored("ꜱʜᴀʀᴅ ", 16777215)).method_10852(this.colored((String)this.shards.get(), 10683385)), this.colored("\ud83d\udde1 ", 16515072).method_10852(this.colored("ᴋɪʟʟꜱ ", 16777215)).method_10852(this.colored((String)this.kills.get(), 16515072)), this.colored("☠ ", 16348675).method_10852(this.colored("ᴅᴇᴀᴛʜꜱ ", 16777215)).method_10852(this.colored((String)this.deaths.get(), 16348675)), this.colored("\ud83d\udd11 ", 16763904).method_10852(this.colored("ᴋᴇʏ ", 16777215)).method_10852(this.colored(this.getKeyTimer(), 16763904)), this.colored("⌚ ", 16573184).method_10852(this.colored("ᴘʟᴀʏᴇᴅ ", 16777215)).method_10852(this.colored((String)this.playtime.get(), 16573184)), this.colored("\ud83e\ude93 ", 16776960).method_10852(this.colored("ᴛᴇᴀᴍ ", 16777215)).method_10852(this.colored((String)this.team.get(), 16776960)), this.text(" "), this.bold("ᴋɪɴɢᴍᴄ.ᴠɴ", 16776960));
   }

   private String getKeyTimer() {
      long elapsed = System.currentTimeMillis() - this.keyStartTime;
      long remaining = Math.max(0L, this.keyInitialSeconds - elapsed / 1000L);
      return String.format("%dm %ds", remaining / 60L, remaining % 60L);
   }

   private class_5250 gradientTitle(String text) {
      int[] colors = new int[]{16772608, 16770816, 16768768, 16766976, 16763136, 16761088, 16759296};
      class_5250 result = class_2561.method_43473();

      for(int i = 0; i < text.length(); ++i) {
         int color = i < colors.length ? colors[i] : colors[colors.length - 1];
         result.method_10852(class_2561.method_43470(String.valueOf(text.charAt(i))).method_10862(class_2583.field_24360.method_27703(class_5251.method_27717(color)).method_10982(true)));
      }

      return result;
   }

   private class_5250 colored(String text, int rgb) {
      return class_2561.method_43470(text).method_10862(class_2583.field_24360.method_27703(class_5251.method_27717(rgb)));
   }

   private class_5250 bold(String text, int rgb) {
      return class_2561.method_43470(text).method_10862(class_2583.field_24360.method_27703(class_5251.method_27717(rgb)).method_10982(true));
   }

   private class_5250 text(String s) {
      return class_2561.method_43470(s);
   }
}
