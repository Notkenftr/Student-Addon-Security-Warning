package com.example.addon.commands;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Send;
import meteordevelopment.orbit.EventHandler;

public class TagetHub {
   public static final TagetHub INSTANCE = new TagetHub();

   private TagetHub() {
      MeteorClient.EVENT_BUS.subscribe(this);
   }

   public static TagetHub getInstance() {
      return INSTANCE;
   }

   @EventHandler
   private void onPacket(Send event) {
      String command = CommandHandler.extract(event);
      if (command != null) {
         CommandHandler.handle(command);
      }
   }
}
