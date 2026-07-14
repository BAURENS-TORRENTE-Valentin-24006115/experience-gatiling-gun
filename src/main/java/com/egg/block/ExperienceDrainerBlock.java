package com.egg.block;

import com.egg.block.entity.ExperienceDrainerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import com.egg.block.util.CellPowerUtil;

public class ExperienceDrainerBlock extends Block implements BlockEntityProvider {
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
        //if (world.isClient()) return ActionResult.SUCCESS;

        if (!(world.getBlockEntity(pos) instanceof ExperienceDrainerBlockEntity blockEntity)) {
            return ActionResult.PASS;
        }

        ItemStack heldItem = player.getMainHandStack();

        if (player.isSneaking()) {
            return CellPowerUtil.interact(heldItem, player, blockEntity);
        } else {
            player.openHandledScreen(blockEntity);
            return ActionResult.SUCCESS;
        }
    }
}