package com.egg.item;

import com.egg.ExperienceGlobalData;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.world.World;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;

import java.util.List;

public class ExperienceCelluleItem extends Item{

    public static final int MAX_XP = 100;

    public ExperienceCelluleItem(Item.Settings settings) {
        super(settings);
    }

    public static int getStoredXp(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.STORED_XP, 0);
    }

    public static int getCharge(ItemStack stack) {
        int xp = getStoredXp(stack);
        if (xp <= 0) return 0;
        if (xp <= 33) return 1;
        if (xp <= 66) return 2;
        if (xp <= 99) return 3;
        return 4;
    }

    public static void setStoredXp(ItemStack stack, int amount) {
        int clamped = Math.max(0, Math.min(MAX_XP, amount));
        stack.set(ModDataComponents.STORED_XP, clamped);

        int charge = getCharge(stack);
        stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(charge));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack cellule = player.getStackInHand(hand);

        if (player.getItemCooldownManager().isCoolingDown(cellule.getItem())) {
            return TypedActionResult.pass(cellule);
        }

        Hand otherHand = (hand == Hand.MAIN_HAND) ? Hand.OFF_HAND : Hand.MAIN_HAND;
        ItemStack other = player.getStackInHand(otherHand);

        if (world.isClient()) {
            return TypedActionResult.success(cellule);
        }

        if (tryFill(cellule, other, player, world)) {
            player.getItemCooldownManager().set(cellule.getItem(), 5);
            return TypedActionResult.success(cellule);
        }
        return TypedActionResult.pass(cellule);
    }

    public static boolean tryFill(ItemStack celluleStack, ItemStack source, PlayerEntity player, World world) {
        if (celluleStack.isEmpty()) return false;

        int currentXp = getStoredXp(celluleStack);
        if (currentXp >= MAX_XP) return false;

        int spaceLeft = MAX_XP - currentXp;
        if (spaceLeft <= 0) return false;

        int newXp = currentXp + Math.min(ExperienceGlobalData.getXpValue(world, source), spaceLeft);

        if (celluleStack.getCount() == 1) {
            setStoredXp(celluleStack, newXp);
        } else {
            ItemStack singleCellule = celluleStack.split(1);
            setStoredXp(singleCellule, newXp);

            if (!player.getInventory().insertStack(singleCellule)) {
                player.dropItem(singleCellule, false);
            }
        }

        if (!player.isCreative()) {
            source.decrement(1);
        }
        if (!world.isClient()) {
            ExperienceGlobalData.playXpSound(world, player);
        }
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal(getStoredXp(stack) + " / " + MAX_XP + " XP"));
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType,
                             PlayerEntity player, StackReference cursorStackReference) {

        boolean otherIsXpSource = otherStack.getItem() instanceof ExperienceOrbeezItem
                || otherStack.isOf(Items.EXPERIENCE_BOTTLE);

        if (otherIsXpSource && clickType == ClickType.RIGHT) {
            ItemStack single = otherStack.copy();
            single.setCount(1);

            if (tryFill(stack, single, player, player.getWorld())) {
                if (!player.isCreative()) {
                    otherStack.decrement(1);
                    cursorStackReference.set(otherStack);
                }
                return true;
            }
        }

        return false;
    }
}
