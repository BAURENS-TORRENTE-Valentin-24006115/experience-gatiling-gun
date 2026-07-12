package com.egg.client;

import com.egg.ExperienceAmmoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ExperienceAmmoRenderer extends EntityRenderer<ExperienceAmmoEntity> {

    // Path to your texture
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("experience-gatling-gun", "textures/entity/experience_ammo.png");
    private final ExperienceAmmoModel model;

    public ExperienceAmmoRenderer(EntityRendererProvider.Context context) {
        super(context);
        // We will register this layer in the next step
        this.model = new ExperienceAmmoModel(context.bakeLayer(ExperienceGatlingGunClient.AMMO_LAYER));
    }

    @Override
    public void render(ExperienceAmmoEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // Math to make the bullet point in the direction it is flying
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));

        // Fetch the texture and render it
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));

        // 1.21.1 uses a single ARGB integer for color. 0xFFFFFFFF means fully opaque white (normal).
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(ExperienceAmmoEntity entity) {
        return TEXTURE;
    }
}
