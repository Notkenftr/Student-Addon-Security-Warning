package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import com.example.addon.utils.glazed.BlockUtil;
import com.example.addon.utils.glazed.KeyUtils;
import java.util.Iterator;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.IntSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1621;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_2868;
import net.minecraft.class_3965;
import net.minecraft.class_3966;

public class CrystalLegit extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Integer> activateKey;
   private final Setting<Double> placeDelay;
   private final Setting<Double> breakDelay;
   private final Setting<Boolean> placeObsidianIfMissing;
   private int placeDelayCounter;
   private int breakDelayCounter;

   public CrystalLegit() {
      super(AddonTemplate.Student_pvp, "CrystalLegit", "Legit crystal place & break.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.activateKey = this.sgGeneral.add(((Builder)((Builder)(new Builder()).name("activate-key")).defaultValue(1)).min(-1).max(400).build());
      this.placeDelay = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)(new meteordevelopment.meteorclient.settings.DoubleSetting.Builder()).name("place-delay")).defaultValue(0.0D).min(0.0D).max(20.0D).sliderMax(20.0D).build());
      this.breakDelay = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)(new meteordevelopment.meteorclient.settings.DoubleSetting.Builder()).name("break-delay")).defaultValue(0.0D).min(0.0D).max(20.0D).sliderMax(20.0D).build());
      this.placeObsidianIfMissing = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("place-obsidian-if-missing")).description("Tự động đặt obsidian nếu block nhìn vào không phải obsidian hoặc bedrock.")).defaultValue(true)).build());
   }

   public void onActivate() {
      this.placeDelayCounter = 0;
      this.breakDelayCounter = 0;
   }

   @EventHandler
   private void onTick(Pre event) {
      if (this.mc.field_1755 == null) {
         if (this.isKeyActive()) {
            if (!this.mc.field_1724.method_6115()) {
               if (this.mc.field_1724.method_6047().method_31574(class_1802.field_8301)) {
                  this.updateCounters();
                  this.handleInteraction();
               }
            }
         }
      }
   }

   private void updateCounters() {
      if (this.placeDelayCounter > 0) {
         --this.placeDelayCounter;
      }

      if (this.breakDelayCounter > 0) {
         --this.breakDelayCounter;
      }

   }

   private boolean isKeyActive() {
      int key = (Integer)this.activateKey.get();
      return key == -1 || KeyUtils.isKeyPressed(key);
   }

   private void handleInteraction() {
      class_239 target = this.mc.field_1765;
      if (target instanceof class_3965) {
         class_3965 blockHit = (class_3965)target;
         this.handleBlockInteraction(blockHit);
         this.tryBreakCrystalAbove(blockHit.method_17777());
      }

      if (target instanceof class_3966) {
         class_3966 entityHit = (class_3966)target;
         this.handleEntityInteraction(entityHit);
      }

   }

   private void handleBlockInteraction(class_3965 hit) {
      if (this.placeDelayCounter <= 0) {
         class_2338 pos = hit.method_17777();
         boolean isObsidianOrBedrock = BlockUtil.isBlockAtPosition(pos, class_2246.field_10540) || BlockUtil.isBlockAtPosition(pos, class_2246.field_9987);
         if (!isObsidianOrBedrock) {
            if ((Boolean)this.placeObsidianIfMissing.get()) {
               int obsidianSlot = this.findObsidianSlot();
               int crystalSlot = this.findCrystalSlot();
               if (obsidianSlot != -1 && crystalSlot != -1) {
                  this.mc.field_1724.field_3944.method_52787(new class_2868(obsidianSlot));
                  BlockUtil.interactWithBlock(hit, true);
                  this.mc.field_1724.method_6104(class_1268.field_5808);
                  this.placeDelayCounter = ((Double)this.placeDelay.get()).intValue();
                  this.mc.field_1724.field_3944.method_52787(new class_2868(crystalSlot));
               }
            }
         } else if (this.isValidCrystalPlacement(pos)) {
            BlockUtil.interactWithBlock(hit, true);
            this.mc.field_1724.method_6104(class_1268.field_5808);
            this.placeDelayCounter = ((Double)this.placeDelay.get()).intValue();
         }
      }
   }

   private void tryBreakCrystalAbove(class_2338 blockPos) {
      if (this.breakDelayCounter <= 0) {
         class_2338 up = blockPos.method_10084();
         class_238 box = new class_238(up);
         Iterator var4 = this.mc.field_1687.method_8335((class_1297)null, box).iterator();

         class_1297 entity;
         do {
            if (!var4.hasNext()) {
               return;
            }

            entity = (class_1297)var4.next();
         } while(!(entity instanceof class_1511));

         this.mc.field_1761.method_2918(this.mc.field_1724, entity);
         this.mc.field_1724.method_6104(class_1268.field_5808);
         this.breakDelayCounter = ((Double)this.breakDelay.get()).intValue();
      }
   }

   private void handleEntityInteraction(class_3966 hit) {
      if (this.breakDelayCounter <= 0) {
         class_1297 entity = hit.method_17782();
         if (entity instanceof class_1511 || entity instanceof class_1621) {
            this.mc.field_1761.method_2918(this.mc.field_1724, entity);
            this.mc.field_1724.method_6104(class_1268.field_5808);
            this.breakDelayCounter = ((Double)this.breakDelay.get()).intValue();
         }
      }
   }

   private boolean isValidCrystalPlacement(class_2338 pos) {
      class_2338 up = pos.method_10084();
      return !this.mc.field_1687.method_22347(up) ? false : this.mc.field_1687.method_8335((class_1297)null, new class_238((double)up.method_10263(), (double)up.method_10264(), (double)up.method_10260(), (double)(up.method_10263() + 1), (double)(up.method_10264() + 2), (double)(up.method_10260() + 1))).isEmpty();
   }

   private int findObsidianSlot() {
      for(int i = 0; i < 9; ++i) {
         class_1799 stack = this.mc.field_1724.method_31548().method_5438(i);
         if (stack.method_31574(class_1802.field_8281)) {
            return i;
         }
      }

      return -1;
   }

   private int findCrystalSlot() {
      for(int i = 0; i < 9; ++i) {
         class_1799 stack = this.mc.field_1724.method_31548().method_5438(i);
         if (stack.method_31574(class_1802.field_8301)) {
            return i;
         }
      }

      return -1;
   }
}
