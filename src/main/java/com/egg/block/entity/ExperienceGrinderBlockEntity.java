package com.egg.block.entity;

import com.egg.block.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public class ExperienceGrinderBlockEntity extends BlockEntity {

    private boolean cell1Inserted = false;
    private boolean cell2Inserted = false;
    private int cell1XpAmount = 0;
    private int cell2XpAmount = 0;
    private int grindTickDelay = 0;

    public ExperienceGrinderBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EXPERIENCE_GRINDER, pos, state);
    }

    public boolean isCell1Inserted() { return cell1Inserted; }
    public boolean isCell2Inserted() { return cell2Inserted; }
    public int getCell1XpAmount() { return cell1XpAmount; }
    public int getCell2XpAmount() { return cell2XpAmount; }

    public void setCell1(boolean inserted, int xp) {
        cell1Inserted = inserted;
        cell1XpAmount = xp;
        markDirty();
    }

    public void setCell2(boolean inserted, int xp) {
        cell2Inserted = inserted;
        cell2XpAmount = xp;
        markDirty();
    }

    public int getGrindTickDelay() { return grindTickDelay; }

    public void incrementGrindTickDelay() {
        grindTickDelay++;
        markDirty();
    }

    public void resetGrindTickDelay() {
        grindTickDelay = 0;
        markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putBoolean("cell1Inserted", cell1Inserted);
        nbt.putBoolean("cell2Inserted", cell2Inserted);
        nbt.putInt("cell1XpAmount", cell1XpAmount);
        nbt.putInt("cell2XpAmount", cell2XpAmount);
        nbt.putInt("grindTickDelay", grindTickDelay);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        cell1Inserted = nbt.getBoolean("cell1Inserted");
        cell2Inserted = nbt.getBoolean("cell2Inserted");
        cell1XpAmount = nbt.getInt("cell1XpAmount");
        cell2XpAmount = nbt.getInt("cell2XpAmount");
        grindTickDelay = nbt.getInt("grindTickDelay");
    }
}