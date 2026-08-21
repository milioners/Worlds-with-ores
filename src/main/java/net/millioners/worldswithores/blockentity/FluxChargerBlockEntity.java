package net.millioners.worldswithores.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;
import net.millioners.worldswithores.energy.ModEnergyStorage;
import net.millioners.worldswithores.menu.FluxChargerMenu;
import net.millioners.worldswithores.registry.ModBlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluxChargerBlockEntity extends BlockEntity implements MenuProvider {
    public static final int CAPACITY = 200_000;
    public static final int MAX_TRANSFER = 1_000;
    public static final int CHARGE_RATE = 500;

    private final ModEnergyStorage energy = new ModEnergyStorage(CAPACITY, MAX_TRANSFER, 0, this::setChanged);
    private final LazyOptional<IEnergyStorage> optional = LazyOptional.of(() -> this.energy);
    private final ItemStackHandler chargeSlot = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::canReceive).orElse(false);
        }
    };

    private int clientEnergy;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return index == 0 ? FluxChargerBlockEntity.this.energy.getEnergyStored() : CAPACITY;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                FluxChargerBlockEntity.this.clientEnergy = value;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public FluxChargerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUX_CHARGER.get(), pos, state);
    }

    public ItemStackHandler getChargeSlot() {
        return this.chargeSlot;
    }

    public ContainerData getData() {
        return this.data;
    }

    public int getClientEnergy() {
        return this.level != null && this.level.isClientSide ? this.clientEnergy : this.energy.getEnergyStored();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FluxChargerBlockEntity be) {
        ItemStack stack = be.chargeSlot.getStackInSlot(0);
        if (stack.isEmpty() || be.energy.getEnergyStored() <= 0) {
            return;
        }

        int toSend = Math.min(CHARGE_RATE, be.energy.getEnergyStored());
        final int[] moved = {0};

        stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(storage -> {
            if (storage.canReceive()) {
                moved[0] = storage.receiveEnergy(toSend, false);
            }
        });

        if (moved[0] > 0) {
            be.energy.extractEnergy(moved[0], false);
            be.chargeSlot.setStackInSlot(0, stack);
            be.setChanged();
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return this.optional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.optional.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Energy", this.energy.save());
        tag.put("Charge", this.chargeSlot.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Energy")) {
            this.energy.load(tag.getCompound("Energy"));
        }
        if (tag.contains("Charge")) {
            this.chargeSlot.deserializeNBT(tag.getCompound("Charge"));
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.worlds_with_ores.flux_charger");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new FluxChargerMenu(id, inventory, this, this.data);
    }

    public void dropContents(Level level, BlockPos pos) {
        net.minecraft.world.Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), this.chargeSlot.getStackInSlot(0));
    }
}
