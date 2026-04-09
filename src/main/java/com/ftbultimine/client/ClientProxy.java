package com.ftbultimine.client;

import com.ftbultimine.CommonProxy;
import com.ftbultimine.network.PacketHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        PacketHandler.initClient();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        KeyHandler handler = new KeyHandler();
        FMLCommonHandler.instance().bus().register(handler);
        MinecraftForge.EVENT_BUS.register(handler);
        MinecraftForge.EVENT_BUS.register(new OutlineRenderer());
    }
}
