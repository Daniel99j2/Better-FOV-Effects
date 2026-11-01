package com.daniel99j.foveffects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ByIdMap;

import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public enum ModMode {
    OFF(0, "options.betterfov.mode.off", () -> false),
    ON(1, "options.betterfov.mode.on", () -> true),
    ELYTRA_ONLY(2, "options.betterfov.mode.elytra", () -> Minecraft.getInstance().player != null && Minecraft.getInstance().player.isFallFlying()),
    FAST_MOVEMENT(3, "options.betterfov.mode.fast", () -> true);

    private final int id;
    private final Component caption;
    private final Supplier<Boolean> enabled;

    ModMode(final int id, final String key, Supplier<Boolean> enabled) {
        this.id = id;
        this.caption = Component.translatable(key);
        this.enabled = enabled;
    }

    public Component caption() {
        return this.caption;
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    private static final IntFunction<ModMode> BY_ID = ByIdMap.continuous((s) -> s.id, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final Codec<ModMode> OPTIONS_CODEC;

    static {
        PrimitiveCodec<Integer> var10000 = Codec.INT;
        IntFunction<ModMode> var10001 = BY_ID;
        Objects.requireNonNull(var10001);
        OPTIONS_CODEC = var10000.xmap(var10001::apply, (s) -> s.id);
    }
}
