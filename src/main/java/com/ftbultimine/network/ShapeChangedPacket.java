package com.ftbultimine.network;

import com.ftbultimine.PlayerDataManager;
import com.ftbultimine.shape.ShapeType;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class ShapeChangedPacket implements IMessage {

    private int shapeOrdinal;

    public ShapeChangedPacket() {
    }

    public ShapeChangedPacket(int shapeOrdinal) {
        this.shapeOrdinal = shapeOrdinal;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        shapeOrdinal = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(shapeOrdinal);
    }

    public static class Handler implements IMessageHandler<ShapeChangedPacket, IMessage> {

        @Override
        public IMessage onMessage(ShapeChangedPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            PlayerDataManager.PlayerData data = PlayerDataManager.get(player);
            ShapeType[] types = ShapeType.values();
            if (message.shapeOrdinal >= 0 && message.shapeOrdinal < types.length) {
                data.setCurrentShape(types[message.shapeOrdinal]);
            }
            return null;
        }
    }
}
