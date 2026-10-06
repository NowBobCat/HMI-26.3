package com.holdmylua.source.mixin.client;

import com.holdmylua.source.access.LivingEntityAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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

   // 26.3: startAttack()Z can return early (attack cooldown via missTime,
   // spectator mode, disabled item, cannotAttackWithItem, etc.) well before
   // ever reaching player.swing(...) - and it's called every tick while
   // attack is held, not just once. A HEAD injection reset swing state on
   // every one of those calls even when no real swing happened, causing the
   // animation to restart continuously. Redirecting the actual swing() call
   // (as the original mod did pre-26.3) means our reset only fires when
   // vanilla itself decides to swing.
   @Redirect(
      method = {"startAttack"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/component/SwingAnimation;Z)Z"
      )
   )
   public boolean doAttackMix(LocalPlayer instance, InteractionHand hand, SwingAnimation animation, boolean broadcast) {
      if (instance instanceof LivingEntityAccessor mixin) {
         mixin.hMI5_0$resetMainHandSwing(false);
      }

      return instance.swing(hand, animation, broadcast);
   }

   // 26.3: same issue as startAttack() above - startUseItem()V can return
   // early in several places (busy hands, disabled item, failed/passed
   // interactions) without ever calling swing(), and runs every tick while
   // the use button is held. All three real swing() call sites inside
   // startUseItem share this exact descriptor, so one @Redirect catches all
   // of them automatically.
   @Redirect(
      method = {"startUseItem"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/component/SwingAnimation;Z)Z"
      )
   )
   private boolean doItemUse(LocalPlayer instance, InteractionHand hand, SwingAnimation animation, boolean broadcast) {
      if (instance instanceof LivingEntityAccessor accessor) {
         if (hand == InteractionHand.MAIN_HAND) {
            accessor.hMI5_0$resetMainHandSwing(true);
         } else {
            accessor.hMI5_0$resetOffHandSwing(true);
         }
      }

      return instance.swing(hand, animation, broadcast);
   }
}
