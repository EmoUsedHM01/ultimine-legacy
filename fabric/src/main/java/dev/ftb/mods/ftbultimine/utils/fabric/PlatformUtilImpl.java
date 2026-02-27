package dev.ftb.mods.ftbultimine.utils.fabric;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.block.state.BlockState;

public class PlatformUtilImpl {
    public static boolean playerHasCorrectTool(Player player, BlockPos pos, BlockState state) {
        return player.hasCorrectToolForDrops(state);
    }

    public static boolean canAxeStrip(ItemStack stack) {
        return stack.getItem() instanceof AxeItem;
    }

    public static boolean canTillSoil(ItemStack stack) {
        return stack.getItem() instanceof HoeItem;
    }

    public static boolean canFlattenPath(ItemStack stack) {
        return stack.getItem() instanceof ShovelItem;
    }
}
