package dev.ftb.mods.ftbultimine.client;

import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

public class UltimineRenderTypes {
	public static final RenderType LINES_TRANSLUCENT = RenderType.create(
			"ultimine_lines_translucent",
			RenderSetup.builder(UltimineRenderPipelines.LINES_NO_DEPTH_TRANSLUCENT)
					.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
					.setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
					.createRenderSetup());
}