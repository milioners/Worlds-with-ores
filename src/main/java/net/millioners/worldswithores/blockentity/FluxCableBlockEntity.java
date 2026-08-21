package net.millioners.worldswithores.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.millioners.worldswithores.energy.ModEnergyStorage;
import net.millioners.worldswithores.registry.ModBlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluxCableBlockEntity extends BlockEntity {
    public static final int CAPACITY = 32_000;
    public static final int MAX_TRANSFER = 8_000;

    private final ModEnergyStorage energy = new ModEnergyStorage(CAPACITY, MAX_TRANSFER, MAX_TRANSFER, this::setChanged);
    private final LazyOptional<IEnergyStorage> optional = LazyOptional.of(() -> this.energy);

    public FluxCableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUX_CABLE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FluxCableBlockEntity be) {
        // Pull from adjacent extractors (skip other cables to avoid thrash; equalize separately).
        for (Direction direction : Direction.values()) {
            if (be.energy.getEnergyStored() >= CAPACITY) {
                break;
            }
            BlockEntity neighbor = level.getBlockEntity(pos.relative(direction));
            if (neighbor == null || neighbor instanceof FluxCableBlockEntity) {
                continue;
            }
            neighbor.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(storage -> {
                if (storage.canExtract()) {
                    int space = CAPACITY - be.energy.getEnergyStored();
                    int want = Math.min(MAX_TRANSFER, space);
                    int extracted = storage.extractEnergy(want, false);
                    if (extracted > 0) {
                        be.energy.receiveEnergy(extracted, false);
                    }
                }
            });
        }

        // Equalize with adjacent cables.
        for (Direction direction : Direction.values()) {
            BlockEntity neighbor = level.getBlockEntity(pos.relative(direction));
            if (!(neighbor instanceof FluxCableBlockEntity other)) {
                continue;
            }
            int mine = be.energy.getEnergyStored();
            int theirs = other.energy.getEnergyStored();
            if (mine <= theirs) {
                continue;
            }
            int move = Math.min(MAX_TRANSFER / 2, (mine - theirs) / 2);
            if (move > 0) {
                be.energy.extractEnergy(move, false);
                other.energy.receiveEnergy(move, false);
            }
        }

        // Push to adjacent receivers (non-cables).
        for (Direction direction : Direction.values()) {
            if (be.energy.getEnergyStored() <= 0) {
                return;
            }
            BlockEntity neighbor = level.getBlockEntity(pos.relative(direction));
            if (neighbor == null || neighbor instanceof FluxCableBlockEntity) {
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
}
