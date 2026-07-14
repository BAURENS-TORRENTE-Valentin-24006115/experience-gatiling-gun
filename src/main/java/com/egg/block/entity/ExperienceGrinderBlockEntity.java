package com.egg.block.entity;

import com.egg.block.ModBlockEntities;
import com.egg.block.util.XpCellSource;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public class ExperienceGrinderBlockEntity extends BlockEntity implements XpCellSource {

    private final int[] cellXp = new int[2];
    private int grindTickDelay = 0;
    private int xpConsumeAccumulator = 0;

    public ExperienceGrinderBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EXPERIENCE_GRINDER, pos, state);
    }

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
}