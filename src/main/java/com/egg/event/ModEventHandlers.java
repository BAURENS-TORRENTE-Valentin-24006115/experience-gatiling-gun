package com.egg.item;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;

public class ModEventHandlers {

    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack held = player.getStackInHand(hand);
            Hand otherHand = (hand == Hand.MAIN_HAND) ? Hand.OFF_HAND : Hand.MAIN_HAND;
            ItemStack other = player.getStackInHand(otherHand);

            boolean heldIsXpSource = held.getItem() instanceof ExperienceOrbeezItem
                    || held.isOf(Items.EXPERIENCE_BOTTLE);
            boolean otherIsCellule = other.getItem() instanceof ExperienceCelluleItem;

            if (heldIsXpSource && otherIsCellule) {
                if (ExperienceCelluleItem.tryFill(other, held, player, world)) {
                    return TypedActionResult.success(held);
                }
            }

            return TypedActionResult.pass(held); // laisse le comportement vanilla/normal s'exécuter sinon
        });
    }
}