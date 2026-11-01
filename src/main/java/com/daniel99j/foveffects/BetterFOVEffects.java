package com.daniel99j.foveffects;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import embinmc.mod.optionsapi.OptionsMenuLocation;
import embinmc.mod.optionsapi.VanillaOptionsAPI;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

import net.minecraft.server.level.ParticleStatus;
import net.minecraft.util.Mth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntFunction;

public class BetterFOVEffects implements ModInitializer {
	public static final String MOD_ID = "better-fov-effects";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static final OptionInstance<ModMode> MODE = new OptionInstance<>("options.betterfov.mode", OptionInstance.cachedConstantTooltip(Component.translatable("options.betterfov.mode.tooltip")), (caption, value) -> value.caption(), new OptionInstance.Enum<>(Arrays.asList(ModMode.values()), ModMode.OPTIONS_CODEC), ModMode.ON, OptionInstance.NO_ACTION);

	public static OptionInstance<ModMode> mode() {
		return MODE;
	}

	@Override
	public void onInitialize() {
		VanillaOptionsAPI.register(Identifier.fromNamespaceAndPath(MOD_ID, "mode"), OptionsMenuLocation.ACCESSIBILITY, BetterFOVEffects::mode);

		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
				dispatcher.register(Commands.literal("betterfov-motion").then(Commands.argument("x", DoubleArgumentType.doubleArg()).then(Commands.argument("y", DoubleArgumentType.doubleArg()).then(Commands.argument("z", DoubleArgumentType.doubleArg()).executes(context -> {
					context.getSource().getPlayerOrException().setDeltaMovement(DoubleArgumentType.getDouble(context, "x"), DoubleArgumentType.getDouble(context, "y"), DoubleArgumentType.getDouble(context, "z"));
					context.getSource().getPlayerOrException().syncVelocity = true;
					context.getSource().sendSuccess(() -> Component.literal("Set motion"), false);
					return 1;
				})))));
			});
		}
	}
}
