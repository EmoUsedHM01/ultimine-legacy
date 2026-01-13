package dev.ftb.mods.ftbultimine.fabric;

import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import dev.ftb.mods.ftbultimine.FTBUltimine;
import dev.ftb.mods.ftbultimine.event.LevelRenderLastEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;

public class FTBUltimineFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		new FTBUltimine();
		EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
			WorldRenderEvents.AFTER_ENTITIES.register((ctx) -> LevelRenderLastEvent.EVENT.invoker().onRenderLast(ctx.matrices()));
		});
	}
}
