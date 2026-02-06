package dev.ftb.mods.ftbultimine.api;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class FTBUltimineTags {
    public static class Blocks {
        public static final TagKey<Block> EXCLUDED_BLOCKS = TagKey.create(Registries.BLOCK, FTBUltimineAPI.id("excluded_blocks"));
        public static final TagKey<Block> BLOCK_WHITELIST = TagKey.create(Registries.BLOCK, FTBUltimineAPI.id("block_whitelist"));
        public static final TagKey<Block> SINGLE_CROP_HARVESTING_BLACKLIST = TagKey.create(Registries.BLOCK, FTBUltimineAPI.id("single_crop_harvesting_blacklist"));
    }

    public static class Items {
        public static final TagKey<Item> DENY_TAG = TagKey.create(Registries.ITEM, FTBUltimineAPI.id("excluded_tools"));
        public static final TagKey<Item> STRICT_DENY_TAG = TagKey.create(Registries.ITEM, FTBUltimineAPI.id("excluded_tools/strict"));
        public static final TagKey<Item> ALLOW_TAG = TagKey.create(Registries.ITEM, FTBUltimineAPI.id("included_tools"));
    }
}
