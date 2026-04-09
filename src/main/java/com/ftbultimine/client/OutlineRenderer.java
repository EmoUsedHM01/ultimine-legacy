package com.ftbultimine.client;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.ftbultimine.Config;
import com.ftbultimine.PlayerDataManager;
import com.ftbultimine.PlayerDataManager.PlayerData;
import com.ftbultimine.shape.BlockPosHelper.MutableBlockPos;
import com.ftbultimine.shape.Shape;
import com.ftbultimine.shape.ShapeRegistry;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Renders an outline around blocks that will be affected by ultimine.
 */
public class OutlineRenderer {

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (Config.renderOutlineMax <= 0) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        PlayerData data = PlayerDataManager.get(player);
        if (!data.isUltimineActive()) return;

        // Get what block the player is looking at
        MovingObjectPosition mop = mc.objectMouseOver;
        if (mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;

        World world = mc.theWorld;
        int x = mop.blockX;
        int y = mop.blockY;
        int z = mop.blockZ;

        Block block = world.getBlock(x, y, z);
        if (block.isAir(world, x, y, z)) return;

        ForgeDirection face = ForgeDirection.getOrientation(mop.sideHit);

        // Calculate blocks to highlight
        Shape shape = ShapeRegistry.getShape(data.getCurrentShape());
        List<MutableBlockPos> blocks = shape.getBlocks(world, player, x, y, z, face,
            Math.min(Config.maxBlocks, Config.renderOutlineMax));

        if (blocks.isEmpty()) return;

        // Render outlines
        float partialTicks = event.partialTicks;
        double px = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double py = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double pz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        GL11.glPushMatrix();
        GL11.glTranslated(-px, -py, -pz);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glLineWidth(2.0F);
        GL11.glColor4f(0.0F, 1.0F, 0.5F, 0.7F);

        for (MutableBlockPos pos : blocks) {
            drawBlockOutline(pos.x, pos.y, pos.z);
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glPopMatrix();
    }

    private void drawBlockOutline(int x, int y, int z) {
        double minX = x + 0.002;
        double minY = y + 0.002;
        double minZ = z + 0.002;
        double maxX = x + 1.0 - 0.002;
        double maxY = y + 1.0 - 0.002;
        double maxZ = z + 1.0 - 0.002;

        Tessellator tess = Tessellator.instance;

        // Draw lines for each edge of the block
        tess.startDrawing(GL11.GL_LINES);

        // Bottom face edges
        tess.addVertex(minX, minY, minZ);
        tess.addVertex(maxX, minY, minZ);
        tess.addVertex(maxX, minY, minZ);
        tess.addVertex(maxX, minY, maxZ);
        tess.addVertex(maxX, minY, maxZ);
        tess.addVertex(minX, minY, maxZ);
        tess.addVertex(minX, minY, maxZ);
        tess.addVertex(minX, minY, minZ);

        // Top face edges
        tess.addVertex(minX, maxY, minZ);
        tess.addVertex(maxX, maxY, minZ);
        tess.addVertex(maxX, maxY, minZ);
        tess.addVertex(maxX, maxY, maxZ);
        tess.addVertex(maxX, maxY, maxZ);
        tess.addVertex(minX, maxY, maxZ);
        tess.addVertex(minX, maxY, maxZ);
        tess.addVertex(minX, maxY, minZ);

        // Vertical edges
        tess.addVertex(minX, minY, minZ);
        tess.addVertex(minX, maxY, minZ);
        tess.addVertex(maxX, minY, minZ);
        tess.addVertex(maxX, maxY, minZ);
        tess.addVertex(maxX, minY, maxZ);
        tess.addVertex(maxX, maxY, maxZ);
        tess.addVertex(minX, minY, maxZ);
        tess.addVertex(minX, maxY, maxZ);

        tess.draw();
    }
}
