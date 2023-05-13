package github.mrh0.createatomic.blocks.reactor_casing;

import com.simibubi.create.Create;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.logistics.block.redstone.StockpileSwitchObservable;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.utility.Pair;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import github.mrh0.createatomic.blocks.rod_assembly.RodAssemblyBlockEntity;
import github.mrh0.createatomic.debug.IDebugDrawer;
import github.mrh0.createatomic.index.AtomicBlocks;
import github.mrh0.createatomic.reactor.IReactor;
import github.mrh0.createatomic.reactor.MagmaticReactor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ReactorCasingBlockEntity extends SmartTileEntity implements IHaveGoggleInformation, IMultiTileReactorContainer, IDebugDrawer, StockpileSwitchObservable {
    public static final int CAPACITY = 0,
            MAX_IN = 0,
            MAX_OUT = 0,
            MAX_HEIGHT = 8,
            MAX_WIDTH = 5;

    protected BlockPos controller;
    protected BlockPos lastKnownPos;
    protected boolean updateConnectivity;
    protected int width;
    protected int height;

    private static final int SYNC_RATE = 8;
    protected int syncCooldown;
    protected boolean queuedSync;

    private LazyOptional<IEnergyStorage> escacheUp = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> escacheDown = LazyOptional.empty();
    // protected LazyOptional<ReactorPeripheral> peripheral;

    private IReactor reactor = new MagmaticReactor();

    public ReactorCasingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        updateConnectivity = false;
        height = 1;
        width = 1;
        refreshCapability();
        setLazyTickRate(20);
        //if (CreateAtomic.CC_ACTIVE)
        //    this.peripheral = LazyOptional.of(() -> Peripherals.createReactorPeripheral(this));
    }

    protected void updateConnectivity() {
        updateConnectivity = false;
        if (level.isClientSide)
            return;
        if (!isController())
            return;
        AtomicConnectivityHandler.formMulti(this);
    }

    public LerpedFloat gauge = LerpedFloat.linear();

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

        if (updateConnectivity)
            updateConnectivity();

        // Tick Logic:
        if (!isController()) return;

        if (level.isClientSide()) {
            gauge.tickChaser();
            float current = gauge.getValue(1);
            if (current > 1 && Create.RANDOM.nextFloat() < 1 / 2f)
                gauge.setValueNoUpdate(current + Math.min(-(current - 1) * Create.RANDOM.nextFloat(), 0));
            return;
        }
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

    @SuppressWarnings("unchecked")
    @Override
    public ReactorCasingBlockEntity getControllerTE() {
        if (isController())
            return this;
        BlockEntity tileEntity = level.getBlockEntity(controller);
        if (tileEntity instanceof ReactorCasingBlockEntity)
            return (ReactorCasingBlockEntity) tileEntity;
        return null;
    }

    public void applySize(int blocks) {

    }

    public void removeController(boolean keepEnergy) {
        if (level.isClientSide)
            return;
        updateConnectivity = true;
        if (!keepEnergy)
            applySize(1);
        controller = null;
        width = 1;
        height = 1;
        //boiler.clear();

        BlockState state = getBlockState();
        if (ReactorCasingBlock.isReactor(state)) {
            state = state.setValue(ReactorCasingBlock.BOTTOM, true);
            state = state.setValue(ReactorCasingBlock.TOP, true);
            getLevel().setBlock(worldPosition, state, 22);
        }

        refreshCapability();
        setChanged();
        sendData();
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

    private void refreshCapability() {

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

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        BlockPos controllerBefore = controller;
        int prevSize = width;
        int prevHeight = height;

        updateConnectivity = compound.contains("Uninitialized");
        controller = null;
        lastKnownPos = null;

        if (compound.contains("LastKnownPos"))
            lastKnownPos = NbtUtils.readBlockPos(compound.getCompound("LastKnownPos"));
        if (compound.contains("Controller"))
            controller = NbtUtils.readBlockPos(compound.getCompound("Controller"));

        if (isController()) {
            width = compound.getInt("Size");
            height = compound.getInt("Height");
        }


        if (!clientPacket)
            return;

        boolean changeOfController =
                controllerBefore == null ? controller != null : !controllerBefore.equals(controller);
        if (changeOfController || prevSize != width || prevHeight != height) {
            if (hasLevel())
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
            if (isController())
                // energyStorage.setCapacity(getCapacityMultiplier() * getTotalAccumulatorSize());
            invalidateRenderBoundingBox();
        }

        if (isController())
            gauge.chase(getFillState(), 0.125f, LerpedFloat.Chaser.EXP);
    }

    public float getFillState() {
        return 0f;
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        if (updateConnectivity)
            compound.putBoolean("Uninitialized", true);
        if (lastKnownPos != null)
            compound.put("LastKnownPos", NbtUtils.writeBlockPos(lastKnownPos));
        if (!isController())
            compound.put("Controller", NbtUtils.writeBlockPos(controller));
        if (isController()) {
            compound.putInt("Size", width);
            compound.putInt("Height", height);
        }
        super.write(compound, clientPacket);

        if (!clientPacket)
            return;
        if (queuedSync)
            compound.putBoolean("LazySync", true);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        // if (CreateAtomic.CC_ACTIVE && Peripherals.isPeripheral(cap)) return this.peripheral.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidate() {
        // energyCap.invalidate();
        super.invalidate();
    }

    public int getTotalSize() {
        return width * width * height;
    }

    public static int getMaxHeight() {
        return MAX_HEIGHT;
    }

    @Override
    public int getMaxWidth() {
        return MAX_WIDTH;
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
            level.setBlock(getBlockPos(), state, Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE);
        }
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
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        ReactorCasingBlockEntity controllerTE = getControllerTE();
        if (controllerTE == null)
            return false;

        return true;
    }

    public void setSize(int reactor, int blocks) {
        applySize(blocks);
    }

    @Override
    public void drawDebug() {
        if (level == null) return;
        ReactorCasingBlockEntity controller = getControllerTE();
        if (controller == null) return;
        // Outline controller.
        VoxelShape shape = level.getBlockState(controller.getBlockPos()).getBlockSupportShape(level, controller.getBlockPos());
        CreateClient.OUTLINER.chaseAABB("atomic_reactor_casing", shape.bounds().move(controller.getBlockPos())).lineWidth(0.0625F).colored(0xFF5B5B);
    }

    @Override
    public float getPercent() {
        ReactorCasingBlockEntity controllerTE = getControllerTE();
        if (controllerTE == null) return 0f;
        return controllerTE.getFillState() * 100f;
    }

    public Pair<Integer, Integer> getRodLevels() {
        int fuelLevel = 0, controlLevel = 0;
        int y = getHeight() + getController().getY() + 1;
        for (int x = 0; x < getWidth(); x++) {
            for (int z = 0; z < getWidth(); z++) {
                BlockEntity be = level.getBlockEntity(new BlockPos(x, y, z));
                if(!(be instanceof RodAssemblyBlockEntity rabe)) continue;
                fuelLevel += rabe.getFuelLevel();
                controlLevel += rabe.getControlLevel();
            }
        }
        return Pair.of(fuelLevel, controlLevel);
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        var rodLevels = getRodLevels();
        reactor.reactorTick(getTotalSize(), rodLevels.getFirst(), rodLevels.getSecond(), 1f);
    }

    public boolean hasReactor() {
        return true;
    }

    public int getHeat() {
        return reactor.getHeat();
    }

    public void setHeat(int heat) {
        reactor.setHeat(heat);
    }

    public boolean isActive() {
        return false;
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
            for(int y = 0; y < getHeight(); y++) {
                for(int z = 0; z < getWidth(); z++) {
                    int i = (int)(Math.random()*3d);
                    switch (i) {
                        case 0:
                            level.setBlock(con.offset(x, y, z), Blocks.LAVA.defaultBlockState(), Block.UPDATE_ALL);
                            break;
                        case 1:
                            level.setBlock(con.offset(x, y, z), AtomicBlocks.REACTOR_DEBRIS.getDefaultState(), Block.UPDATE_ALL);
                            break;
                    }
                }
            }
        }
    }
}
