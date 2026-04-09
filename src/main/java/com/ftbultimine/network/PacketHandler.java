package com.ftbultimine.network;

import com.ftbultimine.FTBUltimine;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {

    public static SimpleNetworkWrapper INSTANCE;

    private static int packetId = 0;

    public static int nextPacketId() {
        return packetId++;
    }

    public static void init() {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(FTBUltimine.MOD_ID);

        // Client -> Server
        INSTANCE.registerMessage(
            KeyPressedPacket.Handler.class, KeyPressedPacket.class, nextPacketId(), Side.SERVER);
        INSTANCE.registerMessage(
            ShapeChangedPacket.Handler.class, ShapeChangedPacket.class, nextPacketId(), Side.SERVER);
    }

    public static void initClient() {
        // Server -> Client (must be registered on client side only,
        // as the handler references client-only classes)
        INSTANCE.registerMessage(
            SyncShapePacket.Handler.class, SyncShapePacket.class, nextPacketId(), Side.CLIENT);
    }
}
