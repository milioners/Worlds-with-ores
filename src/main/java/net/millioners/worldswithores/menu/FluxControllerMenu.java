package net.millioners.worldswithores.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;
import net.millioners.worldswithores.blockentity.FluxControllerBlockEntity;
import net.millioners.worldswithores.energy.PortalFuels;
import net.millioners.worldswithores.registry.ModBlocks;
import net.millioners.worldswithores.registry.ModMenus;

public class FluxControllerMenu extends AbstractContainerMenu {
    private final FluxControllerBlockEntity controller;
    private final ContainerData data;

    public FluxControllerMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, playerInv.player.level().getBlockEntity(buf.readBlockPos()), new SimpleContainerData(5));
    }

    public FluxControllerMenu(int id, Inventory playerInv, BlockEntity be, ContainerData data) {
        super(ModMenus.FLUX_CONTROLLER.get(), id);
        this.controller = (FluxControllerBlockEntity) be;
        this.data = data;
        this.addDataSlots(data);

        this.addSlot(new SlotItemHandler(this.controller.getFuel(), 0, 44, 42) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return PortalFuels.isFuel(stack);
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 16 + col * 18, 92 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 16 + col * 18, 150));
        }
    }

    public FluxControllerBlockEntity getController() {
        return this.controller;
    }

    public ContainerData getData() {
        return this.data;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(
                this.controller.getBlockPos().getX() + 0.5D,
                this.controller.getBlockPos().getY() + 0.5D,
                this.controller.getBlockPos().getZ() + 0.5D) <= 64.0D
                && this.controller.getBlockState().is(ModBlocks.FLUX_CONTROLLER.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index == 0) {
                if (!this.moveItemStackTo(stack, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (PortalFuels.isFuel(stack)) {
                if (!this.moveItemStackTo(stack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 28) {
                if (!this.moveItemStackTo(stack, 28, this.slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 1, 28, false)) {
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
