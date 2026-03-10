package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.ChunkDataEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.ColorSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1109;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1923;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2561;
import net.minecraft.class_2680;
import net.minecraft.class_2791;
import net.minecraft.class_2818;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_3417;
import net.minecraft.class_368;
import net.minecraft.class_374;
import net.minecraft.class_368.class_369;

public class ClusterFinder extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgNotifications;
   private final Setting<SettingColor> chunkColor;
   private final Setting<Boolean> showToast;
   private final Setting<Boolean> playSound;
   private static final int CLUSTER_THRESHOLD = 5;
   private final Set<class_1923> flaggedChunks;
   private final Map<class_1923, Long> lastNotificationTimes;
   private final Queue<Long> recentNotifications;
   private ExecutorService threadPool;
   private boolean active;

   public ClusterFinder() {
      super(AddonTemplate.Student_esp, "ClusterFinder", "Finds Amethyst Clusters.");
      this.sgGeneral = this.settings.createGroup("General");
      this.sgNotifications = this.settings.createGroup("Notifications");
      this.chunkColor = this.sgGeneral.add(((Builder)(new Builder()).name("chunk-color")).defaultValue(new SettingColor(180, 50, 230, 60)).build());
      this.showToast = this.sgNotifications.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("show-toast")).defaultValue(true)).build());
      this.playSound = this.sgNotifications.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("play-sound")).defaultValue(true)).build());
      this.flaggedChunks = ConcurrentHashMap.newKeySet();
      this.lastNotificationTimes = new ConcurrentHashMap();
      this.recentNotifications = new ConcurrentLinkedQueue();
      this.active = false;
   }

   public void onActivate() {
      this.active = true;
      this.threadPool = Executors.newFixedThreadPool(2);
      this.flaggedChunks.clear();
      Iterator var1 = Utils.chunks().iterator();

      while(var1.hasNext()) {
         class_2791 chunk = (class_2791)var1.next();
         if (chunk instanceof class_2818) {
            class_2818 wc = (class_2818)chunk;
            this.threadPool.submit(() -> {
               this.scanChunk(wc);
            });
         }
      }

   }

   public void onDeactivate() {
      this.active = false;
      if (this.threadPool != null) {
         this.threadPool.shutdownNow();
      }

   }

   @EventHandler
   private void onChunkLoad(ChunkDataEvent event) {
      if (this.threadPool != null) {
         this.threadPool.submit(() -> {
            this.scanChunk(event.chunk());
         });
      }

   }

   private void scanChunk(class_2818 chunk) {
      if (this.active && chunk != null) {
         class_1923 cpos = chunk.method_12004();
         int count = 0;
         int minY = chunk.method_31607();
         int topY = minY + chunk.method_31605();

         for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
               for(int y = minY; y < topY; ++y) {
                  class_2680 state = chunk.method_8320(new class_2338(cpos.method_8326() + x, y, cpos.method_8328() + z));
                  if (state.method_27852(class_2246.field_27161)) {
                     ++count;
                  }
               }
            }
         }

         if (count >= 5 && this.flaggedChunks.add(cpos)) {
            this.mc.execute(() -> {
               if ((Boolean)this.showToast.get()) {
                  this.mc.method_1566().method_1999(new ClusterFinder.ClusterToast(new class_1799(class_1802.field_27063), class_2561.method_43470("Cluster Found"), class_2561.method_43470("Count " + count)));
               }

               if ((Boolean)this.playSound.get()) {
                  this.mc.method_1483().method_4873(class_1109.method_4758(class_3417.field_26942, 1.5F));
               }

            });
         }

      }
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      Color color = new Color((Color)this.chunkColor.get());
      Iterator var3 = this.flaggedChunks.iterator();

      while(var3.hasNext()) {
         class_1923 cp = (class_1923)var3.next();
         class_238 box = new class_238((double)cp.method_8326(), 64.0D, (double)cp.method_8328(), (double)(cp.method_8327() + 1), 64.5D, (double)(cp.method_8329() + 1));
         event.renderer.box(box, color, color, ShapeMode.Both, 0);
      }

   }

   private static class ClusterToast implements class_368 {
      private final class_1799 icon;
      private final class_2561 title;
      private final class_2561 description;
      private long startTime = -1L;
      private class_369 visibility;

      public ClusterToast(class_1799 icon, class_2561 title, class_2561 description) {
         this.visibility = class_369.field_2210;
         this.icon = icon;
         this.title = title;
         this.description = description;
      }

      public class_369 method_61988() {
         return this.visibility;
      }

      public void method_61989(class_374 manager, long time) {
         if (this.startTime == -1L) {
            this.startTime = time;
         }

         if (time - this.startTime >= 5000L) {
            this.visibility = class_369.field_2209;
         }

      }

      public void method_1986(class_332 context, class_327 textRenderer, long currentTime) {
         context.method_25294(0, 0, 160, 32, -1442840576);
         int purple = -6736897;
         context.method_25294(0, 0, 160, 1, purple);
         context.method_25294(0, 31, 160, 32, purple);
         context.method_25294(0, 1, 1, 31, purple);
         context.method_25294(159, 1, 160, 31, purple);
         if (this.icon != null && !this.icon.method_7960()) {
            context.method_51427(this.icon, 8, 8);
         }

         if (this.title != null) {
            context.method_51439(textRenderer, this.title, 32, 7, 16776960, false);
         }

         if (this.description != null) {
            context.method_51439(textRenderer, this.description, 32, 18, 16777215, false);
         }

      }
   }
}
