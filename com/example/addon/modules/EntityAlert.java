package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1657;
import net.minecraft.class_2561;
import net.minecraft.class_3414;
import net.minecraft.class_3417;

public class EntityAlert extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Set<class_1299<?>>> entities;
   private final Setting<List<String>> whitelist;
   private final Setting<EntityAlert.AlertSound> sound;
   private long lastSoundTime;
   private static final long COOLDOWN_MS = 500L;

   public EntityAlert() {
      super(AddonTemplate.Student, "entity-notifier", "Alert when entity appears.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.entities = this.sgGeneral.add(((Builder)((Builder)(new Builder()).name("entities")).description("Entity Notifier")).defaultValue(new class_1299[]{class_1299.field_6097}).build());
      this.whitelist = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.StringListSetting.Builder)((meteordevelopment.meteorclient.settings.StringListSetting.Builder)(new meteordevelopment.meteorclient.settings.StringListSetting.Builder()).name("whitelist")).description("White List")).build());
      this.sound = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)(new meteordevelopment.meteorclient.settings.EnumSetting.Builder()).name("sound")).description("Sound Notifier")).defaultValue(EntityAlert.AlertSound.ANVIL)).build());
      this.lastSoundTime = 0L;
   }

   @EventHandler
   private void onTick(Post event) {
      if (this.mc.field_1687 != null && this.mc.field_1724 != null) {
         long now = System.currentTimeMillis();
         if (now - this.lastSoundTime >= 500L) {
            Iterator var4 = this.mc.field_1687.method_18112().iterator();

            while(var4.hasNext()) {
               class_1297 e = (class_1297)var4.next();
               if (e != this.mc.field_1724 && ((Set)this.entities.get()).contains(e.method_5864())) {
                  String name;
                  if (e instanceof class_1657) {
                     class_1657 player = (class_1657)e;
                     name = player.method_5477().getString();
                     if (((List)this.whitelist.get()).contains(name)) {
                        continue;
                     }

                     this.sendChat("Player found: " + name);
                  } else {
                     name = e.method_5864().method_5897().getString();
                     this.sendChat("Mob found: " + name);
                  }

                  this.mc.field_1724.method_5783(((EntityAlert.AlertSound)this.sound.get()).sound, 1.0F, 1.0F);
                  this.lastSoundTime = now;
                  break;
               }
            }

         }
      }
   }

   private void sendChat(String message) {
      this.mc.field_1724.method_7353(class_2561.method_43470(message), false);
   }

   public static enum AlertSound {
      ANVIL(class_3417.field_14833),
      XP(class_3417.field_14627),
      WITHER(class_3417.field_14792),
      DRAGON(class_3417.field_14671),
      BELL(class_3417.field_17265),
      EXPLODE(class_3417.field_15057);

      public final class_3414 sound;

      private AlertSound(class_3414 sound) {
         this.sound = sound;
      }

      // $FF: synthetic method
      private static EntityAlert.AlertSound[] $values() {
         return new EntityAlert.AlertSound[]{ANVIL, XP, WITHER, DRAGON, BELL, EXPLODE};
      }
   }
}
