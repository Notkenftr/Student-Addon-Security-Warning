package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import java.util.Set;
import meteordevelopment.meteorclient.events.entity.player.AttackEntityEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.BoolSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1299;

public class BlockAttackEntity extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> enabled;
   private final Setting<Set<class_1299<?>>> blockedEntities;

   public BlockAttackEntity() {
      super(AddonTemplate.Student, "block-attack-entity", "Block attacking selected entities.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.enabled = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("enabled")).description("Enable block attack")).defaultValue(true)).build());
      this.blockedEntities = this.sgGeneral.add(((meteordevelopment.meteorclient.settings.EntityTypeListSetting.Builder)((meteordevelopment.meteorclient.settings.EntityTypeListSetting.Builder)(new meteordevelopment.meteorclient.settings.EntityTypeListSetting.Builder()).name("blocked-entities")).description("Entities that cannot be attacked")).defaultValue(new class_1299[0]).build());
   }

   @EventHandler
   private void onAttack(AttackEntityEvent event) {
      if ((Boolean)this.enabled.get()) {
         if (((Set)this.blockedEntities.get()).contains(event.entity.method_5864())) {
            event.cancel();
         }

      }
   }
}
