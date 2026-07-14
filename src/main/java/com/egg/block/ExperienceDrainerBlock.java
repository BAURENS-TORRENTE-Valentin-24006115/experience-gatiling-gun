package com.egg.block;

import com.egg.block.entity.ExperienceDrainerBlockEntity;
import com.egg.block.util.CellPowerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ExperienceDrainerBlock extends Block implements BlockEntityProvider {

    public static final int STANDBY_XP_COST = 1;
    public static final int ACTIVE_XP_COST = 10;
    public static final int XP_TICK_INTERVAL = 600;

    public ExperienceDrainerBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ExperienceDrainerBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) return ActionResult.SUCCESS;

        if (!(world.getBlockEntity(pos) instanceof ExperienceDrainerBlockEntity blockEntity)) {
            return ActionResult.PASS;
        }

        ItemStack heldItem = player.getMainHandStack();

        if (heldItem.isOf(com.egg.item.ModItems.EXPERIENCE_CELLULE)) {
            return CellPowerUtil.interact(heldItem, player, blockEntity);
        }

        if (player.isSneaking()) {
            return CellPowerUtil.interact(heldItem, player, blockEntity);
        }

        player.openHandledScreen(blockEntity);
        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient()) return null;
        if (type != ModBlockEntities.EXPERIENCE_DRAINER) return null;

        return (world1, pos, state1, blockEntity) -> tick(world1, pos, state1, (ExperienceDrainerBlockEntity) blockEntity);
    }

    public static void tick(World world, BlockPos pos, BlockState state, ExperienceDrainerBlockEntity blockEntity) {
        boolean redstonePowered = world.isReceivingRedstonePower(pos);
        boolean hasCellPower = CellPowerUtil.getTotalXp(blockEntity) > 0;

        if (!redstonePowered || !hasCellPower) return;

        boolean active = blockEntity.hasWorkToDo();
        int costPerTick = active ? ACTIVE_XP_COST : STANDBY_XP_COST;

        blockEntity.addXpConsumeAccumulator(costPerTick);
        while (blockEntity.getXpConsumeAccumulator() >= XP_TICK_INTERVAL) {
            blockEntity.decrementXpConsumeAccumulator(XP_TICK_INTERVAL);
            if (!CellPowerUtil.consumeXp(blockEntity, 1)) {
                blockEntity.resetXpConsumeAccumulator();
                return;
            }
        }

        if (active) {
            blockEntity.tryExtractOneOrbeez();
        }
    }
}