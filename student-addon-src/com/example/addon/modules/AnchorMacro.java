package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import com.example.addon.VersionUtil;
import com.example.addon.utils.glazed.BlockUtil;
import com.example.addon.utils.glazed.KeyUtils;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.DoubleSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_1819;
import net.minecraft.class_2246;
import net.minecraft.class_239;
import net.minecraft.class_2561;
import net.minecraft.class_2868;
import net.minecraft.class_3965;
import net.minecraft.class_9334;
import org.lwjgl.glfw.GLFW;

public class AnchorMacro extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> switchDelay;
   private final Setting<Double> glowstoneDelay;
   private final Setting<Double> explodeDelay;
   private final Setting<Integer> totemSlot;
   private final Setting<Boolean> stopOnKill;
   private final Setting<Boolean> autoSwingHand;
   private int keybindCounter;
   private int glowstoneDelayCounter;
   private int explodeDelayCounter;
   private boolean hasPlacedGlowstone;
   private boolean hasExplodedAnchor;
   private class_3965 lastBlockHitResult;
   private boolean paused;
   private long resumeTime;
   private final Set<class_1657> deadPlayers;

   public AnchorMacro() {
      super(AddonTemplate.Student_pvp, "AnchorMacro", "Automatically charges and explodes respawn anchors.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.switchDelay = this.sgGeneral.add(((Builder)((Builder)(new Builder()).name("switch-delay")).description("Delay in ticks before switching items.")).defaultValue(0.0D).min(0.0D).max(20.0D).sliderMax(20.0D).build());
      this.glowstoneDelay = this.sgGeneral.add(((Builder)((Builder)(new Builder()).name("glowstone-delay")).description("Delay in ticks before placing glowstone.")).defaultValue(0.0D).min(0.0D).max(20.0D).sliderMax(20.0D).build());
      this.explodeDelay = this.sgGeneral.add(((Builder)((Builder)(new Builder()).name("explode-delay")).description("Delay in ticks before exploding the anchor.")).defaultValue(0.0D).min(0.0D).max(20.0D).sliderMax(20.0D).build());
      this.totemSlot = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)(new meteordevelopment.meteorclient.settings.IntSetting.Builder()).name("totem-slot")).description("Hotbar slot to switch to when exploding (1-9).")).defaultValue(1)).min(1).max(9).build());
      this.stopOnKill = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("stop-on-kill")).description("Wait 5s when player die nearby.")).defaultValue(true)).build());
      this.autoSwingHand = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("auto-swing-hand")).defaultValue(true)).build());
      this.hasPlacedGlowstone = false;
      this.hasExplodedAnchor = false;
      this.lastBlockHitResult = null;
      this.paused = false;
      this.resumeTime = 0L;
      this.deadPlayers = new HashSet();
   }

   public void onActivate() {
      this.resetAll();
   }

   public void onDeactivate() {
      this.resetAll();
   }

   private void resetAll() {
      this.keybindCounter = 0;
      this.glowstoneDelayCounter = 0;
      this.explodeDelayCounter = 0;
      this.hasPlacedGlowstone = false;
      this.hasExplodedAnchor = false;
      this.lastBlockHitResult = null;
      this.paused = false;
      this.resumeTime = 0L;
      this.deadPlayers.clear();
   }

   @EventHandler
   private void onTick(Pre event) {
      if (this.mc.field_1755 == null) {
         if (!this.isShieldOrFoodActive()) {
            if (this.paused) {
               if (System.currentTimeMillis() >= this.resumeTime) {
                  this.paused = false;
                  this.mc.field_1724.method_7353(class_2561.method_43470("§7[§bAnchorMacro§7] §aResumed after stop-on-kill"), false);
               }

            } else if ((Boolean)this.stopOnKill.get() && this.checkForDeadPlayers()) {
               this.paused = true;
               this.resumeTime = System.currentTimeMillis() + 5000L;
               this.mc.field_1724.method_7353(class_2561.method_43470("§7[§bAnchorMacro§7] §cPaused due to player death (will resume in 5s)"), false);
            } else {
               if (KeyUtils.isKeyPressed(1)) {
                  this.handleAnchorInteraction();
               } else {
                  this.hasPlacedGlowstone = false;
                  this.hasExplodedAnchor = false;
                  this.lastBlockHitResult = null;
                  this.keybindCounter = 0;
                  this.glowstoneDelayCounter = 0;
                  this.explodeDelayCounter = 0;
               }

            }
         }
      }
   }

   private boolean isShieldOrFoodActive() {
      boolean isFood = this.mc.field_1724.method_6047().method_7909().method_57347().method_57832(class_9334.field_50075) || this.mc.field_1724.method_6079().method_7909().method_57347().method_57832(class_9334.field_50075);
      boolean isShield = this.mc.field_1724.method_6047().method_7909() instanceof class_1819 || this.mc.field_1724.method_6079().method_7909() instanceof class_1819;
      boolean isRightClickPressed = GLFW.glfwGetMouseButton(this.mc.method_22683().method_4490(), 1) == 1;
      return (isFood || isShield) && isRightClickPressed;
   }

   private void handleAnchorInteraction() {
      class_239 var2 = this.mc.field_1765;
      if (var2 instanceof class_3965) {
         class_3965 blockHitResult = (class_3965)var2;
         this.lastBlockHitResult = blockHitResult;
         if (BlockUtil.isBlockAtPosition(blockHitResult.method_17777(), class_2246.field_23152)) {
            this.mc.field_1690.field_1904.method_23481(false);
            if (BlockUtil.isRespawnAnchorUncharged(blockHitResult.method_17777()) && !this.hasPlacedGlowstone) {
               this.placeGlowstone(blockHitResult);
            } else if (BlockUtil.isRespawnAnchorCharged(blockHitResult.method_17777()) && !this.hasExplodedAnchor) {
               this.explodeAnchor(blockHitResult);
            }

         }
      }
   }

   private void placeGlowstone(class_3965 blockHitResult) {
      if (!this.mc.field_1724.method_6047().method_31574(class_1802.field_8801)) {
         if (this.keybindCounter < ((Double)this.switchDelay.get()).intValue()) {
            ++this.keybindCounter;
         } else {
            this.keybindCounter = 0;
            this.swapToItem(class_1802.field_8801);
         }
      } else if (this.glowstoneDelayCounter < ((Double)this.glowstoneDelay.get()).intValue()) {
         ++this.glowstoneDelayCounter;
      } else {
         this.glowstoneDelayCounter = 0;
         BlockUtil.interactWithBlock(blockHitResult, true);
         if ((Boolean)this.autoSwingHand.get()) {
            this.mc.field_1724.method_6104(class_1268.field_5808);
         }

         this.hasPlacedGlowstone = true;
      }
   }

   private void explodeAnchor(class_3965 blockHitResult) {
      int selectedSlot = (Integer)this.totemSlot.get() - 1;
      if (VersionUtil.getSelectedSlot(this.mc.field_1724) != selectedSlot) {
         if (this.keybindCounter < ((Double)this.switchDelay.get()).intValue()) {
            ++this.keybindCounter;
         } else {
            this.keybindCounter = 0;
            this.mc.field_1724.field_3944.method_52787(new class_2868(selectedSlot));
            VersionUtil.setSelectedSlot(this.mc.field_1724, selectedSlot);
         }
      } else if (this.explodeDelayCounter < ((Double)this.explodeDelay.get()).intValue()) {
         ++this.explodeDelayCounter;
      } else {
         this.explodeDelayCounter = 0;
         BlockUtil.interactWithBlock(blockHitResult, true);
         if ((Boolean)this.autoSwingHand.get()) {
            this.mc.field_1724.method_6104(class_1268.field_5808);
         }

         this.hasExplodedAnchor = true;
      }
   }

   private void swapToItem(class_1792 item) {
      FindItemResult result = InvUtils.findInHotbar(new class_1792[]{item});
      if (result.found()) {
         this.mc.field_1724.field_3944.method_52787(new class_2868(result.slot()));
         VersionUtil.setSelectedSlot(this.mc.field_1724, result.slot());
      }

   }

   private boolean checkForDeadPlayers() {
      if (this.mc.field_1687 == null) {
         return false;
      } else {
         Iterator var1 = this.mc.field_1687.method_18456().iterator();

         class_1657 player;
         do {
            do {
               do {
                  if (!var1.hasNext()) {
                     this.deadPlayers.removeIf((p) -> {
                        return !p.method_29504() && p.method_6032() > 0.0F;
                     });
                     return false;
                  }

                  player = (class_1657)var1.next();
               } while(player == this.mc.field_1724);
            } while(!player.method_29504() && !(player.method_6032() <= 0.0F));
         } while(this.deadPlayers.contains(player));

         this.deadPlayers.add(player);
         return true;
      }
   }
}
