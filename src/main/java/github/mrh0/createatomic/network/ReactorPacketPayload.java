package github.mrh0.createatomic.network;

import github.mrh0.createatomic.CreateAtomic;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.function.Supplier;

public record ReactorPacketPayload (BlockPos pos, int heat, int coolant) implements CustomPacketPayload {
	public static int clientHeat = 0;
	public static int clientCoolant = 0;

	public static final CustomPacketPayload.Type<ReactorPacketPayload> TYPE = new CustomPacketPayload.Type<>(CreateAtomic.asResource("reactor_packet"));

	public static final StreamCodec<ByteBuf, ReactorPacketPayload> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC,
			ReactorPacketPayload::pos,
			ByteBufCodecs.VAR_INT,
			ReactorPacketPayload::heat,
			ByteBufCodecs.VAR_INT,
			ReactorPacketPayload::coolant,
			ReactorPacketPayload::new
	);

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void updateClientCache(BlockPos pos, int heat, int coolant) {
		clientHeat = heat;
		clientCoolant = coolant;
	}

	public static boolean send(BlockPos pos, int heat, int coolant, ServerPlayer player) {
		PacketDistributor.sendToPlayer(player, new ReactorPacketPayload(pos, heat, coolant));
		return true;
	}
}


