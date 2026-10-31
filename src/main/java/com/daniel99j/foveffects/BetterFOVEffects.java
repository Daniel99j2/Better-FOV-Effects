package com.daniel99j.foveffects;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterFOVEffects implements ModInitializer {
	public static final String MOD_ID = "better-fov-effects";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

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
