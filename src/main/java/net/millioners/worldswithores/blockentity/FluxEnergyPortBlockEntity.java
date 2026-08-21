package net.millioners.worldswithores.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.millioners.worldswithores.registry.ModBlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluxEnergyPortBlockEntity extends BlockEntity {
    private BlockPos controllerPos;

    private final IEnergyStorage output = new IEnergyStorage() {
        private IEnergyStorage controllerStorage() {
            if (level == null) return null;
            if (controllerPos != null
                    && level.getBlockEntity(controllerPos) instanceof FluxControllerBlockEntity cached
                    && cached.isFormed()) {
                return cached.getEnergyStorage();
            }
            controllerPos = null;
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos candidate = worldPosition.relative(direction, 4);
                if (level.getBlockEntity(candidate) instanceof FluxControllerBlockEntity controller
                        && FluxMultiblock.isValid(level, candidate)) {
                    controllerPos = candidate.immutable();
                    setChanged();
                    return controller.getEnergyStorage();
                }
            }
            return null;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            IEnergyStorage storage = controllerStorage();
            return storage == null ? 0 : storage.extractEnergy(maxExtract, simulate);
        }

        @Override
        public int getEnergyStored() {
            IEnergyStorage storage = controllerStorage();
            return storage == null ? 0 : storage.getEnergyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            IEnergyStorage storage = controllerStorage();
            return storage == null ? 0 : storage.getMaxEnergyStored();
        }

        @Override
        public boolean canExtract() {
            return controllerStorage() != null;
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    };
    private final LazyOptional<IEnergyStorage> outputOptional = LazyOptional.of(() -> output);

    public FluxEnergyPortBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUX_ENERGY_PORT.get(), pos, state);
    }

    public void setControllerPos(BlockPos controllerPos) {
        if (!controllerPos.equals(this.controllerPos)) {
            this.controllerPos = controllerPos.immutable();
            setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (controllerPos != null) tag.putLong("ControllerPos", controllerPos.asLong());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        controllerPos = tag.contains("ControllerPos") ? BlockPos.of(tag.getLong("ControllerPos")) : null;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return outputOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        outputOptional.invalidate();
    }
}
