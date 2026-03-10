package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.IntSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1703;
import net.minecraft.class_1707;
import net.minecraft.class_1713;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2846;
import net.minecraft.class_2846.class_2847;

public class ShopBuyer extends Module {
   private final SettingGroup sg;
   private final Setting<Integer> delay;
   private final Setting<Boolean> infinite;
   private final Setting<Integer> buyAmount;
   private final Setting<ShopBuyer.CategoryType> category;
   private final Setting<ShopBuyer.EndItems> endItems;
   private final Setting<ShopBuyer.NetherItems> netherItems;
   private final Setting<ShopBuyer.GearItems> gearItems;
   private final Setting<ShopBuyer.FoodItems> foodItems;
   private int delayTick;
   private int stage;
   private int bought;
   private boolean checkedOnce;
   private boolean clicked17;

   public ShopBuyer() {
      super(AddonTemplate.Student, "Shop Buyer", "Auto buy shop.");
      this.sg = this.settings.getDefaultGroup();
      this.delay = this.sg.add(((Builder)((Builder)(new Builder()).name("delay")).defaultValue(10)).min(1).max(100).build());
      this.infinite = this.sg.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("infinite")).defaultValue(false)).build());
      this.buyAmount = this.sg.add(((Builder)((Builder)((Builder)(new Builder()).name("buy-amount")).defaultValue(5)).min(1).sliderMax(100).visible(() -> {
         return !(Boolean)this.infinite.get();
      })).build());
      this.category = this.sg.add(((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)(new meteordevelopment.meteorclient.settings.EnumSetting.Builder()).name("category")).defaultValue(ShopBuyer.CategoryType.END)).build());
      this.endItems = this.sg.add(((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)(new meteordevelopment.meteorclient.settings.EnumSetting.Builder()).name("end-items")).visible(() -> {
         return this.category.get() == ShopBuyer.CategoryType.END;
      })).defaultValue(ShopBuyer.EndItems.ENDER_CHEST)).build());
      this.netherItems = this.sg.add(((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)(new meteordevelopment.meteorclient.settings.EnumSetting.Builder()).name("nether-items")).visible(() -> {
         return this.category.get() == ShopBuyer.CategoryType.NETHER;
      })).defaultValue(ShopBuyer.NetherItems.BLAZE_ROD)).build());
      this.gearItems = this.sg.add(((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)(new meteordevelopment.meteorclient.settings.EnumSetting.Builder()).name("gear-items")).visible(() -> {
         return this.category.get() == ShopBuyer.CategoryType.GEAR;
      })).defaultValue(ShopBuyer.GearItems.OBSIDIAN)).build());
      this.foodItems = this.sg.add(((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)(new meteordevelopment.meteorclient.settings.EnumSetting.Builder()).name("food-items")).visible(() -> {
         return this.category.get() == ShopBuyer.CategoryType.END_FOOD;
      })).defaultValue(ShopBuyer.FoodItems.POTATO)).build());
      this.delayTick = 0;
      this.stage = 0;
      this.bought = 0;
      this.checkedOnce = false;
      this.clicked17 = false;
   }

   public void onActivate() {
      this.delayTick = 0;
      this.stage = 0;
      this.bought = 0;
      this.checkedOnce = false;
      this.clicked17 = false;
   }

   @EventHandler
   private void onTick(Pre event) {
      if (this.mc.field_1724 != null) {
         this.ensureSafeHotbar();
         if (this.isInventoryFull()) {
            ChatUtils.info("Inventory full! Module stopped.", new Object[0]);
            this.toggle();
         } else if (!(Boolean)this.infinite.get() && this.bought >= (Integer)this.buyAmount.get()) {
            this.toggle();
         } else if (this.delayTick > 0) {
            --this.delayTick;
         } else {
            class_1703 handler = this.mc.field_1724.field_7512;
            if (this.stage != 0 && !(handler instanceof class_1707)) {
               ChatUtils.info("Shop GUI closed. Module stopped.", new Object[0]);
               this.toggle();
            } else if (!(handler instanceof class_1707)) {
               ChatUtils.sendPlayerMsg("/shop");
               this.delayTick = (Integer)this.delay.get();
            } else {
               class_1707 container = (class_1707)handler;
               if (this.stage == 0) {
                  this.clickCategory(container);
                  this.stage = 1;
                  this.delayTick = (Integer)this.delay.get();
               } else if (this.stage == 1) {
                  this.clickItem(container);
                  this.stage = 2;
                  this.delayTick = (Integer)this.delay.get();
               } else {
                  if (this.stage == 2) {
                     class_1792 expected = this.getSelectedItem();
                     if (!this.checkedOnce) {
                        this.checkedOnce = true;
                        if (!container.method_7611(13).method_7677().method_31574(expected)) {
                           ChatUtils.info("Wrong item detected.", new Object[0]);
                           this.toggle();
                           return;
                        }

                        this.delayTick = (Integer)this.delay.get();
                        return;
                     }

                     if (!this.clicked17) {
                        this.mc.field_1761.method_2906(container.field_7763, 17, 0, class_1713.field_7790, this.mc.field_1724);
                        this.clicked17 = true;
                        this.delayTick = (Integer)this.delay.get();
                        return;
                     }

                     this.stage = 3;
                  }

                  if (this.stage == 3) {
                     this.mc.field_1761.method_2906(container.field_7763, 23, 0, class_1713.field_7790, this.mc.field_1724);
                     this.mc.field_1724.field_3944.method_52787(new class_2846(class_2847.field_12970, class_2338.field_10980, class_2350.field_11033));
                     ++this.bought;
                     this.stage = 0;
                     this.checkedOnce = false;
                     this.clicked17 = false;
                     this.delayTick = (Integer)this.delay.get();
                  }

               }
            }
         }
      }
   }

   private class_1792 getSelectedItem() {
      class_1792 var10000;
      switch(((ShopBuyer.CategoryType)this.category.get()).ordinal()) {
      case 0:
         var10000 = this.mapEnd();
         break;
      case 1:
         var10000 = this.mapNether();
         break;
      case 2:
         var10000 = this.mapGear();
         break;
      case 3:
         var10000 = this.mapFood();
         break;
      default:
         throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private void clickCategory(class_1707 handler) {
      class_1792 var10000;
      switch(((ShopBuyer.CategoryType)this.category.get()).ordinal()) {
      case 0:
         var10000 = class_1802.field_20399;
         break;
      case 1:
         var10000 = class_1802.field_8328;
         break;
      case 2:
         var10000 = class_1802.field_8288;
         break;
      case 3:
         var10000 = class_1802.field_8176;
         break;
      default:
         throw new MatchException((String)null, (Throwable)null);
      }

      class_1792 icon = var10000;

      for(int i = 0; i < handler.method_17388() * 9; ++i) {
         if (handler.method_7611(i).method_7677().method_31574(icon)) {
            this.mc.field_1761.method_2906(handler.field_7763, i, 0, class_1713.field_7790, this.mc.field_1724);
            return;
         }
      }

   }

   private void clickItem(class_1707 handler) {
      class_1792 target = this.getSelectedItem();

      for(int i = 0; i < handler.method_17388() * 9; ++i) {
         if (handler.method_7611(i).method_7677().method_31574(target)) {
            this.mc.field_1761.method_2906(handler.field_7763, i, 0, class_1713.field_7790, this.mc.field_1724);
            return;
         }
      }

   }

   private class_1792 mapEnd() {
      class_1792 var10000;
      switch(((ShopBuyer.EndItems)this.endItems.get()).ordinal()) {
      case 0:
         var10000 = class_1802.field_8466;
         break;
      case 1:
         var10000 = class_1802.field_8634;
         break;
      case 2:
         var10000 = class_1802.field_20399;
         break;
      case 3:
         var10000 = class_1802.field_8613;
         break;
      case 4:
         var10000 = class_1802.field_8056;
         break;
      case 5:
         var10000 = class_1802.field_8233;
         break;
      case 6:
         var10000 = class_1802.field_8882;
         break;
      case 7:
         var10000 = class_1802.field_8815;
         break;
      case 8:
         var10000 = class_1802.field_8545;
         break;
      default:
         throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private class_1792 mapNether() {
      class_1792 var10000;
      switch(((ShopBuyer.NetherItems)this.netherItems.get()).ordinal()) {
      case 0:
         var10000 = class_1802.field_8894;
         break;
      case 1:
         var10000 = class_1802.field_8790;
         break;
      case 2:
         var10000 = class_1802.field_8601;
         break;
      case 3:
         var10000 = class_1802.field_8135;
         break;
      case 4:
         var10000 = class_1802.field_8070;
         break;
      case 5:
         var10000 = class_1802.field_8155;
         break;
      case 6:
         var10000 = class_1802.field_8067;
         break;
      case 7:
         var10000 = class_1802.field_8354;
         break;
      case 8:
         var10000 = class_1802.field_22421;
         break;
      default:
         throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private class_1792 mapGear() {
      class_1792 var10000;
      switch(((ShopBuyer.GearItems)this.gearItems.get()).ordinal()) {
      case 0:
         var10000 = class_1802.field_8281;
         break;
      case 1:
         var10000 = class_1802.field_8301;
         break;
      case 2:
         var10000 = class_1802.field_23141;
         break;
      case 3:
         var10000 = class_1802.field_8801;
         break;
      case 4:
         var10000 = class_1802.field_8288;
         break;
      case 5:
         var10000 = class_1802.field_8634;
         break;
      case 6:
         var10000 = class_1802.field_8463;
         break;
      case 7:
         var10000 = class_1802.field_8287;
         break;
      case 8:
         var10000 = class_1802.field_8087;
         break;
      default:
         throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private class_1792 mapFood() {
      class_1792 var10000;
      switch(((ShopBuyer.FoodItems)this.foodItems.get()).ordinal()) {
      case 0:
         var10000 = class_1802.field_8567;
         break;
      case 1:
         var10000 = class_1802.field_16998;
         break;
      case 2:
         var10000 = class_1802.field_8497;
         break;
      case 3:
         var10000 = class_1802.field_8179;
         break;
      case 4:
         var10000 = class_1802.field_8279;
         break;
      case 5:
         var10000 = class_1802.field_8544;
         break;
      case 6:
         var10000 = class_1802.field_8176;
         break;
      case 7:
         var10000 = class_1802.field_8071;
         break;
      case 8:
         var10000 = class_1802.field_8463;
         break;
      default:
         throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private void ensureSafeHotbar() {
      int current = this.mc.field_1724.method_31548().method_67532();
      if (!this.mc.field_1724.method_31548().method_5438(current).method_7960()) {
         for(int i = 0; i < 9; ++i) {
            if (this.mc.field_1724.method_31548().method_5438(i).method_7960()) {
               this.mc.field_1724.method_31548().method_61496(i);
               return;
            }
         }

      }
   }

   private boolean isInventoryFull() {
      for(int i = 0; i < this.mc.field_1724.method_31548().method_5439(); ++i) {
         if (this.mc.field_1724.method_31548().method_5438(i).method_7960()) {
            return false;
         }
      }

      return true;
   }

   public static enum CategoryType {
      END,
      NETHER,
      GEAR,
      END_FOOD;

      // $FF: synthetic method
      private static ShopBuyer.CategoryType[] $values() {
         return new ShopBuyer.CategoryType[]{END, NETHER, GEAR, END_FOOD};
      }
   }

   public static enum EndItems {
      ENDER_CHEST,
      ENDER_PEARL,
      END_STONE,
      DRAGON_BREATH,
      END_ROD,
      CHORUS_FRUIT,
      POPPED_CHORUS_FRUIT,
      SHULKER_SHELL,
      SHULKER_BOX;

      // $FF: synthetic method
      private static ShopBuyer.EndItems[] $values() {
         return new ShopBuyer.EndItems[]{ENDER_CHEST, ENDER_PEARL, END_STONE, DRAGON_BREATH, END_ROD, CHORUS_FRUIT, POPPED_CHORUS_FRUIT, SHULKER_SHELL, SHULKER_BOX};
      }
   }

   public static enum NetherItems {
      BLAZE_ROD,
      NETHER_WART,
      GLOWSTONE_DUST,
      MAGMA_CREAM,
      GHAST_TEAR,
      QUARTZ,
      SOUL_SAND,
      MAGMA_BLOCK,
      CRYING_OBSIDIAN;

      // $FF: synthetic method
      private static ShopBuyer.NetherItems[] $values() {
         return new ShopBuyer.NetherItems[]{BLAZE_ROD, NETHER_WART, GLOWSTONE_DUST, MAGMA_CREAM, GHAST_TEAR, QUARTZ, SOUL_SAND, MAGMA_BLOCK, CRYING_OBSIDIAN};
      }
   }

   public static enum GearItems {
      OBSIDIAN,
      END_CRYSTAL,
      RESPAWN_ANCHOR,
      GLOWSTONE,
      TOTEM_OF_UNDYING,
      ENDER_PEARL,
      GOLDEN_APPLE,
      EXPERIENCE_BOTTLE,
      TIPPED_ARROW;

      // $FF: synthetic method
      private static ShopBuyer.GearItems[] $values() {
         return new ShopBuyer.GearItems[]{OBSIDIAN, END_CRYSTAL, RESPAWN_ANCHOR, GLOWSTONE, TOTEM_OF_UNDYING, ENDER_PEARL, GOLDEN_APPLE, EXPERIENCE_BOTTLE, TIPPED_ARROW};
      }
   }

   public static enum FoodItems {
      POTATO,
      SWEET_BERRIES,
      MELON_SLICE,
      CARROT,
      APPLE,
      COOKED_CHICKEN,
      COOKED_BEEF,
      GOLDEN_CARROT,
      GOLDEN_APPLE;

      // $FF: synthetic method
      private static ShopBuyer.FoodItems[] $values() {
         return new ShopBuyer.FoodItems[]{POTATO, SWEET_BERRIES, MELON_SLICE, CARROT, APPLE, COOKED_CHICKEN, COOKED_BEEF, GOLDEN_CARROT, GOLDEN_APPLE};
      }
   }
}
