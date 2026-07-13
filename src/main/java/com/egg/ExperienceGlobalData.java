package com.egg;

import com.egg.item.ExperienceOrbeezItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class ExperienceGlobalData {

    public static void playXpSound(World world, PlayerEntity player){
        float pitch = 0.5F * ((world.random.nextFloat() - world.random.nextFloat()) * 0.7F + 1.8F);
        world.playSound(null, player.getBlockPos(),
                SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                SoundCategory.PLAYERS, 0.1F, pitch);
    }


    public static int getXpValue(World world, ItemStack source) {

        int xpPerUnit;
        if (source.getItem() instanceof ExperienceOrbeezItem) {
            xpPerUnit = 1;
        } else if (source.isOf(Items.EXPERIENCE_BOTTLE)) {
            xpPerUnit = 3 + world.random.nextInt(5) + world.random.nextInt(5);
        } else {
            xpPerUnit = 0;
        }
        return xpPerUnit;
    }
}
