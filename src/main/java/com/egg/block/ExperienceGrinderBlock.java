package com.egg.block;

import com.egg.item.ModItems;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ExperienceGrinderBlock extends Block implements BlockEntityProvider {

    public static final MapCodec<ExperienceGrinderBlock> CODEC = createCodec(ExperienceGrinderBlock::new);

    public static int GRIND_TICK_MAX_DELAY = 5;
    public static int grindTickDelay = 0;
    public static int GRINDER_DAMAGE = 2;

    public ExperienceGrinderBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        super.onSteppedOn(world, pos, state, entity);

        if (world.isClient()) return;

        if (grindTickDelay < GRIND_TICK_MAX_DELAY) {
            grindTickDelay++;
            return;
        }

        if (entity instanceof LivingEntity living) {
            float currentHealth = living.getHealth();
            if (currentHealth > 1.0f) {
                living.setHealth(Math.max(1.0f, currentHealth - GRINDER_DAMAGE));
            } else {
                int amount = 0;
                if (living instanceof PlayerEntity player) {
                    amount = player.totalExperience;
                    player.addExperience(-amount);
                }

                long remaining = fillAvailableStorageBlocks(world, pos, amount);
                if (remaining > 0) {
                    dropOrbeez(world, pos, remaining);
                }
                living.kill();
            }
            grindTickDelay = 0;
        }
    }

    //public Storage<ItemVariant> checkForStorageBlock(World world, BlockPos pos) {
    //    ItemVariant orbeezVariant = ItemVariant.of(ModItems.EXPERIENCE_ORBEEZ);
    //
    //    for (Direction dir : Direction.values()) {
    //        BlockPos neighborPos = pos.offset(dir);
    //        Storage<ItemVariant> storage = ItemStorage.SIDED.find(world, neighborPos, dir.getOpposite());
    //        if (storage == null) continue;
    //
    //        long freeSpace = StorageUtil.simulateInsert(storage, orbeezVariant, Long.MAX_VALUE, null);
    //        if (freeSpace > 0) {
    //            return storage;
    //        }
    //    }
    //    return null;
    //}

    public long fillAvailableStorageBlocks(World world, BlockPos pos, long amount) {
        ItemVariant orbeezVariant = ItemVariant.of(ModItems.EXPERIENCE_ORBEEZ);
        long remaining = amount;

        for (Direction dir : Direction.values()) {
            if (remaining <= 0) break;

            BlockPos neighborPos = pos.offset(dir);
            Storage<ItemVariant> storage = ItemStorage.SIDED.find(world, neighborPos, dir.getOpposite());
            if (storage == null) continue;

            long freeSpace = StorageUtil.simulateInsert(storage, orbeezVariant, remaining, null);
            if (freeSpace <= 0) continue;

            remaining -= fillStorageBlock(storage, remaining);
        }

        return remaining;
    }

    public long fillStorageBlock(Storage<ItemVariant> storage, long amount) {
        if (amount <= 0) return 0;

        ItemVariant orbeezVariant = ItemVariant.of(ModItems.EXPERIENCE_ORBEEZ);
        long inserted;
        try (Transaction transaction = Transaction.openOuter()) {
            inserted = storage.insert(orbeezVariant, amount, transaction);
            transaction.commit();
        }
        return inserted;
    }

    private void dropOrbeez(World world, BlockPos pos, long amount) {
        int maxStack = ModItems.EXPERIENCE_ORBEEZ.getDefaultStack().getMaxCount();

        while (amount > 0) {
            int dropCount = (int) Math.min(amount, maxStack);
            Block.dropStack(world, pos, new ItemStack(ModItems.EXPERIENCE_ORBEEZ, dropCount));
            amount -= dropCount;
        }
    }
}