package com.nnpg.glazed.utils.glazed;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import net.minecraft.class_243;
import org.joml.Vector3d;

public final class Utils {
   public static Color getMainColor(int alpha, int offset) {
      return new Color(255, 255, 255, alpha);
   }

   public static File getCurrentJarPath() throws URISyntaxException {
      return new File(Utils.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
   }

   public static void overwriteFile(String urlString, File targetFile) {
      try {
         URL url = new URL(urlString);
         HttpURLConnection connection = (HttpURLConnection)url.openConnection();
         connection.setRequestMethod("GET");
         connection.setConnectTimeout(5000);
         connection.setReadTimeout(10000);
         InputStream inputStream = connection.getInputStream();

         try {
            FileOutputStream outputStream = new FileOutputStream(targetFile);

            try {
               byte[] buffer = new byte[1024];

               while(true) {
                  int bytesRead;
                  if ((bytesRead = inputStream.read(buffer)) == -1) {
                     outputStream.flush();
                     break;
                  }

                  outputStream.write(buffer, 0, bytesRead);
               }
            } catch (Throwable var10) {
               try {
                  outputStream.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }

               throw var10;
            }

            outputStream.close();
         } catch (Throwable var11) {
            if (inputStream != null) {
               try {
                  inputStream.close();
               } catch (Throwable var8) {
                  var11.addSuppressed(var8);
               }
            }

            throw var11;
         }

         if (inputStream != null) {
            inputStream.close();
         }

         connection.disconnect();
      } catch (Exception var12) {
         System.err.println("Error downloading file: " + var12.getMessage());
         var12.printStackTrace();
      }

   }

   public static void copyVector(Vector3d destination, class_243 source) {
      destination.x = source.field_1352;
      destination.y = source.field_1351;
      destination.z = source.field_1350;
   }

   public static void copyVector(Vector3d destination, Vector3d source) {
      destination.x = source.x;
      destination.y = source.y;
      destination.z = source.z;
   }

   public static Vector3d toVector3d(class_243 vec3d) {
      return new Vector3d(vec3d.field_1352, vec3d.field_1351, vec3d.field_1350);
   }

   public static class_243 toVec3d(Vector3d vector3d) {
      return new class_243(vector3d.x, vector3d.y, vector3d.z);
   }

   public static double lerp(double start, double end, double progress) {
      return start + (end - start) * progress;
   }

   public static Vector3d lerp(Vector3d start, Vector3d end, double progress) {
      return new Vector3d(lerp(start.x, end.x, progress), lerp(start.y, end.y, progress), lerp(start.z, end.z, progress));
   }

   public static double clamp(double value, double min, double max) {
      return Math.max(min, Math.min(max, value));
   }

   public static float clamp(float value, float min, float max) {
      return Math.max(min, Math.min(max, value));
   }

   public static int clamp(int value, int min, int max) {
      return Math.max(min, Math.min(max, value));
   }
}
