package com.nnpg.glazed.utils;

import java.lang.reflect.Field;
import net.minecraft.class_1661;

public final class InventoryUtils {
   private static Field selectedSlotField;

   private InventoryUtils() {
   }

   public static int getSelectedSlot(class_1661 inv) {
      if (selectedSlotField == null) {
         return 0;
      } else {
         try {
            return selectedSlotField.getInt(inv);
         } catch (IllegalAccessException var2) {
            return 0;
         }
      }
   }

   public static void setSelectedSlot(class_1661 inv, int slot) {
      if (selectedSlotField != null) {
         try {
            selectedSlotField.setInt(inv, slot);
         } catch (IllegalAccessException var3) {
         }

      }
   }

   static {
      try {
         selectedSlotField = class_1661.class.getDeclaredField("selectedSlot");
         selectedSlotField.setAccessible(true);
      } catch (NoSuchFieldException var1) {
         selectedSlotField = null;
      }

   }
}
