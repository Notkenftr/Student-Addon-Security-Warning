package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.IntSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2561;
import net.minecraft.class_2586;
import net.minecraft.class_2818;
import net.minecraft.class_3417;
import net.minecraft.class_3419;

public class TunnelFinderBase extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgWebhook;
   private final SettingGroup sgSound;
   private final Setting<Integer> targetY;
   private final Setting<Integer> containerLimit;
   private final Setting<List<class_2248>> targetBlocks;
   private final Setting<Integer> scanRadius;
   private final Setting<Boolean> webhookEnabled;
   private final Setting<String> webhookUrl;
   private final Setting<Boolean> disconnectOnFound;
   private final Setting<Double> volume;
   private TunnelFinderBase.State state;
   private boolean baseFound;
   private int waitTimer;

   public TunnelFinderBase() {
      super(AddonTemplate.Student_esp, "TunnelFinderBase", "Goto Y level, tunnel, and find base.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgWebhook = this.settings.createGroup("Webhook");
      this.sgSound = this.settings.createGroup("Sound");
      this.targetY = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("target-y")).description("Y level gửi #goto")).defaultValue(-60)).min(-64).sliderMin(-64).sliderMax(320).build());
      this.containerLimit = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("container-limit")).description("Số container tối thiểu để coi là base")).defaultValue(10)).min(1).sliderMax(100).build());
      this.targetBlocks = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.BlockListSetting.Builder)(new meteordevelopment.meteorclient.settings.BlockListSetting.Builder()).name("target-blocks")).defaultValue(new class_2248[]{class_2246.field_10034, class_2246.field_16328, class_2246.field_10603}).build());
      this.scanRadius = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("scan-radius")).description("Bán kính chunk scan tìm base")).defaultValue(6)).min(1).sliderMax(20).build());
      this.webhookEnabled = this.sgWebhook.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("enable-webhook")).defaultValue(true)).build());
      this.webhookUrl = this.sgWebhook.add(((meteordevelopment.meteorclient.settings.StringSetting.Builder)((meteordevelopment.meteorclient.settings.StringSetting.Builder)((meteordevelopment.meteorclient.settings.StringSetting.Builder)(new meteordevelopment.meteorclient.settings.StringSetting.Builder()).name("webhook-url")).description("Discord Webhook URL")).defaultValue("")).build());
      this.disconnectOnFound = this.sgWebhook.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("disconnect-on-found")).description("Tự out game khi phát hiện base")).defaultValue(true)).build());
      this.volume = this.sgSound.add(((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)(new meteordevelopment.meteorclient.settings.DoubleSetting.Builder()).name("volume")).defaultValue(1.0D).min(0.0D).max(5.0D).build());
      this.state = TunnelFinderBase.State.GOTO;
      this.baseFound = false;
      this.waitTimer = 0;
   }

   public void onActivate() {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         this.baseFound = false;
         this.waitTimer = 0;
         this.state = TunnelFinderBase.State.GOTO;
         this.mc.method_1562().method_45729("#goto " + String.valueOf(this.targetY.get()));
         this.state = TunnelFinderBase.State.WAIT_Y;
      }
   }

   public void onDeactivate() {
      this.mc.method_1562().method_45729("#stop");
   }

   @EventHandler
   private void onTick(Post event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (!this.baseFound) {
            switch(this.state.ordinal()) {
            case 1:
               int currentY = this.mc.field_1724.method_24515().method_10264();
               if (currentY <= (Integer)this.targetY.get() + 2) {
                  this.mc.method_1562().method_45729("#tunnel");
                  this.waitTimer = 0;
                  this.state = TunnelFinderBase.State.TUNNEL;
               }

               ++this.waitTimer;
               if (this.waitTimer >= 600) {
                  this.waitTimer = 0;
                  this.mc.method_1562().method_45729("#goto " + String.valueOf(this.targetY.get()));
               }
               break;
            case 2:
               ++this.waitTimer;
               if (this.waitTimer >= 20) {
                  this.waitTimer = 0;
                  this.state = TunnelFinderBase.State.SCANNING;
               }
               break;
            case 3:
               this.scanForBase();
            }

         }
      }
   }

   private void scanForBase() {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         int px = this.mc.field_1724.method_24515().method_10263() >> 4;
         int pz = this.mc.field_1724.method_24515().method_10260() >> 4;
         int radius = (Integer)this.scanRadius.get();

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
         this.mc.method_1562().method_45729("#stop");
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

   private static enum State {
      GOTO,
      WAIT_Y,
      TUNNEL,
      SCANNING;

      // $FF: synthetic method
      private static TunnelFinderBase.State[] $values() {
         return new TunnelFinderBase.State[]{GOTO, WAIT_Y, TUNNEL, SCANNING};
      }
   }
}
