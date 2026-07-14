package com.egg.screen;

import com.egg.block.entity.ExperienceDrainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class ExperienceDrainerScreenHandler extends ScreenHandler {

    private final Inventory blockInventory;

    public ExperienceDrainerScreenHandler(int syncId, PlayerInventory playerInv) {
        this(syncId, playerInv, new SimpleInventory(18));
    }

    public ExperienceDrainerScreenHandler(int syncId, PlayerInventory playerInv, Inventory blockInventory) {
        super(ModScreenHandlers.EXPERIENCE_DRAINER, syncId);
        checkSize(blockInventory, 18);
        this.blockInventory = blockInventory;
        blockInventory.onOpen(playerInv.player);

        // 5 slots de traitement
        int[] procX = {44, 62, 80, 98, 116};
        for (int i = 0; i < 5; i++) {
            this.addSlot(new Slot(blockInventory, ExperienceDrainerBlockEntity.PROCESSING_START + i, procX[i], 20));
        }

        // slot d'extraction (verrouillé)
        this.addSlot(new Slot(blockInventory, ExperienceDrainerBlockEntity.EXTRACTION_SLOT, 80, 52) {
            @Override
            public boolean canTakeItems(PlayerEntity player) {
                return false;
            }
        });

        // 12 slots de stockage (6x2)
        int[] storX = {35, 53, 71, 89, 107, 125};
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 6; col++) {
                int index = ExperienceDrainerBlockEntity.STORAGE_START + row * 6 + col;
                this.addSlot(new Slot(blockInventory, index, storX[col], 83 + row * 18));
            }
        }

        // Inventaire joueur (3x9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 132 + row * 18));
            }
        }
        // Hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 190));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return blockInventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        // logique de shift-click à implémenter selon vos règles
        // (ex: un item xp-source doit aller vers les slots de traitement, pas le stockage)
        return ItemStack.EMPTY;
    }
}