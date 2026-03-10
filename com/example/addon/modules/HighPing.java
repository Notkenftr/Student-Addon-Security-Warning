package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Send;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.BoolSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2596;

public class HighPing extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> loopMode;
   private final Setting<Double> loopLagSeconds;
   private final Setting<Double> loopNormalSeconds;
   private boolean lagging;
   private double tickCounter;
   private final List<class_2596<?>> storedPackets;

   public HighPing() {
      super(AddonTemplate.Student_pvp, "High Ping", "Fake high ping.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.loopMode = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("loop-mode")).description("Lag")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      meteordevelopment.meteorclient.settings.DoubleSetting.Builder var10002 = ((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)(new meteordevelopment.meteorclient.settings.DoubleSetting.Builder()).name("loop-lag-seconds")).description("Lag")).defaultValue(1.0D).min(0.1D).sliderMin(0.1D).sliderMax(10.0D);
      Setting var10003 = this.loopMode;
      Objects.requireNonNull(var10003);
      this.loopLagSeconds = var10001.add(((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      var10002 = ((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)(new meteordevelopment.meteorclient.settings.DoubleSetting.Builder()).name("loop-normal-seconds")).description("Lag")).defaultValue(1.0D).min(0.1D).sliderMin(0.1D).sliderMax(10.0D);
      var10003 = this.loopMode;
      Objects.requireNonNull(var10003);
      this.loopNormalSeconds = var10001.add(((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      this.lagging = true;
      this.tickCounter = 0.0D;
      this.storedPackets = new ArrayList();
   }

   public void onActivate() {
      this.lagging = true;
      this.tickCounter = 0.0D;
      this.storedPackets.clear();
   }

   public void onDeactivate() {
      this.flushPackets();
      this.storedPackets.clear();
   }

   @EventHandler
   private void onTick(Pre event) {
      if (this.mc.field_1724 != null) {
         if ((Boolean)this.loopMode.get()) {
            ++this.tickCounter;
            double lagTicks = (Double)this.loopLagSeconds.get() * 20.0D;
            double normalTicks = (Double)this.loopNormalSeconds.get() * 20.0D;
            if (this.lagging && this.tickCounter >= lagTicks) {
               this.lagging = false;
               this.tickCounter = 0.0D;
               this.flushPackets();
            }

            if (!this.lagging && this.tickCounter >= normalTicks) {
               this.lagging = true;
               this.tickCounter = 0.0D;
            }

         }
      }
   }

   @EventHandler
   private void onPacketSend(Send event) {
      if (this.lagging) {
         this.storedPackets.add(event.packet);
         event.cancel();
      }
   }

   private void flushPackets() {
      if (this.mc.method_1562() != null) {
         Iterator var1 = this.storedPackets.iterator();

         while(var1.hasNext()) {
            class_2596<?> packet = (class_2596)var1.next();
            this.mc.method_1562().method_52787(packet);
         }

         this.storedPackets.clear();
      }
   }
}
