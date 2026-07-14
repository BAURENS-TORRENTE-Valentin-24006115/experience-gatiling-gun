package com.egg.block.util;

import com.egg.item.ExperienceCelluleItem;
import com.egg.item.ModItems;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.ActionResult;

public final class CellPowerUtil {

    private CellPowerUtil() {}

    public static int getTotalXp(XpCellSource source) {
        int total = 0;
        for (int i = 0; i < source.getCellCount(); i++) {
            total += source.getXp(i);
        }
        return total;
    }

    public static boolean consumeXp(XpCellSource source, int cost) {
        if (getTotalXp(source) < cost) return false;

        int remaining = cost;
        for (int i = 0; i < source.getCellCount() && remaining > 0; i++) {
            int xp = source.getXp(i);
            int taken = Math.min(xp, remaining);
            source.setXp(i, xp - taken);
            remaining -= taken;
        }
        return true;
    }

    public static boolean insertCell(XpCellSource source, int xp) {
        for (int i = 0; i < source.getCellCount(); i++) {
            if (source.getXp(i) <= 0) {
                source.setXp(i, xp);
                return true;
            }
        }
        return false;
    }

    public static boolean removeCell(BlockEntity blockEntity, PlayerEntity player) {
        int xp = removeFirstCell((XpCellSource) blockEntity);
        if (xp < 0) return false;
        ItemStack cellule = new ItemStack(ModItems.EXPERIENCE_CELLULE);
        ExperienceCelluleItem.setStoredXp(cellule, xp);
        player.giveItemStack(cellule);
        return true;
    }

    public static int removeFirstCell(XpCellSource source) {
        for (int i = 0; i < source.getCellCount(); i++) {
            int xp = source.getXp(i);
            if (xp > 0) {
                source.setXp(i, 0);
                return xp;
            }
        }
        return -1;
    }

    public static ActionResult interact(ItemStack heldItem, PlayerEntity player, BlockEntity blockEntity) {
        if (heldItem.isOf(ModItems.EXPERIENCE_CELLULE)) {
            int storedXp = ExperienceCelluleItem.getStoredXp(heldItem);
            if (CellPowerUtil.insertCell((XpCellSource) blockEntity, storedXp)) {
                if (!player.isCreative()) {
                    heldItem.decrement(1);
                }
            }
            return ActionResult.SUCCESS;
        } else {
            return CellPowerUtil.removeCell(blockEntity, player) ? ActionResult.SUCCESS : ActionResult.PASS;
        }
    }
}