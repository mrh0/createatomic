package github.mrh0.createatomic.blocks.reactor_casing;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.simibubi.create.content.fluids.tank.BoilerData;
import github.mrh0.createatomic.blocks.reactor_redstone_interface.ReactorRedstoneInterfaceBlock;
import github.mrh0.createatomic.blocks.rod_assembly.RodAssemblyBlockEntity;
import github.mrh0.createatomic.blocks.turbine.TurbineBlock;
import github.mrh0.createatomic.blocks.turbine.TurbineBlockEntity;
import github.mrh0.createatomic.index.AtomicBlockEntities;
import github.mrh0.createatomic.index.AtomicBlocks;
import github.mrh0.createatomic.network.IObserveBlockEntity;
import github.mrh0.createatomic.network.ObservePacketPayload;
import net.createmod.catnip.data.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import github.mrh0.createatomic.config.AtomicConfigs;
import github.mrh0.createatomic.index.AtomicSounds;

import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.animation.LerpedFloat.Chaser;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
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

    int reactorHeat   = 25;
    float reactorHealth = 100f;
    boolean hasMeltdown = false;
    private boolean wasRunning = false;
    public boolean cachedScrammed = false;
    public int poweredInterfaces = 0; // synced to client; isArmed() derives from this

    int cachedEffectivePower;
    int cachedControlRodLevel;
    int cachedHullCapacity;
    int cachedInstalledFuelRods;
    float cachedReactivityFactor = 1f;
    public float turbineTargetRpm;  // RPM each connected turbine should reach
    int cachedTurbineCount;

    public LerpedFloat gauge;

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
        if (gauge != null)
            gauge.tickChaser();
        // boiler.tick() requires FluidTankBlockEntity; steam engine integration deferred

        if (level != null && level.isClientSide() && isController())
            ReactorSoundManager.tick(this);
    }


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

            reactorHeat         = compound.getInt("ReactorHeat");
            reactorHealth       = compound.contains("ReactorHealth") ? compound.getFloat("ReactorHealth") : 100f;
            cachedEffectivePower  = compound.getInt("EffectivePower");
            cachedHullCapacity    = compound.getInt("HullCapacity");
            cachedInstalledFuelRods = compound.getInt("InstalledRods");
            cachedReactivityFactor  = compound.contains("ReactivityFactor") ? compound.getFloat("ReactivityFactor") : 1f;
            turbineTargetRpm    = compound.getFloat("TurbineRpm");
            cachedTurbineCount  = compound.getInt("TurbineCount");
            poweredInterfaces = compound.getInt("PoweredInterfaces");
            cachedScrammed    = compound.contains("Scrammed") && compound.getBoolean("Scrammed");
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

        if (isController()) {
            // Gauge tracks temperature: 25°C = 0, 315°C = 1 (pegs at 1 and shakes above 315°C)
            float gaugeRatio = Math.min(1f, (reactorHeat - 25f) / 290f);
            if (gauge == null)
                gauge = LerpedFloat.linear().startWithValue(gaugeRatio);
            gauge.chase(gaugeRatio, 0.4f, Chaser.EXP);
        }
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
            compound.putInt("ReactorHeat", reactorHeat);
            compound.putFloat("ReactorHealth", reactorHealth);
            compound.putInt("EffectivePower", cachedEffectivePower);
            compound.putInt("HullCapacity", cachedHullCapacity);
            compound.putInt("InstalledRods", cachedInstalledFuelRods);
            compound.putFloat("ReactivityFactor", cachedReactivityFactor);
            compound.putFloat("TurbineRpm", turbineTargetRpm);
            compound.putInt("TurbineCount", cachedTurbineCount);
            compound.putBoolean("Scrammed", cachedScrammed);
            compound.putInt("PoweredInterfaces", poweredInterfaces);
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
        return 1000; // 1 bucket (1000 mB) per casing block
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

        ReactorCasingBlockEntity con = getControllerBE();
        if (con == null) return false;
        String s = "  ";

        tooltip.add(Component.literal(s).append(
                Component.translatable("createatomic.tooltip.reactor.info").withStyle(ChatFormatting.WHITE)));

        // SCRAM state (interface powered = control rods forcibly inserted)
        if (con.cachedScrammed) {
            tooltip.add(Component.literal(s).append(
                    Component.translatable("createatomic.tooltip.reactor.scrammed").withStyle(ChatFormatting.RED)));
        }

        // Active / inactive status
        boolean active = con.isActive();
        tooltip.add(Component.literal(s).append(
                Component.translatable(active
                        ? "createatomic.tooltip.reactor.active"
                        : "createatomic.tooltip.reactor.inactive")
                        .withStyle(active ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY)));

        // Net power vs capacity: green = safe, yellow = hot (>1×), red = damaging (>2×)
        int netPowerDisplay = con.cachedEffectivePower - con.cachedControlRodLevel;
        boolean hot      = netPowerDisplay > con.cachedHullCapacity;
        boolean damaging = netPowerDisplay > con.cachedHullCapacity * 2;
        ChatFormatting powerColour = damaging ? ChatFormatting.RED : hot ? ChatFormatting.YELLOW : ChatFormatting.GREEN;
        tooltip.add(Component.literal(s).append(
                Component.translatable("createatomic.tooltip.reactor.capacity").withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.literal(s + " ").append(
                Component.literal(String.valueOf(con.cachedEffectivePower))
                        .withStyle(powerColour))
                        .append(Component.literal(" (-" + String.valueOf(con.cachedControlRodLevel) + ")")
                        .withStyle(con.cachedControlRodLevel > 0 ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY))
                        .append(Component.literal(" / " + con.cachedHullCapacity)
                        .withStyle(powerColour)));

        // Temperature (display-only)
        tooltip.add(Component.literal(s).append(
                Component.translatable("createatomic.tooltip.reactor.heat").withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.literal(s + " ").append(
                Component.literal(con.reactorHeat + "°C").withStyle(con.reactorHeat > 315 ? ChatFormatting.RED : ChatFormatting.AQUA)));

        // Hull integrity - colour-coded by damage level
        int hp = (int) con.reactorHealth;
        ChatFormatting hpColour = hp > 75 ? ChatFormatting.GREEN : hp > 40 ? ChatFormatting.YELLOW : ChatFormatting.RED;
        tooltip.add(Component.literal(s).append(
                Component.translatable("createatomic.tooltip.reactor.health").withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.literal(s + " ").append(
                Component.literal(hp + "%").withStyle(hpColour)));

        // Water content
        int waterMb  = con.tankInventory.getFluidAmount();
        int waterCap = con.tankInventory.getCapacity();
        boolean waterLow = con.isActive() && waterMb == 0;
        tooltip.add(Component.literal(s).append(
                Component.translatable("createatomic.tooltip.reactor.water").withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.literal(s + " ").append(
                Component.literal(waterMb + " / " + waterCap + " mB")
                        .withStyle(waterLow ? ChatFormatting.RED : ChatFormatting.AQUA)));

        // Turbines (shown only when at least one is connected)
        if (con.cachedTurbineCount > 0) {
            tooltip.add(Component.literal(s).append(
                    Component.translatable("createatomic.tooltip.reactor.turbines").withStyle(ChatFormatting.GRAY)));
            tooltip.add(Component.literal(s + " ").append(
                    Component.literal(con.cachedTurbineCount + "× @ " + String.format("%.0f", con.turbineTargetRpm) + " RPM")
                            .withStyle(ChatFormatting.AQUA)));
        }

        int effective = Math.round(con.cachedInstalledFuelRods * con.cachedReactivityFactor);
        ChatFormatting rxColour = con.cachedReactivityFactor >= 2f ? ChatFormatting.RED : ChatFormatting.YELLOW;
        tooltip.add(Component.literal(s).append(
                Component.translatable("createatomic.tooltip.reactor.reactivity").withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.literal(s + " ").append(
                Component.literal(effective + " effective (" + String.format("%.1f", con.cachedReactivityFactor) + "×)")
                        .withStyle(rxColour)));
        return IHaveGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }

    @Override
    public void onObserved(ServerPlayer player, ObservePacketPayload pack) {
        // Push the controller's latest NBT (including reactorHeat / reactorCoolant)
        // to all nearby clients immediately. The tooltip reads from the per-instance
        // block entity fields, so this one call correctly updates any reactor the player
        // is looking at without relying on a global static cache.
        ReactorCasingBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null) return;
        controllerBE.sendDataImmediately();
    }

    public Pair<Integer, Integer> getRodLevels(boolean tick) {
        int w = getWidth();
        // Pass 1: populate grids for fuel rods and neutron reflectors
        RodAssemblyBlockEntity[][] grid          = new RodAssemblyBlockEntity[w][w];
        int[][]                   fuelGrid       = new int[w][w]; // 1 = active fuel rod
        boolean[][]               reflectorGrid  = new boolean[w][w]; // true = neutron reflector

        for (int x = 0; x < w; x++) {
            for (int z = 0; z < w; z++) {
                BlockEntity be = level.getBlockEntity(getController().offset(x, getHeight(), z));
                if (be instanceof RodAssemblyBlockEntity rabe) {
                    grid[x][z]         = rabe;
                    fuelGrid[x][z]     = rabe.getFuelLevel();
                    reflectorGrid[x][z] = rabe.getConfig().isReflector();
                }
            }
        }

        // Pass 2: tally fuel/control with adjacency reactivity bonus.
        // Each fuel rod gains +50% power per orthogonally adjacent fuel rod OR neutron reflector.
        //   1 neighbour 1.5x,  2 neighbours 2.0x,  3 neighbours 2.5x, etc.
        int[] dx = {-1, 1, 0, 0};
        int[] dz = { 0, 0,-1, 1};

        float effectiveFuel = 0f;
        int installedFuel   = 0;
        int controlLevel    = 0;

        for (int x = 0; x < w; x++) {
            for (int z = 0; z < w; z++) {
                RodAssemblyBlockEntity rabe = grid[x][z];
                if (rabe == null) continue;

                controlLevel += rabe.getControlLevel();

                int fuel = fuelGrid[x][z];
                if (fuel > 0) {
                    installedFuel += fuel;
                    int neighbours = 0;
                    for (int d = 0; d < 4; d++) {
                        int nx = x + dx[d], nz = z + dz[d];
                        if (nx >= 0 && nx < w && nz >= 0 && nz < w
                                && (fuelGrid[nx][nz] > 0 || reflectorGrid[nx][nz]))
                            neighbours++;
                    }
                    effectiveFuel += fuel * (1f + 0.5f * neighbours);
                }

                if (tick) rabe.tickRod();
            }
        }

        // Cache for display / goggle tooltip (updated server-side each lazy tick)
        cachedInstalledFuelRods  = installedFuel;
        cachedReactivityFactor   = installedFuel > 0 ? effectiveFuel / installedFuel : 1f;

        return Pair.of(Math.round(effectiveFuel), controlLevel);
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (!isController()) return;
        var rodLevels = getRodLevels(true);
        cachedEffectivePower  = rodLevels.getFirst();
        cachedControlRodLevel = rodLevels.getSecond();
        // SCRAMed = control rods inserted (not lifted) AND sufficient to stop the reactor
        cachedScrammed = !isArmed() && cachedControlRodLevel > 0
                && cachedControlRodLevel >= cachedEffectivePower;
        reactorTick(cachedEffectivePower, cachedControlRodLevel);
        sendData();
    }

    // Called by ReactorRedstoneInterfaceBlock whenever it is placed, removed, or changes power state.
    // wasActive = the interface was contributing a signal before this event.
    // nowActive = the interface is contributing a signal after this event.
    public void onInterfaceSignalChanged(boolean wasActive, boolean nowActive) {
        ReactorCasingBlockEntity con = getControllerBE();
        if (con == null) return;
        if (wasActive) con.poweredInterfaces = Math.max(0, con.poweredInterfaces - 1);
        if (nowActive) con.poweredInterfaces++;
        con.sendData();
    }

    public int getTotalSize() {
        var con = getControllerBE();
        if(con == null) return 1;
        return con.width * con.width * con.height;
    }

    private static final float MAX_TURBINE_RPM = 256f;

    private void reactorTick(int effectivePower, int controlLevel) {
        // Each casing block contributes 1 capacity unit.
        int hullCapacity = getTotalSize();
        cachedHullCapacity = hullCapacity;

        // Armed (signal ON) = control rods lifted, their suppression ignored.
        int effectiveControl = isArmed() ? 0 : controlLevel;
        int netPower = Math.max(0, effectivePower - effectiveControl);

        // When meltdowns are disabled, a reactor at 0% health is forced offline until repaired.
        boolean meltdownsEnabled = AtomicConfigs.server().meltdownEnabled.get();
        if (!meltdownsEnabled && reactorHealth <= 0f) netPower = 0;

        boolean isRunning = netPower > 0;

        // 25°C idle → 315°C at capacity → 895°C at 3× overload
        if (isRunning) {
            reactorHeat = (int)(25 + 290 * Math.min(3.0, (double) netPower / Math.max(1, hullCapacity)));
        } else {
            reactorHeat = Math.max(25, reactorHeat - 5); // passive cool-down display
        }

        // Each active turbine requires 8 mB per lazy tick; turbines stop if water runs dry.
        int requiredWater = cachedTurbineCount * 8;
        boolean hasEnoughWater = cachedTurbineCount == 0 || tankInventory.getFluidAmount() >= requiredWater;
        if (isRunning && cachedTurbineCount > 0)
            tankInventory.drain(new FluidStack(Fluids.WATER, requiredWater), FluidAction.EXECUTE);

        // Hull damage only begins when net power exceeds 2× capacity.
        if (netPower > hullCapacity * 2) {
            float damage = (float)(netPower - hullCapacity * 2) / Math.max(1, hullCapacity);
            reactorHealth = Math.max(0f, reactorHealth - damage);
            if (reactorHealth <= 0f) {
                if (meltdownsEnabled) { onMeltdown(); return; }
                // Meltdowns disabled: clamp health to 0, reactor is shut down (netPower already 0 next tick).
            }
        }

        // Self-repair when fully cooled (same threshold as rod unlock and active indicator).
        if (reactorHeat <= 25 && reactorHealth < 100f)
            reactorHealth = Math.min(100f, reactorHealth + AtomicConfigs.server().hullRegenRate.get().floatValue());

        // Fire startup beep once from the multiblock centre when transitioning to active.
        if (isRunning && !wasRunning) {
            BlockPos centre = getBlockPos().offset(width / 2, height / 2, width / 2);
            level.playSound(null, centre, AtomicSounds.REACTOR_BEEP.value(), SoundSource.BLOCKS, 1.5f, 1.0f);
        }
        wasRunning = isRunning;

        // Control rods throttle turbines; turbines also need sufficient water to spin.
        scanAndUpdateTurbines(isRunning && hasEnoughWater ? netPower : 0);

        boiler.needsHeatLevelUpdate = true;
    }

    // Scans every face of the multiblock bounding box for attached turbine chains,
    // then sets turbineTargetRpm = MAX_TURBINE_RPM x min(1, netPower / totalTurbines).
    // Turbines may be stacked inline (Reactor -> T1 -> T2 -> T3) and all count toward
    // the total.  Because every turbine in a chain generates the same RPM, the total
    // SU output is constant regardless of chain length (N turbines x RPM/N x 8 SU/RPM).
    private void scanAndUpdateTurbines(int netPower) {
        int w = getWidth(), h = getHeight();
        BlockPos con = getController();
        Set<BlockPos> adjacentChecked = new HashSet<>(); // guards against double-scanning faces
        Set<BlockPos> allTurbines     = new HashSet<>(); // deduplicates across overlapping chains

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                for (int z = 0; z < w; z++) {
                    for (Direction dir : Direction.values()) {
                        BlockPos neighbor = con.offset(x, y, z).relative(dir);
                        // Skip if inside the bounding box
                        if (neighbor.getX() >= con.getX() && neighbor.getX() < con.getX() + w
                         && neighbor.getY() >= con.getY() && neighbor.getY() < con.getY() + h
                         && neighbor.getZ() >= con.getZ() && neighbor.getZ() < con.getZ() + w)
                            continue;
                        if (!adjacentChecked.add(neighbor)) continue;

                        BlockEntity adj = level.getBlockEntity(neighbor);
                        if (!(adj instanceof TurbineBlockEntity entryTurbine)) continue;
                        if (entryTurbine.getBlockState().getValue(TurbineBlock.FACING) != dir) continue;

                        // Found an entry-point turbine - follow the chain outward (max 16 deep)
                        BlockPos chainPos = neighbor;
                        for (int depth = 0; depth < 16; depth++) {
                            if (!allTurbines.add(chainPos)) break; // already counted
                            BlockPos nextPos = chainPos.relative(dir);
                            BlockEntity nextBE = level.getBlockEntity(nextPos);
                            if (!(nextBE instanceof TurbineBlockEntity nextT)) break;
                            if (nextT.getBlockState().getValue(TurbineBlock.FACING) != dir) break;
                            chainPos = nextPos;
                        }
                    }
                }
            }
        }

        cachedTurbineCount = allTurbines.size();
        // 1 turbine required per effective power unit
        turbineTargetRpm = cachedTurbineCount == 0 ? 0f
                : Math.min(MAX_TURBINE_RPM, MAX_TURBINE_RPM * (float)(netPower) / cachedTurbineCount);
    }

    public boolean shouldMeltdownOnBreak() {
        return !hasMeltdown && isActive();
    }

    public void observe() {
        if (level == null || !level.isClientSide()) return;
        ObservePacketPayload.send(worldPosition, 0);
    }

    public boolean isActive() { return reactorHeat > 25; }

    public boolean isArmed() { return poweredInterfaces > 0; }

    public int getTemperature() {
        return reactorHeat;
    }

    // Used by AtomicConnectivityHandler to redistribute hull integrity when multis split/merge.
    public int getHeat() {
        ReactorCasingBlockEntity con = getControllerBE();
        return con == null ? 100 : (int) con.reactorHealth;
    }

    public void setHeat(int value) {
        ReactorCasingBlockEntity con = getControllerBE();
        if (con != null) con.reactorHealth = Math.max(0, Math.min(100, value));
    }

    public boolean hasReactor() { return true; }

    public void onMeltdown() {
        if (hasMeltdown) return;
        hasMeltdown = true;
        BlockPos con = getController();
        if (con == null || level == null) return;

        if (AtomicConfigs.server().meltdownExplosion.get()) {
            float radius = Math.max(2.0f, (width + height) / 2.0f);
            level.explode(null,
                    con.getX() + width / 2.0,
                    con.getY() + height / 2.0,
                    con.getZ() + width / 2.0,
                    radius,
                    Level.ExplosionInteraction.TNT);
        }

        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight() + 1; y++) {
                for (int z = 0; z < getWidth(); z++) {
                    int i = (int) (Math.random() * 3d);
                    level.setBlock(con.offset(x, y, z), switch (i) {
                        case 0 -> Blocks.LAVA.defaultBlockState();
                        case 1 -> AtomicBlocks.REACTOR_DEBRIS.getDefaultState();
                        default -> Blocks.OBSIDIAN.defaultBlockState();
                    }, Block.UPDATE_ALL);
                }
            }
        }
    }
}