package dev.ftb.mods.ftbultimine.api.util;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

/**
 * Allows for feedback to be provided to the player about why an ultimining operation may not be performed at a
 * given time. Predefined instances are returned by Ultimine itself, and custom instances of this can be returned by
 * {@link dev.ftb.mods.ftbultimine.api.restriction.RestrictionHandler#ultimineBlockReason(Player)}
 */
@FunctionalInterface
public interface CanUltimineResult {
    // the "no valid block" message is shown when everything is OK, but the block selection is empty
    Allow ALLOWED = () -> "ftbultimine.info.no_valid_block";
    CanUltimineResult NO_BLOCK_TARGETED = prevent("ftbultimine.info.no_block_targeted");
    CanUltimineResult NO_FOOD = prevent("ftbultimine.info.no_food");
    CanUltimineResult NO_TOOL = prevent("ftbultimine.info.no_tool");
    CanUltimineResult NO_PERMISSION = prevent("ftbultimine.info.no_permission");
    CanUltimineResult BLOCKED_TOOL = prevent("ftbultimine.info.denied_tool");
    CanUltimineResult ON_COOLDOWN = prevent("ftbultimine.info.cooldown");
    CanUltimineResult OTHER_RESTRICTION = prevent("ftbultimine.info.other_restriction");

    // Currently unimplemented. This restriction is grouped under the "ALLOWED" result because we cannot know it on client side right now.
    // public static final CanUltimineResult NO_EXPERIENCE = prevent("ftbultimine.info.no_experience");

    /**
     * {@return a new prevention instance}
     * @param key a translation key for the reason, which should translate to a brief message
     */
    static CanUltimineResult prevent(String key) {
        return () -> key;
    }

    /**
     * {@return true if ultimining should be allowed, false to prevent it}
     * <p>
     * Don't override this; if you need an allowed result, just use {@link #ALLOWED}.
     */
    @ApiStatus.NonExtendable
    default boolean isAllowed() {
        return false;
    }

    /**
     * {@return the translation key for the reason, which should translate to a brief message}
     */
    String getTranslationKey();

    @FunctionalInterface
    interface Allow extends CanUltimineResult {
        @Override
        default boolean isAllowed() {
            return true;
        }
    }
}
