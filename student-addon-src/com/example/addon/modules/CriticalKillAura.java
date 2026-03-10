package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import java.util.Iterator;
import java.util.Set;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.DoubleSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;

public class CriticalKillAura extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> range;
   private final Setting<CriticalKillAura.TargetMode> targetMode;
   private final Setting<Set<class_1299<?>>> targets;
   private final Setting<Boolean> autoJump;
   private boolean wasInAir;
   private int attackCooldown;

   public CriticalKillAura() {
      super(AddonTemplate.Student_pvp, "critical-kill-aura", "");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.range = this.sgGeneral.add(((Builder)(new Builder()).name("range")).defaultValue(4.0D).min(1.0D).max(6.0D).build());
      this.targetMode = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)(new meteordevelopment.meteorclient.settings.EnumSetting.Builder()).name("target-mode")).description("choose target")).defaultValue(CriticalKillAura.TargetMode.Closest)).build());
      this.targets = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.EntityTypeListSetting.Builder)((meteordevelopment.meteorclient.settings.EntityTypeListSetting.Builder)(new meteordevelopment.meteorclient.settings.EntityTypeListSetting.Builder()).name("targets")).description("Entities to attack.")).defaultValue(new class_1299[]{class_1299.field_6097}).build());
      this.autoJump = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()).name("auto-jump")).description("auto jump to attack critical")).defaultValue(true)).build());
      this.wasInAir = false;
      this.attackCooldown = 0;
   }

   public void onDeactivate() {
      this.wasInAir = false;
      this.attackCooldown = 0;
   }

   @EventHandler
   private void onTick(Pre event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (this.attackCooldown > 0) {
            --this.attackCooldown;
         } else {
            class_1309 target = this.getTarget();
            if (target != null) {
               if (this.mc.field_1724.method_24828()) {
                  if ((Boolean)this.autoJump.get()) {
                     this.mc.field_1724.method_6043();
                  }

                  this.wasInAir = false;
               } else {
                  if (!this.mc.field_1724.method_24828()) {
                     this.wasInAir = true;
                  }

                  double velY = this.mc.field_1724.method_18798().field_1351;
                  double height = this.mc.field_1724.method_23318() - Math.floor(this.mc.field_1724.method_23318());
                  if (this.wasInAir && velY < 0.0D && height >= 0.3D && height <= 0.5D) {
                     this.mc.field_1761.method_2918(this.mc.field_1724, target);
                     this.mc.field_1724.method_6104(this.mc.field_1724.method_6058());
                     this.attackCooldown = 5;
                     this.wasInAir = false;
                  }

               }
            }
         }
      }
   }

   private class_1309 getTarget() {
      class_1309 best = null;
      double bestValue = 0.0D;
      Iterator var4 = this.mc.field_1687.method_18112().iterator();

      while(true) {
         class_1309 living;
         double value;
         do {
            class_1297 entity;
            double distance;
            do {
               do {
                  do {
                     do {
                        do {
                           if (!var4.hasNext()) {
                              return best;
                           }

                           entity = (class_1297)var4.next();
                        } while(!(entity instanceof class_1309));

                        living = (class_1309)entity;
                     } while(entity == this.mc.field_1724);
                  } while(!entity.method_5805());

                  distance = (double)this.mc.field_1724.method_5739(entity);
               } while(distance > (Double)this.range.get());
            } while(!((Set)this.targets.get()).contains(entity.method_5864()));

            double var10000;
            switch(((CriticalKillAura.TargetMode)this.targetMode.get()).ordinal()) {
            case 0:
               var10000 = -distance;
               break;
            case 1:
               var10000 = distance;
               break;
            case 2:
               var10000 = (double)(-living.method_6032());
               break;
            case 3:
               var10000 = (double)living.method_6032();
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
            }

            value = var10000;
         } while(best != null && !(value > bestValue));

         bestValue = value;
         best = living;
      }
   }

   public static enum TargetMode {
      Closest,
      Farthest,
      LowestHealth,
      HighestHealth;

      // $FF: synthetic method
      private static CriticalKillAura.TargetMode[] $values() {
         return new CriticalKillAura.TargetMode[]{Closest, Farthest, LowestHealth, HighestHealth};
      }
   }
}
