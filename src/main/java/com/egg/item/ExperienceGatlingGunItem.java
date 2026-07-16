package com.egg.item;

import com.egg.entity.ExperienceAmmoEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ExperienceGatlingGunItem extends Item {

    public static final int MAX_USAGE_TIME = 20;
    public int actualTime = 0;
    public float regenPerTick = 2; // 2 is time two
    public boolean isFiring = false;

    public ExperienceGatlingGunItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        player.setCurrentHand(hand);
        return TypedActionResult.consume(player.getStackInHand(hand));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot , boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (world.isClient()) return;

        if (entity instanceof PlayerEntity player) {
            if (player.isUsingItem()) {
                isFiring = false;
            }
        }

        if (actualTime > 0 && !isFiring) {
            actualTime = Math.max(0, actualTime - (int) regenPerTick);
        }
    }

    @Override
    public void usageTick(World world, LivingEntity livingEntity, ItemStack stack, int remainingUseTicks) {
        if (livingEntity instanceof PlayerEntity player) {

            if (actualTime >= MAX_USAGE_TIME) {return;}
            int count = stack.getMaxUseTime(player) - remainingUseTicks;
            if (count % 2 == 0) {
                boolean isCreative = player.getAbilities().creativeMode;

                if (isCreative || player.totalExperience > 0) {

                    world.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS,
                            1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);

                    if (!world.isClient()) {
                        ExperienceAmmoEntity projectile = new ExperienceAmmoEntity(world, player);

                        //Vec3d look = player.getRotationVector();

                        double spawnX = player.getX();
                        double spawnY = player.getEyeY();
                        double spawnZ = player.getZ();

                        projectile.setPosition(spawnX, spawnY, spawnZ);

                        projectile.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, 3.0F, 0.0F);

                        world.spawnEntity(projectile);

                        if (!isCreative) {
                            player.addExperience(-1);
                        }
                        actualTime++;
                        if (!isFiring) {isFiring = true;}
                    }
                }
            }
        }
    }
}