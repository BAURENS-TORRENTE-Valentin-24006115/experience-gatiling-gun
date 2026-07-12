package com.egg.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ExperienceOrbeezItem extends Item {
    public ExperienceOrbeezItem(Item.Settings settings) {
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

            if (player.isCreative() || !player.getInventory().isEmpty()) {
                if (player.isSneaking()) {
                    player.addExperience(stack.getCount());
                    for (int i = 0; i <= Math.min(10, stack.getCount()); i++) {
                        playXpSound(world, player);
                    }
                    if (!player.isCreative()) {
                        stack.decrement(stack.getCount());
                        if (stack.isEmpty()) {
                            player.stopUsingItem();
                        }
                    }
                }

                else {
                    player.addExperience(1);

                    if (count % 2 == 0) {
                        playXpSound(world, player);
                    }

                    if (!player.isCreative()) {
                        stack.decrement(1);
                        if (stack.isEmpty()) {
                            player.stopUsingItem();
                        }
                    }
                }
            }

        }
    }

    public static void playXpSound(World world, PlayerEntity player){
        float pitch = 0.5F * ((world.random.nextFloat() - world.random.nextFloat()) * 0.7F + 1.8F);
        world.playSound(null, player.getBlockPos(),
                SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                SoundCategory.PLAYERS, 0.1F, pitch);
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        ItemStack slotStack = slot.getStack();
        if (slotStack.getItem() instanceof ExperienceCelluleItem) {
            ItemStack singleOrbeez = stack.copy();
            singleOrbeez.setCount(1);
            boolean filled = ExperienceCelluleItem.tryFill(slotStack, singleOrbeez, player, player.getWorld());
            if (filled && !player.isCreative()) {
                stack.decrement(1);
            }
            return filled;
        }
        return false;
    }
}