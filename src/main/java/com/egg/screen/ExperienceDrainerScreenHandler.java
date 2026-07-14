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

    // Constructeur client (appelé automatiquement lors de l'ouverture réseau)
    public ExperienceDrainerScreenHandler(int syncId, PlayerInventory playerInv) {
        this(syncId, playerInv, new SimpleInventory(23));
    }

    // Constructeur serveur (appelé depuis createMenu)
    public ExperienceDrainerScreenHandler(int syncId, PlayerInventory playerInv, Inventory blockInventory) {
        super(ModScreenHandlers.EXPERIENCE_DRAINER, syncId);
        checkSize(blockInventory, 23);
        this.blockInventory = blockInventory;
        blockInventory.onOpen(playerInv.player);

        // 5 slots de traitement
        for (int i = 0; i < 5; i++) {
            this.addSlot(new Slot(blockInventory, ExperienceDrainerBlockEntity.PROCESSING_START + i, 17 + i * 18, 20));
        }

        // slot d'extraction : verrouillé, le joueur ne peut PAS le retirer manuellement
        this.addSlot(new Slot(blockInventory, ExperienceDrainerBlockEntity.EXTRACTION_SLOT, 80, 50) {
            @Override
            public boolean canTakeItems(PlayerEntity player) {
                return false; // empêche le retrait manuel, cohérent avec votre spec
            }
        });

        // 12 slots de stockage (grille 6x2 par exemple)
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 6; col++) {
                int index = ExperienceDrainerBlockEntity.STORAGE_START + row * 6 + col;
                this.addSlot(new Slot(blockInventory, index, 17 + col * 18, 80 + row * 18));
            }
        }

        // 5 slots de cellule
        for (int i = 0; i < 5; i++) {
            this.addSlot(new Slot(blockInventory, ExperienceDrainerBlockEntity.CELLULE_START + i, 17 + i * 18, 120));
        }

        // Inventaire joueur (3x9 + hotbar), positions standards
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 160 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 218));
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