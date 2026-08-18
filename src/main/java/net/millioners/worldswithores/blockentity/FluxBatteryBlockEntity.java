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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.millioners.worldswithores.energy.ModEnergyStorage;
import net.millioners.worldswithores.menu.FluxBatteryMenu;
import net.millioners.worldswithores.registry.ModBlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluxBatteryBlockEntity extends BlockEntity implements MenuProvider {
    public static final int CAPACITY = 1_000_000;
    public static final int MAX_TRANSFER = 4_000;

    private final ModEnergyStorage energy = new ModEnergyStorage(CAPACITY, MAX_TRANSFER, MAX_TRANSFER, this::setChanged);
    private final LazyOptional<IEnergyStorage> optional = LazyOptional.of(() -> this.energy);
    private int clientEnergy;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return index == 0 ? FluxBatteryBlockEntity.this.energy.getEnergyStored() : CAPACITY;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                FluxBatteryBlockEntity.this.clientEnergy = value;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public FluxBatteryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUX_BATTERY.get(), pos, state);
    }

    public ContainerData getData() {
        return this.data;
    }

    public int getClientEnergy() {
        return this.level != null && this.level.isClientSide ? this.clientEnergy : this.energy.getEnergyStored();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FluxBatteryBlockEntity be) {
        for (Direction direction : Direction.values()) {
            if (be.energy.getEnergyStored() <= 0) {
                return;
            }
            BlockEntity neighbor = level.getBlockEntity(pos.relative(direction));
            if (neighbor == null) {
                continue;
            }
            neighbor.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(storage -> {
                if (storage.canReceive()) {
                    int sent = storage.receiveEnergy(Math.min(MAX_TRANSFER, be.energy.getEnergyStored()), false);
                    if (sent > 0) {
                        be.energy.extractEnergy(sent, false);
                    }
                }
            });
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
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Energy")) {
            this.energy.load(tag.getCompound("Energy"));
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.worlds_with_ores.flux_battery");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new FluxBatteryMenu(id, inventory, this, this.data);
    }
}
