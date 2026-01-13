package dev.ftb.mods.ftbultimine.client;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import net.minecraft.client.renderer.RenderPipelines;

public class UltimineRenderPipelines {
    public static final RenderPipeline LINES_NO_DEPTH_TRANSLUCENT = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
            .withLocation("pipeline/lines_translucent")
            .withDepthWrite(false)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withCull(false)
            .withColorWrite(true)
            .withBlend(BlendFunction.TRANSLUCENT)
            .build();
}
