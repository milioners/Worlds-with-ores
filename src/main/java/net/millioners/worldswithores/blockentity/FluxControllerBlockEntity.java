package net.millioners.worldswithores.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;
import net.millioners.worldswithores.block.FluxCoilTier;
import net.millioners.worldswithores.block.FluxControllerBlock;
import net.millioners.worldswithores.energy.ModEnergyStorage;
import net.millioners.worldswithores.energy.PortalFuels;
import net.millioners.worldswithores.menu.FluxControllerMenu;
import net.millioners.worldswithores.registry.ModBlockEntities;
import net.millioners.worldswithores.registry.ModItems;
import net.millioners.worldswithores.registry.ModSounds;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluxControllerBlockEntity extends BlockEntity implements MenuProvider {
    public static final int CAPACITY = 2_000_000;
    public static final int MAX_TRANSFER = 8_000;
    public static final int MAX_HEAT = 1_000;
    public static final int RESTART_HEAT = 550;
    public static final int COOLANT_DURATION = 1_200;

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
    private final ItemStackHandler coolant = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.is(ModItems.FLUX_COOLANT_CELL.get());
        }
    };
    private final ItemStackHandler modules = new ItemStackHandler(FluxModuleType.SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            FluxModuleType type = FluxModuleType.bySlot(slot);
            return type != null && type.matches(stack);
        }
    };

    private int burnLeft;
    private int burnTotal;
    private int fuelHeatPercent = 100;
    private int heat;
    private int coolantLeft;
    private int generation;
    private FluxCoilTier tier = FluxCoilTier.BASIC;
    private boolean overheated;
    private boolean formed;
    private int powerScale = 100;
    private boolean autoStop = true;
    private BlockPos linkedPort;
    private int mismatchCount;
    private int energyTrend;
    private int heatTrend;
    private int lastTransfer;
    private int clientEnergy;
    private int clientBurnLeft;
    private int clientBurnTotal;
    private int clientFormed;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> FluxControllerBlockEntity.this.energy.getEnergyStored();
                case 1 -> FluxControllerBlockEntity.this.energy.getMaxEnergyStored();
                case 2 -> FluxControllerBlockEntity.this.burnLeft;
                case 3 -> FluxControllerBlockEntity.this.burnTotal;
                case 4 -> FluxControllerBlockEntity.this.formed ? 1 : 0;
                case 5 -> FluxControllerBlockEntity.this.heat;
                case 6 -> FluxControllerBlockEntity.this.effectiveMaxHeat();
                case 7 -> FluxControllerBlockEntity.this.generation;
                case 8 -> FluxControllerBlockEntity.this.tier.ordinal();
                case 9 -> FluxControllerBlockEntity.this.overheated ? 1 : 0;
                case 10 -> FluxControllerBlockEntity.this.coolantLeft;
                case 11 -> FluxControllerBlockEntity.this.powerScale;
                case 12 -> FluxControllerBlockEntity.this.efficiencyPercent();
                case 13 -> FluxControllerBlockEntity.this.linkedPort != null ? 1 : 0;
                case 14 -> FluxControllerBlockEntity.this.hasModule(FluxModuleType.CAPACITY) ? 1 : 0;
                case 15 -> FluxControllerBlockEntity.this.hasModule(FluxModuleType.OUTPUT) ? 1 : 0;
                case 16 -> FluxControllerBlockEntity.this.hasModule(FluxModuleType.COOLING) ? 1 : 0;
                case 17 -> FluxControllerBlockEntity.this.autoStop ? 1 : 0;
                case 18 -> FluxControllerBlockEntity.this.mismatchCount;
                case 19 -> FluxControllerBlockEntity.this.energyTrend;
                case 20 -> FluxControllerBlockEntity.this.heatTrend;
                case 21 -> FluxControllerBlockEntity.this.lastTransfer;
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
                case 5 -> FluxControllerBlockEntity.this.heat = value;
                case 7 -> FluxControllerBlockEntity.this.generation = value;
                case 8 -> FluxControllerBlockEntity.this.tier = FluxCoilTier.values()[Math.max(0,
                        Math.min(FluxCoilTier.values().length - 1, value))];
                case 9 -> FluxControllerBlockEntity.this.overheated = value == 1;
                case 10 -> FluxControllerBlockEntity.this.coolantLeft = value;
                case 11 -> FluxControllerBlockEntity.this.powerScale = value;
                case 13 -> {
                    if (value == 0) FluxControllerBlockEntity.this.linkedPort = null;
                }
                case 17 -> FluxControllerBlockEntity.this.autoStop = value == 1;
                case 18 -> FluxControllerBlockEntity.this.mismatchCount = value;
                case 19 -> FluxControllerBlockEntity.this.energyTrend = value;
                case 20 -> FluxControllerBlockEntity.this.heatTrend = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 22;
        }
    };

    public FluxControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUX_CONTROLLER.get(), pos, state);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(this.worldPosition).inflate(5.0D);
    }

    public ItemStackHandler getFuel() {
        return this.fuel;
    }

    public ItemStackHandler getCoolant() {
        return this.coolant;
    }

    public ItemStackHandler getModules() {
        return this.modules;
    }

    public boolean hasModule(int slot) {
        return !this.modules.getStackInSlot(slot).isEmpty();
    }

    public boolean hasModule(FluxModuleType type) {
        return hasModule(type.slot());
    }

    public int efficiencyPercent() {
        int base = hasModule(FluxModuleType.EFFICIENCY) ? 125 : 100;
        if (hasModule(FluxModuleType.OVERCLOCK)) {
            base = Math.max(50, base - 25);
        }
        return base;
    }

    public int coolingBonus() {
        int bonus = hasModule(FluxModuleType.COOLING) ? 5 : 0;
        if (hasModule(FluxModuleType.SAFEGUARD)) {
            bonus += 3;
        }
        return bonus;
    }

    public int effectiveCapacity() {
        return CAPACITY + (hasModule(FluxModuleType.CAPACITY) ? 1_000_000 : 0);
    }

    public int effectiveMaxHeat() {
        return MAX_HEAT + (hasModule(FluxModuleType.HEAT_CAP) ? 200 : 0);
    }

    public int transferRate() {
        int rate = MAX_TRANSFER;
        if (hasModule(FluxModuleType.TRANSFER)) {
            rate *= 2;
        }
        if (hasModule(FluxModuleType.OUTPUT)) {
            rate = rate * 125 / 100;
        }
        return rate;
    }

    public int getLastTransfer() {
        return this.lastTransfer;
    }

    public void setPowerScale(int powerScale) {
        if (powerScale == 25 || powerScale == 50 || powerScale == 75 || powerScale == 100) {
            this.powerScale = powerScale;
            setChanged();
        }
    }

    public void toggleAutoStop() {
        this.autoStop = !this.autoStop;
        setChanged();
    }

    public int getPowerScale() {
        return this.powerScale;
    }

    public FluxMultiblock.Mismatch getFirstMismatch() {
        if (this.level == null) return null;
        return FluxMultiblock.validateDetailed(this.level, this.worldPosition).firstMismatch();
    }

    public IEnergyStorage getEnergyStorage() {
        return this.energy;
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

    public boolean isOverheated() {
        return this.overheated;
    }

    public int getHeat() {
        return this.heat;
    }

    public int getGeneration() {
        return this.generation;
    }

    public FluxCoilTier getTier() {
        return this.tier;
    }

    public float getBurnProgress() {
        int total = this.data.get(3);
        int left = this.data.get(2);
        return total == 0 ? 0.0F : (float) left / (float) total;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FluxControllerBlockEntity be) {
        boolean wasFormed = be.formed;
        boolean wasOverheated = be.overheated;
        int previousEnergy = be.energy.getEnergyStored();
        int previousHeat = be.heat;
        FluxMultiblock.ValidationResult validation = FluxMultiblock.validateDetailed(level, pos);
        be.formed = validation.isComplete();
        be.mismatchCount = validation.mismatches().size();
        be.tier = be.formed ? FluxMultiblock.getTier(level, pos) : FluxCoilTier.BASIC;
        int outputPercent = be.hasModule(FluxModuleType.OUTPUT) ? 125 : 100;
        if (be.hasModule(FluxModuleType.OVERCLOCK)) {
            outputPercent += 50;
        }
        be.generation = be.tier.generation() * be.powerScale * outputPercent / 10_000;
        be.energy.setCapacity(be.effectiveCapacity());
        boolean active = false;

        if (be.formed) {
            be.linkedPort = FluxMultiblock.getEnergyPort(level, pos);
            if (level.getBlockEntity(be.linkedPort) instanceof FluxEnergyPortBlockEntity port) {
                port.setControllerPos(pos);
            }
            if (be.overheated && be.heat <= RESTART_HEAT) {
                be.overheated = false;
            }
            if (be.burnLeft <= 0) {
                ItemStack stack = be.fuel.getStackInSlot(0);
                PortalFuels.FuelStats stats = PortalFuels.stats(stack);
                if (stats.energy() > 0) {
                    be.fuel.extractItem(0, 1, false);
                    be.burnLeft = stats.energy();
                    be.burnTotal = be.burnLeft;
                    be.fuelHeatPercent = stats.heatPercent();
                    be.setChanged();
                }
            }

            int coolantThreshold = be.hasModule(FluxModuleType.SAFEGUARD) ? 250 : 350;
            if (be.coolantLeft <= 0 && be.heat >= coolantThreshold) {
                ItemStack coolantStack = be.coolant.getStackInSlot(0);
                if (coolantStack.is(ModItems.FLUX_COOLANT_CELL.get())) {
                    be.coolant.extractItem(0, 1, false);
                    be.coolantLeft = COOLANT_DURATION;
                    level.playSound(null, pos, ModSounds.REACTOR_COOLANT.get(), SoundSource.BLOCKS, 0.75F, 1.1F);
                }
            }

            int cooling = (be.coolantLeft > 0 ? 10 : 2) + be.coolingBonus();
            if (be.hasModule(FluxModuleType.SAFEGUARD) && be.heat > be.effectiveMaxHeat() - 150) {
                cooling += 4;
            }
            if (be.coolantLeft > 0 && be.heat > 0) {
                be.coolantLeft--;
            }

            boolean storageAllowsRun = !be.autoStop || be.energy.getEnergyStored() < be.energy.getMaxEnergyStored();
            if (!be.overheated && be.burnLeft > 0 && storageAllowsRun) {
                int generated = Math.min(be.generation,
                        Math.max(0, be.energy.getMaxEnergyStored() - be.energy.getEnergyStored()));
                int fuelCost = Math.max(1, be.generation * 100 / be.efficiencyPercent());
                be.burnLeft = Math.max(0, be.burnLeft - fuelCost);
                be.energy.addEnergy(generated);
                int addedHeat = Math.max(1, be.tier.heatPerTick() * be.fuelHeatPercent / 100
                        + (be.hasModule(FluxModuleType.OUTPUT) ? 2 : 0)
                        + (be.hasModule(FluxModuleType.OVERCLOCK) ? 4 : 0));
                be.heat = Math.min(be.effectiveMaxHeat(), be.heat + addedHeat);
                active = true;
            }
            if (be.heat >= be.effectiveMaxHeat()) {
                be.overheated = true;
                active = false;
            }
            be.heat = Math.max(0, be.heat - cooling);

            pushEnergyThroughPort(level, pos, be);
        } else {
            be.generation = 0;
            be.linkedPort = null;
            be.lastTransfer = 0;
            be.heat = Math.max(0, be.heat - 4);
        }
        be.energyTrend = be.energy.getEnergyStored() - previousEnergy;
        be.heatTrend = be.heat - previousHeat;

        boolean lit = state.getValue(FluxControllerBlock.LIT);
        boolean formedState = state.getValue(FluxControllerBlock.FORMED);
        if (!lit && active) {
            level.playSound(null, pos, ModSounds.REACTOR_START.get(), SoundSource.BLOCKS, 0.9F, 1.0F);
        } else if (active && level.getGameTime() % 100L == 0L) {
            level.playSound(null, pos, ModSounds.REACTOR_HUM.get(), SoundSource.BLOCKS, 0.5F, 0.9F);
        }
        if (!wasOverheated && be.overheated) {
            level.playSound(null, pos, ModSounds.REACTOR_OVERHEAT.get(), SoundSource.BLOCKS, 1.0F, 0.8F);
        }
        if (lit != active || formedState != be.formed) {
            level.setBlock(pos, state.setValue(FluxControllerBlock.LIT, active).setValue(FluxControllerBlock.FORMED, be.formed), 3);
        }
        if (wasFormed != be.formed) {
            FluxMultiblock.notifyNeighbors(level, pos);
            be.setChanged();
        }
        if (wasOverheated != be.overheated || level.getGameTime() % 20L == 0L) {
            be.setChanged();
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), Block.UPDATE_CLIENTS);
        }
    }

    private static void pushEnergyThroughPort(Level level, BlockPos pos, FluxControllerBlockEntity be) {
        be.lastTransfer = 0;
        if (be.energy.getEnergyStored() <= 0) {
            return;
        }
        BlockPos portPos = FluxMultiblock.getEnergyPort(level, pos);
        BlockState portState = level.getBlockState(portPos);
        if (!portState.is(net.millioners.worldswithores.registry.ModBlocks.FLUX_ENERGY_PORT.get())
                || !portState.hasProperty(net.millioners.worldswithores.block.FluxEnergyPortBlock.FACING)) {
            return;
        }
        Direction outputDirection = portState.getValue(net.millioners.worldswithores.block.FluxEnergyPortBlock.FACING);
        BlockEntity neighbor = level.getBlockEntity(portPos.relative(outputDirection));
        if (neighbor == null) {
            return;
        }
        int rate = be.transferRate();
        neighbor.getCapability(ForgeCapabilities.ENERGY, outputDirection.getOpposite()).ifPresent(storage -> {
            if (storage.canReceive()) {
                int sent = storage.receiveEnergy(Math.min(rate, be.energy.getEnergyStored()), false);
                if (sent > 0) {
                    be.energy.extractEnergy(sent, false);
                    be.lastTransfer = sent;
                }
            }
        });
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
        tag.put("Coolant", this.coolant.serializeNBT());
        tag.put("Modules", this.modules.serializeNBT());
        tag.putInt("BurnLeft", this.burnLeft);
        tag.putInt("BurnTotal", this.burnTotal);
        tag.putInt("FuelHeatPercent", this.fuelHeatPercent);
        tag.putInt("Heat", this.heat);
        tag.putInt("CoolantLeft", this.coolantLeft);
        tag.putInt("Generation", this.generation);
        tag.putInt("Tier", this.tier.ordinal());
        tag.putBoolean("Overheated", this.overheated);
        tag.putBoolean("Formed", this.formed);
        tag.putInt("PowerScale", this.powerScale);
        tag.putBoolean("AutoStop", this.autoStop);
        if (this.linkedPort != null) {
            tag.putLong("LinkedPort", this.linkedPort.asLong());
        }
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
        if (tag.contains("Coolant")) {
            this.coolant.deserializeNBT(tag.getCompound("Coolant"));
        }
        if (tag.contains("Modules")) {
            CompoundTag modulesTag = tag.getCompound("Modules");
            if (modulesTag.getInt("Size") < FluxModuleType.SLOT_COUNT) {
                modulesTag.putInt("Size", FluxModuleType.SLOT_COUNT);
            }
            this.modules.deserializeNBT(modulesTag);
        }
        this.burnLeft = tag.getInt("BurnLeft");
        this.burnTotal = tag.getInt("BurnTotal");
        this.fuelHeatPercent = tag.contains("FuelHeatPercent") ? tag.getInt("FuelHeatPercent") : 100;
        this.heat = tag.getInt("Heat");
        this.coolantLeft = tag.getInt("CoolantLeft");
        this.generation = tag.getInt("Generation");
        int tierIndex = Math.max(0, Math.min(FluxCoilTier.values().length - 1, tag.getInt("Tier")));
        this.tier = FluxCoilTier.values()[tierIndex];
        this.overheated = tag.getBoolean("Overheated");
        this.formed = tag.getBoolean("Formed");
        if (tag.contains("PowerScale")) this.powerScale = tag.getInt("PowerScale");
        this.autoStop = !tag.contains("AutoStop") || tag.getBoolean("AutoStop");
        this.linkedPort = tag.contains("LinkedPort") ? BlockPos.of(tag.getLong("LinkedPort")) : null;
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
        net.minecraft.world.Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), this.coolant.getStackInSlot(0));
        for (int slot = 0; slot < this.modules.getSlots(); slot++) {
            net.minecraft.world.Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(),
                    this.modules.getStackInSlot(slot));
        }
    }
}
