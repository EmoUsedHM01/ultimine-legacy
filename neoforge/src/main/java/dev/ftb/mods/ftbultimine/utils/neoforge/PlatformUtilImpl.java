package dev.ftb.mods.ftbultimine.utils.neoforge;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;

public class PlatformUtilImpl {
    public static boolean playerHasCorrectTool(Player player, BlockPos pos, BlockState state) {
        return player.hasCorrectToolForDrops(state, player.level(), pos);
    }

    public static boolean canAxeStrip(ItemStack stack) {
        return stack.getItem().canPerformAction(stack, ItemAbilities.AXE_STRIP);
    }

    public static boolean canTillSoil(ItemStack stack) {
        return stack.getItem().canPerformAction(stack, ItemAbilities.HOE_TILL);
    }

    public static boolean canFlattenPath(ItemStack stack) {
        return stack.getItem().canPerformAction(stack, ItemAbilities.SHOVEL_FLATTEN);
    }
}
