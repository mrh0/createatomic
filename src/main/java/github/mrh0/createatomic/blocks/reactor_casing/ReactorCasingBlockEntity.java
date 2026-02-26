package github.mrh0.createatomic.blocks.reactor_casing;

import static java.lang.Math.abs;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import com.simibubi.create.content.fluids.tank.BoilerData;
import github.mrh0.createatomic.blocks.rod_assembly.RodAssemblyBlockEntity;
import github.mrh0.createatomic.index.AtomicBlockEntities;
import github.mrh0.createatomic.index.AtomicBlocks;
import github.mrh0.createatomic.network.IObserveBlockEntity;
import github.mrh0.createatomic.network.ObservePacketPayload;
import github.mrh0.createatomic.network.ReactorPacketPayload;
import net.createmod.catnip.data.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.infrastructure.config.AllConfigs;

import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.animation.LerpedFloat.Chaser;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class ReactorCasingBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IMultiBlockEntityContainer.Fluid, IObserveBlockEntity {

    private static final int MAX_WIDTH = 6,
        MAX_HEIGHT = 5;

    protected IFluidHandler fluidCapability;
    protected boolean forceFluidLevelUpdate;
    protected FluidTank tankInventory;
    protected BlockPos controller;
    protected BlockPos lastKnownPos;
    protected boolean updateConnectivity;
    protected boolean updateCapability;
    protected int luminosity;
    protected int width;
    protected int height;

    public BoilerData boiler;

    private static final int SYNC_RATE = 8;
    protected int syncCooldown;
    protected boolean queuedSync;

    // Reactor specific
    private int reactorHeat = 0;
    private int reactorCoolant = 0;
    private float rodInsertion = 1f;

    // For rendering purposes only
    private LerpedFloat fluidLevel;

    public ReactorCasingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        tankInventory = createInventory();
        forceFluidLevelUpdate = true;
        updateConnectivity = false;
        updateCapability = false;
        height = 1;
        width = 1;
        boiler = new BoilerData();
        refreshCapability();
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                AtomicBlockEntities.REACTOR_CASING.get(),
                (be, context) -> {
                    if (be.fluidCapability == null)
                        be.refreshCapability();
                    return be.fluidCapability;
                }
        );
    }

    protected SmartFluidTank createInventory() {
        return new SmartFluidTank(getCapacityMultiplier(), this::onFluidStackChanged);
    }

    protected void updateConnectivity() {
        updateConnectivity = false;
        if (level.isClientSide)
            return;
        if (!isController())
            return;
        ConnectivityHandler.formMulti(this);
    }

    @Override
    public void tick() {
        super.tick();
        if (syncCooldown > 0) {
            syncCooldown--;
            if (syncCooldown == 0 && queuedSync)
                sendData();
        }

        if (lastKnownPos == null)
            lastKnownPos = getBlockPos();
        else if (!lastKnownPos.equals(worldPosition) && worldPosition != null) {
            onPositionChanged();
            return;
        }

        if (updateCapability) {
            updateCapability = false;
            refreshCapability();
        }
        if (updateConnectivity)
            updateConnectivity();
        if (fluidLevel != null)
            fluidLevel.tickChaser();
        //if (isController())
        //    boiler.tick(this);
    }

    /*
    @Override
    public void lazyTick() {
        super.lazyTick();
        if (isController())
            boiler.updateOcclusion(this);
    }
    */

    @Override
    public BlockPos getLastKnownPos() {
        return lastKnownPos;
    }

    @Override
    public boolean isController() {
        return controller == null || worldPosition.getX() == controller.getX()
                && worldPosition.getY() == controller.getY() && worldPosition.getZ() == controller.getZ();
    }

    @Override
    public void initialize() {
        super.initialize();
        sendData();
        if (level.isClientSide)
            invalidateRenderBoundingBox();
    }

    private void onPositionChanged() {
        removeController(true);
        lastKnownPos = worldPosition;
    }

    protected void onFluidStackChanged(FluidStack newFluidStack) {
        if (!hasLevel())
            return;

        FluidType attributes = newFluidStack.getFluid()
                .getFluidType();
        int luminosity = (int) (attributes.getLightLevel(newFluidStack) / 1.2f);
        boolean reversed = attributes.isLighterThanAir();
        int maxY = (int) ((getFillState() * height) + 1);

        for (int yOffset = 0; yOffset < height; yOffset++) {
            boolean isBright = reversed ? (height - yOffset <= maxY) : (yOffset < maxY);
            int actualLuminosity = isBright ? luminosity : luminosity > 0 ? 1 : 0;

            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {
                    BlockPos pos = this.worldPosition.offset(xOffset, yOffset, zOffset);
                    ReactorCasingBlockEntity tankAt = ConnectivityHandler.partAt(getType(), level, pos);
                    if (tankAt == null)
                        continue;
                    level.updateNeighbourForOutputSignal(pos, tankAt.getBlockState()
                            .getBlock());
                    if (tankAt.luminosity == actualLuminosity)
                        continue;
                    tankAt.setLuminosity(actualLuminosity);
                }
            }
        }

        if (!level.isClientSide) {
            setChanged();
            sendData();
        }

        if (isVirtual()) {
            if (fluidLevel == null)
                fluidLevel = LerpedFloat.linear()
                        .startWithValue(getFillState());
            fluidLevel.chase(getFillState(), .5f, Chaser.EXP);
        }
    }

    protected void setLuminosity(int luminosity) {
        if (level.isClientSide)
            return;
        if (this.luminosity == luminosity)
            return;
        this.luminosity = luminosity;
        sendData();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ReactorCasingBlockEntity getControllerBE() {
        if (isController() || !hasLevel())
            return this;
        BlockEntity blockEntity = level.getBlockEntity(controller);
        if (blockEntity instanceof ReactorCasingBlockEntity)
            return (ReactorCasingBlockEntity) blockEntity;
        return null;
    }

    public void applyReactorCasingSize(int blocks) {
        tankInventory.setCapacity(blocks * getCapacityMultiplier());
        int overflow = tankInventory.getFluidAmount() - tankInventory.getCapacity();
        if (overflow > 0)
            tankInventory.drain(overflow, FluidAction.EXECUTE);
        forceFluidLevelUpdate = true;
    }

    public void removeController(boolean keepFluids) {
        if (level.isClientSide)
            return;
        updateConnectivity = true;
        if (!keepFluids)
            applyReactorCasingSize(1);
        controller = null;
        width = 1;
        height = 1;
        boiler.clear();
        onFluidStackChanged(tankInventory.getFluid());

        BlockState state = getBlockState();
        if (ReactorCasingBlock.isReactor(state)) {
            state = state.setValue(ReactorCasingBlock.BOTTOM, true);
            state = state.setValue(ReactorCasingBlock.TOP, true);
            getLevel().setBlock(worldPosition, state, Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE | Block.UPDATE_KNOWN_SHAPE);
        }

        refreshCapability();
        setChanged();
        sendData();
    }

    public void updateBoilerTemperature() {
        ReactorCasingBlockEntity be = getControllerBE();
        if (be == null)
            return;
        if (!be.boiler.isActive())
            return;
        be.boiler.needsHeatLevelUpdate = true;
    }

    public void sendDataImmediately() {
        syncCooldown = 0;
        queuedSync = false;
        sendData();
    }

    @Override
    public void sendData() {
        if (syncCooldown > 0) {
            queuedSync = true;
            return;
        }
        super.sendData();
        queuedSync = false;
        syncCooldown = SYNC_RATE;
    }

    @Override
    public void setController(BlockPos controller) {
        if (level.isClientSide && !isVirtual())
            return;
        if (controller.equals(this.controller))
            return;
        this.controller = controller;
        refreshCapability();
        setChanged();
        sendData();
    }

    void refreshCapability() {
        fluidCapability = handlerForCapability();
        invalidateCapabilities();
    }

    private IFluidHandler handlerForCapability() {
        return isController() ? (boiler.isActive() ? boiler.createHandler() : tankInventory)
                : ((getControllerBE() != null) ? getControllerBE().handlerForCapability() : new FluidTank(0));
    }

    @Override
    public BlockPos getController() {
        return isController() ? worldPosition : controller;
    }

    @Override
    protected AABB createRenderBoundingBox() {
        if (isController())
            return super.createRenderBoundingBox().expandTowards(width - 1, height - 1, width - 1);
        else
            return super.createRenderBoundingBox();
    }

    @Nullable
    public ReactorCasingBlockEntity getOtherReactorCasingBlockEntity(Direction direction) {
        BlockEntity otherBE = level.getBlockEntity(worldPosition.relative(direction));
        if (otherBE instanceof ReactorCasingBlockEntity)
            return (ReactorCasingBlockEntity) otherBE;
        return null;
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);

        BlockPos controllerBefore = controller;
        int prevSize = width;
        int prevHeight = height;
        int prevLum = luminosity;

        updateConnectivity = compound.contains("Uninitialized");
        luminosity = compound.getInt("Luminosity");

        lastKnownPos = null;
        if (compound.contains("LastKnownPos"))
            lastKnownPos = NBTHelper.readBlockPos(compound, "LastKnownPos");

        controller = null;
        if (compound.contains("Controller"))
            controller = NBTHelper.readBlockPos(compound, "Controller");

        if (isController()) {
            width = compound.getInt("Size");
            height = compound.getInt("Height");
            tankInventory.setCapacity(getTotalTankSize() * getCapacityMultiplier());

            tankInventory.readFromNBT(registries, compound.getCompound("TankContent"));
            if (tankInventory.getSpace() < 0)
                tankInventory.drain(-tankInventory.getSpace(), FluidAction.EXECUTE);
        }

        boiler.read(compound.getCompound("Boiler"), width * width * height);

        if (compound.contains("ForceFluidLevel") || fluidLevel == null)
            fluidLevel = LerpedFloat.linear()
                    .startWithValue(getFillState());

        updateCapability = true;

        if (!clientPacket)
            return;

        boolean changeOfController = !Objects.equals(controllerBefore, controller);
        if (changeOfController || prevSize != width || prevHeight != height) {
            if (hasLevel())
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
            if (isController())
                tankInventory.setCapacity(getCapacityMultiplier() * getTotalTankSize());
            invalidateRenderBoundingBox();
        }
        if (isController()) {
            float fillState = getFillState();
            if (compound.contains("ForceFluidLevel") || fluidLevel == null)
                fluidLevel = LerpedFloat.linear()
                        .startWithValue(fillState);
            fluidLevel.chase(fillState, 0.5f, Chaser.EXP);
        }
        if (luminosity != prevLum && hasLevel())
            level.getChunkSource()
                    .getLightEngine()
                    .checkBlock(worldPosition);

        if (compound.contains("LazySync"))
            fluidLevel.chase(fluidLevel.getChaseTarget(), 0.125f, Chaser.EXP);
    }

    public float getFillState() {
        return (float) tankInventory.getFluidAmount() / tankInventory.getCapacity();
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (updateConnectivity)
            compound.putBoolean("Uninitialized", true);
        compound.put("Boiler", boiler.write());
        if (lastKnownPos != null)
            compound.put("LastKnownPos", NbtUtils.writeBlockPos(lastKnownPos));
        if (!isController())
            compound.put("Controller", NbtUtils.writeBlockPos(controller));
        if (isController()) {
            compound.put("TankContent", tankInventory.writeToNBT(registries, new CompoundTag()));
            compound.putInt("Size", width);
            compound.putInt("Height", height);
        }
        compound.putInt("Luminosity", luminosity);
        super.write(compound, registries, clientPacket);

        if (!clientPacket)
            return;
        if (forceFluidLevelUpdate)
            compound.putBoolean("ForceFluidLevel", true);
        if (queuedSync)
            compound.putBoolean("LazySync", true);
        forceFluidLevelUpdate = false;
    }

    @Override
    public void writeSafe(CompoundTag compound, HolderLookup.Provider registries) {
        if (isController()) {
            compound.putInt("Size", width);
            compound.putInt("Height", height);
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        registerAwardables(behaviours, AllAdvancements.STEAM_ENGINE_MAXED, AllAdvancements.PIPE_ORGAN);
    }

    public FluidTank getTankInventory() {
        return tankInventory;
    }

    public int getTotalTankSize() {
        return width * width * height;
    }

    public static int getMaxSize() {
        return MAX_WIDTH;
    }

    public static int getCapacityMultiplier() {
        return AllConfigs.server().fluids.fluidTankCapacity.get() * 1000;
    }

    public static int getMaxHeight() {
        return MAX_HEIGHT;
    }

    public LerpedFloat getFluidLevel() {
        return fluidLevel;
    }

    public void setFluidLevel(LerpedFloat fluidLevel) {
        this.fluidLevel = fluidLevel;
    }

    @Override
    public void preventConnectivityUpdate() {
        updateConnectivity = false;
    }

    @Override
    public void notifyMultiUpdated() {
        BlockState state = this.getBlockState();
        if (ReactorCasingBlock.isReactor(state)) { // safety
            state = state.setValue(ReactorCasingBlock.BOTTOM, getController().getY() == getBlockPos().getY());
            state = state.setValue(ReactorCasingBlock.TOP, getController().getY() + height - 1 == getBlockPos().getY());
            level.setBlock(getBlockPos(), state, Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE);
        }
        onFluidStackChanged(tankInventory.getFluid());
        //updateBoilerState();
        setChanged();
    }

    @Override
    public Direction.Axis getMainConnectionAxis() {
        return Direction.Axis.Y;
    }

    @Override
    public int getMaxLength(Direction.Axis longAxis, int width) {
        if (longAxis == Direction.Axis.Y)
            return getMaxHeight();
        return getMaxWidth();
    }

    @Override
    public int getMaxWidth() {
        return MAX_WIDTH;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public boolean hasTank() {
        return true;
    }

    @Override
    public int getTankSize(int tank) {
        return getCapacityMultiplier();
    }

    @Override
    public void setTankSize(int tank, int blocks) {
        applyReactorCasingSize(blocks);
    }

    @Override
    public IFluidTank getTank(int tank) {
        return tankInventory;
    }

    @Override
    public FluidStack getFluid(int tank) {
        return tankInventory.getFluid()
                .copy();
    }

    // Reactor Implementation

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        ObservePacketPayload.send(worldPosition, 0);

        ReactorCasingBlockEntity controllerTE = getControllerBE();
        if (controllerTE == null) return false;
        String spacing = "  ";
        tooltip.add(Component.literal(spacing)
                .append(Component.translatable("createatomic.tooltip.reactor.info").withStyle(ChatFormatting.WHITE)));

        tooltip.add(Component.literal(spacing)
                .append(Component.translatable("createatomic.tooltip.reactor.heat").withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.literal(spacing).append(Component.literal(" "))
                .append(Component.literal(ReactorPacketPayload.clientHeat + "/" + getMaxHeat() + "T").withStyle(ChatFormatting.AQUA)));

        tooltip.add(Component.literal(spacing)
                .append(Component.translatable("createatomic.tooltip.reactor.coolant").withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.literal(spacing).append(" ")
                .append(ReactorPacketPayload.clientCoolant + "/" + getMaxCoolant() + "U").withStyle(ChatFormatting.AQUA));

        return IHaveGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }

    @Override
    public void onObserved(ServerPlayer player, ObservePacketPayload pack) {
        ReactorCasingBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null) return;
        //System.out.println("Observed " + getHeat() + ":" + getCoolant());
        ReactorPacketPayload.send(worldPosition, getHeat(), getCoolant(), player);
    }

    public Pair<Integer, Integer> getRodLevels(boolean tick) {
        int fuelLevel = 0, controlLevel = 0;
        for (int x = 0; x < getWidth(); x++) {
            for (int z = 0; z < getWidth(); z++) {
                var pos = getController().offset(x, getHeight(), z);
                BlockEntity be = level.getBlockEntity(pos);
                if(!(be instanceof RodAssemblyBlockEntity rabe)) continue;
                fuelLevel += rabe.getFuelLevel();
                controlLevel += rabe.getControlLevel();
                if(tick) rabe.tickRod();
            }
        }
        return Pair.of(fuelLevel, controlLevel);
    }

    int cachedFuelRodLevel;
    int cachedControlRodLevel;
    @Override
    public void lazyTick() {
        super.lazyTick();
        if(!isController()) return;
        var rodLevels = getRodLevels(true);
        cachedFuelRodLevel = rodLevels.getFirst();
        cachedControlRodLevel = rodLevels.getSecond();
        reactorTick(cachedFuelRodLevel, cachedControlRodLevel);
    }

    public int getTotalSize() {
        var con = getControllerBE();
        if(con == null) return 1;
        return con.width * con.width * con.height;
    }

    private void reactorTick(int fuelLevel, int controlLevel) {
        /*
         * Fuel Rod +3 heat
         * Control Rod -1 heat
         * -1 * coolant where < size
         * meltdown: heat > 25 * size
         * */

        setHeat(Math.max(getHeat() + fuelLevel*3 - controlLevel, 0));
        if(getHeat() > 25*getTotalSize()) onMeltdown();

        int usedCoolant = Math.min(Math.min(getHeat(), getCoolant()), getTotalSize());
        setCoolant(getCoolant()-usedCoolant);

        System.out.println("reactorTick " + getHeat() + "T |" + getCoolant() + "U |" + fuelLevel + "F |" + controlLevel + "C");
    }

    public boolean hasReactor() {
        return true;
    }

    public int getHeat() {
        ReactorCasingBlockEntity controllerTE = getControllerBE();
        if (controllerTE == null) return 0;
        return controllerTE.reactorHeat;
    }

    public void setHeat(int heat) {
        ReactorCasingBlockEntity controllerTE = getControllerBE();
        if (controllerTE == null) return;
        controllerTE.reactorHeat = heat;
    }

    public int getCoolant() {
        ReactorCasingBlockEntity controllerTE = getControllerBE();
        if (controllerTE == null) return 0;
        return controllerTE.reactorCoolant;
    }

    public void setCoolant(int coolant) {
        ReactorCasingBlockEntity controllerTE = getControllerBE();
        if (controllerTE == null) return;
        controllerTE.reactorCoolant = coolant;
    }

    public boolean isActive() {
        return rodInsertion > 0f;
    }

    public void onRodChange() {

    }

    private boolean hasMeltdown = false;
    public void onMeltdown() {
        if(hasMeltdown) return;
        hasMeltdown = true;
        BlockPos con = getController();
        if(con == null || level == null) return;
        for(int x = 0; x < getWidth(); x++) {
            for(int y = 0; y < getHeight()+1; y++) {
                for(int z = 0; z < getWidth(); z++) {
                    int i = (int)(Math.random()*3d);
                    switch (i) {
                        case 0:
                            level.setBlock(con.offset(x, y, z), Blocks.LAVA.defaultBlockState(), Block.UPDATE_ALL);
                            break;
                        case 1:
                            level.setBlock(con.offset(x, y, z), AtomicBlocks.REACTOR_DEBRIS.getDefaultState(), Block.UPDATE_ALL);
                            break;
                        case 2:
                            level.setBlock(con.offset(x, y, z), Blocks.OBSIDIAN.defaultBlockState(), Block.UPDATE_ALL);
                            break;
                    }
                }
            }
        }
    }

    public int getMaxHeat() {
        return getTotalSize() * 64;
    }

    public int getMaxCoolant() {
        return getTotalSize() * 8;
    }
}