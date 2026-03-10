package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.EnumSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1703;
import net.minecraft.class_1707;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2561;
import net.minecraft.class_2586;
import net.minecraft.class_2818;
import net.minecraft.class_3417;
import net.minecraft.class_3419;

public class RTPFinderBase extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgWebhook;
   private final SettingGroup sgSound;
   private final Setting<RTPFinderBase.WorldType> world;
   private final Setting<Boolean> baseFinder;
   private final Setting<Integer> depth;
   private final Setting<Integer> spamDelay;
   private final Setting<Integer> containerLimit;
   private final Setting<List<class_2248>> targetBlocks;
   private final Setting<String> webhookUrl;
   private final Setting<Boolean> webhookEnabled;
   private final Setting<Boolean> disconnectOnFound;
   private final Setting<Double> volume;
   private RTPFinderBase.State state;
   private boolean baseFound;
   private int serverCooldown;
   private int rtpWaitTimer;
   private int waitTimer;
   private int loopTimer;

   public RTPFinderBase() {
      super(AddonTemplate.Student_esp, "RTP Finder Base", "RTP and mine down to find base.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgWebhook = this.settings.createGroup("Webhook");
      this.sgSound = this.settings.createGroup("Sound");
      this.world = this.sgGeneral.add(((Builder)((Builder)(new Builder()).name("world")).defaultValue(RTPFinderBase.WorldType.Overworld)).build());
      this.baseFinder = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("base-finder")).defaultValue(false)).visible(() -> {
         return this.world.get() == RTPFinderBase.WorldType.Overworld;
      })).build());
      this.depth = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)(new meteordevelopment.meteorclient.settings.IntSetting.Builder()).name("depth")).description("Y level đào xuống khi dùng #goto")).defaultValue(30)).min(-64).sliderMin(-64).sliderMax(320).visible(() -> {
         return this.world.get() == RTPFinderBase.WorldType.Overworld && (Boolean)this.baseFinder.get();
      })).build());
      this.spamDelay = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)(new meteordevelopment.meteorclient.settings.IntSetting.Builder()).name("spam-delay")).defaultValue(40)).min(10).build());
      this.containerLimit = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)(new meteordevelopment.meteorclient.settings.IntSetting.Builder()).name("container-limit")).defaultValue(10)).min(1).sliderMax(100).build());
      this.targetBlocks = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.BlockListSetting.Builder)(new meteordevelopment.meteorclient.settings.BlockListSetting.Builder()).name("target-blocks")).defaultValue(new class_2248[]{class_2246.field_10034, class_2246.field_16328, class_2246.field_10603}).build());
      this.webhookUrl = this.sgWebhook.add(((meteordevelopment.meteorclient.settings.StringSetting.Builder)((meteordevelopment.meteorclient.settings.StringSetting.Builder)((meteordevelopment.meteorclient.settings.StringSetting.Builder)(new meteordevelopment.meteorclient.settings.StringSetting.Builder()).name("webhook-url")).description("Discord Webhook URL")).defaultValue("")).build());
      this.webhookEnabled = this.sgWebhook.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("enable-webhook")).defaultValue(true)).build());
      this.disconnectOnFound = this.sgWebhook.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("disconnect-on-found")).description("Tự out game khi phát hiện base")).defaultValue(true)).build());
      this.volume = this.sgSound.add(((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)(new meteordevelopment.meteorclient.settings.DoubleSetting.Builder()).name("volume")).defaultValue(1.0D).min(0.0D).max(5.0D).build());
      this.state = RTPFinderBase.State.WAIT_GUI;
      this.baseFound = false;
      this.serverCooldown = 0;
      this.rtpWaitTimer = 0;
      this.waitTimer = 0;
      this.loopTimer = 0;
   }

   public void onActivate() {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         this.baseFound = false;
         this.serverCooldown = 0;
         this.rtpWaitTimer = 0;
         this.waitTimer = 0;
         this.loopTimer = 0;
         this.scanForBase();
         if (!this.baseFound) {
            this.sendRtp();
         }
      }
   }

   public void onDeactivate() {
   }

   @EventHandler
   private void onTick(Post event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (!this.baseFound) {
            switch(this.state.ordinal()) {
            case 0:
               if (this.serverCooldown > 0) {
                  --this.serverCooldown;
               } else {
                  this.sendRtp();
               }
               break;
            case 1:
               ++this.rtpWaitTimer;
               if (this.mc.field_1724.field_7512 instanceof class_1707) {
                  this.state = RTPFinderBase.State.STEAL;
                  this.rtpWaitTimer = 0;
                  return;
               }

               if (this.rtpWaitTimer >= 100) {
                  this.rtpWaitTimer = 0;
                  this.sendRtp();
               }
               break;
            case 2:
               class_1703 var3 = this.mc.field_1724.field_7512;
               if (var3 instanceof class_1707) {
                  class_1707 handler = (class_1707)var3;
                  this.stealBlock(handler);
               } else {
                  this.sendRtp();
               }
               break;
            case 3:
               ++this.waitTimer;
               ++this.loopTimer;
               this.scanForBase();
               if (this.baseFound) {
                  return;
               }

               if (this.waitTimer >= 200) {
                  this.afterWait();
                  this.state = RTPFinderBase.State.GOTO;
               }

               if (this.loopTimer >= 1300) {
                  this.resetAndRtp();
               }
               break;
            case 4:
               ++this.loopTimer;
               this.scanForBase();
               if (this.baseFound) {
                  return;
               }

               if (this.loopTimer >= 1300) {
                  this.resetAndRtp();
               }
            }

         }
      }
   }

   @EventHandler
   private void onMessage(ReceiveMessageEvent event) {
      String msg = event.getMessage().getString();
      if (msg.contains("Vui lòng chờ")) {
         try {
            String[] parts = msg.split(" ");

            for(int i = 0; i < parts.length; ++i) {
               if (parts[i].equals("chờ") && i + 1 < parts.length) {
                  int seconds = Integer.parseInt(parts[i + 1]);
                  this.serverCooldown = seconds * 20 + 20;
                  this.state = RTPFinderBase.State.WAIT_COOLDOWN;
                  break;
               }
            }
         } catch (Exception var6) {
         }
      }

   }

   private void sendRtp() {
      this.state = RTPFinderBase.State.WAIT_GUI;
      this.rtpWaitTimer = 0;
      this.mc.field_1724.field_3944.method_45730("rtp");
   }

   private void resetAndRtp() {
      if (!this.baseFound) {
         this.waitTimer = 0;
         this.loopTimer = 0;
         this.mc.method_1562().method_45729("#stop");
         this.sendRtp();
      }
   }

   private void stealBlock(class_1707 handler) {
      for(int i = 0; i < handler.method_7629().method_5439(); ++i) {
         class_1799 stack = handler.method_7629().method_5438(i);
         if (!stack.method_7960()) {
            boolean match = this.world.get() == RTPFinderBase.WorldType.Overworld && stack.method_31574(class_1802.field_8270) || this.world.get() == RTPFinderBase.WorldType.Nether && stack.method_31574(class_1802.field_8328) || this.world.get() == RTPFinderBase.WorldType.End && stack.method_31574(class_1802.field_20399);
            if (match) {
               this.mc.field_1761.method_2906(handler.field_7763, i, 0, class_1713.field_7794, this.mc.field_1724);
               this.waitTimer = 0;
               this.loopTimer = 0;
               this.state = RTPFinderBase.State.WAIT_AFTER;
               break;
            }
         }
      }

   }

   private void afterWait() {
      if (this.world.get() == RTPFinderBase.WorldType.Overworld && (Boolean)this.baseFinder.get()) {
         this.mc.method_1562().method_45729("#goto " + String.valueOf(this.depth.get()));
      }

   }

   private void scanForBase() {
      int px = this.mc.field_1724.method_24515().method_10263() >> 4;
      int pz = this.mc.field_1724.method_24515().method_10260() >> 4;
      int radius = 6;

      for(int x = px - radius; x <= px + radius; ++x) {
         for(int z = pz - radius; z <= pz + radius; ++z) {
            if (this.mc.field_1687.method_8393(x, z)) {
               class_2818 chunk = this.mc.field_1687.method_8497(x, z);
               int containerCount = 0;
               Iterator var8 = chunk.method_12214().values().iterator();

               while(var8.hasNext()) {
                  class_2586 be = (class_2586)var8.next();
                  class_2338 pos = be.method_11016();
                  class_2248 block = this.mc.field_1687.method_8320(pos).method_26204();
                  if (((List)this.targetBlocks.get()).contains(block)) {
                     ++containerCount;
                  }
               }

               if (containerCount >= (Integer)this.containerLimit.get()) {
                  this.foundBase();
                  return;
               }
            }
         }
      }

   }

   private void foundBase() {
      if (!this.baseFound) {
         this.baseFound = true;
         int x = this.mc.field_1724.method_24515().method_10263();
         int y = this.mc.field_1724.method_24515().method_10264();
         int z = this.mc.field_1724.method_24515().method_10260();
         String server = this.mc.method_1558() != null ? this.mc.method_1558().field_3761 : "Unknown";
         String playerName = this.mc.field_1724.method_5477().getString();
         this.info("§a§lBase Found! X: %d Y: %d Z: %d", new Object[]{x, y, z});
         this.mc.field_1687.method_8396(this.mc.field_1724, this.mc.field_1724.method_24515(), class_3417.field_14627, class_3419.field_15250, ((Double)this.volume.get()).floatValue(), 1.0F);
         if ((Boolean)this.webhookEnabled.get() && !((String)this.webhookUrl.get()).isEmpty()) {
            this.sendWebhook(playerName, server, x, y, z);
         }

         if ((Boolean)this.disconnectOnFound.get()) {
            this.mc.execute(() -> {
               if (this.mc.method_1562() != null) {
                  this.mc.method_1562().method_48296().method_10747(class_2561.method_43470("Base found - auto disconnect"));
               }

            });
         }

         this.toggle();
      }
   }

   private void sendWebhook(String player, String server, int x, int y, int z) {
      String json = String.format("{\n  \"embeds\": [{\n    \"title\": \"\ud83c\udfe0 Base Found!\",\n    \"color\": 65280,\n    \"fields\": [\n      {\"name\": \"Player\", \"value\": \"%s\", \"inline\": true},\n      {\"name\": \"Server\", \"value\": \"%s\", \"inline\": true},\n      {\"name\": \"Coordinates\", \"value\": \"X: %d Y: %d Z: %d\", \"inline\": false}\n    ]\n  }]\n}\n", player, server, x, y, z);
      Thread.ofVirtual().start(() -> {
         try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create((String)this.webhookUrl.get())).header("Content-Type", "application/json").POST(BodyPublishers.ofString(json)).build();
            client.send(request, BodyHandlers.ofString());
         } catch (Exception var4) {
            var4.printStackTrace();
         }

      });
   }

   public static enum WorldType {
      Overworld,
      Nether,
      End;

      // $FF: synthetic method
      private static RTPFinderBase.WorldType[] $values() {
         return new RTPFinderBase.WorldType[]{Overworld, Nether, End};
      }
   }

   private static enum State {
      WAIT_COOLDOWN,
      WAIT_GUI,
      STEAL,
      WAIT_AFTER,
      GOTO;

      // $FF: synthetic method
      private static RTPFinderBase.State[] $values() {
         return new RTPFinderBase.State[]{WAIT_COOLDOWN, WAIT_GUI, STEAL, WAIT_AFTER, GOTO};
      }
   }
}
