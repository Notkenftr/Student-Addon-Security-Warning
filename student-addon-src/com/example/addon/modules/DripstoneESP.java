package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.BlockUpdateEvent;
import meteordevelopment.meteorclient.events.world.ChunkDataEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.ColorSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.MeteorToast;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_2791;
import net.minecraft.class_2818;
import net.minecraft.class_327;
import net.minecraft.class_332;

public class DripstoneESP extends Module {
   private final SettingGroup sgStalactite;
   private final SettingGroup sgStalagmite;
   private final SettingGroup sgThreading;
   private final SettingGroup sgNotifications;
   private final Setting<SettingColor> stalactiteColor;
   private final Setting<ShapeMode> stalactiteShapeMode;
   private final Setting<Boolean> stalactiteTracers;
   private final Setting<SettingColor> stalactiteTracerColor;
   private final Setting<Integer> stalactiteMinLength;
   private final Setting<SettingColor> stalagmiteColor;
   private final Setting<ShapeMode> stalagmiteShapeMode;
   private final Setting<Boolean> stalagmiteTracers;
   private final Setting<SettingColor> stalagmiteTracerColor;
   private final Setting<Integer> stalagmiteMinLength;
   private final Setting<Boolean> showNotifications;
   private final Setting<DripstoneESP.Mode> notificationMode;
   private final Setting<Integer> notificationLengthThreshold;
   private final Setting<Boolean> useThreading;
   private final Setting<Integer> threadPoolSize;
   private final Set<class_2338> longStalactiteBottoms;
   private final Set<class_2338> longStalagmiteTops;
   private ExecutorService threadPool;

   public DripstoneESP() {
      super(AddonTemplate.Student_esp, "DripstoneESP", "ESP for long dripstones with tracers and threading.");
      this.sgStalactite = this.settings.createGroup("Stalactite ESP");
      this.sgStalagmite = this.settings.createGroup("Stalagmite ESP");
      this.sgThreading = this.settings.createGroup("Threading");
      this.sgNotifications = this.settings.createGroup("Notifications");
      this.stalactiteColor = this.sgStalactite.add(((Builder)(new Builder()).name("esp-color")).defaultValue(new SettingColor(128, 128, 128, 100)).build());
      this.stalactiteShapeMode = this.sgStalactite.add(((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)(new meteordevelopment.meteorclient.settings.EnumSetting.Builder()).name("shape-mode")).defaultValue(ShapeMode.Both)).build());
      this.stalactiteTracers = this.sgStalactite.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("tracers")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgStalactite;
      Builder var10002 = ((Builder)(new Builder()).name("tracer-color")).defaultValue(new SettingColor(128, 128, 128, 200));
      Setting var10003 = this.stalactiteTracers;
      Objects.requireNonNull(var10003);
      this.stalactiteTracerColor = var10001.add(((Builder)var10002.visible(var10003::get)).build());
      this.stalactiteMinLength = this.sgStalactite.add(((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)(new meteordevelopment.meteorclient.settings.IntSetting.Builder()).name("min-length")).defaultValue(4)).min(4).max(16).build());
      this.stalagmiteColor = this.sgStalagmite.add(((Builder)(new Builder()).name("esp-color")).defaultValue(new SettingColor(160, 160, 160, 100)).build());
      this.stalagmiteShapeMode = this.sgStalagmite.add(((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)(new meteordevelopment.meteorclient.settings.EnumSetting.Builder()).name("shape-mode")).defaultValue(ShapeMode.Both)).build());
      this.stalagmiteTracers = this.sgStalagmite.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("tracers")).defaultValue(true)).build());
      var10001 = this.sgStalagmite;
      var10002 = ((Builder)(new Builder()).name("tracer-color")).defaultValue(new SettingColor(160, 160, 160, 200));
      var10003 = this.stalagmiteTracers;
      Objects.requireNonNull(var10003);
      this.stalagmiteTracerColor = var10001.add(((Builder)var10002.visible(var10003::get)).build());
      this.stalagmiteMinLength = this.sgStalagmite.add(((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)(new meteordevelopment.meteorclient.settings.IntSetting.Builder()).name("min-length")).defaultValue(8)).min(4).max(16).build());
      this.showNotifications = this.sgNotifications.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("notifications")).defaultValue(true)).build());
      var10001 = this.sgNotifications;
      meteordevelopment.meteorclient.settings.EnumSetting.Builder var1 = (meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)(new meteordevelopment.meteorclient.settings.EnumSetting.Builder()).name("notification-mode")).defaultValue(DripstoneESP.Mode.Both);
      var10003 = this.showNotifications;
      Objects.requireNonNull(var10003);
      this.notificationMode = var10001.add(((meteordevelopment.meteorclient.settings.EnumSetting.Builder)var1.visible(var10003::get)).build());
      var10001 = this.sgNotifications;
      meteordevelopment.meteorclient.settings.IntSetting.Builder var2 = ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)(new meteordevelopment.meteorclient.settings.IntSetting.Builder()).name("notification-length")).defaultValue(8)).min(4).max(20);
      var10003 = this.showNotifications;
      Objects.requireNonNull(var10003);
      this.notificationLengthThreshold = var10001.add(((meteordevelopment.meteorclient.settings.IntSetting.Builder)var2.visible(var10003::get)).build());
      this.useThreading = this.sgThreading.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("enable-threading")).defaultValue(true)).build());
      var10001 = this.sgThreading;
      var2 = ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)(new meteordevelopment.meteorclient.settings.IntSetting.Builder()).name("thread-pool-size")).defaultValue(2)).min(1).max(8);
      var10003 = this.useThreading;
      Objects.requireNonNull(var10003);
      this.threadPoolSize = var10001.add(((meteordevelopment.meteorclient.settings.IntSetting.Builder)var2.visible(var10003::get)).build());
      this.longStalactiteBottoms = ConcurrentHashMap.newKeySet();
      this.longStalagmiteTops = ConcurrentHashMap.newKeySet();
   }

   public void onActivate() {
      if ((Boolean)this.useThreading.get()) {
         this.threadPool = Executors.newFixedThreadPool((Integer)this.threadPoolSize.get());
      }

      this.longStalactiteBottoms.clear();
      this.longStalagmiteTops.clear();
      Iterator var1 = Utils.chunks().iterator();

      while(var1.hasNext()) {
         class_2791 chunk = (class_2791)var1.next();
         if (chunk instanceof class_2818) {
            class_2818 wc = (class_2818)chunk;
            if ((Boolean)this.useThreading.get()) {
               this.threadPool.submit(() -> {
                  this.scanChunk(wc);
               });
            } else {
               this.scanChunk(wc);
            }
         }
      }

   }

   public void onDeactivate() {
      if (this.threadPool != null) {
         this.threadPool.shutdown();
         this.threadPool = null;
      }

      this.longStalactiteBottoms.clear();
      this.longStalagmiteTops.clear();
   }

   @EventHandler
   private void onChunkLoad(ChunkDataEvent event) {
      if ((Boolean)this.useThreading.get() && this.threadPool != null) {
         this.threadPool.submit(() -> {
            this.scanChunk(event.chunk());
         });
      } else {
         this.scanChunk(event.chunk());
      }

   }

   @EventHandler
   private void onBlockUpdate(BlockUpdateEvent event) {
      Runnable scanTask = () -> {
         if (event.newState.method_27852(class_2246.field_28048)) {
            for(int dx = -2; dx <= 2; ++dx) {
               for(int dz = -2; dz <= 2; ++dz) {
                  for(int dy = -16; dy <= 16; ++dy) {
                     class_2338 scanPos = event.pos.method_10069(dx, dy, dz);
                     class_2680 scanState = this.mc.field_1687.method_8320(scanPos);
                     if (this.isDripstoneTipDown(scanState)) {
                        DripstoneESP.StalactiteInfo info = this.getStalactiteInfo(scanPos);
                        if (info != null && info.length >= (Integer)this.stalactiteMinLength.get() && this.longStalactiteBottoms.add(info.bottomPos)) {
                           this.notifyDripstoneFound("Stalactite", scanPos, info.length);
                        }
                     }

                     if (this.isDripstoneTipUp(scanState)) {
                        DripstoneESP.StalagmiteInfo infox = this.getStalagmiteInfo(scanPos);
                        if (infox != null && infox.length >= (Integer)this.stalagmiteMinLength.get() && this.longStalagmiteTops.add(infox.topPos)) {
                           this.notifyDripstoneFound("Stalagmite", scanPos, infox.length);
                        }
                     }
                  }
               }
            }
         }

      };
      if ((Boolean)this.useThreading.get() && this.threadPool != null) {
         this.threadPool.submit(scanTask);
      } else {
         scanTask.run();
      }

   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      Iterator var2 = this.longStalactiteBottoms.iterator();

      class_2338 pos;
      double distSq;
      Color color;
      while(var2.hasNext()) {
         pos = (class_2338)var2.next();
         distSq = this.mc.field_1724.method_33571().method_1028((double)pos.method_10263() + 0.5D, (double)pos.method_10264() + 0.5D, (double)pos.method_10260() + 0.5D);
         if (!(distSq > 10000.0D)) {
            color = new Color((Color)this.stalactiteColor.get());
            event.renderer.box(pos, color, color, (ShapeMode)this.stalactiteShapeMode.get(), 0);
            if ((Boolean)this.stalactiteTracers.get()) {
               event.renderer.line(RenderUtils.center.field_1352, RenderUtils.center.field_1351, RenderUtils.center.field_1350, (double)pos.method_10263() + 0.5D, (double)pos.method_10264() + 0.5D, (double)pos.method_10260() + 0.5D, (Color)this.stalactiteTracerColor.get());
            }
         }
      }

      var2 = this.longStalagmiteTops.iterator();

      while(var2.hasNext()) {
         pos = (class_2338)var2.next();
         distSq = this.mc.field_1724.method_33571().method_1028((double)pos.method_10263() + 0.5D, (double)pos.method_10264() + 0.5D, (double)pos.method_10260() + 0.5D);
         if (!(distSq > 10000.0D)) {
            color = new Color((Color)this.stalagmiteColor.get());
            event.renderer.box(pos, color, color, (ShapeMode)this.stalagmiteShapeMode.get(), 0);
            if ((Boolean)this.stalagmiteTracers.get()) {
               event.renderer.line(RenderUtils.center.field_1352, RenderUtils.center.field_1351, RenderUtils.center.field_1350, (double)pos.method_10263() + 0.5D, (double)pos.method_10264() + 0.5D, (double)pos.method_10260() + 0.5D, (Color)this.stalagmiteTracerColor.get());
            }
         }
      }

   }

   private void scanChunk(class_2818 chunk) {
      int startX = chunk.method_12004().method_8326();
      int startZ = chunk.method_12004().method_8328();
      int minY = chunk.method_31607();
      int maxY = minY + chunk.method_31605();

      for(int x = startX; x < startX + 16; ++x) {
         for(int z = startZ; z < startZ + 16; ++z) {
            for(int y = minY; y < maxY; ++y) {
               class_2338 pos = new class_2338(x, y, z);
               class_2680 state = chunk.method_8320(pos);
               if (this.isDripstoneTipDown(state)) {
                  DripstoneESP.StalactiteInfo info = this.getStalactiteInfo(pos);
                  if (info != null && info.length >= (Integer)this.stalactiteMinLength.get()) {
                     this.longStalactiteBottoms.add(info.bottomPos);
                  }
               } else if (this.isDripstoneTipUp(state)) {
                  DripstoneESP.StalagmiteInfo info = this.getStalagmiteInfo(pos);
                  if (info != null && info.length >= (Integer)this.stalagmiteMinLength.get()) {
                     this.longStalagmiteTops.add(info.topPos);
                  }
               }
            }
         }
      }

   }

   private boolean isDripstoneTipDown(class_2680 state) {
      return state.method_27852(class_2246.field_28048) && state.method_28498(class_2741.field_28062) && state.method_11654(class_2741.field_28062) == class_2350.field_11033;
   }

   private boolean isDripstoneTipUp(class_2680 state) {
      return state.method_27852(class_2246.field_28048) && state.method_28498(class_2741.field_28062) && state.method_11654(class_2741.field_28062) == class_2350.field_11036;
   }

   private DripstoneESP.StalactiteInfo getStalactiteInfo(class_2338 tipPos) {
      if (this.mc.field_1687 == null) {
         return null;
      } else {
         int length = 0;
         class_2338 current = tipPos;

         class_2338 bottom;
         for(bottom = tipPos; current.method_10264() >= this.mc.field_1687.method_31607() && this.isDripstoneTipDown(this.mc.field_1687.method_8320(current)); current = current.method_10074()) {
            ++length;
            bottom = current;
         }

         return length > 0 ? new DripstoneESP.StalactiteInfo(length, bottom) : null;
      }
   }

   private DripstoneESP.StalagmiteInfo getStalagmiteInfo(class_2338 tipPos) {
      if (this.mc.field_1687 == null) {
         return null;
      } else {
         int length = 0;
         class_2338 current = tipPos;

         class_2338 top;
         for(top = tipPos; current.method_10264() < 320 && this.isDripstoneTipUp(this.mc.field_1687.method_8320(current)); current = current.method_10084()) {
            ++length;
            top = current;
         }

         return length > 0 ? new DripstoneESP.StalagmiteInfo(length, top) : null;
      }
   }

   private void notifyDripstoneFound(String type, class_2338 pos, int length) {
      if ((Boolean)this.showNotifications.get() && length >= (Integer)this.notificationLengthThreshold.get()) {
         String msg = String.format("%s found at [%d, %d, %d] (len: %d)", type, pos.method_10263(), pos.method_10264(), pos.method_10260(), length);
         String subText = type + " (" + length + ")";
         if (this.notificationMode.get() == DripstoneESP.Mode.Chat || this.notificationMode.get() == DripstoneESP.Mode.Both) {
            this.info("(highlight)%s", new Object[]{msg});
         }

         if (this.notificationMode.get() == DripstoneESP.Mode.Toast || this.notificationMode.get() == DripstoneESP.Mode.Both) {
            this.mc.method_1566().method_1999(new DripstoneESP.DripstoneToast("DripstoneESP", subText));
         }

      }
   }

   public static enum Mode {
      Chat,
      Toast,
      Both;

      // $FF: synthetic method
      private static DripstoneESP.Mode[] $values() {
         return new DripstoneESP.Mode[]{Chat, Toast, Both};
      }
   }

   private static class StalactiteInfo {
      final int length;
      final class_2338 bottomPos;

      StalactiteInfo(int length, class_2338 bottomPos) {
         this.length = length;
         this.bottomPos = bottomPos;
      }
   }

   private static class StalagmiteInfo {
      final int length;
      final class_2338 topPos;

      StalagmiteInfo(int length, class_2338 topPos) {
         this.length = length;
         this.topPos = topPos;
      }
   }

   private static class DripstoneToast extends MeteorToast {
      private final String customTitle;
      private final String customDesc;

      public DripstoneToast(String title, String desc) {
         super(class_1802.field_28042, title, desc);
         this.customTitle = title;
         this.customDesc = desc;
      }

      public void method_1986(class_332 context, class_327 textRenderer, long currentTime) {
         context.method_25294(0, 0, 160, 32, -1442840576);
         int grey = -8355712;
         context.method_25294(0, 0, 160, 1, grey);
         context.method_25294(0, 31, 160, 32, grey);
         context.method_25294(0, 1, 1, 31, grey);
         context.method_25294(159, 1, 160, 31, grey);
         context.method_51427(class_1802.field_28042.method_7854(), 8, 8);
         context.method_51433(textRenderer, this.customTitle, 32, 7, 16777215, false);
         context.method_51433(textRenderer, this.customDesc, 32, 18, grey, false);
      }
   }
}
