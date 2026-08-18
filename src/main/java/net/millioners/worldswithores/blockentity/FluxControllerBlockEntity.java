package net.millioners.worldswithores.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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
import net.millioners.worldswithores.block.FluxControllerBlock;
import net.millioners.worldswithores.energy.ModEnergyStorage;
import net.millioners.worldswithores.energy.PortalFuels;
import net.millioners.worldswithores.menu.FluxControllerMenu;
import net.millioners.worldswithores.registry.ModBlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluxControllerBlockEntity extends BlockEntity implements MenuProvider {
    public static final int CAPACITY = 500_000;
    public static final int MAX_TRANSFER = 2_000;
    public static final int GEN_PER_TICK = 80;

    private final ModEnergyStorage energy = new ModEnergyStorage(CAPACITY, 0, MAX_TRANSFER, this::setChanged);
    private final LazyOptional<IEnergyStorage> energyOptional = LazyOptional.of(() -> this.energy);
    private final ItemStackHandler fuel = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return PortalFuels.isFuel(stack);
        }
    };

    private int burnLeft;
    private int burnTotal;
    private boolean formed;
    private int clientEnergy;
    private int clientBurnLeft;
    private int clientBurnTotal;
    private int clientFormed;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> FluxControllerBlockEntity.this.energy.getEnergyStored();
                case 1 -> CAPACITY;
                case 2 -> FluxControllerBlockEntity.this.burnLeft;
                case 3 -> FluxControllerBlockEntity.this.burnTotal;
                case 4 -> FluxControllerBlockEntity.this.formed ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> FluxControllerBlockEntity.this.clientEnergy = value;
                case 1 -> {
                }
                case 2 -> FluxControllerBlockEntity.this.clientBurnLeft = value;
                case 3 -> FluxControllerBlockEntity.this.clientBurnTotal = value;
                case 4 -> FluxControllerBlockEntity.this.clientFormed = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public FluxControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUX_CONTROLLER.get(), pos, state);
    }

    public ItemStackHandler getFuel() {
        return this.fuel;
    }

    public ContainerData getData() {
        return this.data;
    }

    public boolean isFormed() {
        return this.getBlockState().hasProperty(FluxControllerBlock.FORMED)
                && this.getBlockState().getValue(FluxControllerBlock.FORMED);
    }

    public boolean isBurning() {
        return this.getBlockState().hasProperty(FluxControllerBlock.LIT)
                && this.getBlockState().getValue(FluxControllerBlock.LIT);
    }

    public float getBurnProgress() {
        int total = this.data.get(3);
        int left = this.data.get(2);
        return total == 0 ? 0.0F : (float) left / (float) total;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FluxControllerBlockEntity be) {
        boolean wasFormed = be.formed;
        be.formed = FluxMultiblock.isValid(level, pos);
        boolean active = false;

        if (be.formed) {
            if (be.burnLeft <= 0) {
                ItemStack stack = be.fuel.getStackInSlot(0);
                int value = PortalFuels.burnEnergy(stack);
                if (value > 0) {
                    be.fuel.extractItem(0, 1, false);
                    be.burnLeft = value / GEN_PER_TICK;
                    be.burnTotal = be.burnLeft;
                    be.setChanged();
                }
            }

            if (be.burnLeft > 0 && be.energy.getEnergyStored() < CAPACITY) {
                be.burnLeft--;
                be.energy.addEnergy(GEN_PER_TICK);
                active = true;
            }

            pushEnergy(level, pos, be);
        } else {
            be.burnLeft = 0;
            be.burnTotal = 0;
        }

        boolean lit = state.getValue(FluxControllerBlock.LIT);
        boolean formedState = state.getValue(FluxControllerBlock.FORMED);
        if (lit != active || formedState != be.formed) {
            level.setBlock(pos, state.setValue(FluxControllerBlock.LIT, active).setValue(FluxControllerBlock.FORMED, be.formed), 3);
        }
        if (wasFormed != be.formed) {
            FluxMultiblock.notifyNeighbors(level, pos);
            be.setChanged();
        }
    }

    private static void pushEnergy(Level level, BlockPos pos, FluxControllerBlockEntity be) {
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
        if (cap == ForgeCapabilities.ENERGY && this.formed) {
            return this.energyOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.energyOptional.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Energy", this.energy.save());
        tag.put("Fuel", this.fuel.serializeNBT());
        tag.putInt("BurnLeft", this.burnLeft);
        tag.putInt("BurnTotal", this.burnTotal);
        tag.putBoolean("Formed", this.formed);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Energy")) {
            this.energy.load(tag.getCompound("Energy"));
        }
        if (tag.contains("Fuel")) {
            this.fuel.deserializeNBT(tag.getCompound("Fuel"));
        }
        this.burnLeft = tag.getInt("BurnLeft");
        this.burnTotal = tag.getInt("BurnTotal");
        this.formed = tag.getBoolean("Formed");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.worlds_with_ores.flux_controller");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new FluxControllerMenu(id, inventory, this, this.data);
    }

    public void dropContents(Level level, BlockPos pos) {
        net.minecraft.world.Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), this.fuel.getStackInSlot(0));
    }
}
