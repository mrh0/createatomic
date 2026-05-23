package com.mrh0.createatomic.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
    public static void handleObservePayload(final ObservePacketPayload pkt, final IPayloadContext ctx) {

    }

    public static void handleReactorPayload(final ReactorPacketPayload pkt, final IPayloadContext ctx) {
        ReactorPacketPayload.updateClientCache(pkt.pos(), pkt.heat(), pkt.coolant());
    }
}
