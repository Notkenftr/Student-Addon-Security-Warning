package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.ColorSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;

public class CustomCrosshair extends Module {
   private final SettingGroup sgGeneral;
   public final Setting<SettingColor> color;
   public final Setting<Integer> pixelScale;
   public final Set<String> pixels;

   public CustomCrosshair() {
      super(AddonTemplate.Student, "custom-crosshair", "Draw your own crosshair.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.color = this.sgGeneral.add(((Builder)(new Builder()).name("color")).defaultValue(new SettingColor(255, 255, 255)).build());
      this.pixelScale = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)(new meteordevelopment.meteorclient.settings.IntSetting.Builder()).name("scale")).defaultValue(2)).min(1).sliderMax(10).build());
      this.pixels = new HashSet();
   }

   public WWidget getWidget(GuiTheme theme) {
      WButton button = theme.button("Open Editor");
      button.action = () -> {
         this.mc.method_1507(new CustomCrosshair.CrosshairEditorScreen(this));
      };
      return button;
   }

   public void renderMixin(class_332 context) {
      if (!this.pixels.isEmpty() && !this.mc.method_53526().method_53536()) {
         float centerX = (float)this.mc.method_22683().method_4486() / 2.0F;
         float centerY = (float)this.mc.method_22683().method_4502() / 2.0F;
         int scale = (Integer)this.pixelScale.get();
         int packed = ((SettingColor)this.color.get()).getPacked();
         Iterator var6 = this.pixels.iterator();

         while(var6.hasNext()) {
            String pixel = (String)var6.next();
            String[] coords = pixel.split(",");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            int rx = (int)(centerX + (float)(x * scale) - (float)scale / 2.0F);
            int ry = (int)(centerY + (float)(y * scale) - (float)scale / 2.0F);
            context.method_25294(rx, ry, rx + scale, ry + scale, packed);
         }

      }
   }

   public class_2487 toTag() {
      class_2487 tag = super.toTag();
      class_2499 list = new class_2499();
      Iterator var3 = this.pixels.iterator();

      while(var3.hasNext()) {
         String pixel = (String)var3.next();
         list.add(class_2519.method_23256(pixel));
      }

      tag.method_10566("pixels", list);
      return tag;
   }

   public Module fromTag(class_2487 tag) {
      super.fromTag(tag);
      this.pixels.clear();
      if (tag.method_10545("pixels")) {
         tag.method_10554("pixels").ifPresent((list) -> {
            for(int i = 0; i < list.size(); ++i) {
               Optional var10000 = list.method_10608(i);
               Set var10001 = this.pixels;
               Objects.requireNonNull(var10001);
               var10000.ifPresent(var10001::add);
            }

         });
      }

      return this;
   }

   private static class CrosshairEditorScreen extends class_437 {
      private final CustomCrosshair module;
      private static final int HALF = 20;
      private static final int CELL = 15;

      public CrosshairEditorScreen(CustomCrosshair module) {
         super(class_2561.method_43470("Crosshair Editor"));
         this.module = module;
      }

      public void method_25394(class_332 ctx, int mouseX, int mouseY, float delta) {
         super.method_25394(ctx, mouseX, mouseY, delta);
         int cx = this.field_22789 / 2;
         int cy = this.field_22790 / 2;
         ctx.method_25294(0, 0, this.field_22789, this.field_22790, -1879048192);

         for(int x = -20; x <= 20; ++x) {
            for(int y = -20; y <= 20; ++y) {
               int xPos = cx + x * 15 - 7;
               int yPos = cy + y * 15 - 7;
               int gridCol = x == 0 && y == 0 ? -2130754492 : 1090519039;
               ctx.method_25294(xPos, yPos, xPos + 15 - 1, yPos + 15 - 1, gridCol);
               if (this.module.pixels.contains(x + "," + y)) {
                  ctx.method_25294(xPos, yPos, xPos + 15 - 1, yPos + 15 - 1, ((SettingColor)this.module.color.get()).getPacked());
               }
            }
         }

         ctx.method_25300(this.field_22793, "LMB: Draw  |  RMB: Erase  |  C: Clear", this.field_22789 / 2, 10, -1);
      }

      public boolean method_25402(double mouseX, double mouseY, int button) {
         this.handleInput(mouseX, mouseY, button);
         return super.method_25402(mouseX, mouseY, button);
      }

      public boolean method_25403(double mouseX, double mouseY, int button, double dx, double dy) {
         this.handleInput(mouseX, mouseY, button);
         return super.method_25403(mouseX, mouseY, button, dx, dy);
      }

      private void handleInput(double mouseX, double mouseY, int button) {
         int cx = this.field_22789 / 2;
         int cy = this.field_22790 / 2;
         int gridX = (int)Math.floor((mouseX - (double)cx + 7.5D) / 15.0D);
         int gridY = (int)Math.floor((mouseY - (double)cy + 7.5D) / 15.0D);
         if (Math.abs(gridX) <= 20 && Math.abs(gridY) <= 20) {
            String key = gridX + "," + gridY;
            if (button == 0) {
               this.module.pixels.add(key);
            } else if (button == 1) {
               this.module.pixels.remove(key);
            }

         }
      }

      public boolean method_25404(int keyCode, int scanCode, int modifiers) {
         if (keyCode == 67) {
            this.module.pixels.clear();
            return true;
         } else {
            return super.method_25404(keyCode, scanCode, modifiers);
         }
      }

      public boolean method_25421() {
         return false;
      }
   }
}
