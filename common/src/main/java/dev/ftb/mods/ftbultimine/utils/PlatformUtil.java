package dev.ftb.mods.ftbultimine.utils;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class PlatformUtil {
    @ExpectPlatform
    public static boolean playerHasCorrectTool(Player player, BlockPos pos, BlockState state) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean canAxeStrip(ItemStack stack) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean canTillSoil(ItemStack stack) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean canFlattenPath(ItemStack stack) {
        throw new AssertionError();
    }
}
