package com.egg.item;

import com.egg.ExperienceGatlingGun;
import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModDataComponents {
    public static final ComponentType<Integer> STORED_XP = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(ExperienceGatlingGun.MOD_ID, "stored_xp"),
            ComponentType.<Integer>builder()
                    .codec(Codec.INT)
                    .packetCodec(PacketCodecs.VAR_INT)
                    .build()
    );

    public static void register() {
    }
}