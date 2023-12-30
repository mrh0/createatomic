package github.mrh0.createatomic.network;

import net.minecraft.server.level.ServerPlayer;

public interface IObserveBlockEntity {
	void onObserved(ServerPlayer player, ObservePacket pack);
}
