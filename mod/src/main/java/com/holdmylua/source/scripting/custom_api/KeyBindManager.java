package com.holdmylua.source.scripting.custom_api;

import com.holdmylua.source.annotation.Safe;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public class KeyBindManager {
   KeyMapping key;

   @Safe
   public boolean isKeyPressed(int keyCode) {
      if (keyCode != 0) {
         return InputConstants.isKeyDown(keyCode);
      } else {
         return false;
      }
   }
}
