package com.egg.block;

import com.egg.block.entity.ExperienceGrinderBlockEntity;
import com.egg.block.util.CellPowerUtil;
import com.egg.item.ModItems;
import com.egg.util.ModDamageTypes;
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
import net.minecraft.entity.damage.DamageSource;
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

        return CellPowerUtil.interact(heldItem, player, blockEntity);
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
        if (block.isActive(blockEntity, world, pos)) return;

        boolean hasEntity = block.hasLivingEntityOnTop(world, pos);
        int costPerTick = hasEntity ? XP_COST_WITH_ENTITY : XP_COST_NO_ENTITY;

        blockEntity.addXpConsumeAccumulator(costPerTick);

        while (blockEntity.getXpConsumeAccumulator() >= XP_TICK_INTERVAL) {
            blockEntity.decrementXpConsumeAccumulator(XP_TICK_INTERVAL);
            if (!CellPowerUtil.consumeXp(blockEntity, 1)) {
                blockEntity.resetXpConsumeAccumulator();
                break;
            }
        }
    }

    private boolean hasLivingEntityOnTop(World world, BlockPos pos) {
        Box box = new Box(pos).stretch(0, 1, 0);
        return !world.getEntitiesByClass(LivingEntity.class, box, entity -> true).isEmpty();
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        super.onSteppedOn(world, pos, state, entity);

        if (world.isClient()) return;
        if (!(world.getBlockEntity(pos) instanceof ExperienceGrinderBlockEntity blockEntity)) return;
        if (!(entity instanceof LivingEntity living)) return;
        if (isActive(blockEntity, world, pos)) return;

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
            living.damage(world.getDamageSources().create(ModDamageTypes.EXPERIENCE_GRINDER), 100000);
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
        if (xpAvailable(blockEntity) == 0) return true;
        return !world.isReceivingRedstonePower(pos);
    }

    public int xpAvailable(ExperienceGrinderBlockEntity blockEntity) {
        return CellPowerUtil.getTotalXp(blockEntity);
    }
}