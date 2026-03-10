package com.nnpg.glazed.utils;

import java.util.Random;

public class RandomBetweenDouble {
   private static final Random random = new Random();
   public final double min;
   public final double max;

   public RandomBetweenDouble(double min, double max) {
      if (min > max) {
         throw new IllegalArgumentException("Min value cannot be greater than max value");
      } else {
         this.min = min;
         this.max = max;
      }
   }

   public double getRandom() {
      return this.min == this.max ? this.min : this.min + (this.max - this.min) * random.nextDouble();
   }

   public double getMin() {
      return this.min;
   }

   public double getMax() {
      return this.max;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         RandomBetweenDouble that = (RandomBetweenDouble)obj;
         return Double.compare(that.min, this.min) == 0 && Double.compare(that.max, this.max) == 0;
      } else {
         return false;
      }
   }

   public int hashCode() {
      long minBits = Double.doubleToLongBits(this.min);
      long maxBits = Double.doubleToLongBits(this.max);
      return (int)(31L * minBits + maxBits);
   }

   public String toString() {
      return String.format("%.2f - %.2f", this.min, this.max);
   }
}
