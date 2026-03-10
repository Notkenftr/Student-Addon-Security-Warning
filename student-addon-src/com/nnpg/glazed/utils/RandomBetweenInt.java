package com.nnpg.glazed.utils;

import java.util.Random;

public class RandomBetweenInt {
   private static final Random random = new Random();
   public final int min;
   public final int max;

   public RandomBetweenInt(int min, int max) {
      if (min > max) {
         throw new IllegalArgumentException("Min value cannot be greater than max value");
      } else {
         this.min = min;
         this.max = max;
      }
   }

   public int getRandom() {
      return this.min == this.max ? this.min : random.nextInt(this.max - this.min + 1) + this.min;
   }

   public int getMin() {
      return this.min;
   }

   public int getMax() {
      return this.max;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         RandomBetweenInt that = (RandomBetweenInt)obj;
         return this.min == that.min && this.max == that.max;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return 31 * this.min + this.max;
   }

   public String toString() {
      return this.min + " - " + this.max;
   }
}
