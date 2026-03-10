package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.IntSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_1707;
import net.minecraft.class_1713;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class SpawnerProtect extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Integer> spawnerRange;
   private final Setting<Boolean> autoDisconnect;
   private final Setting<List<String>> whitelist;
   private final Setting<List<class_1792>> storeItems;
   private SpawnerProtect.State state;
   private final List<class_2338> spawners;
   private int index;
   private int emptyConfirmTicks;
   private static final int CONFIRM_TICKS_REQUIRED = 5;
   private long lootTimer;
   private class_2338 enderChestPos;

   public SpawnerProtect() {
      super(AddonTemplate.Student_esp, "spawner-protect", "Break → stand → loot → store when unknown player detected.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.spawnerRange = this.sgGeneral.add(((Builder)((Builder)(new Builder()).name("spawner-range")).defaultValue(16)).range(1, 50).sliderRange(1, 50).build());
      this.autoDisconnect = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("auto-disconnect")).defaultValue(true)).build());
      this.whitelist = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.StringListSetting.Builder)((meteordevelopment.meteorclient.settings.StringListSetting.Builder)(new meteordevelopment.meteorclient.settings.StringListSetting.Builder()).name("whitelist")).description("Players allowed nearby.")).build());
      this.storeItems = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.ItemListSetting.Builder)((meteordevelopment.meteorclient.settings.ItemListSetting.Builder)(new meteordevelopment.meteorclient.settings.ItemListSetting.Builder()).name("store-items")).description("Only these items will be moved into Ender Chest.")).build());
      this.state = SpawnerProtect.State.IDLE;
      this.spawners = new ArrayList();
      this.index = 0;
      this.emptyConfirmTicks = 0;
      this.lootTimer = 0L;
      this.enderChestPos = null;
   }

   public void onActivate() {
      this.state = SpawnerProtect.State.IDLE;
      this.spawners.clear();
      this.index = 0;
      this.emptyConfirmTicks = 0;
      this.enderChestPos = null;
   }

   public void onDeactivate() {
      this.stopAll();
   }

   @EventHandler
   private void onTick(Pre event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (this.state == SpawnerProtect.State.IDLE) {
            if (!this.hasUnknownPlayer()) {
               return;
            }

            if (this.mc.field_1755 != null) {
               this.mc.field_1724.method_3137();
            }

            this.state = SpawnerProtect.State.SCAN;
         }

         switch(this.state.ordinal()) {
         case 1:
            this.scan();
            break;
         case 2:
            this.mine();
            break;
         case 3:
            this.loot();
            break;
         case 4:
            this.store();
         }

      }
   }

   private boolean hasUnknownPlayer() {
      Iterator var1 = this.mc.field_1687.method_18456().iterator();

      while(var1.hasNext()) {
         class_1657 player = (class_1657)var1.next();
         if (player != this.mc.field_1724) {
            String name = player.method_7334().getName();
            if (!((List)this.whitelist.get()).contains(name)) {
               return true;
            }
         }
      }

      return false;
   }

   private void scan() {
      this.spawners.clear();
      this.index = 0;
      this.enderChestPos = null;
      class_2338 center = this.mc.field_1724.method_24515();
      int r = (Integer)this.spawnerRange.get();
      Iterator var3 = class_2338.method_10097(center.method_10069(-r, -r, -r), center.method_10069(r, r, r)).iterator();

      while(var3.hasNext()) {
         class_2338 pos = (class_2338)var3.next();
         if (this.mc.field_1687.method_2935().method_12123(pos.method_10263() >> 4, pos.method_10260() >> 4)) {
            if (this.mc.field_1687.method_8320(pos).method_26204() == class_2246.field_10260) {
               this.spawners.add(pos.method_10062());
            }

            if (this.mc.field_1687.method_8320(pos).method_26204() == class_2246.field_10443) {
               this.enderChestPos = pos.method_10062();
            }
         }
      }

      this.spawners.sort(Comparator.comparingDouble((posx) -> {
         return posx.method_40081(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318(), this.mc.field_1724.method_23321());
      }));
      if (!this.spawners.isEmpty()) {
         this.emptyConfirmTicks = 0;
         this.state = SpawnerProtect.State.MINING;
      }

   }

   private void mine() {
      if (this.index >= this.spawners.size()) {
         this.scan();
         if (this.spawners.isEmpty()) {
            ++this.emptyConfirmTicks;
            if (this.emptyConfirmTicks >= 5) {
               this.stopMove();
               this.mc.field_1690.field_1832.method_23481(false);
               this.lootTimer = System.currentTimeMillis();
               this.state = SpawnerProtect.State.LOOT;
            }
         } else {
            this.emptyConfirmTicks = 0;
            this.index = 0;
         }
      } else {
         class_2338 target = (class_2338)this.spawners.get(this.index);
         if (!this.mc.field_1687.method_2935().method_12123(target.method_10263() >> 4, target.method_10260() >> 4)) {
            ++this.index;
         } else if (this.mc.field_1724.method_5707(class_243.method_24953(target)) > 20.0D) {
            this.moveTo(target);
         } else {
            this.mc.field_1690.field_1832.method_23481(true);
            this.switchPickaxe();
            this.lookAt(target);
            class_3965 hit = this.mc.field_1687.method_17742(new class_3959(this.mc.field_1724.method_33571(), class_243.method_24953(target), class_3960.field_17559, class_242.field_1348, this.mc.field_1724));
            if (hit != null && hit.method_17783() == class_240.field_1332 && hit.method_17777().equals(target)) {
               this.mc.field_1761.method_2902(target, hit.method_17780());
               this.mc.field_1724.method_6104(class_1268.field_5808);
            }

            if (this.mc.field_1687.method_8320(target).method_26215()) {
               ++this.index;
               this.stopMove();
            }

         }
      }
   }

   private void loot() {
      class_1542 nearest = null;
      double closest = 999.0D;
      Iterator var4 = this.mc.field_1687.method_18112().iterator();

      while(var4.hasNext()) {
         class_1297 e = (class_1297)var4.next();
         if (e instanceof class_1542) {
            class_1542 item = (class_1542)e;
            double d = (double)item.method_5739(this.mc.field_1724);
            if (d < 6.0D && d < closest) {
               closest = d;
               nearest = item;
            }
         }
      }

      if (nearest != null) {
         this.lookAt(nearest.method_24515());
         this.mc.field_1690.field_1894.method_23481(true);
         this.lootTimer = System.currentTimeMillis();
      } else {
         this.stopMove();
         if (System.currentTimeMillis() - this.lootTimer > 500L) {
            this.state = SpawnerProtect.State.STORE;
         }

      }
   }

   private void store() {
      if (this.enderChestPos == null) {
         this.finish();
      } else if (this.mc.field_1724.method_5707(class_243.method_24953(this.enderChestPos)) > 16.0D) {
         this.moveTo(this.enderChestPos);
      } else {
         this.lookAt(this.enderChestPos);
         this.stopMove();
         if (!(this.mc.field_1724.field_7512 instanceof class_1707)) {
            this.mc.field_1761.method_2896(this.mc.field_1724, class_1268.field_5808, new class_3965(class_243.method_24953(this.enderChestPos), class_2350.field_11036, this.enderChestPos, false));
         } else {
            class_1707 handler = (class_1707)this.mc.field_1724.field_7512;
            int containerSlots = handler.method_17388() * 9;

            for(int i = containerSlots; i < handler.field_7761.size(); ++i) {
               if (handler.method_7611(i).method_7681()) {
                  class_1799 stack = handler.method_7611(i).method_7677();
                  if (((List)this.storeItems.get()).contains(stack.method_7909())) {
                     this.mc.field_1761.method_2906(handler.field_7763, i, 0, class_1713.field_7794, this.mc.field_1724);
                     return;
                  }
               }
            }

            this.mc.field_1724.method_3137();
            this.finish();
         }
      }
   }

   private void finish() {
      if ((Boolean)this.autoDisconnect.get()) {
         this.mc.field_1724.field_3944.method_48296().method_10747(class_2561.method_43470("SpawnerProtect"));
      }

      this.toggle();
   }

   private void switchPickaxe() {
      for(int i = 0; i < 9; ++i) {
         class_1799 stack = this.mc.field_1724.method_31548().method_5438(i);
         if (!stack.method_7960()) {
            if (stack.method_7951(this.mc.field_1687.method_8320(this.mc.field_1724.method_24515()))) {
               this.mc.field_1724.method_31548().method_61496(i);
               break;
            }

            String name = stack.method_7964().getString().toLowerCase();
            if (name.contains("pickaxe")) {
               this.mc.field_1724.method_31548().method_61496(i);
               break;
            }
         }
      }

   }

   private void moveTo(class_2338 pos) {
      this.lookAt(pos);
      this.mc.field_1690.field_1894.method_23481(true);
   }

   private void stopMove() {
      this.mc.field_1690.field_1894.method_23481(false);
   }

   private void stopAll() {
      this.stopMove();
      this.mc.field_1690.field_1832.method_23481(false);
      if (this.mc.field_1761 != null) {
         this.mc.field_1761.method_2925();
      }

   }

   private void lookAt(class_2338 pos) {
      class_243 eyes = this.mc.field_1724.method_33571();
      class_243 target = class_243.method_24953(pos);
      class_243 diff = target.method_1020(eyes);
      double yaw = Math.toDegrees(Math.atan2(-diff.field_1352, diff.field_1350));
      double pitch = Math.toDegrees(-Math.asin(diff.field_1351 / diff.method_1033()));
      this.mc.field_1724.method_36456((float)yaw);
      this.mc.field_1724.method_36457((float)pitch);
   }

   private static enum State {
      IDLE,
      SCAN,
      MINING,
      LOOT,
      STORE;

      // $FF: synthetic method
      private static SpawnerProtect.State[] $values() {
         return new SpawnerProtect.State[]{IDLE, SCAN, MINING, LOOT, STORE};
      }
   }
}
