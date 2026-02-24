package dev.ftb.mods.ftbultimine.client.fabric;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.KeyEvent;
import org.jspecify.annotations.Nullable;

// arch expect
@SuppressWarnings("unused")
public class ClientPlatformUtilImpl {
    public static boolean doesKeybindMatch(@Nullable KeyMapping keyMapping, KeyEvent event) {
        // TODO how can we handle key modifiers on Fabric?
        return keyMapping != null && keyMapping.matches(event);
    }
}
