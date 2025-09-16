package dev.ftb.mods.ftbultimine.neoforge;

import dev.ftb.mods.ftbultimine.event.LevelRenderLastEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;

class FTBUltimineNeoForgeClient {
    static void init() {
        NeoForge.EVENT_BUS.<RenderLevelStageEvent.AfterTranslucentBlocks>addListener(event ->
                LevelRenderLastEvent.EVENT.invoker().onRenderLast(event.getPoseStack())
        );
    }
}
