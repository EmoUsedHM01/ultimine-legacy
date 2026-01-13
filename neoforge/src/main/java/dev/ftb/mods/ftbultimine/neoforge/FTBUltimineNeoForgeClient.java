package dev.ftb.mods.ftbultimine.neoforge;

import dev.ftb.mods.ftbultimine.api.FTBUltimineAPI;
import dev.ftb.mods.ftbultimine.event.LevelRenderLastEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = FTBUltimineAPI.MOD_ID, dist = Dist.CLIENT)
public class FTBUltimineNeoForgeClient {
    public FTBUltimineNeoForgeClient() {
        NeoForge.EVENT_BUS.<RenderLevelStageEvent.AfterTranslucentBlocks>addListener(event ->
                LevelRenderLastEvent.EVENT.invoker().onRenderLast(event.getPoseStack())
        );
    }
}
