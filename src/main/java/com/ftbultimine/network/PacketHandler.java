package com.ftbultimine.network;

import com.ftbultimine.FTBUltimine;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {

    public static SimpleNetworkWrapper INSTANCE;

    private static int packetId = 0;

    public static void init() {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(FTBUltimine.MOD_ID);

        // Client -> Server
        INSTANCE.registerMessage(
            KeyPressedPacket.Handler.class, KeyPressedPacket.class, packetId++, Side.SERVER);
        INSTANCE.registerMessage(
            ShapeChangedPacket.Handler.class, ShapeChangedPacket.class, packetId++, Side.SERVER);

        // Server -> Client
        INSTANCE.registerMessage(
            SyncShapePacket.Handler.class, SyncShapePacket.class, packetId++, Side.CLIENT);
    }
}
