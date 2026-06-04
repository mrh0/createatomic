package com.mrh0.createatomic.network;

import com.mrh0.createatomic.CreateAtomic;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public record RodAssemblyPacketPayload(int fuelTicks) implements CustomPacketPayload {
    public static int clientFuelTicks = 0;

    public static final Type<RodAssemblyPacketPayload> TYPE = new Type<>(CreateAtomic.asResource("rod_assembly_packet"));

    public static final StreamCodec<ByteBuf, RodAssemblyPacketPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            RodAssemblyPacketPayload::fuelTicks,
            RodAssemblyPacketPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void updateClientCache(int fuelTicks) {
        clientFuelTicks = fuelTicks;
    }

    public static boolean send(int fuelTicks, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new RodAssemblyPacketPayload(fuelTicks));
        return true;
    }
}
