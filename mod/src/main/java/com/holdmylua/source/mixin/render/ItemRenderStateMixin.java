package com.holdmylua.source.mixin.render;

import com.holdmylua.source.global.DispatcherStorage;
import com.holdmylua.source.global.item_model.ItemModelStorage;
import com.mojang.renderpearl.api.commands.RenderPass;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({FeatureRenderDispatcher.class})
public class ItemRenderStateMixin {
   // 26.3: renderAllFeatures(SubmitNodeStorage) became the static
   // renderAllFeatures(RenderPass, FeatureRenderDispatcher.PreparedFrame) -
   // neither new param is used by this handler, so only the signature changes.
   @Inject(
      method = {"renderAllFeatures"},
      at = {@At("TAIL")}
   )
   private static void tester(RenderPass renderPass, FeatureRenderDispatcher.PreparedFrame preparedFrame, CallbackInfo ci) {
      DispatcherStorage.clear();
      ItemModelStorage.clear();
   }
}
