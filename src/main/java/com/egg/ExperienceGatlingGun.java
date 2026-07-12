package com.egg;

import com.egg.entity.ExperienceAmmoEntity;
import com.egg.item.ModDataComponents;
import com.egg.item.ModEventHandlers;
import com.egg.item.ModItemGroups;
import com.egg.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExperienceGatlingGun implements ModInitializer {
    public static final String MOD_ID = "egg";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final EntityType<ExperienceAmmoEntity> EXPERIENCE_AMMO = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "experience_ammo"),
            FabricEntityTypeBuilder.<ExperienceAmmoEntity>create(SpawnGroup.MISC, ExperienceAmmoEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                    .build()
    );

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Experience Gatling GUN!");
        ModItems.registerItems();
        ModItemGroups.registerItemGroups();
        ModDataComponents.register();
        ModEventHandlers.register();
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}