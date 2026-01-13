package dev.ftb.mods.ftbultimine.utils;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.ItemStack;

public class PlatformUtil {
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
