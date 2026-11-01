package com.daniel99j.foveffects.mixin;

import com.daniel99j.foveffects.BetterFOVEffects;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Camera.class)
public abstract class UnboundFovMixin {
	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"), method = "tickFov")
	private float better2(float value, float min, float max, Operation<Float> original) {
		if(BetterFOVEffects.mode().get().isEnabled()) {
			float maximum = Math.min((float) Minecraft.getInstance().options.fov().get() * value, 160) / Minecraft.getInstance().options.fov().get();
			//Minecraft.getInstance().player.sendOverlayMessage(Component.literal("M: " + maximum + " V: " + value));
			return original.call(value, min, maximum);
		} else return original.call(value, min, max);
	}
}