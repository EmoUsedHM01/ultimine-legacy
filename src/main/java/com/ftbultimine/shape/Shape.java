package com.ftbultimine.shape;

import java.util.List;

import com.ftbultimine.shape.BlockPosHelper.MutableBlockPos;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public interface Shape {

    /**
     * Get all block positions that should be affected by this shape.
     *
     * @param world     The world
     * @param player    The player
     * @param x         Origin block X
     * @param y         Origin block Y
     * @param z         Origin block Z
     * @param face      The face that was hit (ForgeDirection)
     * @param maxBlocks Maximum number of blocks to return
     * @return List of block positions to affect
     */
    List<MutableBlockPos> getBlocks(World world, EntityPlayer player, int x, int y, int z,
                                     ForgeDirection face, int maxBlocks);
}
