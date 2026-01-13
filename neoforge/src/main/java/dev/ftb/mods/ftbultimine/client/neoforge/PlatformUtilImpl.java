package dev.ftb.mods.ftbultimine.client.neoforge;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbilities;

public class PlatformUtilImpl {
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
