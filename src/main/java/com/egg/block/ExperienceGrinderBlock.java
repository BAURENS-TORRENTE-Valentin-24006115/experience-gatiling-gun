package com.egg.block;

import com.egg.block.entity.ExperienceGrinderBlockEntity;
import com.egg.item.ExperienceCelluleItem;
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
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ExperienceGrinderBlock extends Block implements BlockEntityProvider {

    public static final MapCodec<ExperienceGrinderBlock> CODEC = createCodec(ExperienceGrinderBlock::new);

    public static final int GRIND_TICK_MAX_DELAY = 5;
    public static final int GRINDER_DAMAGE = 2;

    public static final int XP_TICK_INTERVAL = 120;
    public static final int XP_COST_NO_ENTITY = 1;
    public static final int XP_COST_WITH_ENTITY = 5;

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
        return new ExperienceGrinderBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) return ActionResult.SUCCESS;

        if (!(world.getBlockEntity(pos) instanceof ExperienceGrinderBlockEntity blockEntity)) {
            return ActionResult.PASS;
        }

        ItemStack heldItem = player.getMainHandStack();

        if (heldItem.isOf(ModItems.EXPERIENCE_CELLULE)) {
            int storedXp = ExperienceCelluleItem.getStoredXp(heldItem);
            if (insertCell(blockEntity, storedXp)) {
                if (!player.isCreative()) {
                    heldItem.decrement(1);
                }
            }
            return ActionResult.SUCCESS;
        } else {
            return removeCell(blockEntity, player) ? ActionResult.SUCCESS : ActionResult.PASS;
        }
    }

    public boolean insertCell(ExperienceGrinderBlockEntity blockEntity, int storedXp) {
        if (!blockEntity.isCell1Inserted()) {
            blockEntity.setCell1(true, storedXp);
            return true;
        } else if (!blockEntity.isCell2Inserted()) {
            blockEntity.setCell2(true, storedXp);
            return true;
        }
        return false;
    }

    public boolean removeCell(ExperienceGrinderBlockEntity blockEntity, PlayerEntity player) {
        if (blockEntity.isCell1Inserted()) {
            ItemStack cellule = new ItemStack(ModItems.EXPERIENCE_CELLULE);
            ExperienceCelluleItem.setStoredXp(cellule, blockEntity.getCell1XpAmount());
            player.giveItemStack(cellule);
            blockEntity.setCell1(false, 0);
            return true;
        } else if (blockEntity.isCell2Inserted()) {
            ItemStack cellule = new ItemStack(ModItems.EXPERIENCE_CELLULE);
            ExperienceCelluleItem.setStoredXp(cellule, blockEntity.getCell2XpAmount());
            player.giveItemStack(cellule);
            blockEntity.setCell2(false, 0);
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient()) return null;
        if (type != ModBlockEntities.EXPERIENCE_GRINDER) return null;

        return (world1, pos, state1, blockEntity) -> tick(world1, pos, state1, (ExperienceGrinderBlockEntity) blockEntity);
    }

    public static void tick(World world, BlockPos pos, BlockState state, ExperienceGrinderBlockEntity blockEntity) {
        if (!(state.getBlock() instanceof ExperienceGrinderBlock block)) return;
        if (!block.isActive(blockEntity, world, pos)) return;

        if (blockEntity.getXpConsumeTickDelay() < XP_TICK_INTERVAL) {
            blockEntity.incrementXpConsumeTickDelay();
            return;
        }

        boolean hasEntity = block.hasLivingEntityOnTop(world, pos);
        int cost = hasEntity ? XP_COST_WITH_ENTITY : XP_COST_NO_ENTITY;

        block.consumeXp(blockEntity, cost);
        blockEntity.resetXpConsumeTickDelay();
    }

    private boolean hasLivingEntityOnTop(World world, BlockPos pos) {
        Box box = new Box(pos).stretch(0, 1, 0);
        return !world.getEntitiesByClass(LivingEntity.class, box, entity -> true).isEmpty();
    }

    public void consumeXp(ExperienceGrinderBlockEntity blockEntity, int cost) {
        int cell1 = blockEntity.getCell1XpAmount();
        int cell2 = blockEntity.getCell2XpAmount();
        int totalAvailable = cell1 + cell2;

        if (totalAvailable < cost) {
            return;
        }

        int remaining = cost;

        int takenFromCell1 = Math.min(cell1, remaining);
        cell1 -= takenFromCell1;
        remaining -= takenFromCell1;

        if (remaining > 0) {
            int takenFromCell2 = Math.min(cell2, remaining);
            cell2 -= takenFromCell2;
            remaining -= takenFromCell2;
        }

        blockEntity.setCell1(cell1 > 0, cell1);
        blockEntity.setCell2(cell2 > 0, cell2);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        super.onSteppedOn(world, pos, state, entity);

        if (world.isClient()) return;
        if (!(world.getBlockEntity(pos) instanceof ExperienceGrinderBlockEntity blockEntity)) return;
        if (!(entity instanceof LivingEntity living)) return;
        if (!isActive(blockEntity, world, pos)) return;

        if (blockEntity.getGrindTickDelay() < GRIND_TICK_MAX_DELAY) {
            blockEntity.incrementGrindTickDelay();
            return;
        }

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
        blockEntity.resetGrindTickDelay();
    }

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

    public boolean isActive(ExperienceGrinderBlockEntity blockEntity, World world, BlockPos pos) {
        if (xpAvailable(blockEntity) == 0) return false;
        return world.isReceivingRedstonePower(pos);
    }

    public int xpAvailable(ExperienceGrinderBlockEntity blockEntity) {
        return blockEntity.getCell1XpAmount() + blockEntity.getCell2XpAmount();
    }
}