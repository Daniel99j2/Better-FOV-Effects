package com.daniel99j.foveffects.mixin;

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

@Mixin(AbstractClientPlayer.class)
public abstract class FovEffectsMixin extends Player {
	@Unique
    private Vec3 lastPos = new Vec3(Double.NaN, Double.NaN, Double.NaN);
	@Unique
	private long lastFrameTime = 0;

	public FovEffectsMixin(Level level, GameProfile gameProfile) {
		super(level, gameProfile);
	}

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Abilities;getWalkingSpeed()F"), method = "getFieldOfViewModifier")
	private float dontDivide(Abilities instance, Operation<Float> original) {
		return 1;
	}

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;getAttributeValue(Lnet/minecraft/core/Holder;)D"), method = "getFieldOfViewModifier")
	private double makeItBetter1(AbstractClientPlayer instance, Holder<Attribute> holder, Operation<Double> original) {
		if(!this.lastPos.isFinite()) {
			this.lastPos = this.position();
		}
		double modifier = 0;
		Vec3 interpolated = this.position().subtract(lastPos);
		modifier = modifier + interpolated.dot(this.getLookAngle()) / 10;
		//Minecraft.getInstance().player.sendOverlayMessage(Component.literal("O: " + modifier + " A: " + this.position().subtract(lastPos).dot(this.getLookAngle())));
		if(Util.getMillis() != this.lastFrameTime) {
			this.lastPos = this.position();
			this.lastFrameTime = Util.getMillis();
		}
		return (modifier + 1) * 2 - 1;
	}
}