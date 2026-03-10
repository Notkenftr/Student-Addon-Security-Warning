package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2561;
import net.minecraft.class_5250;
import net.minecraft.class_640;

public class FakeNotifier extends Module {
   private final SettingGroup sg;
   private final Setting<String> text;
   private final Setting<Boolean> useOnlinePlayers;
   private final Setting<String> customNames;
   private final Setting<Boolean> loop;
   private final Setting<Integer> repeatTimes;
   private final Setting<Double> delaySeconds;
   private final Random random;
   private int sent;
   private double tickCounter;

   public FakeNotifier() {
      super(AddonTemplate.Student, "Fake Notifier", "Custom colored fake notifier.");
      this.sg = this.settings.getDefaultGroup();
      this.text = this.sg.add(((Builder)((Builder)(new Builder()).name("TEXT")).defaultValue("Student addon on top")).build());
      this.useOnlinePlayers = this.sg.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("use-online-players")).defaultValue(true)).build());
      this.customNames = this.sg.add(((Builder)((Builder)((Builder)(new Builder()).name("custom-names (,)")).defaultValue("Player1,Player2")).visible(() -> {
         return !(Boolean)this.useOnlinePlayers.get();
      })).build());
      this.loop = this.sg.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("loop")).defaultValue(false)).build());
      SettingGroup var10001 = this.sg;
      meteordevelopment.meteorclient.settings.IntSetting.Builder var10002 = ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)(new meteordevelopment.meteorclient.settings.IntSetting.Builder()).name("repeat-times (-1 = infinite)")).defaultValue(1)).min(-1).sliderMax(50);
      Setting var10003 = this.loop;
      Objects.requireNonNull(var10003);
      this.repeatTimes = var10001.add(((meteordevelopment.meteorclient.settings.IntSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sg;
      meteordevelopment.meteorclient.settings.DoubleSetting.Builder var1 = ((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)(new meteordevelopment.meteorclient.settings.DoubleSetting.Builder()).name("delay-seconds")).defaultValue(1.0D).min(0.1D).sliderMin(0.1D).sliderMax(10.0D);
      var10003 = this.loop;
      Objects.requireNonNull(var10003);
      this.delaySeconds = var10001.add(((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)var1.visible(var10003::get)).build());
      this.random = new Random();
      this.sent = 0;
      this.tickCounter = 0.0D;
   }

   public void onActivate() {
      this.sent = 0;
      this.tickCounter = 0.0D;
      if (!(Boolean)this.loop.get()) {
         this.send();
         this.toggle();
      }

   }

   @EventHandler
   private void onTick(Pre event) {
      if ((Boolean)this.loop.get()) {
         if (this.mc.field_1724 != null) {
            ++this.tickCounter;
            if (this.tickCounter >= (Double)this.delaySeconds.get() * 20.0D) {
               this.tickCounter = 0.0D;
               if ((Integer)this.repeatTimes.get() != -1) {
                  if (this.sent >= (Integer)this.repeatTimes.get()) {
                     this.toggle();
                     return;
                  }

                  ++this.sent;
               }

               this.send();
            }

         }
      }
   }

   private void send() {
      String player = this.getRandomName();
      String raw = ((String)this.text.get()).replace("{player}", player);
      this.mc.field_1724.method_7353(this.parseColors(raw), false);
   }

   private String getRandomName() {
      if ((Boolean)this.useOnlinePlayers.get() && this.mc.method_1562() != null) {
         List<class_640> list = new ArrayList(this.mc.method_1562().method_2880());
         if (!list.isEmpty()) {
            return ((class_640)list.get(this.random.nextInt(list.size()))).method_2966().getName();
         }
      }

      String[] names = ((String)this.customNames.get()).split(",");
      return names[this.random.nextInt(names.length)].trim();
   }

   private class_5250 parseColors(String input) {
      class_5250 result = class_2561.method_43473();
      int currentColor = 16777215;
      StringBuilder buffer = new StringBuilder();

      for(int i = 0; i < input.length(); ++i) {
         char c = input.charAt(i);
         if (c == '&' && i + 1 < input.length()) {
            if (buffer.length() > 0) {
               result.method_10852(class_2561.method_43470(buffer.toString()).method_27694((style) -> {
                  return style.method_36139(currentColor);
               }));
               buffer.setLength(0);
            }

            currentColor = this.getColor(input.charAt(i + 1));
            ++i;
         } else {
            buffer.append(c);
         }
      }

      if (buffer.length() > 0) {
         result.method_10852(class_2561.method_43470(buffer.toString()).method_27694((style) -> {
            return style.method_36139(currentColor);
         }));
      }

      return result;
   }

   private int getColor(char code) {
      int var10000;
      switch(Character.toLowerCase(code)) {
      case '0':
         var10000 = 0;
         break;
      case '1':
         var10000 = 170;
         break;
      case '2':
         var10000 = 43520;
         break;
      case '3':
         var10000 = 43690;
         break;
      case '4':
         var10000 = 11141120;
         break;
      case '5':
         var10000 = 11141290;
         break;
      case '6':
         var10000 = 16755200;
         break;
      case '7':
         var10000 = 11184810;
         break;
      case '8':
         var10000 = 5592405;
         break;
      case '9':
         var10000 = 5592575;
         break;
      case ':':
      case ';':
      case '<':
      case '=':
      case '>':
      case '?':
      case '@':
      case 'A':
      case 'B':
      case 'C':
      case 'D':
      case 'E':
      case 'F':
      case 'G':
      case 'H':
      case 'I':
      case 'J':
      case 'K':
      case 'L':
      case 'M':
      case 'N':
      case 'O':
      case 'P':
      case 'Q':
      case 'R':
      case 'S':
      case 'T':
      case 'U':
      case 'V':
      case 'W':
      case 'X':
      case 'Y':
      case 'Z':
      case '[':
      case '\\':
      case ']':
      case '^':
      case '_':
      case '`':
      default:
         var10000 = 16777215;
         break;
      case 'a':
         var10000 = 5635925;
         break;
      case 'b':
         var10000 = 5636095;
         break;
      case 'c':
         var10000 = 16733525;
         break;
      case 'd':
         var10000 = 16733695;
         break;
      case 'e':
         var10000 = 16777045;
         break;
      case 'f':
         var10000 = 16777215;
      }

      return var10000;
   }
}
