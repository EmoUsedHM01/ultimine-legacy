package com.ftbultimine.network;

import com.ftbultimine.PlayerDataManager;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class KeyPressedPacket implements IMessage {

    private boolean pressed;

    public KeyPressedPacket() {
    }

    public KeyPressedPacket(boolean pressed) {
        this.pressed = pressed;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pressed = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(pressed);
    }

    public static class Handler implements IMessageHandler<KeyPressedPacket, IMessage> {

        @Override
        public IMessage onMessage(KeyPressedPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            PlayerDataManager.PlayerData data = PlayerDataManager.get(player);
            data.setUltimineActive(message.pressed);
            return null;
        }
    }
}
