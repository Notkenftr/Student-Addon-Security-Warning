package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.BlockUpdateEvent;
import meteordevelopment.meteorclient.events.world.ChunkDataEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.BoolSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.MeteorToast;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1533;
import net.minecraft.class_1542;
import net.minecraft.class_1694;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_1923;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2561;
import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_2791;
import net.minecraft.class_2818;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_374;
import net.minecraft.class_5250;
import net.minecraft.class_2350.class_2351;

public class ChunkFinder extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> showCoords;
   private final Setting<Integer> renderDistance;
   private final Setting<ChunkFinder.HoleMode> holeMode;
   private final Setting<SettingColor> chunkColor;
   private final Setting<Boolean> showTracers;
   private final Setting<Integer> holeCheckRadius;
   private static final int MIN_VINE_LENGTH = 20;
   private static final int MIN_KELP_LENGTH = 20;
   private static final int ROTATED_DEEPSLATE_THRESHOLD = 5;
   private static final int ENTITY_MAX_DISTANCE = 256;
   private static final int MIN_Y_LEVEL = 16;
   private final Map<class_1923, ChunkFinder.DetectionInfo> detectedChunks;
   private final Set<class_1923> chunksWithBrokenBlocks;
   private final Set<Integer> detectedEntities;
   private final Set<class_1923> scannedChunks;
   private ExecutorService threadPool;

   public ChunkFinder() {
      super(AddonTemplate.Student_esp, "chunk-finder", "Detects suspicious chunks.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.showCoords = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("show-coordinates")).description("Show coordinates in chat and notifications")).defaultValue(true)).build());
      this.renderDistance = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)(new meteordevelopment.meteorclient.settings.IntSetting.Builder()).name("render-distance")).description("Hide detections further than this distance.")).defaultValue(512)).min(32).sliderMax(2048).build());
      this.holeMode = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)(new meteordevelopment.meteorclient.settings.EnumSetting.Builder()).name("hole-detection")).description("Detect chunks with or without holes")).defaultValue(ChunkFinder.HoleMode.Both)).build());
      this.chunkColor = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)(new meteordevelopment.meteorclient.settings.ColorSetting.Builder()).name("chunk-color")).description("Color of detected chunks")).defaultValue(new SettingColor(0, 255, 255, 100)).build());
      this.showTracers = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("show-tracers")).description("Draw tracers to detected chunks")).defaultValue(false)).build());
      this.holeCheckRadius = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)(new meteordevelopment.meteorclient.settings.IntSetting.Builder()).name("hole-check-radius")).description("Blocks radius to check for holes")).defaultValue(8)).min(0).max(64).visible(() -> {
         return this.holeMode.get() == ChunkFinder.HoleMode.Without_Holes;
      })).build());
      this.detectedChunks = new ConcurrentHashMap();
      this.chunksWithBrokenBlocks = ConcurrentHashMap.newKeySet();
      this.detectedEntities = ConcurrentHashMap.newKeySet();
      this.scannedChunks = ConcurrentHashMap.newKeySet();
   }

   public void onActivate() {
      this.clearData();
      this.threadPool = Executors.newFixedThreadPool(2);
      if (this.mc.field_1687 != null) {
         Iterator var1 = Utils.chunks().iterator();

         while(var1.hasNext()) {
            class_2791 chunk = (class_2791)var1.next();
            if (chunk instanceof class_2818) {
               class_2818 worldChunk = (class_2818)chunk;
               this.threadPool.submit(() -> {
                  this.scanChunk(worldChunk);
               });
            }
         }
      }

   }

   public void onDeactivate() {
      if (this.threadPool != null) {
         this.threadPool.shutdownNow();
      }

      this.clearData();
   }

   private void clearData() {
      this.detectedChunks.clear();
      this.chunksWithBrokenBlocks.clear();
      this.detectedEntities.clear();
      this.scannedChunks.clear();
   }

   @EventHandler
   private void onChunkLoad(ChunkDataEvent event) {
      if (this.threadPool != null && !this.threadPool.isShutdown()) {
         this.threadPool.submit(() -> {
            this.scanChunk(event.chunk());
         });
      }

   }

   @EventHandler
   private void onBlockUpdate(BlockUpdateEvent event) {
      if (event.pos.method_10264() > 16) {
         if (event.newState.method_26215()) {
            class_1923 cp = new class_1923(event.pos);
            if (this.detectedChunks.containsKey(cp)) {
               this.chunksWithBrokenBlocks.add(cp);
            }
         }

         if (this.threadPool != null && !this.threadPool.isShutdown()) {
            this.threadPool.submit(() -> {
               class_2680 state = event.newState;
               if (state.method_27852(class_2246.field_10597)) {
                  this.checkVine(event.pos);
               }

               if (state.method_27852(class_2246.field_9993) || state.method_27852(class_2246.field_10463)) {
                  this.checkKelp(event.pos);
               }

               if (state.method_27852(class_2246.field_28888)) {
                  this.checkRotatedDeepslate(event.pos);
               }

            });
         }

      }
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         this.checkEntities();
         Color sideColor = (Color)this.chunkColor.get();
         Color lineColor = (new Color(sideColor)).a(255);
         Iterator var4 = this.detectedChunks.entrySet().iterator();

         while(var4.hasNext()) {
            Entry<class_1923, ChunkFinder.DetectionInfo> entry = (Entry)var4.next();
            class_1923 cp = (class_1923)entry.getKey();
            class_2338 pos = ((ChunkFinder.DetectionInfo)entry.getValue()).position();
            double distSq = PlayerUtils.squaredDistanceTo((double)pos.method_10263() + 0.5D, (double)pos.method_10264() + 0.5D, (double)pos.method_10260() + 0.5D);
            if (!(distSq > Math.pow((double)(Integer)this.renderDistance.get(), 2.0D))) {
               double x1 = (double)cp.method_8326();
               double z1 = (double)cp.method_8328();
               double y = (double)pos.method_10264();
               event.renderer.box(x1, y, z1, x1 + 16.0D, y + 0.3D, z1 + 16.0D, sideColor, lineColor, ShapeMode.Both, 0);
               if ((Boolean)this.showTracers.get() && !this.chunksWithBrokenBlocks.contains(cp)) {
                  event.renderer.line(RenderUtils.center.field_1352, RenderUtils.center.field_1351, RenderUtils.center.field_1350, x1 + 8.0D, y + 0.15D, z1 + 8.0D, lineColor);
               }
            }
         }

      }
   }

   private void addDetection(class_2338 pos) {
      if (pos.method_10264() > 16) {
         class_1923 chunkPos = new class_1923(pos);
         Iterator var3 = this.detectedChunks.keySet().iterator();

         class_1923 existing;
         do {
            if (!var3.hasNext()) {
               if (this.holeMode.get() == ChunkFinder.HoleMode.Without_Holes && this.chunkHasHoles(chunkPos)) {
                  return;
               }

               this.detectedChunks.put(chunkPos, new ChunkFinder.DetectionInfo(pos, System.currentTimeMillis()));
               if (this.mc.field_1687 != null) {
                  this.mc.field_1687.method_8396(this.mc.field_1724, pos, class_3417.field_14627, class_3419.field_15250, 1.0F, 1.0F);
               }

               if ((Boolean)this.showCoords.get()) {
                  this.info("Detected suspicious chunk at X: %d, Z: %d", new Object[]{pos.method_10263(), pos.method_10260()});
                  class_374 var10000 = this.mc.method_1566();
                  class_1792 var10003 = class_1802.field_8449;
                  class_5250 var10004 = class_2561.method_43470("ChunkFinderV2");
                  int var10005 = pos.method_10263();
                  var10000.method_1999(new ChunkFinder.CustomChunkToast(var10003, var10004, class_2561.method_43470("X: " + var10005 + " Z: " + pos.method_10260())));
               }

               return;
            }

            existing = (class_1923)var3.next();
         } while(Math.abs(existing.field_9181 - chunkPos.field_9181) > 2 || Math.abs(existing.field_9180 - chunkPos.field_9180) > 2);

      }
   }

   private void scanChunk(class_2818 chunk) {
      class_1923 cpos = chunk.method_12004();
      if (this.scannedChunks.add(cpos)) {
         this.scanChunkForVines(chunk);
         this.scanChunkForKelp(chunk);
         this.scanChunkForRotatedDeepslate(chunk);
      }
   }

   private void scanChunkForRotatedDeepslate(class_2818 chunk) {
      int count = 0;
      class_2338 firstFound = null;
      class_1923 cPos = chunk.method_12004();

      for(int x = cPos.method_8326(); x < cPos.method_8327(); ++x) {
         for(int z = cPos.method_8328(); z < cPos.method_8329(); ++z) {
            for(int y = 17; y < 319; ++y) {
               class_2338 pos = new class_2338(x, y, z);
               class_2680 state = chunk.method_8320(pos);
               if (state.method_27852(class_2246.field_28888) && state.method_28498(class_2741.field_12496)) {
                  class_2351 axis = (class_2351)state.method_11654(class_2741.field_12496);
                  if (axis == class_2351.field_11048 || axis == class_2351.field_11051) {
                     if (firstFound == null) {
                        firstFound = pos;
                     }

                     ++count;
                     if (count >= 5) {
                        this.addDetection(firstFound);
                        return;
                     }
                  }
               }
            }
         }
      }

   }

   private void scanChunkForKelp(class_2818 chunk) {
      class_1923 cPos = chunk.method_12004();

      for(int x = cPos.method_8326(); x < cPos.method_8327(); ++x) {
         for(int z = cPos.method_8328(); z < cPos.method_8329(); ++z) {
            for(int y = 17; y < 64; ++y) {
               class_2338 pos = new class_2338(x, y, z);
               if (chunk.method_8320(pos).method_27852(class_2246.field_9993) && this.getKelpLength(pos) >= 20) {
                  this.addDetection(pos);
               }
            }
         }
      }

   }

   private void scanChunkForVines(class_2818 chunk) {
      class_1923 cPos = chunk.method_12004();

      for(int x = cPos.method_8326(); x < cPos.method_8327(); ++x) {
         for(int z = cPos.method_8328(); z < cPos.method_8329(); ++z) {
            for(int y = 318; y > 16; --y) {
               class_2338 pos = new class_2338(x, y, z);
               if (chunk.method_8320(pos).method_27852(class_2246.field_10597) && this.vineExtendsToY16(pos) && this.getVineLength(pos) >= 20) {
                  this.addDetection(pos);
               }
            }
         }
      }

   }

   private void checkRotatedDeepslate(class_2338 pos) {
      if (this.mc.field_1687 != null && pos.method_10264() > 16) {
         class_2818 chunk = this.mc.field_1687.method_8500(pos);
         if (chunk != null) {
            this.scanChunkForRotatedDeepslate(chunk);
         }

      }
   }

   private void checkVine(class_2338 pos) {
      if (pos.method_10264() > 16 && this.vineExtendsToY16(pos) && this.getVineLength(pos) >= 20) {
         this.addDetection(pos);
      }

   }

   private void checkKelp(class_2338 pos) {
      if (pos.method_10264() > 16 && this.getKelpLength(pos) >= 20) {
         this.addDetection(pos);
      }

   }

   private void checkEntities() {
      if (this.mc.field_1687 != null && this.mc.field_1724 != null) {
         Iterator var1 = this.mc.field_1687.method_18112().iterator();

         while(true) {
            class_1297 entity;
            do {
               do {
                  if (!var1.hasNext()) {
                     return;
                  }

                  entity = (class_1297)var1.next();
               } while(entity.method_24515().method_10264() <= 16);
            } while(!(entity instanceof class_1533) && !(entity instanceof class_1542) && !(entity instanceof class_1694));

            if (this.mc.field_1724.method_5739(entity) <= 256.0F && this.detectedEntities.add(entity.method_5628())) {
               this.addDetection(entity.method_24515());
            }
         }
      }
   }

   private boolean chunkHasHoles(class_1923 cp) {
      if (this.mc.field_1687 == null) {
         return false;
      } else {
         int air = 0;
         int total = 0;
         int radius = (Integer)this.holeCheckRadius.get();
         int cx = cp.method_33940();
         int cz = cp.method_33942();

         for(int x = cx - radius; x <= cx + radius; ++x) {
            for(int z = cz - radius; z <= cz + radius; ++z) {
               for(int y = 60; y <= 70; ++y) {
                  if (this.mc.field_1687.method_8320(new class_2338(x, y, z)).method_26215()) {
                     ++air;
                  }

                  ++total;
               }
            }
         }

         return total > 0 && (double)air / (double)total > 0.1D;
      }
   }

   private boolean vineExtendsToY16(class_2338 start) {
      if (this.mc.field_1687 == null) {
         return false;
      } else {
         for(class_2338 p = start; p.method_10264() >= 16; p = p.method_10074()) {
            if (!this.mc.field_1687.method_8320(p).method_27852(class_2246.field_10597)) {
               return false;
            }

            if (p.method_10264() == 16) {
               return true;
            }
         }

         return false;
      }
   }

   private int getVineLength(class_2338 start) {
      if (this.mc.field_1687 == null) {
         return 0;
      } else {
         int l = 0;

         class_2338 p;
         for(p = start; this.mc.field_1687.method_8320(p).method_27852(class_2246.field_10597); p = p.method_10084()) {
            ++l;
         }

         for(p = start.method_10074(); p.method_10264() >= 16 && this.mc.field_1687.method_8320(p).method_27852(class_2246.field_10597); p = p.method_10074()) {
            ++l;
         }

         return l;
      }
   }

   private int getKelpLength(class_2338 start) {
      if (this.mc.field_1687 == null) {
         return 0;
      } else {
         int l = 0;

         class_2338 p;
         for(p = start; this.mc.field_1687.method_8320(p).method_27852(class_2246.field_9993) || this.mc.field_1687.method_8320(p).method_27852(class_2246.field_10463); p = p.method_10084()) {
            ++l;
         }

         for(p = start.method_10074(); p.method_10264() >= 16 && (this.mc.field_1687.method_8320(p).method_27852(class_2246.field_9993) || this.mc.field_1687.method_8320(p).method_27852(class_2246.field_10463)); p = p.method_10074()) {
            ++l;
         }

         return l;
      }
   }

   public String getInfoString() {
      return this.detectedChunks.isEmpty() ? null : String.valueOf(this.detectedChunks.size());
   }

   public static enum HoleMode {
      Without_Holes,
      Both;

      // $FF: synthetic method
      private static ChunkFinder.HoleMode[] $values() {
         return new ChunkFinder.HoleMode[]{Without_Holes, Both};
      }
   }

   private static record DetectionInfo(class_2338 position, long timestamp) {
      private DetectionInfo(class_2338 position, long timestamp) {
         this.position = position;
         this.timestamp = timestamp;
      }

      public class_2338 position() {
         return this.position;
      }

      public long timestamp() {
         return this.timestamp;
      }
   }

   private static class CustomChunkToast extends MeteorToast {
      public CustomChunkToast(class_1792 icon, class_2561 title, class_2561 description) {
         super(icon, title.getString(), description.getString());
      }
   }
}
