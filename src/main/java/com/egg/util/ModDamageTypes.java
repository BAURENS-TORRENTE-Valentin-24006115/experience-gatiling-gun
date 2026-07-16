package com.egg.util;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.entity.damage.DamageType;

public class ModDamageTypes {
    public static final RegistryKey<DamageType> EXPERIENCE_GRINDER =
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of("egg", "experience_grinder"));
}