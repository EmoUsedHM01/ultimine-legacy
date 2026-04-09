package com.ftbultimine;

import com.ftbultimine.handler.UltimineHandler;
import com.ftbultimine.handler.RightClickHandler;
import com.ftbultimine.network.PacketHandler;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(
    modid = FTBUltimine.MOD_ID,
    name = FTBUltimine.MOD_NAME,
    version = Tags.VERSION,
    acceptedMinecraftVersions = "[1.7.10]"
)
public class FTBUltimine {

    public static final String MOD_ID = "ftbultimine";
    public static final String MOD_NAME = "FTB Ultimine";

    @Mod.Instance(MOD_ID)
    public static FTBUltimine instance;

    @SidedProxy(
        clientSide = "com.ftbultimine.client.ClientProxy",
        serverSide = "com.ftbultimine.CommonProxy"
    )
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.init(event.getSuggestedConfigurationFile());
        PacketHandler.init();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        UltimineHandler.init();
        RightClickHandler.init();
        proxy.init(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        PlayerDataManager.clear();
    }
}
