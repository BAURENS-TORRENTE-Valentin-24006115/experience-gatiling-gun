package com.egg.entity.model;

import com.egg.entity.ExperienceAmmoEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class ExperienceAmmoModel extends EntityModel<ExperienceAmmoEntity> {

    private final ModelPart root;

    public ExperienceAmmoModel(ModelPart root) {
        super(RenderLayer::getEntityCutout);
        this.root = root.getChild("experience_ammo");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData partData = modelData.getRoot();

        partData.addChild("experience_ammo",
                ModelPartBuilder.create()
                        .uv(0, 6).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(0, 0).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 22.5F, 0.0F));

        return TexturedModelData.of(modelData, 16, 16);
    }

    @Override
    public void setAngles(ExperienceAmmoEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Vide pour un projectile simple
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.root.render(matrices, vertices, light, overlay, color);
    }
}