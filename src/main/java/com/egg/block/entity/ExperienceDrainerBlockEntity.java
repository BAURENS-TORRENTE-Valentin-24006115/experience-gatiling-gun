package com.egg.block.entity;

import com.egg.block.ModBlockEntities;
import com.egg.screen.ExperienceDrainerScreenHandler;
import com.egg.util.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ExperienceDrainerBlockEntity extends BlockEntity implements ImplementedInventory, NamedScreenHandlerFactory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(23, ItemStack.EMPTY);

    public static final int PROCESSING_START = 0;
    public static final int EXTRACTION_SLOT = 5;
    public static final int STORAGE_START = 6;
    public static final int CELLULE_START = 18;

    public ExperienceDrainerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EXPERIENCE_DRAINER, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.egg.experience_drainer");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInv, PlayerEntity player) {
        return new ExperienceDrainerScreenHandler(syncId, playerInv, this);
    }
}