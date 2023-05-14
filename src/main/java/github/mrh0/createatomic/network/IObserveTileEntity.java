package github.mrh0.createatomic.network;

import net.minecraft.server.level.ServerPlayer;

public interface IObserveTileEntity {
	void onObserved(ServerPlayer player, ObservePacket pack);
}
