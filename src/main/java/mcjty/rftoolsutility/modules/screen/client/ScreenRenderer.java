package mcjty.rftoolsutility.modules.screen.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mcjty.lib.client.CustomRenderTypes;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.phys.HitResult;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraftforge.client.ClientRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ScreenRenderer extends BlockEntityRenderer<ScreenTileEntity> {


    public ScreenRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
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
            case NORTH:
                yRotation = -180.0F;
                break;
            case WEST:
                yRotation = -90.0F;
                break;
            case EAST:
                yRotation = 90.0F;
        }
        switch (facing) {
            case DOWN:
                xRotation = 90.0F;
                break;
            case UP:
                xRotation = -90.0F;
        }

        // TileEntity can be null if this is used for an item renderer.
        matrixStack.translate(0.5F, 0.5F, 0.5F);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(yRotation));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(xRotation));
        matrixStack.translate(0.0F, 0.0F, -0.4375F);

        if (tileEntity.isDummy()) {
            RenderSystem.disableLighting();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
            GuiComponent.fill(matrixStack, 98, 28, 252, 182, 0xffdddddd);
            GuiComponent.fill(matrixStack, 100, 30, 250, 180, 0xff333333);
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
            renderModules(matrixStack, buffer, fontrenderer, tileEntity, modules, screenData, tileEntity.isDummy() ? 0 : tileEntity.getSize());
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

    private static ClientScreenModuleHelper clientScreenModuleHelper = new ClientScreenModuleHelper();

    private static void renderModules(PoseStack matrixStack, MultiBufferSource buffer, Font fontrenderer, ScreenTileEntity tileEntity, List<IClientScreenModule<?>> modules, Map<Integer, IModuleData> screenData, int size) {
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
            stack = matrixStack;
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
                        hitModule = modules.get(hit.getModuleIndex());
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
                        case TEXT:
                            stack.translate(-0.5F, 0.5F, 0.03F);
                            stack.scale(f * factor, minf3 * f * factor, f);
                            break;
                        case TEXTLARGE:
                            stack.translate(-0.5F, 0.5F, 0.03F);
                            stack.scale(f * 2 * factor, minf3 * f * 2 * factor, f * 2);
                            break;
                        case ITEM:
                            stack.translate(0, 0, -0.04F);
                            if (tileEntity.isDummy()) {
                                stack.translate(65, 70, 0);
                                stack.scale(1.0f / 0.0075f, -1.0f / 0.0075f, 10000.0f);
                            } else {
//                                    stack.translate(0, -size * 1.06, 0);
                            }
                            break;
                        default:
                            break;
                    }

                    IModuleData data = screenData.get(moduleIndex);
                    // @todo this is a bit clumsy way to check if the data is compatible with the given module:
                    try {
                        int hitx = -1;
                        int hity = -1;
                        if (module == hitModule) {
                            hitx = hit.getX();
                            hity = hit.getY() - hit.getCurrenty();
                        }
                        boolean truetype = false;
                        switch (tileEntity.getTrueTypeMode()) {
                            case -1:
                                break;
                            case 1: {
                                if (ScreenConfiguration.forceNoTruetype.get()) {
                                    truetype = false;
                                } else {
                                    truetype = true;
                                }
                            }
                            break;
                            case 0: {
                                if (ScreenConfiguration.forceNoTruetype.get()) {
                                    truetype = false;
                                } else {
                                    truetype = ScreenConfiguration.useTruetype.get();
                                }
                            }
                            break;
                        }
                        ModuleRenderInfo renderInfo = new ModuleRenderInfo(factor, pos, hitx, hity, truetype, tileEntity.isBright() || tileEntity.isDummy(), ScreenConfiguration.getTrueTypeFont());
                        module.render(stack, buffer, clientScreenModuleHelper, fontrenderer, currenty, data, renderInfo);

                    } catch (ClassCastException e) {
                    }
                    currenty += height;

                    stack.popPose();
                }
            }
            moduleIndex++;
        }
    }

    private static void renderScreenBoard(PoseStack matrixStack, @Nullable MultiBufferSource buffer, int size, int color, int packedLightIn, int packedOverlayIn) {
        matrixStack.pushPose();
        matrixStack.scale(1, -1, -1);

        Matrix4f matrix = matrixStack.last().pose();

        VertexConsumer builder = buffer.getBuffer(CustomRenderTypes.QUADS_NOTEXTURE);

        float dim;
        float s;
        if (size == ScreenTileEntity.SIZE_HUGE) {
            dim = 2.46f;
            s = 2;
        } else if (size == ScreenTileEntity.SIZE_LARGE) {
            dim = 1.46f;
            s = 1;
        } else {
            dim = .46f;
            s = 0;
        }


        float fr = 0.5f;
        float fg = 0.5f;
        float fb = 0.5f;

        float zback = .05f;
        float zfront = 0f;//10f;//-.00f;

        float ss = .5f;//50;//.5f;

        // BACK
        builder.vertex(matrix, -ss, -ss, zback).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, ss + s, -ss, zback).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, ss + s, ss + s, zback).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, -ss, ss + s, zback).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();

        // FRONT
        builder.vertex(matrix, -ss, ss + s, zfront).color(fr * .8f, fg * .8f, fb * .8f, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, ss + s, ss + s, zfront).color(fr * .8f, fg * .8f, fb * .8f, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, ss + s, -ss, zfront).color(fr * .8f, fg * .8f, fb * .8f, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, -ss, -ss, zfront).color(fr * .8f, fg * .8f, fb * .8f, 1f).uv2(packedLightIn).endVertex();

        // DOWN
        builder.vertex(matrix, -ss, ss + s, zback).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, ss + s, ss + s, zback).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, ss + s, ss + s, zfront).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, -ss, ss + s, zfront).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();

        // UP
        builder.vertex(matrix, -ss, -ss, zfront).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, ss + s, -ss, zfront).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, ss + s, -ss, zback).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, -ss, -ss, zback).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();

        // LEFT
        builder.vertex(matrix, -ss, -ss, zfront).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, -ss, -ss, zback).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, -ss, ss + s, zback).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, -ss, ss + s, zfront).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();

        // RIGHT
        builder.vertex(matrix, ss + s, ss + s, zfront).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, ss + s, ss + s, zback).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, ss + s, -ss, zback).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, ss + s, -ss, zfront).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();


        float r = ((color & 16711680) >> 16) / 255.0F;
        float g = ((color & 65280) >> 8) / 255.0F;
        float b = ((color & 255)) / 255.0F;
        builder.vertex(matrix, -.46f, dim, -0.01f).color(r, g, b, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, dim, dim, -0.01f).color(r, g, b, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, dim, -.46f, -0.01f).color(r, g, b, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, -.46f, -.46f, -0.01f).color(r, g, b, 1f).uv2(packedLightIn).endVertex();

        matrixStack.popPose();
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(ScreenModule.TYPE_SCREEN.get(), ScreenRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ScreenModule.TYPE_CREATIVE_SCREEN.get(), ScreenRenderer::new);
    }
}
