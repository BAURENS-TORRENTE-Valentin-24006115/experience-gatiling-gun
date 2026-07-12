package com.egg;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExperienceGatlingGun implements ModInitializer {
	public static final String MOD_ID = "experience-gatling-gun";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Initializing Experience Gatling GUN!");
        ModItems.initialize();
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}

    public static final EntityType<ExperienceAmmoEntity> EXPERIENCE_AMMO = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath("experience-gatling-gun", "experience_ammo"),
            EntityType.Builder.<ExperienceAmmoEntity>of(ExperienceAmmoEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build("experience_ammo")
    );
}
