package com.ftbultimine.client;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.ftbultimine.PlayerDataManager;
import com.ftbultimine.PlayerDataManager.PlayerData;
import com.ftbultimine.network.KeyPressedPacket;
import com.ftbultimine.network.PacketHandler;
import com.ftbultimine.network.ShapeChangedPacket;
import com.ftbultimine.shape.ShapeType;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.MouseEvent;

public class KeyHandler {

    public static final KeyBinding ULTIMINE_KEY = new KeyBinding(
        "key.ftbultimine", Keyboard.KEY_GRAVE, "key.categories.ftbultimine");

    private boolean wasPressed = false;

    public KeyHandler() {
        ClientRegistry.registerKeyBinding(ULTIMINE_KEY);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        boolean isPressed = ULTIMINE_KEY.getIsKeyPressed();

        if (isPressed != wasPressed) {
            wasPressed = isPressed;
            PacketHandler.INSTANCE.sendToServer(new KeyPressedPacket(isPressed));

            // Update client-side data too for rendering
            PlayerData data = PlayerDataManager.get(mc.thePlayer);
            data.setUltimineActive(isPressed);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMouseEvent(MouseEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        if (!ULTIMINE_KEY.getIsKeyPressed()) return;

        int scroll = event.dwheel;
        if (scroll == 0) return;

        // Cancel the event to prevent hotbar scrolling
        event.setCanceled(true);

        PlayerData data = PlayerDataManager.get(mc.thePlayer);
        data.cycleShape(scroll > 0);

        // Sync to server
        PacketHandler.INSTANCE.sendToServer(
            new ShapeChangedPacket(data.getCurrentShape().ordinal()));

        // Show shape name in chat
        ShapeType shape = data.getCurrentShape();
        mc.thePlayer.addChatMessage(
            new net.minecraft.util.ChatComponentText(
                "\u00a7e[Ultimine] \u00a7fShape: \u00a7a" + shape.getDisplayName()));
    }
}
