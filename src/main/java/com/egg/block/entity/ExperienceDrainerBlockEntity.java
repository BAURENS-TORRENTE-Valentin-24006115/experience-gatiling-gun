package com.egg.block.entity;

import com.egg.block.ModBlockEntities;
import com.egg.block.util.XpCellSource;
import com.egg.item.ExperienceCelluleItem;
import com.egg.item.ModItems;
import com.egg.screen.ExperienceDrainerScreenHandler;
import com.egg.util.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class ExperienceDrainerBlockEntity extends BlockEntity implements ImplementedInventory, SidedInventory, NamedScreenHandlerFactory, XpCellSource {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(18, ItemStack.EMPTY);

    public static final int PROCESSING_START = 0;
    public static final int PROCESSING_COUNT = 5;
    public static final int EXTRACTION_SLOT = 5;
    public static final int STORAGE_START = 6;
    public static final int STORAGE_COUNT = 12;

    public ExperienceDrainerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EXPERIENCE_DRAINER, pos, state);
    }

    private final int[] cellXp = new int[5];
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

    public int getXpConsumeAccumulator() { return xpConsumeAccumulator; }
    public void addXpConsumeAccumulator(int amount) { xpConsumeAccumulator += amount; markDirty(); }
    public void decrementXpConsumeAccumulator(int amount) { xpConsumeAccumulator -= amount; markDirty(); }
    public void resetXpConsumeAccumulator() { xpConsumeAccumulator = 0; markDirty(); }

    public boolean hasActiveExtraction() {
        ItemStack extraction = inventory.get(EXTRACTION_SLOT);
        return !extraction.isEmpty(); // && ExperienceCelluleItem.getStoredXp(extraction) > 0;
    }

    public boolean hasWorkToDo() {
        if (hasActiveExtraction()) return true;
        if (!inventory.get(EXTRACTION_SLOT).isEmpty()) return false;
        return findNextProcessingSlot() >= 0;
    }

    private int findNextProcessingSlot() {
        for (int i = PROCESSING_START; i < PROCESSING_START + PROCESSING_COUNT; i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty() && stack.isOf(ModItems.EXPERIENCE_CELLULE) && ExperienceCelluleItem.getStoredXp(stack) > 0) {
                return i;
            }
        }
        return -1;
    }

    private boolean pullItemIntoExtractionSlot() {
        if (!inventory.get(EXTRACTION_SLOT).isEmpty()) return false;

        int slot = findNextProcessingSlot();
        if (slot < 0) return false;

        ItemStack source = inventory.get(slot);
        ItemStack single = source.copyWithCount(1);
        source.decrement(1);
        if (source.isEmpty()) {
            inventory.set(slot, ItemStack.EMPTY);
        }
        inventory.set(EXTRACTION_SLOT, single);
        markDirty();
        return true;
    }

    private boolean insertOrbeezIntoStorage() {
        ItemStack orbeezSingle = new ItemStack(ModItems.EXPERIENCE_ORBEEZ, 1);

        for (int i = STORAGE_START; i < STORAGE_START + STORAGE_COUNT; i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty() && stack.isOf(ModItems.EXPERIENCE_ORBEEZ) && stack.getCount() < stack.getMaxCount()) {
                stack.increment(1);
                markDirty();
                return true;
            }
        }
        for (int i = STORAGE_START; i < STORAGE_START + STORAGE_COUNT; i++) {
            if (inventory.get(i).isEmpty()) {
                inventory.set(i, orbeezSingle);
                markDirty();
                return true;
            }
        }
        return false;
    }

    private boolean insertCelluleIntoStorage(ItemStack cellStack) {
        for (int i = STORAGE_START; i < STORAGE_START + STORAGE_COUNT; i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()
                    && ItemStack.areItemsAndComponentsEqual(stack, cellStack)
                    && stack.getCount() < stack.getMaxCount()) {
                stack.increment(cellStack.getCount());
                markDirty();
                return true;
            }
        }
        for (int i = STORAGE_START; i < STORAGE_START + STORAGE_COUNT; i++) {
            if (inventory.get(i).isEmpty()) {
                inventory.set(i, cellStack);
                markDirty();
                return true;
            }
        }
        return false;
    }

    public void tryExtractOneOrbeez() {
        ItemStack extraction = inventory.get(EXTRACTION_SLOT);

        // Cellule déjà vidée mais pas encore casée en storage (place manquante
        // au tick précédent) : on ne fait que retenter le dépôt, sans reprendre
        // l'extraction d'xp tant qu'elle n'a pas trouvé de place.
        if (!extraction.isEmpty() && ExperienceCelluleItem.getStoredXp(extraction) <= 0) {
            if (insertCelluleIntoStorage(extraction)) {
                inventory.set(EXTRACTION_SLOT, ItemStack.EMPTY);
                markDirty();
            }
            return;
        }

        if (extraction.isEmpty()) {
            if (!pullItemIntoExtractionSlot()) return;
            extraction = inventory.get(EXTRACTION_SLOT);
        }

        int storedXp = ExperienceCelluleItem.getStoredXp(extraction);
        if (storedXp <= 0) return; // ne devrait plus jamais arriver ici désormais

        if (!insertOrbeezIntoStorage()) {
            return; // pas de place pour l'orbeez non plus : on retente au prochain tick
        }

        int remainingXp = storedXp - 1;
        ExperienceCelluleItem.setStoredXp(extraction, remainingXp);
        if (remainingXp <= 0) {
            if (insertCelluleIntoStorage(extraction)) {
                inventory.set(EXTRACTION_SLOT, ItemStack.EMPTY);
            }
            // sinon : reste dans le slot, sera retentée au prochain tick par la
            // branche du haut, sans jamais être supprimée silencieusement.
        }
        markDirty();
    }

    public boolean canRemoveFromSlot(int slot) {
        if (slot != EXTRACTION_SLOT) return true;
        return !hasActiveExtraction();
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putIntArray("cellXp", cellXp);
        nbt.putInt("grindTickDelay", grindTickDelay);
        nbt.putInt("xpConsumeAccumulator", xpConsumeAccumulator);
        Inventories.writeNbt(nbt, inventory, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        int[] saved = nbt.getIntArray("cellXp");
        for (int i = 0; i < cellXp.length && i < saved.length; i++) cellXp[i] = saved[i];
        grindTickDelay = nbt.getInt("grindTickDelay");
        xpConsumeAccumulator = nbt.getInt("xpConsumeAccumulator");
        inventory.clear();
        Inventories.readNbt(nbt, inventory, registryLookup);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    private static final int[] PROCESSING_SLOTS = buildRange(PROCESSING_START, PROCESSING_COUNT);
    private static final int[] STORAGE_SLOTS = buildRange(STORAGE_START, STORAGE_COUNT);

    private static int[] buildRange(int start, int count) {
        int[] slots = new int[count];
        for (int i = 0; i < count; i++) slots[i] = start + i;
        return slots;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.UP) return PROCESSING_SLOTS;
        if (side == Direction.DOWN) return STORAGE_SLOTS;
        return new int[0];
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot >= PROCESSING_START && slot < PROCESSING_START + PROCESSING_COUNT
                && stack.isOf(ModItems.EXPERIENCE_CELLULE);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot >= STORAGE_START && slot < STORAGE_START + STORAGE_COUNT;
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