package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2561;
import net.minecraft.class_266;
import net.minecraft.class_268;
import net.minecraft.class_269;
import net.minecraft.class_5251;
import net.minecraft.class_8646;
import net.minecraft.class_9013;

public class ScoreboardDump extends Module {
   private boolean dumped = false;

   public ScoreboardDump() {
      super(AddonTemplate.Student_esp, "Scoreboard-Dump", "Dump scoreboard to chat.");
   }

   public void onActivate() {
      this.dumped = false;
   }

   @EventHandler
   private void onTick(Post event) {
      if (this.mc.field_1687 != null && this.mc.field_1724 != null && !this.dumped) {
         class_269 scoreboard = this.mc.field_1687.method_8428();
         class_266 sidebar = scoreboard.method_1189(class_8646.field_45157);
         if (sidebar == null) {
            this.mc.field_1724.method_7353(class_2561.method_43470("§cKhông có scoreboard sidebar!"), false);
            this.dumped = true;
            this.toggle();
         } else {
            StringBuilder sb = new StringBuilder();
            sb.append("========== TITLE ==========\n");
            this.dumpTextRecursive(sb, sidebar.method_1114(), 0);
            sb.append("\n");
            scoreboard.method_1178().stream().filter((holder) -> {
               return scoreboard.method_55430(holder, sidebar) != null;
            }).sorted((a, b) -> {
               class_9013 sa = scoreboard.method_55430(a, sidebar);
               class_9013 sb2 = scoreboard.method_55430(b, sidebar);
               return Integer.compare(sb2.method_55397(), sa.method_55397());
            }).forEach((holder) -> {
               class_9013 s = scoreboard.method_55430(holder, sidebar);
               class_268 team = scoreboard.method_1164(holder.method_5820());
               sb.append("========== [score=").append(s.method_55397()).append("] ==========\n");
               sb.append("holder: ").append(holder.method_5820()).append("\n");
               if (team != null) {
                  sb.append("  -- PREFIX --\n");
                  this.dumpTextRecursive(sb, team.method_1144(), 1);
                  sb.append("  -- SUFFIX --\n");
                  this.dumpTextRecursive(sb, team.method_1136(), 1);
                  sb.append("  -- TEAM OPTIONS --\n");
                  sb.append("  nameVisible: ").append(team.method_1199()).append("\n");
                  sb.append("  color: ").append(team.method_1202()).append("\n");
               } else {
                  sb.append("  (no team)\n");
               }

               sb.append("\n");
            });
            Path path = this.mc.field_1697.toPath().resolve("scoreboard_dump.txt");

            try {
               FileWriter fw = new FileWriter(path.toFile());

               try {
                  fw.write(sb.toString());
                  this.mc.field_1724.method_7353(class_2561.method_43470("§aSaved: " + String.valueOf(path)), false);
               } catch (Throwable var10) {
                  try {
                     fw.close();
                  } catch (Throwable var9) {
                     var10.addSuppressed(var9);
                  }

                  throw var10;
               }

               fw.close();
            } catch (IOException var11) {
               this.mc.field_1724.method_7353(class_2561.method_43470("§cLỗi ghi file: " + var11.getMessage()), false);
            }

            this.dumped = true;
            this.toggle();
         }
      }
   }

   private void dumpTextRecursive(StringBuilder sb, class_2561 text, int depth) {
      String indent = "  ".repeat(depth);
      String content = text.getString();
      class_5251 color = text.method_10866().method_10973();
      String hex = color != null ? String.format("#%06X", color.method_27716()) : "none";
      sb.append(indent).append("[\"").append(content).append("\"").append(" color=").append(hex).append(text.method_10866().method_10984() ? " BOLD" : "").append(text.method_10866().method_10966() ? " ITALIC" : "").append(text.method_10866().method_10965() ? " UNDERLINE" : "").append(text.method_10866().method_10986() ? " STRIKETHROUGH" : "").append(text.method_10866().method_10987() ? " OBFUSCATED" : "").append("]\n");
      Iterator var8 = text.method_10855().iterator();

      while(var8.hasNext()) {
         class_2561 sibling = (class_2561)var8.next();
         this.dumpTextRecursive(sb, sibling, depth + 1);
      }

   }
}
