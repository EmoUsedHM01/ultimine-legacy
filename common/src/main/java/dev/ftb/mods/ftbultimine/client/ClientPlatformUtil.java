package dev.ftb.mods.ftbultimine.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.ftb.mods.ftblibrary.client.gui.input.Key;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.KeyEvent;
import org.jspecify.annotations.Nullable;

public class ClientPlatformUtil {
    @SuppressWarnings("unused")
    @ExpectPlatform
    public static boolean doesKeybindMatch(@Nullable KeyMapping keyMapping, KeyEvent event) {
        throw new AssertionError();
    }
}
