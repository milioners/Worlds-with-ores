package net.millioners.worldswithores.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.millioners.worldswithores.block.FluxWirelessBlock;
import net.millioners.worldswithores.energy.ModEnergyStorage;
import net.millioners.worldswithores.registry.ModBlockEntities;
import net.millioners.worldswithores.registry.ModBlocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluxWirelessBlockEntity extends BlockEntity {
    public static final int CAPACITY = 64_000;
    public static final int MAX_TRANSFER = 8_000;
    public static final int MAX_RANGE = 48;

    private final ModEnergyStorage energy = new ModEnergyStorage(CAPACITY, MAX_TRANSFER, MAX_TRANSFER, this::setChanged);
    private final LazyOptional<IEnergyStorage> optional = LazyOptional.of(() -> this.energy);
    @Nullable
    private BlockPos linkedPos;

    public FluxWirelessBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUX_WIRELESS.get(), pos, state);
    }

    public boolean isTransmitter() {
        return this.getBlockState().getBlock() instanceof FluxWirelessBlock wireless && wireless.isTransmitter();
    }

    @Nullable
    public BlockPos getLinkedPos() {
        return this.linkedPos;
    }

    public void setLinkedPos(@Nullable BlockPos linkedPos) {
        this.linkedPos = linkedPos == null ? null : linkedPos.immutable();
        setChanged();
    }

    public boolean isInRange() {
        if (this.linkedPos == null || this.level == null) {
            return false;
        }
        return this.worldPosition.distManhattan(this.linkedPos) <= MAX_RANGE;
    }

    public Component statusMessage() {
        if (this.linkedPos == null) {
            return Component.translatable("message.worlds_with_ores.wireless.unlinked");
        }
        if (!isInRange()) {
            return Component.translatable("message.worlds_with_ores.wireless.out_of_range",
                    this.linkedPos.getX(), this.linkedPos.getY(), this.linkedPos.getZ());
        }
        return Component.translatable("message.worlds_with_ores.wireless.linked",
                this.linkedPos.getX(), this.linkedPos.getY(), this.linkedPos.getZ());
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FluxWirelessBlockEntity be) {
        if (be.isTransmitter()) {
            pullFromNeighbors(level, pos, be);
            transferToPartner(level, be);
        } else {
            pushToNeighbors(level, pos, be);
        }
    }

    private static void pullFromNeighbors(Level level, BlockPos pos, FluxWirelessBlockEntity be) {
        for (Direction direction : Direction.values()) {
            if (be.energy.getEnergyStored() >= CAPACITY) {
                return;
            }
            BlockEntity neighbor = level.getBlockEntity(pos.relative(direction));
            if (neighbor == null) {
                continue;
            }
            neighbor.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(storage -> {
                if (storage.canExtract()) {
                    int space = CAPACITY - be.energy.getEnergyStored();
                    int extracted = storage.extractEnergy(Math.min(MAX_TRANSFER, space), false);
                    if (extracted > 0) {
                        be.energy.receiveEnergy(extracted, false);
                    }
                }
            });
        }
    }

    private static void pushToNeighbors(Level level, BlockPos pos, FluxWirelessBlockEntity be) {
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

    private static void transferToPartner(Level level, FluxWirelessBlockEntity tx) {
        if (tx.linkedPos == null || tx.energy.getEnergyStored() <= 0) {
            return;
        }
        if (!tx.isInRange()) {
            return;
        }
        BlockEntity partner = level.getBlockEntity(tx.linkedPos);
        if (!(partner instanceof FluxWirelessBlockEntity rx) || rx.isTransmitter()) {
            return;
        }
        if (!rx.getBlockState().is(ModBlocks.FLUX_WIRELESS_RECEIVER.get())) {
            return;
        }
        int space = CAPACITY - rx.energy.getEnergyStored();
        int move = Math.min(MAX_TRANSFER, Math.min(space, tx.energy.getEnergyStored()));
        if (move <= 0) {
            return;
        }
        tx.energy.extractEnergy(move, false);
        rx.energy.receiveEnergy(move, false);
        if (rx.linkedPos == null || !rx.linkedPos.equals(tx.worldPosition)) {
            rx.setLinkedPos(tx.worldPosition);
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
        if (this.linkedPos != null) {
            tag.putLong("Linked", this.linkedPos.asLong());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Energy")) {
            this.energy.load(tag.getCompound("Energy"));
        }
        this.linkedPos = tag.contains("Linked") ? BlockPos.of(tag.getLong("Linked")) : null;
    }
}
