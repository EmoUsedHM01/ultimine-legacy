package com.ftbultimine.shape;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.ftbultimine.shape.BlockPosHelper.MutableBlockPos;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Flood-fill shape: finds all connected blocks of the same type.
 */
public class ShapelessShape implements Shape {

    public static final ShapelessShape INSTANCE = new ShapelessShape();

    @Override
    public List<MutableBlockPos> getBlocks(World world, EntityPlayer player, int x, int y, int z,
                                            ForgeDirection face, int maxBlocks) {
        List<MutableBlockPos> result = new ArrayList<MutableBlockPos>();
        Block originBlock = world.getBlock(x, y, z);
        int originMeta = world.getBlockMetadata(x, y, z);

        if (originBlock.isAir(world, x, y, z)) {
            return result;
        }

        Set<Long> visited = new HashSet<Long>();
        Queue<MutableBlockPos> queue = new LinkedList<MutableBlockPos>();

        long originKey = BlockPosHelper.toLong(x, y, z);
        visited.add(originKey);
        queue.add(new MutableBlockPos(x, y, z));

        while (!queue.isEmpty() && result.size() < maxBlocks) {
            MutableBlockPos pos = queue.poll();
            result.add(pos);

            // Check all 6 neighbors
            for (int i = 0; i < 6; i++) {
                int nx = pos.x + ForgeDirection.VALID_DIRECTIONS[i].offsetX;
                int ny = pos.y + ForgeDirection.VALID_DIRECTIONS[i].offsetY;
                int nz = pos.z + ForgeDirection.VALID_DIRECTIONS[i].offsetZ;

                long key = BlockPosHelper.toLong(nx, ny, nz);
                if (visited.contains(key)) continue;
                visited.add(key);

                if (ny < 0 || ny > 255) continue;

                if (BlockMatcher.matches(world, originBlock, originMeta, nx, ny, nz)) {
                    queue.add(new MutableBlockPos(nx, ny, nz));
                }
            }
        }

        return result;
    }
}
