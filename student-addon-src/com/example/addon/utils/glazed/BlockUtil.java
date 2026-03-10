package com.example.addon.utils.glazed;

import java.util.Objects;
import java.util.stream.Stream;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1923;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2818;
import net.minecraft.class_3965;
import net.minecraft.class_4969;

public final class BlockUtil {
   public static Stream<class_2818> getLoadedChunks() {
      int radius = Math.max(2, MeteorClient.mc.field_1690.method_38521()) + 3;
      int diameter = radius * 2 + 1;
      class_1923 center = MeteorClient.mc.field_1724.method_31476();
      class_1923 min = new class_1923(center.field_9181 - radius, center.field_9180 - radius);
      class_1923 max = new class_1923(center.field_9181 + radius, center.field_9180 + radius);
      return Stream.iterate(min, (pos) -> {
         int x = pos.field_9181;
         int z = pos.field_9180;
         ++x;
         if (x > max.field_9181) {
            x = min.field_9181;
            ++z;
         }

         if (z > max.field_9180) {
            throw new IllegalStateException("Stream limit didn't work.");
         } else {
            return new class_1923(x, z);
         }
      }).limit((long)diameter * (long)diameter).filter((c) -> {
         return MeteorClient.mc.field_1687.method_8393(c.field_9181, c.field_9180);
      }).map((c) -> {
         return MeteorClient.mc.field_1687.method_8497(c.field_9181, c.field_9180);
      }).filter(Objects::nonNull);
   }

   public static boolean isBlockAtPosition(class_2338 blockPos, class_2248 block) {
      return MeteorClient.mc.field_1687.method_8320(blockPos).method_26204() == block;
   }

   public static boolean isRespawnAnchorCharged(class_2338 blockPos) {
      return isBlockAtPosition(blockPos, class_2246.field_23152) && (Integer)MeteorClient.mc.field_1687.method_8320(blockPos).method_11654(class_4969.field_23153) != 0;
   }

   public static boolean isRespawnAnchorUncharged(class_2338 blockPos) {
      return isBlockAtPosition(blockPos, class_2246.field_23152) && (Integer)MeteorClient.mc.field_1687.method_8320(blockPos).method_11654(class_4969.field_23153) == 0;
   }

   public static void interactWithBlock(class_3965 blockHitResult, boolean shouldSwingHand) {
      class_1269 result = MeteorClient.mc.field_1761.method_2896(MeteorClient.mc.field_1724, class_1268.field_5808, blockHitResult);
      if (result.method_23665() && shouldSwingHand) {
         MeteorClient.mc.field_1724.method_6104(class_1268.field_5808);
      }

   }
}
