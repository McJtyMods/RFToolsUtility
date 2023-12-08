package mcjty.rftoolsutility.modules.screen.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mcjty.lib.client.CustomRenderTypes;
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
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ScreenRenderer implements BlockEntityRenderer<ScreenTileEntity> {


    public ScreenRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@Nonnull ScreenTileEntity tileEntity, float v, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource buffer, int packedLightIn, int packedOverlayIn) {
        renderInternal(tileEntity, matrixStack, buffer, packedLightIn, packedOverlayIn);
    }

    public static void renderInternal(ScreenTileEntity tileEntity, PoseStack matrixStack, MultiBufferSource buffer, int packedLightIn, int packedOverlayIn) {
        float xRotation = 0.0F;
        float yRotation = 0.0F;

        Direction facing = Direction.SOUTH, horizontalFacing = Direction.SOUTH;
        if (!tileEntity.isDummy()) {
            BlockState state = Minecraft.getInstance().level.getBlockState(tileEntity.getBlockPos());
            if (state.getBlock() instanceof ScreenBlock) {
                facing = state.getValue(BlockStateProperties.FACING);
                horizontalFacing = state.getValue(ScreenBlock.HORIZ_FACING);
            } else {
                return;
            }
        }

        matrixStack.pushPose();

        switch (horizontalFacing) {
            case NORTH -> yRotation = -180.0F;
            case WEST -> yRotation = -90.0F;
            case EAST -> yRotation = 90.0F;
        }
        switch (facing) {
            case DOWN -> xRotation = 90.0F;
            case UP -> xRotation = -90.0F;
        }

        // TileEntity can be null if this is used for an item renderer.
        matrixStack.translate(0.5F, 0.5F, 0.5F);
        RenderHelper.rotateYP(matrixStack, yRotation);
        RenderHelper.rotateXP(matrixStack, xRotation);
        matrixStack.translate(0.0F, 0.0F, -0.4375F);

        if (tileEntity.isDummy()) {
//            RenderSystem.disableLighting();// @todo 1.18
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
            // @todo 1.20 more efficient?
            GuiGraphics graphics = new GuiGraphics(Minecraft.getInstance(), Minecraft.getInstance().renderBuffers().bufferSource());
            graphics.pose().last().pose().set(matrixStack.last().pose());
            graphics.pose().last().normal().set(matrixStack.last().normal());

            graphics.fill(98, 28, 252, 182, 0xffdddddd);
            graphics.fill(100, 30, 250, 180, 0xff333333);
            matrixStack.translate(100, 30, 0);
        } else if (!tileEntity.isTransparent()) {
            renderScreenBoard(matrixStack, buffer, tileEntity.getSize(), tileEntity.getColor(), packedLightIn, packedOverlayIn);
        }

        if (tileEntity.isRenderable()) {
            Font fontrenderer = Minecraft.getInstance().font;

            // @todo 1.15
//            GlStateManager.depthMask(false);
//            GlStateManager.disableLighting();

            Map<Integer, IModuleData> screenData = updateScreenData(tileEntity);

            List<IClientScreenModule<?>> modules = tileEntity.getClientScreenModules();
            if (tileEntity.isShowHelp()) {
                modules = ScreenTileEntity.getHelpingScreenModules();
            }

            GuiGraphics graphics = new GuiGraphics(Minecraft.getInstance(), Minecraft.getInstance().renderBuffers().bufferSource());
            graphics.pose().last().pose().set(matrixStack.last().pose());
            graphics.pose().last().normal().set(matrixStack.last().normal());
            renderModules(graphics, buffer, fontrenderer, tileEntity, modules, screenData, tileEntity.isDummy() ? 0 : tileEntity.getSize());
        }

        matrixStack.popPose();
    }

    private static Map<Integer, IModuleData> updateScreenData(ScreenTileEntity screenTileEntity) {
        long millis = System.currentTimeMillis();
        if ((millis - screenTileEntity.lastTime > ScreenConfiguration.SCREEN_REFRESH_TIMING.get()) && screenTileEntity.isNeedsServerData()) {
            screenTileEntity.lastTime = millis;
            GlobalPos pos = GlobalPos.of(screenTileEntity.getDimension(), screenTileEntity.getBlockPos());
            RFToolsUtilityMessages.INSTANCE.sendToServer(new PacketGetScreenData(RFToolsUtility.MODID, pos, millis));
        }

        GlobalPos key = GlobalPos.of(screenTileEntity.getDimension(), screenTileEntity.getBlockPos());
        Map<Integer, IModuleData> screenData = ScreenTileEntity.screenData.get(key);
        if (screenData == null) {
            screenData = Collections.emptyMap();
        }
        return screenData;
    }

    private static final ClientScreenModuleHelper clientScreenModuleHelper = new ClientScreenModuleHelper();

    private static void renderModules(GuiGraphics graphics, MultiBufferSource buffer, Font fontrenderer, ScreenTileEntity tileEntity, List<IClientScreenModule<?>> modules, Map<Integer, IModuleData> screenData, int size) {
        float f3;
        float factor = size + 1.0f;
        int currenty = 7;
        int moduleIndex = 0;

        float f = 0.0075F;
        float minf3 = -1.0f;
        PoseStack stack;
        if (tileEntity.isDummy()) {
            stack = new PoseStack();  // @todo 1.16 check this!
            stack.translate(100, 30, 0);
            f = 1.0f;
            minf3 = 1.0f;
        } else {
            stack = graphics.pose();
        }

        // @todo 1.15 Use RenderHelper.MAX_BRIGHTNESS for the last parameter of renderText
//        IRenderTypeBuffer buffer = new IRenderTypeBuffer() {
//            @Override
//            public IVertexBuilder getBuffer(RenderType renderType) {
//                if ("text".equals(renderType.toString())) {
//                    renderType = new FullBrightTextType("fb_text", renderType);
//                } else if ("text_see_through".equals(renderType.toString())) {
//                    renderType = new FullBrightTextType("fb_text_transp", renderType);
//                }
//                return originalBuffer.getBuffer(renderType);
//            }
//        };

        BlockPos pos = tileEntity.getBlockPos();

        HitResult mouseOver = Minecraft.getInstance().hitResult;
        IClientScreenModule<?> hitModule = null;
        ScreenTileEntity.ModuleRaytraceResult hit = null;
        if (!tileEntity.isDummy()) {
            BlockState blockState = tileEntity.getLevel().getBlockState(pos);
            Block block = blockState.getBlock();
            if ((block != ScreenModule.SCREEN.get() && block != ScreenModule.CREATIVE_SCREEN.get() && block != ScreenModule.SCREEN_HIT.get())) {
                // Safety
                return;
            }
            if (mouseOver instanceof BlockHitResult) {
                Direction sideHit = ((BlockHitResult) mouseOver).getDirection();
                if (sideHit == blockState.getValue(BlockStateProperties.FACING)) {
                    double xx = mouseOver.getLocation().x - pos.getX();
                    double yy = mouseOver.getLocation().y - pos.getY();
                    double zz = mouseOver.getLocation().z - pos.getZ();
                    Direction horizontalFacing = blockState.getValue(ScreenBlock.HORIZ_FACING);
                    hit = tileEntity.getHitModule(xx, yy, zz, sideHit, horizontalFacing, tileEntity.isDummy() ? 1 : tileEntity.getSize());
                    if (hit != null) {
                        hitModule = modules.get(hit.moduleIndex());
                    }
                    // @todo 1.14
//                if (RFToolsUtility.setup.top) {
//                    tileEntity.focusModuleClient(xx, yy, zz, sideHit, horizontalFacing);
//                }
                }
            }
        }

        for (IClientScreenModule module : modules) {
            if (module != null) {
                int height = module.getHeight();
                // Check if this module has enough room
                if (currenty + height <= 124) {
                    stack.pushPose();
                    switch (module.getTransformMode()) {
                        case TEXT -> {
                            stack.translate(-0.5F, 0.5F, 0.03F);
                            stack.scale(f * factor, minf3 * f * factor, f);
                        }
                        case TEXTLARGE -> {
                            stack.translate(-0.5F, 0.5F, 0.03F);
                            stack.scale(f * 2 * factor, minf3 * f * 2 * factor, f * 2);
                        }
                        case ITEM -> {
                            stack.translate(0, 0, -0.04F);
                            if (tileEntity.isDummy()) {
                                stack.translate(65, 70, 0);
                                stack.scale(1.0f / 0.0075f, -1.0f / 0.0075f, 10000.0f);
                            } else {
//                                    stack.translate(0, -size * 1.06, 0);
                            }
                        }
                    }

                    IModuleData data = screenData.get(moduleIndex);
                    // @todo this is a bit clumsy way to check if the data is compatible with the given module:
                    try {
                        int hitx = -1;
                        int hity = -1;
                        if (module == hitModule) {
                            hitx = hit.x();
                            hity = hit.y() - hit.currenty();
                        }
                        boolean truetype = false;
                        switch (tileEntity.getTrueTypeMode()) {
                            case -1:
                                break;
                            case 1:
                                truetype = !ScreenConfiguration.forceNoTruetype.get();
                                break;
                            case 0: {
                                if (!ScreenConfiguration.forceNoTruetype.get()) {
                                    truetype = ScreenConfiguration.useTruetype.get();
                                }
                            }
                            break;
                        }
                        ModuleRenderInfo renderInfo = new ModuleRenderInfo(factor, pos, hitx, hity, truetype, tileEntity.isBright() || tileEntity.isDummy(), ScreenConfiguration.getTrueTypeFont());
                        module.render(graphics, buffer, clientScreenModuleHelper, fontrenderer, currenty, data, renderInfo);

                    } catch (ClassCastException ignored) {
                    }
                    currenty += height;

                    stack.popPose();
                }
            }
            moduleIndex++;
        }
    }

    private static void renderScreenBoard(PoseStack matrixStack, @Nonnull MultiBufferSource buffer, int size, int color, int packedLightIn, int packedOverlayIn) {
        matrixStack.pushPose();
        matrixStack.scale(1, -1, -1);

        VertexConsumer builder = buffer.getBuffer(CustomRenderTypes.QUADS_NOTEXTURE);

        float dim;
        switch (size) {
            case ScreenTileEntity.SIZE_HUGE -> dim = 2.46f;
            case ScreenTileEntity.SIZE_LARGE -> dim = 1.46f;
            default -> dim = .46f;
        }

        float fr = 0.5f;
        float fg = 0.5f;
        float fb = 0.5f;

        float zback = .05f;
        float zfront = 0f;//10f;//-.00f;

        float ss = .5f;//50;//.5f;

        RenderHelper.drawBox(matrixStack, builder, -ss, ss+ size, -ss, ss+ size, zback, zfront, fr, fg, fb, packedLightIn);
        RenderHelper.drawQuadGui(matrixStack, builder, -.46f, dim, -.46f, dim, -0.01f, 0xff000000 | color, packedLightIn);

        matrixStack.popPose();
    }

    public static void register() {
        BlockEntityRenderers.register(ScreenModule.TYPE_SCREEN.get(), ScreenRenderer::new);
        BlockEntityRenderers.register(ScreenModule.TYPE_CREATIVE_SCREEN.get(), ScreenRenderer::new);
    }
}
