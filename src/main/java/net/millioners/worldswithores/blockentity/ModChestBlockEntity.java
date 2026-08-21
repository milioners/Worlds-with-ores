package net.millioners.worldswithores.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import net.millioners.worldswithores.menu.ModChestMenu;
import net.millioners.worldswithores.registry.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModChestBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SIZE = 33;
    private final ItemStackHandler items = new ItemStackHandler(SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
            }
        }
    };

    public ModChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHEST.get(), pos, state);
    }

    public ItemStackHandler getItems() {
        return this.items;
    }

    public int getComparatorSignal() {
        int filled = 0;
        int total = 0;
        for (int i = 0; i < SIZE; i++) {
            ItemStack stack = this.items.getStackInSlot(i);
            if (!stack.isEmpty()) {
                filled++;
                total += stack.getCount() * 14 / Math.max(1, stack.getMaxStackSize());
            }
        }
        if (filled == 0) {
            return 0;
        }
        return Math.min(15, 1 + total / SIZE);
    }

    public void sortContents() {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            ItemStack stack = this.items.getStackInSlot(i);
            if (!stack.isEmpty()) {
                stacks.add(stack.copy());
                this.items.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
        stacks.sort(Comparator
                .comparing((ItemStack s) -> Item.getId(s.getItem()))
                .thenComparing(s -> s.getHoverName().getString(), String.CASE_INSENSITIVE_ORDER)
                .thenComparingInt(ItemStack::getCount));
        List<ItemStack> merged = new ArrayList<>();
        for (ItemStack stack : stacks) {
            if (merged.isEmpty() || !ItemStack.isSameItemSameTags(merged.get(merged.size() - 1), stack)) {
                merged.add(stack);
                continue;
            }
            ItemStack last = merged.get(merged.size() - 1);
            int space = last.getMaxStackSize() - last.getCount();
            if (space <= 0) {
                merged.add(stack);
                continue;
            }
            int move = Math.min(space, stack.getCount());
            last.grow(move);
            stack.shrink(move);
            if (!stack.isEmpty()) {
                merged.add(stack);
            }
        }
        for (int i = 0; i < merged.size() && i < SIZE; i++) {
            this.items.setStackInSlot(i, merged.get(i));
        }
        setChanged();
    }

    public void depositMatching(Inventory playerInv) {
        for (int p = 0; p < playerInv.getContainerSize(); p++) {
            ItemStack playerStack = playerInv.getItem(p);
            if (playerStack.isEmpty() || !containsMatching(playerStack)) {
                continue;
            }
            ItemStack remaining = insertMatching(playerStack);
            playerInv.setItem(p, remaining);
        }
        setChanged();
    }

    private boolean containsMatching(ItemStack stack) {
        for (int i = 0; i < SIZE; i++) {
            ItemStack existing = this.items.getStackInSlot(i);
            if (!existing.isEmpty() && ItemStack.isSameItemSameTags(existing, stack)) {
                return true;
            }
        }
        return false;
    }

    private ItemStack insertMatching(ItemStack stack) {
        ItemStack remaining = stack.copy();
        for (int i = 0; i < SIZE && !remaining.isEmpty(); i++) {
            ItemStack existing = this.items.getStackInSlot(i);
            if (existing.isEmpty() || !ItemStack.isSameItemSameTags(existing, remaining)) {
                continue;
            }
            int space = existing.getMaxStackSize() - existing.getCount();
            if (space <= 0) {
                continue;
            }
            int move = Math.min(space, remaining.getCount());
            existing.grow(move);
            remaining.shrink(move);
            this.items.setStackInSlot(i, existing);
        }
        for (int i = 0; i < SIZE && !remaining.isEmpty(); i++) {
            if (!this.items.getStackInSlot(i).isEmpty()) {
                continue;
            }
            this.items.setStackInSlot(i, remaining.copy());
            remaining = ItemStack.EMPTY;
        }
        return remaining;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Items", this.items.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Items")) {
            this.items.deserializeNBT(tag.getCompound("Items"));
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.worlds_with_ores.chest");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ModChestMenu(id, inventory, this);
    }

    public void dropContents(Level level, BlockPos pos) {
        NonNullList<ItemStack> list = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        for (int i = 0; i < SIZE; i++) {
            list.set(i, this.items.getStackInSlot(i));
        }
        net.minecraft.world.Containers.dropContents(level, pos, list);
    }
}
