package com.holdmylua.source.mixin.client;

import com.holdmylua.source.access.LivingEntityAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Minecraft.class})
public class MinecraftClientMixin {
   @Shadow
   @Nullable
   public LocalPlayer player;
   @Shadow
   @Nullable
   public ClientLevel level;
   @Shadow
   @Final
   public GameRenderer gameRenderer;
   @Shadow
   @Nullable
   public MultiPlayerGameMode gameMode;
   @Shadow
   private int rightClickDelay;
   @Shadow
   @Nullable
   public HitResult hitResult;
   @Shadow
   @Final
   private static Logger LOGGER;

   // 26.3: swing()'s signature/owner changed internally, but startAttack()Z itself
   // didn't, and this injection never needed anything from the old call site -
   // so we just fire at HEAD instead of chasing the new internal invoke target.
   @Inject(
      method = {"startAttack"},
      at = {@At("HEAD")}
   )
   public void doAttackMix(CallbackInfoReturnable<Boolean> cir) {
      if (this.player instanceof LivingEntityAccessor mixin) {
         mixin.hMI5_0$resetMainHandSwing(false);
      }
   }

   // TODO 26.3: startUseItem()V no longer exposes which hand at a clean HEAD
   // injection point - that's decided partway through its body now (old code
   // redirected LocalPlayer.swing(InteractionHand), which no longer exists in
   // that 1-arg form). Approximating by resetting both hands here so this
   // compiles and runs; revisit with the real decompiled startUseItem() body
   // (gradlew genSources) to restore the original hand-specific behavior.
   @Inject(
      method = {"startUseItem"},
      at = @At("HEAD")
   )
   private void doItemUse(CallbackInfo ci) {
      if (this.player instanceof LivingEntityAccessor accessor) {
         accessor.hMI5_0$resetMainHandSwing(true);
         accessor.hMI5_0$resetOffHandSwing(true);
      }
   }
}
