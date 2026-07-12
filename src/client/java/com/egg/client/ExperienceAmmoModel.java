package com.egg.client;

import com.egg.ExperienceAmmoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class ExperienceAmmoModel extends EntityModel<ExperienceAmmoEntity> {

    // This is the main part of your model
    private final ModelPart root;

    public ExperienceAmmoModel(ModelPart root) {
        super(RenderType::entityCutout);
        this.root = root.getChild("experience_ammo");
    }


    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition experience_ammo = partdefinition.addOrReplaceChild("experience_ammo", CubeListBuilder.create().texOffs(0, 6).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 22.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void setupAnim(ExperienceAmmoEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Leave empty for a simple bullet.
        // If you wanted it to spin while flying, you would add rotation logic here.
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        // Renders the model to the screen
        this.root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}