package github.mrh0.createatomic.network;

import github.mrh0.createatomic.CreateAtomic;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class SyncReactorPacket {
	private BlockPos pos;
	private int heat;
	private int coolant;
	private float rodInsertion;

	public static int clientHeat = 0;
	public static int clientCoolant = 0;
	public static float clientRodInsertion = 0;

	public SyncReactorPacket(BlockPos pos, int heat, int coolant, float rodInsertion) {
		this.pos = pos;
		this.heat = heat;
		this.coolant = coolant;
		this.rodInsertion = rodInsertion;
	}
	
	public static void encode(SyncReactorPacket packet, FriendlyByteBuf tag) {
        tag.writeBlockPos(packet.pos);
        tag.writeInt(packet.heat);
        tag.writeInt(packet.coolant);
		tag.writeFloat(packet.rodInsertion);
    }
	
	public static SyncReactorPacket decode(FriendlyByteBuf buf) {
		SyncReactorPacket scp = new SyncReactorPacket(buf.readBlockPos(), buf.readInt(), buf.readInt(), buf.readFloat());
        return scp;
    }
	
	public static void handle(SyncReactorPacket pkt, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			try {
				updateClientCache(pkt.pos, pkt.heat, pkt.coolant, pkt.rodInsertion);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
	
	private static void updateClientCache(BlockPos pos, int heat, int coolant, float rodInsertion) {
		clientHeat = heat;
		clientCoolant = coolant;
		clientRodInsertion = rodInsertion;
    }
	
	public static void send(BlockPos pos, int heat, int coolant, float rodInsertion, ServerPlayer player) {
		CreateAtomic.Network.send(PacketDistributor.PLAYER.with(() -> player), new SyncReactorPacket(pos, heat, coolant, rodInsertion));
	}
}
