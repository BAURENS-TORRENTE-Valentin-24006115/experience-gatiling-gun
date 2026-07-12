package com.egg;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class ExperienceGatlingGunItem extends Item {

    public ExperienceGatlingGunItem(Properties properties) {
        super(properties);
    }

    // 1. Tell the game this item can be "held down" for a long time (like a bow)
    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000; // This is the standard max duration (about 1 hour of holding)
    }

    // 2. When the player clicks, start the "using" action immediately
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    // 3. This runs every single tick (20 times a second) while the button is held!
    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int count) {
        if (livingEntity instanceof Player player) {

            // Fire rate: 10 shots per second
            if (count % 2 == 0) {
                boolean isCreative = player.getAbilities().instabuild;

                if (isCreative || player.totalExperience > 0) {

                    // Play the standard arrow shooting sound
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
                            1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);

                    if (!level.isClientSide()) {
                        ExperienceAmmoEntity projectile = new ExperienceAmmoEntity(level, player);

                        // Get the player's look vector
                        net.minecraft.world.phys.Vec3 look = player.getLookAngle();

                        // Spawn it 1.2 blocks in front of the player and 0.2 blocks below eye level
                        // This clears your own hitbox entirely, even when looking straight up!
                        double spawnX = player.getX() + (look.x * 1.6);
                        double spawnY = player.getEyeY() - 2 + (look.y * 1.2);
                        double spawnZ = player.getZ() + (look.z * 1.6);

                        projectile.setPos(spawnX, spawnY, spawnZ);

                        // Now shoot it
                        projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.0F, 0.0F);

                        level.addFreshEntity(projectile);

                        if (!isCreative) {
                            player.giveExperiencePoints(-1);
                        }
                    }
                }
            }
        }
    }
}