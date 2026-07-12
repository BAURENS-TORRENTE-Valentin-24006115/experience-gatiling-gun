package com.egg.client;

import com.egg.ExperienceGatlingGun;
import com.egg.entity.model.ExperienceAmmoModel;
import com.egg.entity.render.ExperienceAmmoRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ExperienceGatlingGunClient implements ClientModInitializer {

    public static final EntityModelLayer AMMO_LAYER = new EntityModelLayer(
            Identifier.of("egg", "experience_ammo"), "main"
    );

    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(AMMO_LAYER, ExperienceAmmoModel::getTexturedModelData);
        EntityRendererRegistry.register(ExperienceGatlingGun.EXPERIENCE_AMMO, ExperienceAmmoRenderer::new);
    }
}