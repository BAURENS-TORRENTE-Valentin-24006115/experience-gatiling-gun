package com.egg.block.entity;

import com.egg.block.ModBlockEntities;
import com.egg.block.util.XpCellSource;
import com.egg.screen.ExperienceDrainerScreenHandler;
import com.egg.util.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ExperienceDrainerBlockEntity extends BlockEntity implements ImplementedInventory, NamedScreenHandlerFactory, XpCellSource {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(18, ItemStack.EMPTY);

    public static final int PROCESSING_START = 0;
    public static final int EXTRACTION_SLOT = 5;
    public static final int STORAGE_START = 6;

    public ExperienceDrainerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EXPERIENCE_DRAINER, pos, state);
    }

    private final int[] cellXp = new int[2];
    private int grindTickDelay = 0;
    private int xpConsumeAccumulator = 0;

    @Override
    public int getCellCount() { return cellXp.length; }

    @Override
    public int getXp(int index) { return cellXp[index]; }

    @Override
    public void setXp(int index, int xp) {
        cellXp[index] = Math.max(0, xp);
        markDirty();
    }

    // TODO link this to the block cell texture
    public boolean isCellInserted(int index) { return cellXp[index] > 0; }

    public int getGrindTickDelay() { return grindTickDelay; }
    public void incrementGrindTickDelay() { grindTickDelay++; markDirty(); }
    public void resetGrindTickDelay() { grindTickDelay = 0; markDirty(); }

    public int getXpConsumeAccumulator() { return xpConsumeAccumulator; }
    public void addXpConsumeAccumulator(int amount) { xpConsumeAccumulator += amount; markDirty(); }
    public void decrementXpConsumeAccumulator(int amount) { xpConsumeAccumulator -= amount; markDirty(); }
    public void resetXpConsumeAccumulator() { xpConsumeAccumulator = 0; markDirty(); }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putIntArray("cellXp", cellXp);
        nbt.putInt("grindTickDelay", grindTickDelay);
        nbt.putInt("xpConsumeAccumulator", xpConsumeAccumulator);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        int[] saved = nbt.getIntArray("cellXp");
        for (int i = 0; i < cellXp.length && i < saved.length; i++) cellXp[i] = saved[i];
        grindTickDelay = nbt.getInt("grindTickDelay");
        xpConsumeAccumulator = nbt.getInt("xpConsumeAccumulator");
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