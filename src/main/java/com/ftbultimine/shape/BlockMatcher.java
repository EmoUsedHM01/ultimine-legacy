package com.ftbultimine.shape;

import com.ftbultimine.Config;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Determines whether a candidate block matches the origin block for ultimine purposes.
 */
public class BlockMatcher {

    /**
     * Check if the block at (cx, cy, cz) matches the origin block for ultimine.
     */
    public static boolean matches(World world, Block originBlock, int originMeta,
                                   int cx, int cy, int cz) {
        Block candidateBlock = world.getBlock(cx, cy, cz);
        int candidateMeta = world.getBlockMetadata(cx, cy, cz);

        if (candidateBlock.isAir(world, cx, cy, cz)) {
            return false;
        }

        // Exact match
        if (candidateBlock == originBlock && candidateMeta == originMeta) {
            return true;
        }

        // Ore dictionary match (shapeless mode)
        if (Config.mergeTagsShapeless) {
            return matchesOreDict(originBlock, originMeta, candidateBlock, candidateMeta);
        }

        return false;
    }

    /**
     * Check if two blocks share any ore dictionary entries.
     */
    private static boolean matchesOreDict(Block block1, int meta1, Block block2, int meta2) {
        ItemStack stack1 = new ItemStack(block1, 1, meta1);
        ItemStack stack2 = new ItemStack(block2, 1, meta2);

        if (stack1.getItem() == null || stack2.getItem() == null) {
            return false;
        }

        int[] ids1 = OreDictionary.getOreIDs(stack1);
        int[] ids2 = OreDictionary.getOreIDs(stack2);

        for (int id1 : ids1) {
            for (int id2 : ids2) {
                if (id1 == id2) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Simple match - same block, ignore metadata. Used for shaped modes.
     */
    public static boolean matchesBlock(World world, Block originBlock, int cx, int cy, int cz) {
        Block candidateBlock = world.getBlock(cx, cy, cz);
        return candidateBlock == originBlock && !candidateBlock.isAir(world, cx, cy, cz);
    }
}
