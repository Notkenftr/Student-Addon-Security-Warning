package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.BoolSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1299;

public class AntiTrap extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> removeExisting;
   private final Setting<Boolean> preventSpawn;
   private final Setting<Boolean> armorStands;
   private final Setting<Boolean> chestMinecarts;

   public AntiTrap() {
      super(AddonTemplate.Student_esp, "Antitrap", "");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.removeExisting = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("remove-existing")).description("Remove existing trap entities when enabled.")).defaultValue(true)).build());
      this.preventSpawn = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("prevent-spawn")).description("Prevent new trap entities from spawning.")).defaultValue(true)).build());
      this.armorStands = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("armor-stands")).description("Target armor stands.")).defaultValue(true)).build());
      this.chestMinecarts = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("chest-minecarts")).description("Target chest minecarts.")).defaultValue(true)).build());
   }

   public void onActivate() {
      if ((Boolean)this.removeExisting.get()) {
         this.removeTrapEntities();
      }

   }

   @EventHandler
   private void onEntityAdded(EntityAddedEvent event) {
      if ((Boolean)this.preventSpawn.get()) {
         class_1297 entity = event.entity;
         if (this.isTrapEntity(entity)) {
            entity.method_31472();
         }

      }
   }

   @EventHandler
   private void onTick(Pre event) {
      if (this.mc.field_1687 != null) {
         if (this.mc.field_1724.field_6012 % 20 == 0) {
            List<class_1297> toRemove = new ArrayList();
            Iterator var3 = this.mc.field_1687.method_18112().iterator();

            class_1297 entity;
            while(var3.hasNext()) {
               entity = (class_1297)var3.next();
               if (this.isTrapEntity(entity)) {
                  toRemove.add(entity);
               }
            }

            var3 = toRemove.iterator();

            while(var3.hasNext()) {
               entity = (class_1297)var3.next();
               entity.method_31472();
            }
         }

      }
   }

   private void removeTrapEntities() {
      if (this.mc.field_1687 != null) {
         List<class_1297> trapEntities = new ArrayList();
         Iterator var2 = this.mc.field_1687.method_18112().iterator();

         class_1297 entity;
         while(var2.hasNext()) {
            entity = (class_1297)var2.next();
            if (this.isTrapEntity(entity)) {
               trapEntities.add(entity);
            }
         }

         var2 = trapEntities.iterator();

         while(var2.hasNext()) {
            entity = (class_1297)var2.next();
            entity.method_31472();
         }

         if (!trapEntities.isEmpty()) {
            this.info("Removed %d trap entities", new Object[]{trapEntities.size()});
         }

      }
   }

   private boolean isTrapEntity(class_1297 entity) {
      if (entity == null) {
         return false;
      } else {
         class_1299<?> type = entity.method_5864();
         if ((Boolean)this.armorStands.get() && type == class_1299.field_6131) {
            return true;
         } else {
            return (Boolean)this.chestMinecarts.get() && type == class_1299.field_6126;
         }
      }
   }
}
