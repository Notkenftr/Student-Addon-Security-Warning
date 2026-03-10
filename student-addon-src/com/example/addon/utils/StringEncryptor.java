package com.example.addon.utils;

public class StringEncryptor {
   private static final int KEY = -559038737;

   public static String decrypt(String input) {
      char[] chars = input.toCharArray();

      for(int i = 0; i < chars.length; ++i) {
         chars[i] = (char)(chars[i] ^ -559038737 >> i % 32 & 255);
      }

      return new String(chars);
   }

   public static String d(String s) {
      return decrypt(s);
   }
}
