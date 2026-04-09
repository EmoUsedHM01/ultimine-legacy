package com.ftbultimine.shape;

import java.util.ArrayList;
import java.util.List;

import com.ftbultimine.shape.BlockPosHelper.MutableBlockPos;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * All shaped mining patterns (tunnels, squares, etc).
 */
public class ShapedShapes {

    /**
     * Small Tunnel: 1-wide, 2-high tunnel in the direction the player faces.
     */
    public static final Shape SMALL_TUNNEL = new Shape() {
        @Override
        public List<MutableBlockPos> getBlocks(World world, EntityPlayer player, int x, int y, int z,
                                                ForgeDirection face, int maxBlocks) {
            List<MutableBlockPos> result = new ArrayList<MutableBlockPos>();
            ForgeDirection dir = getPlayerFacingHorizontal(player);

            for (int i = 0; i < maxBlocks / 2 && result.size() < maxBlocks; i++) {
                int bx = x + dir.offsetX * i;
                int bz = z + dir.offsetZ * i;
                addIfNotAir(world, result, bx, y, bz, maxBlocks);
                addIfNotAir(world, result, bx, y + 1, bz, maxBlocks);
            }
            return result;
        }
    };

    /**
     * Large Tunnel: 3-wide, 3-high tunnel in the direction the player faces.
     */
    public static final Shape LARGE_TUNNEL = new Shape() {
        @Override
        public List<MutableBlockPos> getBlocks(World world, EntityPlayer player, int x, int y, int z,
                                                ForgeDirection face, int maxBlocks) {
            List<MutableBlockPos> result = new ArrayList<MutableBlockPos>();
            ForgeDirection dir = getPlayerFacingHorizontal(player);
            ForgeDirection right = rotateRight(dir);

            int depth = maxBlocks / 9;
            if (depth < 1) depth = 1;

            for (int i = 0; i < depth && result.size() < maxBlocks; i++) {
                int bx = x + dir.offsetX * i;
                int bz = z + dir.offsetZ * i;
                for (int dy = 0; dy < 3; dy++) {
                    for (int side = -1; side <= 1; side++) {
                        int cx = bx + right.offsetX * side;
                        int cz = bz + right.offsetZ * side;
                        addIfNotAir(world, result, cx, y + dy, cz, maxBlocks);
                    }
                }
            }
            return result;
        }
    };

    /**
     * Small Square: 3x3 on the face the player is looking at.
     */
    public static final Shape SMALL_SQUARE = new Shape() {
        @Override
        public List<MutableBlockPos> getBlocks(World world, EntityPlayer player, int x, int y, int z,
                                                ForgeDirection face, int maxBlocks) {
            List<MutableBlockPos> result = new ArrayList<MutableBlockPos>();

            // Determine the two axes perpendicular to the face
            int[][] offsets = getPlanarOffsets(face);

            for (int u = -1; u <= 1; u++) {
                for (int v = -1; v <= 1; v++) {
                    int bx = x + offsets[0][0] * u + offsets[1][0] * v;
                    int by = y + offsets[0][1] * u + offsets[1][1] * v;
                    int bz = z + offsets[0][2] * u + offsets[1][2] * v;
                    addIfNotAir(world, result, bx, by, bz, maxBlocks);
                }
            }
            return result;
        }
    };

    /**
     * Mining Tunnel: 1x3 horizontal row + 2-high tunnel, going forward.
     */
    public static final Shape MINING_TUNNEL = new Shape() {
        @Override
        public List<MutableBlockPos> getBlocks(World world, EntityPlayer player, int x, int y, int z,
                                                ForgeDirection face, int maxBlocks) {
            List<MutableBlockPos> result = new ArrayList<MutableBlockPos>();
            ForgeDirection dir = getPlayerFacingHorizontal(player);
            ForgeDirection right = rotateRight(dir);

            int depth = maxBlocks / 6;
            if (depth < 1) depth = 1;

            for (int i = 0; i < depth && result.size() < maxBlocks; i++) {
                int bx = x + dir.offsetX * i;
                int bz = z + dir.offsetZ * i;
                for (int dy = 0; dy < 2; dy++) {
                    for (int side = -1; side <= 1; side++) {
                        int cx = bx + right.offsetX * side;
                        int cz = bz + right.offsetZ * side;
                        addIfNotAir(world, result, cx, y + dy, cz, maxBlocks);
                    }
                }
            }
            return result;
        }
    };

    /**
     * Escape Tunnel: staircase going upward in the direction the player faces.
     */
    public static final Shape ESCAPE_TUNNEL = new Shape() {
        @Override
        public List<MutableBlockPos> getBlocks(World world, EntityPlayer player, int x, int y, int z,
                                                ForgeDirection face, int maxBlocks) {
            List<MutableBlockPos> result = new ArrayList<MutableBlockPos>();
            ForgeDirection dir = getPlayerFacingHorizontal(player);

            int steps = maxBlocks / 3;
            if (steps < 1) steps = 1;

            for (int i = 0; i < steps && result.size() < maxBlocks; i++) {
                int bx = x + dir.offsetX * i;
                int by = y + i;
                int bz = z + dir.offsetZ * i;
                addIfNotAir(world, result, bx, by, bz, maxBlocks);
                addIfNotAir(world, result, bx, by + 1, bz, maxBlocks);
                addIfNotAir(world, result, bx, by + 2, bz, maxBlocks);
            }
            return result;
        }
    };

    // --- Utility methods ---

    private static void addIfNotAir(World world, List<MutableBlockPos> list, int x, int y, int z, int max) {
        if (list.size() >= max) return;
        if (y < 0 || y > 255) return;
        Block block = world.getBlock(x, y, z);
        if (!block.isAir(world, x, y, z)) {
            list.add(new MutableBlockPos(x, y, z));
        }
    }

    public static ForgeDirection getPlayerFacingHorizontal(EntityPlayer player) {
        int facing = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        switch (facing) {
            case 0: return ForgeDirection.SOUTH;
            case 1: return ForgeDirection.WEST;
            case 2: return ForgeDirection.NORTH;
            case 3: return ForgeDirection.EAST;
            default: return ForgeDirection.SOUTH;
        }
    }

    public static ForgeDirection rotateRight(ForgeDirection dir) {
        switch (dir) {
            case NORTH: return ForgeDirection.EAST;
            case EAST: return ForgeDirection.SOUTH;
            case SOUTH: return ForgeDirection.WEST;
            case WEST: return ForgeDirection.NORTH;
            default: return dir;
        }
    }

    /**
     * Get two perpendicular offset vectors for a given face normal.
     * Returns int[2][3] where each row is {dx, dy, dz} for one axis.
     */
    private static int[][] getPlanarOffsets(ForgeDirection face) {
        switch (face) {
            case UP:
            case DOWN:
                return new int[][] { {1, 0, 0}, {0, 0, 1} };
            case NORTH:
            case SOUTH:
                return new int[][] { {1, 0, 0}, {0, 1, 0} };
            case EAST:
            case WEST:
                return new int[][] { {0, 0, 1}, {0, 1, 0} };
            default:
                return new int[][] { {1, 0, 0}, {0, 1, 0} };
        }
    }
}
