package com.egg.item;

import com.egg.entity.ExperienceAmmoEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ExperienceGatlingGunItem extends Item {

    public ExperienceGatlingGunItem(Settings settings) {
        super(settings);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        player.setCurrentHand(hand);
        return TypedActionResult.consume(player.getStackInHand(hand));
    }

    @Override
    public void usageTick(World world, LivingEntity livingEntity, ItemStack stack, int remainingUseTicks) {
        if (livingEntity instanceof PlayerEntity player) {

            int count = stack.getMaxUseTime(player) - remainingUseTicks;
            if (count % 2 == 0) {
                boolean isCreative = player.getAbilities().creativeMode;

                if (isCreative || player.totalExperience > 0) {

                    world.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS,
                            1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);

                    if (!world.isClient()) {
                        ExperienceAmmoEntity projectile = new ExperienceAmmoEntity(world, player);

                        Vec3d look = player.getRotationVector();

                        //double spawnX = player.getX() + (look.x * 1.6);
                        //double spawnY = player.getEyeY() - 2 + (look.y * 1.2);
                        //double spawnZ = player.getZ() + (look.z * 1.6);

                        double spawnX = player.getX();
                        double spawnY = player.getEyeY();
                        double spawnZ = player.getZ();

                        projectile.setPosition(spawnX, spawnY, spawnZ);

                        //projectile.setPosition(player.getPos() + player.getHeadYaw());

                        projectile.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, 3.0F, 0.0F);

                        world.spawnEntity(projectile);

                        if (!isCreative) {
                            player.addExperience(-1);
                        }
                    }
                }
            }
        }
    }
}