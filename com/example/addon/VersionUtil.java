package com.example.addon;

import net.minecraft.class_1297;
import net.minecraft.class_1799;
import net.minecraft.class_2371;
import net.minecraft.class_746;

public class VersionUtil {
   public static class_1799 getArmorStack(class_746 player, int slot) {
      return player.method_31548().method_5438(slot);
   }

   public static class_1799 getArmorStackByType(class_746 player, int armorType) {
      return player.method_31548().method_5438(armorType);
   }

   public static int getSelectedSlot(class_746 player) {
      return player.method_31548().method_67532();
   }

   public static void setSelectedSlot(class_746 player, int slot) {
      player.method_31548().method_61496(slot);
   }

   public static double getPrevX(class_1297 entity) {
      return entity.field_6038;
   }

   public static double getPrevY(class_1297 entity) {
      return entity.field_5971;
   }

   public static double getPrevZ(class_1297 entity) {
      return entity.field_5989;
   }

   public static class_2371<class_1799> getMainInventory(class_746 player) {
      return null;
   }
}
