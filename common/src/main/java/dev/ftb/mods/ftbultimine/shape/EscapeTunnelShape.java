package dev.ftb.mods.ftbultimine.shape;

import dev.ftb.mods.ftbultimine.api.FTBUltimineAPI;
import net.minecraft.resources.Identifier;

public class EscapeTunnelShape extends DiagonalTunnelShape {
	private static final Identifier ID = FTBUltimineAPI.id("escape_tunnel");

	@Override
	public Identifier getName() {
		return ID;
	}

	@Override
	protected int yDirection() {
		return 1;
	}
}