package dev.ftb.mods.ftbultimine.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.ftb.mods.ftblibrary.client.gui.GuiHelper;
import dev.ftb.mods.ftblibrary.client.icon.Color4IRenderer;
import dev.ftb.mods.ftblibrary.client.util.ClientUtils;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftbultimine.CooldownTracker;
import dev.ftb.mods.ftbultimine.FTBUltimine;
import dev.ftb.mods.ftbultimine.FTBUltimineCommon;
import dev.ftb.mods.ftbultimine.api.FTBUltimineAPI;
import dev.ftb.mods.ftbultimine.api.util.CanUltimineResult;
import dev.ftb.mods.ftbultimine.config.FTBUltimineClientConfig;
import dev.ftb.mods.ftbultimine.event.LevelRenderLastEvent;
import dev.ftb.mods.ftbultimine.net.KeyPressedPacket;
import dev.ftb.mods.ftbultimine.net.ModeChangedPacket;
import dev.ftb.mods.ftbultimine.shape.ShapeRegistry;
import dev.ftb.mods.ftbultimine.utils.ShapeMerger;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;

import java.util.*;

public class FTBUltimineClient extends FTBUltimineCommon {
	public static KeyMapping keyBindUltimine;
	public static KeyMapping keyBindNextMode;
	public static KeyMapping keyBindPrevMode;

	private boolean pressed;
	private boolean canUltimine;
	private CanUltimineResult canUltimineStatus;
	private List<BlockPos> shapeBlocks = Collections.emptyList();
	private int actualBlocks = 0;
	private List<CachedEdge> cachedEdges = null;
	private BlockPos cachedPos = null;
	public boolean hasScrolledYet = false;
	private long lastToggle = 0;
	public final int INPUT_DELAY = 125;
	private int shapeIdx = 0;  // shape index of client player's current shape
	private int maxPanelWidth = 100;
	private List<IndentedLine> infoPanelList = List.of();

	private static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(FTBUltimineAPI.id("default"));

	public FTBUltimineClient() {
		KeyMappingRegistry.register(keyBindUltimine = new KeyMapping("key.ftbultimine", InputConstants.Type.KEYSYM, InputConstants.KEY_GRAVE, KEY_CATEGORY));
		KeyMappingRegistry.register(keyBindNextMode = new KeyMapping("ftbultimine.change_shape.next", InputConstants.Type.KEYSYM, InputConstants.KEY_UP, KEY_CATEGORY));
		KeyMappingRegistry.register(keyBindPrevMode = new KeyMapping("ftbultimine.change_shape.prev", InputConstants.Type.KEYSYM, InputConstants.KEY_DOWN, KEY_CATEGORY));

		ClientTickEvent.CLIENT_PRE.register(this::clientTick);
		ClientGuiEvent.RENDER_HUD.register(this::renderGameOverlay);

		// TODO: remove once #6 gets fixed
		LevelRenderLastEvent.EVENT.register(this::renderInGame);

		ClientRawInputEvent.MOUSE_SCROLLED.register(this::onMouseScrolled);
		ClientRawInputEvent.KEY_PRESSED.register(this::onKeyPress);
	}

	public static Player getClientPlayer() {
		return Objects.requireNonNull(Minecraft.getInstance().player);
	}

	@Override
	public void setShape(int shapeIdx, List<BlockPos> blocks) {
		this.shapeIdx = shapeIdx;
		if (blocks != null) {
			actualBlocks = blocks.size();
			int maxRendered = Math.min(actualBlocks, FTBUltimineClientConfig.RENDER_OUTLINE.get());
			shapeBlocks = blocks.subList(0, maxRendered);
			cachedEdges = null;
			updateEdges();
		}
		if (!pressed && FTBUltimineClientConfig.SHAPE_FEEDBACK_MESSAGE.get()) {
			ClientUtils.getClientPlayer().displayClientMessage(
					Component.translatable("key.ftbultimine").append(" : ").append(ShapeRegistry.INSTANCE.getShape(shapeIdx).getDisplayName()),
					true);
		}
	}

	public void renderInGame(PoseStack stack) {
		if (!pressed || cachedPos == null || cachedEdges == null || cachedEdges.isEmpty()) {
			return;
		}

		Minecraft mc = Minecraft.getInstance();

		if (!canUltimine) {
			return;
		}

		// Rewrite this to use shader that does outline instead

		Camera activeRenderInfo = mc.getEntityRenderDispatcher().camera;
		if (activeRenderInfo == null) {
			return;
		}
		Vec3 projectedView = activeRenderInfo.position();

		stack.pushPose();
		stack.translate(cachedPos.getX() - projectedView.x, cachedPos.getY() - projectedView.y, cachedPos.getZ() - projectedView.z);
		Matrix4f matrix = stack.last().pose();

		VertexConsumer buffer = mc.renderBuffers().bufferSource().getBuffer(RenderTypes.LINES);

		for (CachedEdge edge : cachedEdges) {
			buffer.addVertex(matrix, edge.x1(), edge.y1(), edge.z1())
					.setColor(255, 255, 255, 255)
					.setNormal(edge.xn(), edge.yn(), edge.zn())
					.setLineWidth(2f);
			buffer.addVertex(matrix, edge.x2(), edge.y2(), edge.z2())
					.setColor(255, 255, 255, 255)
					.setNormal(edge.xn(), edge.yn(), edge.zn())
					.setLineWidth(2f);
		}

		VertexConsumer buffer2 = mc.renderBuffers().bufferSource().getBuffer(UltimineRenderTypes.LINES_TRANSLUCENT);

		int alpha = FTBUltimineClientConfig.PREVIEW_LINE_ALPHA.get();
		for (CachedEdge edge : cachedEdges) {
			buffer2.addVertex(matrix, edge.x1(), edge.y1(), edge.z1())
					.setColor(255, 255, 255, alpha)
					.setNormal(edge.xn(), edge.yn(), edge.zn())
					.setLineWidth(1.5f);
			buffer2.addVertex(matrix, edge.x2(), edge.y2(), edge.z2())
					.setColor(255, 255, 255, alpha)
					.setNormal(edge.xn(), edge.yn(), edge.zn())
					.setLineWidth(1.5f);
		}

		stack.popPose();
	}

	public EventResult onMouseScrolled(Minecraft client, double amountX, double amountY) {
		if (pressed && (amountY != 0 || amountX != 0) && isMenuSneaking()) {
			NetworkManager.sendToServer(new ModeChangedPacket(amountX < 0D || amountY < 0D));
			hasScrolledYet = true;
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	public EventResult onKeyPress(Minecraft client, int action, KeyEvent event) {
		if ((System.currentTimeMillis() - lastToggle) < INPUT_DELAY) {
			return EventResult.pass();
		}

		boolean nextMode;
		if (keyBindPrevMode.matches(event)) {
			nextMode = false;
		} else if (keyBindNextMode.matches(event)) {
			nextMode = true;
		} else {
			return EventResult.pass();
		}

        if ((pressed || !FTBUltimineClientConfig.REQUIRE_ULTIMINE_KEY_FOR_CYCLING.get()) && isMenuSneaking()) {
            NetworkManager.sendToServer(new ModeChangedPacket(nextMode));
            lastToggle = System.currentTimeMillis();
            hasScrolledYet = true;
        }
        return EventResult.pass();
    }

	private boolean isMenuSneaking() {
		if (!FTBUltimineClientConfig.REQUIRE_SNEAK_FOR_MENU.get()) return true;

		return keyBindUltimine.getDefaultKey().getValue() == InputConstants.KEY_LSHIFT || keyBindUltimine.getDefaultKey().getValue() == InputConstants.KEY_RSHIFT ?
				Minecraft.getInstance().hasControlDown() :
				Minecraft.getInstance().hasShiftDown();
	}

	private List<IndentedLine> addPressedInfo() {
		ImmutableList.Builder<IndentedLine> builder = ImmutableList.builder();

		Component msg;
		boolean isActive = true;
		if (CooldownTracker.isOnCooldown(getClientPlayer())) {
			msg = Component.translatable("ftbultimine.info.cooldown").withStyle(style -> style.withColor(TextColor.fromRgb(0xBFBF6C)));
		} else if (canUltimine && actualBlocks > 0) {
			msg = Component.translatable("ftbultimine.info.active").withStyle(style -> style.withColor(TextColor.fromRgb(0xA3BE8C)));
		} else {
			msg = Component.translatable("ftbultimine.info.not_active").withStyle(style -> style.withColor(TextColor.fromRgb(0xBF616A)));
			isActive = false;
		}
		builder.add(new IndentedLine(0, Component.translatable("ftbultimine.info.base", msg)));
		if (!isActive) {
			builder.add(new IndentedLine(8, Component.translatable(canUltimineStatus.getTranslationKey()).withStyle(style -> style.withColor(TextColor.fromRgb(0xBF616A)))));
		}

		ShapeRegistry shapeRegistry = ShapeRegistry.INSTANCE;
		int context = Math.min((shapeRegistry.shapeCount() - 1) / 2, FTBUltimineClientConfig.SHAPE_MENU_CONTEXT_LINES.get());

		boolean showingMenu = isMenuSneaking();

		if (showingMenu) {
			if (isActive) {
				builder.add(new IndentedLine(0, Component.empty()));
			}
			for (int i = -context; i < 0; i++) {
				builder.add(new IndentedLine(16, shapeRegistry.getShape(shapeIdx + i).getDisplayName().withStyle(ChatFormatting.GRAY)));
			}
		} else {
			builder.add(new IndentedLine(0, Component.translatable("ftbultimine.change_shape.short").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)));
			builder.add(new IndentedLine(0, Component.empty()));
		}

		builder.add(new IndentedLine(showingMenu ? 16 : 0, shapeRegistry.getShape(shapeIdx).getDisplayName().withStyle(ChatFormatting.YELLOW)));

		if (showingMenu) {
			for (int i = 1; i <= context; i++) {
				builder.add(new IndentedLine(16, shapeRegistry.getShape(shapeIdx + i).getDisplayName().withStyle(ChatFormatting.GRAY)));
			}
		}

		if (canUltimine && actualBlocks != 0) {
			MutableComponent mining = Component.empty();
			mining.append(Component.translatable("ftbultimine.info.blocks", actualBlocks));
			if (actualBlocks > shapeBlocks.size()) {
				mining.append(", ").append(Component.translatable("ftbultimine.info.partial_render", shapeBlocks.size()));
			}
			builder.add(new IndentedLine(0, Component.empty()));
			builder.add(new IndentedLine(0, mining.withColor(0xA3BE8C)));
		}

		return builder.build();
	}

	public void renderGameOverlay(GuiGraphics graphics, DeltaTracker tickDelta) {
		if (pressed) {
			Minecraft mc = Minecraft.getInstance();

			Font font = mc.font;
			int width = Math.max(maxPanelWidth, 10 + infoPanelList.stream().map(l -> font.width(l.text)).max(Integer::compareTo).orElse(100));
			maxPanelWidth = width;
			int height = font.lineHeight * infoPanelList.size();
			float scale = FTBUltimineClientConfig.OVERLAY_SCALE.get().floatValue();

			int insetX = FTBUltimineClientConfig.OVERLAY_INSET_X.get();
			int insetY = FTBUltimineClientConfig.OVERLAY_INSET_Y.get();
			var pos = FTBUltimineClientConfig.OVERLAY_POS.get().getPanelPos(
					mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight(),
					(int) (width * scale), (int) (height * scale),
					insetX, insetY
			);

			graphics.pose().pushMatrix();
			graphics.pose().translate(pos.x(), pos.y());
			graphics.pose().scale(scale, scale);

			Color4IRenderer colorRenderer = Color4IRenderer.INSTANCE;
			// panel background
			colorRenderer.render(Color4I.DARK_GRAY.withAlpha(128), graphics, -2, -2, width + 4, height + 4);
			GuiHelper.drawHollowRect(graphics, -2, -2, width + 4, height + 4, Color4I.GRAY.withAlpha(128), false);

			// draw cooldown progress bar if needed
			float f = CooldownTracker.getCooldownRemaining(getClientPlayer());
			if (f < 1f) {
				colorRenderer.render(Color4I.rgba(0xAA40A040), graphics,0, 0, (int) (width * f) + 1, font.lineHeight + 1);
			}

			if (isMenuSneaking()) {
				// draw the "scrollbar"
				int barUpper = font.lineHeight * 2;
				int barHeight = font.lineHeight * (ShapeRegistry.INSTANCE.shapeCount() - 1) - 2;
				Color4I col = Color4I.WHITE.withAlpha(192);
				colorRenderer.render(col, graphics, 3, barUpper, 2, barHeight);
				colorRenderer.render(col, graphics, 2, barUpper + 1, 4, 1);
				colorRenderer.render(col, graphics, 1, barUpper + 2, 6, 1);
				colorRenderer.render(col, graphics, 2, barUpper + barHeight - 2, 4, 1);
				colorRenderer.render(col, graphics, 1, barUpper + barHeight - 3, 6, 1);
			}

			// render the text lines
			int top = 0;
			for (IndentedLine line : infoPanelList) {
				FormattedCharSequence formatted = line.text.getVisualOrderText();
				graphics.drawString(font, formatted, line.indent, top, 0xFFECEFF4, true);
				top += font.lineHeight;
			}

			graphics.pose().popMatrix();
		}
	}

	public void clientTick(Minecraft mc) {
		if (Minecraft.getInstance().player == null) {
			return;
		}

		boolean p = pressed;

		if ((pressed = keyBindUltimine.isDown()) != p) {
			NetworkManager.sendToServer(new KeyPressedPacket(pressed));

			if (pressed && !hasScrolledYet && mc.player != null) {
				MutableComponent msg1 = Component.translatable(FTBUltimineClientConfig.REQUIRE_SNEAK_FOR_MENU.get() ?
						"ftbultimine.change_shape" : "ftbultimine.change_shape.no_shift");
				mc.player.displayClientMessage(msg1, true);
			}
		}
		canUltimineStatus = mc.hitResult instanceof BlockHitResult b && b.getType() == HitResult.Type.BLOCK ?
				FTBUltimine.getInstance().canUltimine(mc.player) :
				CanUltimineResult.NO_BLOCK_TARGETED;
		canUltimine = pressed && (canUltimineStatus.isAllowed());

		if (pressed) {
			infoPanelList = addPressedInfo();
		}
	}

	private void updateEdges() {
		if (cachedEdges != null) {
			return;
		}
		if (shapeBlocks.isEmpty()) {
			cachedEdges = Collections.emptyList();
			return;
		}

		cachedPos = shapeBlocks.getFirst();
		cachedEdges = new ArrayList<>();

		Collection<VoxelShape> shapes = new HashSet<>();
		for (AABB aabb : ShapeMerger.merge(shapeBlocks, cachedPos)) {
			shapes.add(Shapes.create(aabb.inflate(0.005D)));
		}

		orShapes(shapes).forAllEdges((x1, y1, z1, x2, y2, z2) -> cachedEdges.add(CachedEdge.fromDoubles(x1, y1, z1, x2, y2, z2)));
	}

	static VoxelShape orShapes(Collection<VoxelShape> shapes) {
		VoxelShape combinedShape = Shapes.empty();
		for (VoxelShape shape : shapes) {
			combinedShape = Shapes.joinUnoptimized(combinedShape, shape, BooleanOp.OR);
		}
		return combinedShape.optimize();
	}

	private record IndentedLine(int indent, Component text) {
	}
}
