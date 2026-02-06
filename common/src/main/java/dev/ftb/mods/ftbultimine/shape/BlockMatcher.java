package dev.ftb.mods.ftbultimine.shape;

import dev.ftb.mods.ftblibrary.util.Lazy;
import dev.ftb.mods.ftbultimine.api.FTBUltimineTags;
import dev.ftb.mods.ftbultimine.api.shape.ShapeContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockState;

public record BlockMatcher(ShapeContext.Matcher wrapped) implements ShapeContext.Matcher {
	public static BlockMatcher wrap(ShapeContext.Matcher matcher) {
		return new BlockMatcher(matcher);
	}

	@Override
	public boolean check(BlockState original, BlockState state) {
		return (TagCache.isEmptyBlockWhitelist() || state.is(FTBUltimineTags.Blocks.BLOCK_WHITELIST))
				&& !state.is(FTBUltimineTags.Blocks.EXCLUDED_BLOCKS)
				&& wrapped.check(original, state);
	}

	public static class TagCache {
		private static final Lazy<Boolean> EMPTY_BLOCK_WHITELIST = Lazy.of(() ->
				!BuiltInRegistries.BLOCK.getTagOrEmpty(FTBUltimineTags.Blocks.BLOCK_WHITELIST).iterator().hasNext()
		);

		private static boolean isEmptyBlockWhitelist() {
			return EMPTY_BLOCK_WHITELIST.get();
		}

		public static void onReload() {
			EMPTY_BLOCK_WHITELIST.invalidate();
		}
	}
}