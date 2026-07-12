package com.egg.entity.render;

import com.egg.entity.model.ExperienceAmmoModel;
import com.egg.client.ExperienceGatlingGunClient;
import com.egg.entity.ExperienceAmmoEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.NotNull;

public class ExperienceAmmoRenderer extends EntityRenderer<ExperienceAmmoEntity> {

    private static final Identifier TEXTURE = Identifier.of("egg", "textures/entity/experience_ammo.png");
    private final ExperienceAmmoModel model;

    public ExperienceAmmoRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new ExperienceAmmoModel(context.getPart(ExperienceGatlingGunClient.AMMO_LAYER));
    }

    @Override
    public void render(ExperienceAmmoEntity entity, float entityYaw, float partialTicks, MatrixStack matrices, VertexConsumerProvider buffer, int packedLight) {
        matrices.push();

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(partialTicks, entity.prevYaw, entity.getYaw()) - 90.0F)); // ⚠️ VÉRIFIER: yRotO -> prevYaw ?
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(partialTicks, entity.prevPitch, entity.getPitch())));

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderLayer.getEntityTranslucent(this.getTexture(entity)));

        this.model.render(matrices, vertexConsumer, packedLight, OverlayTexture.DEFAULT_UV, -1);

        matrices.pop();
        super.render(entity, entityYaw, partialTicks, matrices, buffer, packedLight);
    }

    @Override
    public @NotNull Identifier getTexture(ExperienceAmmoEntity entity) {
        return TEXTURE;
    }
}