package net.millioners.worldswithores.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;
import net.millioners.worldswithores.blockentity.ModChestBlockEntity;
import net.millioners.worldswithores.registry.ModBlocks;
import net.millioners.worldswithores.registry.ModMenus;

public class ModChestMenu extends AbstractContainerMenu {
    private final ModChestBlockEntity chest;
    private final Inventory playerInv;

    public ModChestMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, playerInv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public ModChestMenu(int id, Inventory playerInv, BlockEntity be) {
        super(ModMenus.CHEST.get(), id);
        this.chest = (ModChestBlockEntity) be;
        this.playerInv = playerInv;

        int index = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 11; col++) {
                this.addSlot(new SlotItemHandler(this.chest.getItems(), index++, 26 + col * 18, 24 + row * 18));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 45 + col * 18, 100 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 45 + col * 18, 158));
        }
    }

    public ModChestBlockEntity getChest() {
        return this.chest;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0) {
            this.chest.sortContents();
            return true;
        }
        if (id == 1) {
            this.chest.depositMatching(this.playerInv);
            return true;
        }
        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(
                this.chest.getBlockPos().getX() + 0.5D,
                this.chest.getBlockPos().getY() + 0.5D,
                this.chest.getBlockPos().getZ() + 0.5D) <= 64.0D
                && this.chest.getBlockState().is(ModBlocks.CHEST.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < ModChestBlockEntity.SIZE) {
                if (!this.moveItemStackTo(stack, ModChestBlockEntity.SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, ModChestBlockEntity.SIZE, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }
}
