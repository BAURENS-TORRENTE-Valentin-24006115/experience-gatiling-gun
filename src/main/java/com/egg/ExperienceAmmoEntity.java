package com.egg;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class ExperienceAmmoEntity extends ThrowableItemProjectile {

    public ExperienceAmmoEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        // 1. Ignore the person who shot the gun
        if (target == this.getOwner()) {
            return false;
        }

        // 2. Ignore other bullets so they pass cleanly through each other!
        if (target instanceof ExperienceAmmoEntity) {
            return false;
        }

        // 3. Otherwise, follow normal Minecraft collision rules
        return super.canHitEntity(target);
    }

    public ExperienceAmmoEntity(Level level, LivingEntity shooter) {
        // Make sure your custom EntityType is registered and passed here!
        super(ExperienceGatlingGun.EXPERIENCE_AMMO, shooter, level);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return Items.EXPERIENCE_BOTTLE;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        // We don't need the owner check here anymore!
        super.onHitEntity(result);

        Entity target = result.getEntity();

        // Deal damage
        target.hurt(this.damageSources().thrown(this, this.getOwner()), 4.0F);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        // Destroy the bullet when it hits a valid target or block
        if (!this.level().isClientSide) {
            this.discard();
        }
    }
}