package dev.ftb.mods.ftbultimine.shape;

import dev.ftb.mods.ftbultimine.api.FTBUltimineAPI;
import net.minecraft.resources.Identifier;

public class SmallSquareShape extends LargeTunnelShape {
	private static final Identifier ID = FTBUltimineAPI.id("small_square");

	@Override
	public Identifier getName() {
		return ID;
	}

	@Override
	protected int maxDepth() {
		return 1;
	}
}