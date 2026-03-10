package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import java.util.Iterator;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1707;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_437;
import net.minecraft.class_476;
import net.minecraft.class_9290;
import net.minecraft.class_9334;

public class AutoOrder extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<String> orderName;
   private final Setting<Boolean> clickCauldron;
   private final Setting<Integer> cauldronSlot;
   private final Setting<Integer> priceSlot;
   private final Setting<class_1792> item;
   private final Setting<Boolean> checkPrice;
   private final Setting<Double> minPrice;
   private final Setting<Integer> delay;
   private final Setting<Boolean> loop;
   private final Setting<Integer> loopCount;
   private int stage;
   private int timer;
   private int currentLoop;

   public AutoOrder() {
      super(AddonTemplate.Student, "Auto-Order", "Automatically fulfills item orders.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.orderName = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("order-name")).description("Item name to use in /order command")).defaultValue("")).build());
      this.clickCauldron = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("click-cauldron")).description("Whether to click the cauldron")).defaultValue(true)).build());
      this.cauldronSlot = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)(new meteordevelopment.meteorclient.settings.IntSetting.Builder()).name("cauldron-slot")).description("Slot to click after opening the cauldron (starting from 0)")).defaultValue(0)).min(0).sliderMax(53).build());
      this.priceSlot = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)(new meteordevelopment.meteorclient.settings.IntSetting.Builder()).name("price-slot")).description("Slot containing price info to check")).defaultValue(0)).min(0).sliderMax(53).build());
      this.item = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.ItemSetting.Builder)((meteordevelopment.meteorclient.settings.ItemSetting.Builder)((meteordevelopment.meteorclient.settings.ItemSetting.Builder)(new meteordevelopment.meteorclient.settings.ItemSetting.Builder()).name("item")).description("Item to put into the order")).defaultValue(class_1802.field_8162)).build());
      this.checkPrice = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("check-price")).description("Enable minimum price check")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgGeneral;
      meteordevelopment.meteorclient.settings.DoubleSetting.Builder var10002 = ((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)(new meteordevelopment.meteorclient.settings.DoubleSetting.Builder()).name("min-price")).description("Minimum price per item (supports K/M/B, e.g. 1500 = 1.5K). 0 = accept all.")).defaultValue(500.0D).min(0.0D).sliderMax(100000.0D);
      Setting var10003 = this.checkPrice;
      Objects.requireNonNull(var10003);
      this.minPrice = var10001.add(((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      this.delay = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)(new meteordevelopment.meteorclient.settings.IntSetting.Builder()).name("delay")).description("Delay in ticks")).defaultValue(5)).min(1).sliderMax(40).build());
      this.loop = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("loop")).description("Repeat automatically")).defaultValue(false)).build());
      var10001 = this.sgGeneral;
      meteordevelopment.meteorclient.settings.IntSetting.Builder var1 = ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)(new meteordevelopment.meteorclient.settings.IntSetting.Builder()).name("loop-count")).description("Number of loops (0 = infinite)")).defaultValue(0)).min(0).sliderMax(100);
      var10003 = this.loop;
      Objects.requireNonNull(var10003);
      this.loopCount = var10001.add(((meteordevelopment.meteorclient.settings.IntSetting.Builder)var1.visible(var10003::get)).build());
      this.stage = 0;
      this.timer = 0;
      this.currentLoop = 0;
   }

   public void onActivate() {
      this.stage = -2;
      this.timer = (Integer)this.delay.get() * 2;
      this.currentLoop = 0;
   }

   public void onDeactivate() {
      this.stage = 0;
      this.timer = 0;
      this.currentLoop = 0;
   }

   private void startOrder() {
      if (this.mc.field_1724 != null) {
         this.mc.field_1724.field_3944.method_45730("order " + (String)this.orderName.get());
      }
   }

   private double parsePrice(String text) {
      try {
         text = text.trim();
         Matcher matcher = Pattern.compile("([0-9]+\\.?[0-9]*)([KkMmBb]?)").matcher(text);
         if (!matcher.find()) {
            return 0.0D;
         } else {
            double value = Double.parseDouble(matcher.group(1));
            String suffix = matcher.group(2).toUpperCase();
            byte var7 = -1;
            switch(suffix.hashCode()) {
            case 66:
               if (suffix.equals("B")) {
                  var7 = 2;
               }
               break;
            case 75:
               if (suffix.equals("K")) {
                  var7 = 0;
               }
               break;
            case 77:
               if (suffix.equals("M")) {
                  var7 = 1;
               }
            }

            double var10000;
            switch(var7) {
            case 0:
               var10000 = value * 1000.0D;
               break;
            case 1:
               var10000 = value * 1000000.0D;
               break;
            case 2:
               var10000 = value * 1.0E9D;
               break;
            default:
               var10000 = value;
            }

            return var10000;
         }
      } catch (Exception var8) {
         return 0.0D;
      }
   }

   private double getPriceFromSlot(class_476 screen, int slotIndex) {
      class_1707 handler = (class_1707)screen.method_17577();
      if (slotIndex >= 0 && slotIndex < handler.field_7761.size()) {
         class_1799 stack = ((class_1735)handler.field_7761.get(slotIndex)).method_7677();
         if (stack.method_7960()) {
            return 0.0D;
         } else {
            class_9290 lore = (class_9290)stack.method_58694(class_9334.field_49632);
            if (lore != null) {
               Iterator var6 = lore.comp_2400().iterator();

               while(var6.hasNext()) {
                  class_2561 line = (class_2561)var6.next();
                  String lineStr = line.getString();
                  if (lineStr.contains("$")) {
                     double price = this.parsePrice(lineStr.replace("$", "").trim());
                     if (price > 0.0D) {
                        return price;
                     }
                  }
               }
            }

            String name = stack.method_7964().getString();
            if (name.contains("$")) {
               double price = this.parsePrice(name.replace("$", "").trim());
               if (price > 0.0D) {
                  return price;
               }
            }

            return 0.0D;
         }
      } else {
         return 0.0D;
      }
   }

   private boolean isPriceOk(double price) {
      if (!(Boolean)this.checkPrice.get()) {
         return true;
      } else {
         double min = (Double)this.minPrice.get();
         if (min <= 0.0D) {
            return true;
         } else {
            return price >= min;
         }
      }
   }

   private void finishLoop() {
      ++this.currentLoop;
      if (!(Boolean)this.loop.get()) {
         this.toggle();
      } else if ((Integer)this.loopCount.get() > 0 && this.currentLoop >= (Integer)this.loopCount.get()) {
         this.toggle();
      } else {
         this.stage = -2;
         this.timer = (Integer)this.delay.get() * 8;
      }
   }

   @EventHandler
   private void onTick(Post event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (this.timer > 0) {
            --this.timer;
         } else if (this.stage == -2) {
            this.startOrder();
            this.stage = -1;
            this.timer = (Integer)this.delay.get() * 6;
         } else if (this.stage != -1 && this.stage != 4) {
            if (this.stage == 6) {
               if (this.mc.field_1755 instanceof class_476) {
                  this.stage = 7;
                  this.timer = (Integer)this.delay.get() * 2;
               }

            } else {
               class_437 var3 = this.mc.field_1755;
               if (var3 instanceof class_476) {
                  class_476 screen = (class_476)var3;
                  class_1707 handler = (class_1707)screen.method_17577();
                  int i;
                  if (this.stage == 0) {
                     if (!(Boolean)this.clickCauldron.get()) {
                        this.stage = 1;
                        this.timer = (Integer)this.delay.get();
                        return;
                     }

                     for(i = 0; i < handler.field_7761.size(); ++i) {
                        class_1799 stack = ((class_1735)handler.field_7761.get(i)).method_7677();
                        if (stack.method_7909() == class_1802.field_8638) {
                           this.mc.field_1761.method_2906(handler.field_7763, i, 0, class_1713.field_7790, this.mc.field_1724);
                           this.stage = 1;
                           this.timer = (Integer)this.delay.get();
                           return;
                        }
                     }
                  } else {
                     double price;
                     if (this.stage == 1) {
                        if ((Boolean)this.checkPrice.get()) {
                           price = this.getPriceFromSlot(screen, (Integer)this.priceSlot.get());
                           if (!this.isPriceOk(price)) {
                              this.mc.field_1761.method_2906(handler.field_7763, 49, 0, class_1713.field_7790, this.mc.field_1724);
                              this.stage = 6;
                              this.timer = (Integer)this.delay.get() * 4;
                              return;
                           }
                        }

                        this.mc.field_1761.method_2906(handler.field_7763, (Integer)this.cauldronSlot.get(), 0, class_1713.field_7790, this.mc.field_1724);
                        this.stage = 2;
                        this.timer = (Integer)this.delay.get();
                     } else if (this.stage == 2) {
                        this.mc.field_1761.method_2906(handler.field_7763, 1, 0, class_1713.field_7790, this.mc.field_1724);
                        this.stage = 3;
                        this.timer = (Integer)this.delay.get();
                     } else if (this.stage == 3) {
                        for(i = 0; i < handler.field_7761.size(); ++i) {
                           class_1735 slot = (class_1735)handler.field_7761.get(i);
                           if (slot.method_7677().method_7909() == this.item.get()) {
                              this.mc.field_1761.method_2906(handler.field_7763, i, 0, class_1713.field_7794, this.mc.field_1724);
                           }
                        }

                        this.mc.field_1724.method_7346();
                        this.stage = 4;
                        this.timer = (Integer)this.delay.get() * 3;
                     } else if (this.stage == 5) {
                        boolean found = false;

                        for(int i = 0; i < handler.field_7761.size(); ++i) {
                           class_1735 slot = (class_1735)handler.field_7761.get(i);
                           if (slot.method_7677().method_7909() == class_1802.field_8581) {
                              this.mc.field_1761.method_2906(handler.field_7763, i, 0, class_1713.field_7790, this.mc.field_1724);
                              this.timer = (Integer)this.delay.get();
                              found = true;
                              this.finishLoop();
                              return;
                           }
                        }

                        if (!found) {
                           this.timer = (Integer)this.delay.get();
                        }
                     } else if (this.stage == 7) {
                        price = this.getPriceFromSlot(screen, (Integer)this.priceSlot.get());
                        if (!this.isPriceOk(price)) {
                           this.mc.field_1761.method_2906(handler.field_7763, 49, 0, class_1713.field_7790, this.mc.field_1724);
                           this.stage = 6;
                           this.timer = (Integer)this.delay.get() * 4;
                           return;
                        }

                        this.mc.field_1761.method_2906(handler.field_7763, (Integer)this.cauldronSlot.get(), 0, class_1713.field_7790, this.mc.field_1724);
                        this.stage = 2;
                        this.timer = (Integer)this.delay.get();
                     }
                  }

               }
            }
         } else {
            if (this.mc.field_1755 instanceof class_476) {
               this.stage = this.stage == -1 ? 0 : 5;
               this.timer = (Integer)this.delay.get();
            }

         }
      }
   }
}
