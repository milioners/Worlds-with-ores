package net.millioners.worldswithores.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import net.millioners.worldswithores.menu.ModChestMenu;
import net.millioners.worldswithores.registry.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class ModChestBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SIZE = 33;
    private final ItemStackHandler items = new ItemStackHandler(SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    public ModChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHEST.get(), pos, state);
    }

    public ItemStackHandler getItems() {
        return this.items;
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
