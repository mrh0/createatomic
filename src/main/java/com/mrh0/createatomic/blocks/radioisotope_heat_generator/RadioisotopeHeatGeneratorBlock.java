package com.mrh0.createatomic.blocks.radioisotope_heat_generator;

import com.mrh0.createatomic.config.AtomicConfigs;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import java.util.List;
import java.util.function.Supplier;

public class RadioisotopeHeatGeneratorBlock extends Block implements IWrenchable {

    // Average random ticks a single block receives per real-world day at 20 TPS and default randomTickSpeed=3:
    // (24h x 3600s x 20tps) game ticks/day x (3 chosen / 4096 blocks per section) ~= 1266 random ticks/day
    private static final double AVG_RANDOM_TICKS_PER_REAL_DAY = 24.0 * 3600.0 * 20.0 * 3.0 / 4096.0;

    private final HeatLevel heatLevel;
    // Null for the fully-decayed (NONE) variant — no further transition.
    private final Supplier<? extends Block> nextBlock;

    public RadioisotopeHeatGeneratorBlock(Properties properties, HeatLevel heatLevel, Supplier<? extends Block> nextBlock) {
        super(properties);
        this.heatLevel = heatLevel;
        this.nextBlock = nextBlock;
        registerDefaultState(defaultBlockState().setValue(BlazeBurnerBlock.HEAT_LEVEL, heatLevel));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlazeBurnerBlock.HEAT_LEVEL);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ChatFormatting color = switch (heatLevel) {
            case KINDLED -> ChatFormatting.GOLD;
            case SMOULDERING -> ChatFormatting.YELLOW;
            default -> ChatFormatting.GRAY;
        };
        Component level = Component.translatable("createatomic.tooltip.rhg.heat." + heatLevel.getSerializedName())
                .withStyle(color);
        tooltip.add(Component.translatable("createatomic.tooltip.rhg.heat_output", level));
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return InteractionResult.PASS;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (nextBlock == null) return;
        // Compute the expected number of random ticks before a decay transition occurs.
        double avgDecayDays = AtomicConfigs.server().rhgDecayDays.get();
        int chance = Math.max(1, (int) Math.round(AVG_RANDOM_TICKS_PER_REAL_DAY * avgDecayDays));
        if (random.nextInt(chance) == 0) {
            level.setBlockAndUpdate(pos, nextBlock.get().defaultBlockState());
        }
    }
}
