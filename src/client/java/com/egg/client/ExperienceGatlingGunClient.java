package com.egg.client;

import com.egg.ExperienceGatlingGun;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ExperienceGatlingGunClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
        // 1. Register the Model (Links the layer to your geometry)
        EntityModelLayerRegistry.registerModelLayer(AMMO_LAYER, ExperienceAmmoModel::createBodyLayer);

        // 2. Register the Renderer (Tells the game to use your custom renderer instead of the 2D one)
        EntityRendererRegistry.register(ExperienceGatlingGun.EXPERIENCE_AMMO, ExperienceAmmoRenderer::new);
	}

    // Create a unique layer location for the model
    public static final ModelLayerLocation AMMO_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath("experience-gatling-gun", "experience_ammo"), "main"
    );
}