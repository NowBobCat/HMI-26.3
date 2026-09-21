package com.holdmylua.source.scripting.custom_api;

import com.holdmylua.source.annotation.Safe;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class KeyBindManager {
   KeyMapping key;

   @Safe
   public boolean isKeyPressed(int keyCode) {
      if (keyCode != 0) {
         long windowHandle = Minecraft.getInstance().getWindow().handle();
         return InputConstants.isKeyDown(windowHandle, keyCode);
      } else {
         return false;
      }
   }
}
