package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.world.ChunkOcclusionEvent;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.DoubleSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import org.joml.Vector3d;

public class StudentFreeCam extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> speed;
   public final Vector3d pos;
   public final Vector3d prevPos;
   public float yaw;
   public float pitch;
   public float prevYaw;
   public float prevPitch;
   private boolean forward;
   private boolean backward;
   private boolean left;
   private boolean right;
   private boolean up;
   private boolean down;

   public StudentFreeCam() {
      super(AddonTemplate.Student_esp, "Student-Free-Cam", "Smooth freecam.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.speed = this.sgGeneral.add(((Builder)((Builder)(new Builder()).name("speed")).description("Tốc độ di chuyển camera")).defaultValue(1.0D).min(0.1D).sliderMax(5.0D).build());
      this.pos = new Vector3d();
      this.prevPos = new Vector3d();
   }

   public void onActivate() {
      if (this.mc.field_1724 != null) {
         class_243 eyePos = this.mc.field_1773.method_19418().method_19326();
         this.pos.set(eyePos.field_1352, eyePos.field_1351, eyePos.field_1350);
         this.prevPos.set(this.pos);
         this.yaw = this.mc.field_1724.method_36454();
         this.pitch = this.mc.field_1724.method_36455();
         this.prevYaw = this.yaw;
         this.prevPitch = this.pitch;
         this.unpress();
         if (this.mc.field_1761 != null) {
            this.mc.field_1761.method_2925();
         }

      }
   }

   public void onDeactivate() {
      this.unpress();
   }

   private void unpress() {
      this.forward = this.backward = this.left = this.right = this.up = this.down = false;
   }

   public void changeLookDirection(double dx, double dy) {
      double s = (Double)this.mc.field_1690.method_42495().method_41753() * 0.6D + 0.2D;
      double multiplier = s * s * s * 8.0D;
      this.yaw += (float)(dx * multiplier * 0.15D);
      this.pitch += (float)(dy * multiplier * 0.15D);
      this.pitch = class_3532.method_15363(this.pitch, -90.0F, 90.0F);
   }

   @EventHandler
   private void onTick(Pre event) {
      this.prevPos.set(this.pos);
      this.prevYaw = this.yaw;
      this.prevPitch = this.pitch;
      class_243 fwd = class_243.method_1030(0.0F, this.yaw);
      class_243 side = class_243.method_1030(0.0F, this.yaw + 90.0F);
      double s = (Double)this.speed.get() * (this.mc.field_1690.field_1867.method_1434() ? 2.0D : 1.0D) * 0.2D;
      double x = 0.0D;
      double y = 0.0D;
      double z = 0.0D;
      if (this.forward) {
         x += fwd.field_1352 * s;
         z += fwd.field_1350 * s;
      }

      if (this.backward) {
         x -= fwd.field_1352 * s;
         z -= fwd.field_1350 * s;
      }

      if (this.right) {
         x += side.field_1352 * s;
         z += side.field_1350 * s;
      }

      if (this.left) {
         x -= side.field_1352 * s;
         z -= side.field_1350 * s;
      }

      if (this.up) {
         y += s;
      }

      if (this.down) {
         y -= s;
      }

      this.pos.add(x, y, z);
   }

   @EventHandler
   private void onChunkOcclusion(ChunkOcclusionEvent event) {
      event.cancel();
   }

   @EventHandler
   private void onKey(KeyEvent event) {
      if (this.mc.field_1755 == null) {
         boolean pressed = !event.action.toString().contains("Release");
         if (this.mc.field_1690.field_1894.method_1417(event.key, 0)) {
            this.forward = pressed;
            event.cancel();
         } else if (this.mc.field_1690.field_1881.method_1417(event.key, 0)) {
            this.backward = pressed;
            event.cancel();
         } else if (this.mc.field_1690.field_1913.method_1417(event.key, 0)) {
            this.left = pressed;
            event.cancel();
         } else if (this.mc.field_1690.field_1849.method_1417(event.key, 0)) {
            this.right = pressed;
            event.cancel();
         } else if (this.mc.field_1690.field_1903.method_1417(event.key, 0)) {
            this.up = pressed;
            event.cancel();
         } else if (this.mc.field_1690.field_1832.method_1417(event.key, 0)) {
            this.down = pressed;
            event.cancel();
         }

      }
   }

   public double getX(float t) {
      return class_3532.method_16436((double)t, this.prevPos.x, this.pos.x);
   }

   public double getY(float t) {
      return class_3532.method_16436((double)t, this.prevPos.y, this.pos.y);
   }

   public double getZ(float t) {
      return class_3532.method_16436((double)t, this.prevPos.z, this.pos.z);
   }

   public float getYaw(float t) {
      return class_3532.method_17821(t, this.prevYaw, this.yaw);
   }

   public float getPitch(float t) {
      return class_3532.method_16439(t, this.prevPitch, this.pitch);
   }
}
