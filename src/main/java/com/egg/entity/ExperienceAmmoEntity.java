package com.egg.entity;

import com.egg.ExperienceGatlingGun;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ExperienceAmmoEntity extends ThrownItemEntity {

    public ExperienceAmmoEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public ExperienceAmmoEntity(World world, LivingEntity shooter) {
        super(ExperienceGatlingGun.EXPERIENCE_AMMO, shooter, world);
    }

    @Override
    protected boolean canHit(Entity target) { // ⚠️ VÉRIFIER: peut aussi s'appeler canHitEntity selon la version
        if (target == this.getOwner()) {
            return false;
        }
        if (target instanceof ExperienceAmmoEntity) {
            return false;
        }
        return super.canHit(target);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return Items.EXPERIENCE_BOTTLE;
    }

    @Override
    protected void onEntityHit(EntityHitResult result) {
        super.onEntityHit(result);

        Entity target = result.getEntity();

        // ⚠️ VÉRIFIER: signature de damage() a pu changer (ServerWorld en 1er paramètre selon version)
        target.damage(this.getWorld().getDamageSources().thrown(this, this.getOwner()), 4.0F);
    }

    @Override
    protected void onCollision(HitResult result) { // ⚠️ VÉRIFIER: nom possible aussi "onHit"
        super.onCollision(result);

        if (!this.getWorld().isClient) {
            this.discard();
        }
    }
}