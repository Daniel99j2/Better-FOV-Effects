package com.daniel99j.foveffects.mixin;

import com.daniel99j.foveffects.BetterFOVEffects;
import com.daniel99j.foveffects.ModMode;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.Holder;
import net.minecraft.util.Util;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayer.class)
public abstract class FovEffectsMixin extends Player {
	@Unique
    private static final Vec3 DISABLED_POS = new Vec3(Double.NaN, Double.NaN, Double.NaN);
	@Unique
    private Vec3 lastPos = DISABLED_POS;
	@Unique
    private Vec3 lastChange = Vec3.ZERO;

	public FovEffectsMixin(Level level, GameProfile gameProfile) {
		super(level, gameProfile);
	}

	@Inject(at = @At(value = "HEAD"), method = "tick")
	private void onTick(CallbackInfo ci) {
		if (!this.lastPos.isFinite()) {
			this.lastPos = this.position();
		}
		this.lastChange = this.position().subtract(this.lastPos);
		this.lastPos = this.position();
	}

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Abilities;getWalkingSpeed()F"), method = "getFieldOfViewModifier")
	private float dontDivide(Abilities instance, Operation<Float> original) {
		if(BetterFOVEffects.mode().get().isEnabled()) {
			return (float) fastMovement(1, original.call(instance));
		} else return original.call(instance);
	}

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;getAttributeValue(Lnet/minecraft/core/Holder;)D"), method = "getFieldOfViewModifier")
	private double makeItBetter1(AbstractClientPlayer instance, Holder<Attribute> holder, Operation<Double> original) {
		if(BetterFOVEffects.mode().get().isEnabled()) {

			double modifier = 0;
			modifier = modifier + this.lastChange.dot(this.getLookAngle()) / 5;
			//Minecraft.getInstance().player.sendOverlayMessage(Component.literal("O: " + modifier + " A: " + this.position().subtract(lastPos).dot(this.getLookAngle())));
            return fastMovement(((modifier + 1) * 2 - 1), original.call(instance, holder));
		} else {
			this.lastPos = DISABLED_POS;
			return original.call(instance, holder);
		}
	}

	@Unique
    private double fastMovement(double newValue, double oldValue) {
		if (BetterFOVEffects.mode().get() == ModMode.FAST_MOVEMENT) {
			double change = this.lastChange.length();
			if (change > 1 && change < 3) {
				double ratio = (change - 1) / 2;
				return newValue * ratio + oldValue * (1 - ratio);
			} else if(change <= 1) {
				return oldValue;
			}
		}
		return newValue;
	}
}