package com.ftbultimine.shape;

/**
 * Simple block position helper since 1.7.10 doesn't have BlockPos.
 */
public class BlockPosHelper {

    public static class MutableBlockPos {

        public int x, y, z;

        public MutableBlockPos(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public MutableBlockPos copy() {
            return new MutableBlockPos(x, y, z);
        }

        @Override
        public int hashCode() {
            return (y + z * 31) * 31 + x;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof MutableBlockPos)) return false;
            MutableBlockPos other = (MutableBlockPos) obj;
            return x == other.x && y == other.y && z == other.z;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ", " + z + ")";
        }
    }

    public static long toLong(int x, int y, int z) {
        return ((long) x & 0x3FFFFFFL) << 38 | ((long) y & 0xFFFL) << 26 | ((long) z & 0x3FFFFFFL);
    }
}
