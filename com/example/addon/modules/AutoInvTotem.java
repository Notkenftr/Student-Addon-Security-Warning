package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Receive;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.IntSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2596;
import net.minecraft.class_2663;
import net.minecraft.class_437;
import net.minecraft.class_490;

public class AutoInvTotem extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Integer> delay;
   private final Setting<Boolean> moveFromHotbar;
   private final Setting<Boolean> openInv;
   private final Setting<Integer> invOpenDelay;
   private final Setting<Integer> invCloseDelay;
   private boolean needsTotem;
   private int delayTicks;
   private boolean hadTotemInOffhand;
   private boolean shouldOpenInv;
   private int invOpenTicks;
   private int invCloseTicks;
   private boolean invAutoOpened;

   public AutoInvTotem() {
      super(AddonTemplate.Student_pvp, "Auto-Inv-Totem", "Auto fill totem to offhand when open inventory.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.delay = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("delay")).description("Delay in ticks before moving totem")).defaultValue(3)).min(1).max(20).sliderMin(1).sliderMax(20).build());
      this.moveFromHotbar = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("move-from-hotbar")).description("Also move totems from hotbar slots")).defaultValue(true)).build());
      this.openInv = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("open-inv")).description("Automatically open inventory when totem pops")).defaultValue(false)).build());
      this.invOpenDelay = this.sgGeneral.add(((Builder)((Builder)((Builder)((Builder)(new Builder()).name("inv-open-delay")).description("Ticks to wait before opening inventory after totem pop")).defaultValue(2)).min(1).max(10).sliderMin(1).sliderMax(10).visible(() -> {
         return (Boolean)this.openInv.get();
      })).build());
      this.invCloseDelay = this.sgGeneral.add(((Builder)((Builder)((Builder)((Builder)(new Builder()).name("inv-close-delay")).description("Ticks to wait before closing inventory after opening")).defaultValue(8)).min(5).max(20).sliderMin(5).sliderMax(20).visible(() -> {
         return (Boolean)this.openInv.get();
      })).build());
      this.needsTotem = false;
      this.delayTicks = 0;
      this.hadTotemInOffhand = false;
      this.shouldOpenInv = false;
      this.invOpenTicks = 0;
      this.invCloseTicks = 0;
      this.invAutoOpened = false;
   }

   public void onActivate() {
      this.resetState();
   }

   public void onDeactivate() {
      this.resetInvAutoState();
   }

   private void resetState() {
      if (this.mc.field_1724 != null) {
         this.hadTotemInOffhand = this.hasTotemInOffhand();
         this.needsTotem = false;
         this.delayTicks = 0;
         this.resetInvAutoState();
      }

   }

   private void resetInvAutoState() {
      this.shouldOpenInv = false;
      this.invOpenTicks = 0;
      this.invCloseTicks = 0;
      this.invAutoOpened = false;
   }

   @EventHandler
   private void onGameJoined(GameJoinedEvent event) {
      this.resetState();
   }

   @EventHandler
   private void onPacketReceive(Receive event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2663) {
         class_2663 packet = (class_2663)var3;
         if (packet.method_11470() == 35 && this.mc.field_1724 != null && packet.method_11469(this.mc.field_1687) == this.mc.field_1724 && (Boolean)this.openInv.get() && this.mc.field_1755 == null) {
            this.shouldOpenInv = true;
            this.invOpenTicks = (Integer)this.invOpenDelay.get();
         }
      }

   }

   @EventHandler
   private void onTick(Pre event) {
      if (this.mc.field_1724 != null) {
         this.handleAutoInventory();
         boolean currentlyHasTotem = this.hasTotemInOffhand();
         if (this.hadTotemInOffhand && !currentlyHasTotem) {
            this.needsTotem = true;
            if (this.mc.field_1755 instanceof class_490) {
               this.delayTicks = (Integer)this.delay.get();
            }
         }

         this.hadTotemInOffhand = currentlyHasTotem;
         if (currentlyHasTotem && this.needsTotem) {
            this.needsTotem = false;
            this.delayTicks = 0;
         }

      }
   }

   private void handleAutoInventory() {
      if (this.shouldOpenInv && this.invOpenTicks > 0) {
         --this.invOpenTicks;
         if (this.invOpenTicks == 0 && this.mc.field_1755 == null) {
            this.mc.method_1507(new class_490(this.mc.field_1724));
            this.invAutoOpened = true;
            this.invCloseTicks = (Integer)this.invCloseDelay.get();
            this.shouldOpenInv = false;
         }
      }

      if (this.invAutoOpened && this.invCloseTicks > 0) {
         --this.invCloseTicks;
         if (this.invCloseTicks == 0 && this.mc.field_1755 instanceof class_490) {
            this.mc.method_1507((class_437)null);
            this.invAutoOpened = false;
         }
      }

      if (this.invAutoOpened && !(this.mc.field_1755 instanceof class_490)) {
         this.invAutoOpened = false;
         this.invCloseTicks = 0;
      }

   }

   @EventHandler
   private void onOpenScreen(OpenScreenEvent event) {
      if (event.screen instanceof class_490 && this.needsTotem && this.mc.field_1724 != null) {
         this.delayTicks = (Integer)this.delay.get();
      }
   }

   @EventHandler
   private void onTickDelayed(Post event) {
      if (this.delayTicks > 0 && this.mc.field_1724 != null) {
         --this.delayTicks;
         if (this.delayTicks == 0) {
            this.moveTotemToOffhand();
         }

      }
   }

   private void moveTotemToOffhand() {
      int totemSlot = this.findTotemSlot();
      if (totemSlot != -1) {
         try {
            int containerSlot = totemSlot < 9 ? totemSlot + 36 : totemSlot;
            class_1799 offhandStack = this.mc.field_1724.method_6079();
            if (offhandStack.method_7960()) {
               this.mc.field_1761.method_2906(0, containerSlot, 40, class_1713.field_7791, this.mc.field_1724);
            } else {
               this.mc.field_1761.method_2906(0, containerSlot, 0, class_1713.field_7790, this.mc.field_1724);
               this.mc.field_1761.method_2906(0, 45, 0, class_1713.field_7790, this.mc.field_1724);
               this.mc.field_1761.method_2906(0, containerSlot, 0, class_1713.field_7790, this.mc.field_1724);
            }

            this.needsTotem = false;
         } catch (Exception var4) {
         }

      }
   }

   private int findTotemSlot() {
      int i;
      for(i = 9; i < 36; ++i) {
         if (this.mc.field_1724.method_31548().method_5438(i).method_7909() == class_1802.field_8288) {
            return i;
         }
      }

      if ((Boolean)this.moveFromHotbar.get()) {
         for(i = 0; i < 9; ++i) {
            if (this.mc.field_1724.method_31548().method_5438(i).method_7909() == class_1802.field_8288) {
               return i;
            }
         }
      }

      return -1;
   }

   private boolean hasTotemInOffhand() {
      if (this.mc.field_1724 == null) {
         return false;
      } else {
         class_1799 offhandStack = this.mc.field_1724.method_6079();
         return !offhandStack.method_7960() && offhandStack.method_7909() == class_1802.field_8288;
      }
   }
}
