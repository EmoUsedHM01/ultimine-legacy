package dev.ftb.mods.ftbultimine;

import net.minecraft.core.BlockPos;
import org.jspecify.annotations.Nullable;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.List;

public class FTBUltimineCommon {
	public void setShape(int shapeIdx, @Nullable List<BlockPos> blocks) {
	}

	@Nullable
	public Collection<BlockPos> getSelectedBlocks(Player player) {
		return FTBUltimine.instance.getOrCreatePlayerData(player).cachedPositions();
	}
}