package mcjty.rftoolsutility.modules.screen.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.client.RenderHelper;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.ModuleRenderInfo;
import mcjty.rftoolsbase.api.screens.data.IModuleData;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenBlock;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import mcjty.rftoolsutility.modules.screen.modulesclient.helper.ClientScreenModuleHelper;
import mcjty.rftoolsutility.modules.screen.network.PacketGetScreenData;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScreenRenderer implements BlockEntityRenderer<ScreenTileEntity, ScreenRenderer.State> {

    private static final ClientScreenModuleHelper HELPER = new ClientScreenModuleHelper();

    public ScreenRenderer(BlockEntityRendererProvider.Context context) {
    }

    private record ModuleFrame(IClientScreenModule module, IModuleData data, ModuleRenderInfo info, int y, boolean large) {
    }

    public static class State extends BlockEntityRenderState {
        Direction facing;
        Direction horizontal;
        int size;
        int color;
        boolean transparent;
        boolean bright;
        List<ModuleFrame> modules = List.of();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(ScreenTileEntity entity, State state, float partialTicks, Vec3 camera, CrumblingOverlay overlay) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTicks, camera, overlay);
        state.facing = entity.getBlockState().getValue(BlockStateProperties.FACING);
        state.horizontal = entity.getBlockState().getValue(ScreenBlock.HORIZ_FACING);
        state.size = entity.getSize();
        state.color = entity.getColor();
        state.transparent = entity.isTransparent();
        state.bright = entity.isBright();
        state.modules = extractModules(entity);
    }

    private static List<ModuleFrame> extractModules(ScreenTileEntity entity) {
        if (!entity.isRenderable()) {
            return List.of();
        }
        long now = System.currentTimeMillis();
        GlobalPos pos = GlobalPos.of(entity.getDimension(), entity.getBlockPos());
        if (now - entity.lastTime > ScreenConfiguration.SCREEN_REFRESH_TIMING.get() && entity.isNeedsServerData()) {
            entity.lastTime = now;
            RFToolsUtilityMessages.sendToServer(PacketGetScreenData.create(RFToolsUtility.MODID, pos, now));
        }
        Map<Integer, IModuleData> data = ScreenTileEntity.screenData.getOrDefault(pos, Map.of());
        var modules = entity.isShowHelp() ? ScreenTileEntity.getHelpingScreenModules() : entity.getClientScreenModules();
        ScreenTileEntity.ModuleRaytraceResult hit = null;
        if (!entity.isDummy() && Minecraft.getInstance().hitResult instanceof BlockHitResult target
                && target.getBlockPos().equals(entity.getBlockPos()) && target.getDirection() == entity.getBlockState().getValue(BlockStateProperties.FACING)) {
            Vec3 relative = target.getLocation().subtract(Vec3.atLowerCornerOf(entity.getBlockPos()));
            hit = entity.getHitModule(relative.x, relative.y, relative.z, target.getDirection(),
                    entity.getBlockState().getValue(ScreenBlock.HORIZ_FACING), entity.getSize());
        }
        boolean truetype = !ScreenConfiguration.forceNoTruetype.get()
                && (entity.getTrueTypeMode() == 1 || entity.getTrueTypeMode() == 0 && ScreenConfiguration.useTruetype.get());
        List<ModuleFrame> result = new ArrayList<>();
        int y = 7;
        for (int index = 0; index < modules.size(); index++) {
            var pair = modules.get(index);
            if (pair == null || pair.getRight() == null) {
                continue;
            }
            IClientScreenModule<?> module = pair.getRight();
            ItemStack stack = pair.getLeft().copy();
            int height = module.getHeight(stack);
            if (y + height <= 124) {
                boolean focused = hit != null && hit.moduleIndex() == index;
                ModuleRenderInfo info = new ModuleRenderInfo(entity.getSize() + 1.0f, entity.getBlockPos(),
                        focused ? hit.x() : -1, focused ? hit.y() - hit.currenty() : -1, truetype,
                        entity.isBright() || entity.isDummy(), ScreenConfiguration.getTrueTypeFont(), stack);
                result.add(new ModuleFrame(module, data.get(index), info, y, module.getTransformMode(stack) == IClientScreenModule.TransformMode.TEXTLARGE));
                y += height;
            }
        }
        return List.copyOf(result);
    }

    @Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        pose.pushPose();
        pose.translate(.5f, .5f, .5f);
        RenderHelper.rotateYP(pose, switch (state.horizontal) {
            case NORTH -> -180;
            case WEST -> -90;
            case EAST -> 90;
            default -> 0;
        });
        RenderHelper.rotateXP(pose, state.facing == Direction.DOWN ? 90 : state.facing == Direction.UP ? -90 : 0);
        pose.translate(0, 0, -.4375f);
        if (!state.transparent) {
            collector.submitCustomGeometry(pose, ScreenRenderTypes.FRAME, (captured, vertices) -> {
                PoseStack copy = new PoseStack();
                copy.last().set(captured);
                copy.scale(1, -1, -1);
                RenderHelper.drawBox(copy, vertices, -.5f, .5f + state.size, -.5f, .5f + state.size, .05f, 0, .5f, .5f, .5f, state.lightCoords);
                RenderHelper.drawQuadGui(copy, vertices, -.46f, .46f + state.size, -.46f, .46f + state.size, -.01f, 0xff000000 | state.color, state.lightCoords);
            });
        }
        pose.translate(-.5f, .5f, .03f);
        float scale = .0075f * (state.size + 1);
        pose.scale(scale, -scale, scale);
        renderModules(new WorldScreenGraphics(pose, collector, state.bright ? RenderHelper.MAX_BRIGHTNESS : state.lightCoords), collector, state.modules);
        pose.popPose();
    }

    private static void renderModules(GuiGraphicsExtractor graphics, SubmitNodeCollector collector, List<ModuleFrame> modules) {
        for (ModuleFrame module : modules) {
            graphics.pose().pushMatrix();
            if (module.large) {
                graphics.pose().scale(2, 2);
            }
            try {
                module.module.render(graphics, collector, HELPER, Minecraft.getInstance().font, module.y, module.data, module.info);
            } catch (ClassCastException ignored) {
                // Server data can arrive one frame after the installed module changes.
            }
            graphics.pose().popMatrix();
        }
    }

    public static void renderTablet(GuiGraphicsExtractor graphics, ScreenTileEntity entity, int x, int y) {
        graphics.fill(x - 2, y - 2, x + 152, y + 152, 0xffdddddd);
        graphics.fill(x, y, x + 150, y + 150, 0xff333333);
        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        renderModules(graphics, null, extractModules(entity));
        graphics.pose().popMatrix();
    }

    public static void register() {
        BlockEntityRenderers.register(ScreenModule.SCREEN.be().get(), ScreenRenderer::new);
        BlockEntityRenderers.register(ScreenModule.CREATIVE_SCREEN.be().get(), ScreenRenderer::new);
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

}
