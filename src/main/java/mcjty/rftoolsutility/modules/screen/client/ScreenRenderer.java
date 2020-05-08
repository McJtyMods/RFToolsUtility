package mcjty.rftoolsutility.modules.screen.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.varia.GlobalCoordinate;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.ModuleRenderInfo;
import mcjty.rftoolsbase.api.screens.data.IModuleData;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.ScreenSetup;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenBlock;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import mcjty.rftoolsutility.modules.screen.modulesclient.helper.ClientScreenModuleHelper;
import mcjty.rftoolsutility.modules.screen.network.PacketGetScreenData;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ScreenRenderer extends TileEntityRenderer<ScreenTileEntity> {


    public ScreenRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(ScreenTileEntity tileEntity, float v, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLightIn, int packedOverlayIn) {
        renderInternal(tileEntity, matrixStack, buffer, packedLightIn, packedOverlayIn);
    }

    public static void renderInternal(ScreenTileEntity tileEntity, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLightIn, int packedOverlayIn) {
        float xRotation = 0.0F, yRotation = 0.0F;

        Direction facing = Direction.SOUTH, horizontalFacing = Direction.SOUTH;
        if (!tileEntity.isDummy()) {
            BlockState state = Minecraft.getInstance().world.getBlockState(tileEntity.getPos());
            if (state.getBlock() instanceof ScreenBlock) {
                facing = state.get(BlockStateProperties.FACING);
                horizontalFacing = state.get(ScreenBlock.HORIZ_FACING);
            } else {
                return;
            }
        }

        matrixStack.push();

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
        matrixStack.rotate(Vector3f.YP.rotationDegrees(yRotation));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(xRotation));
        matrixStack.translate(0.0F, 0.0F, -0.4375F);

        if (tileEntity.isDummy()) {
            RenderSystem.disableLighting();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
            AbstractGui.fill(98, 28, 252, 182, 0xffdddddd);
            AbstractGui.fill(100, 30, 250, 180, 0xff333333);
        } else if (!tileEntity.isTransparent()) {
            renderScreenBoard(matrixStack, buffer, tileEntity.getSize(), tileEntity.getColor(), packedLightIn, packedOverlayIn);
        }

        if (tileEntity.isRenderable()) {
            FontRenderer fontrenderer = Minecraft.getInstance().fontRenderer;

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

        matrixStack.pop();
    }

    private static Map<Integer, IModuleData> updateScreenData(ScreenTileEntity screenTileEntity) {
        long millis = System.currentTimeMillis();
        if ((millis - screenTileEntity.lastTime > ScreenConfiguration.SCREEN_REFRESH_TIMING.get()) && screenTileEntity.isNeedsServerData()) {
            screenTileEntity.lastTime = millis;
            GlobalCoordinate pos = new GlobalCoordinate(screenTileEntity.getPos(), screenTileEntity.getDimensionType());
            RFToolsUtilityMessages.INSTANCE.sendToServer(new PacketGetScreenData(RFToolsUtility.MODID, pos, millis));
        }

        GlobalCoordinate key = new GlobalCoordinate(screenTileEntity.getPos(), screenTileEntity.getDimensionType());
        Map<Integer,IModuleData> screenData = ScreenTileEntity.screenData.get(key);
        if (screenData == null) {
            screenData = Collections.emptyMap();
        }
        return screenData;
    }

    private static ClientScreenModuleHelper clientScreenModuleHelper = new ClientScreenModuleHelper();

    private static void renderModules(MatrixStack matrixStack, IRenderTypeBuffer buffer, FontRenderer fontrenderer, ScreenTileEntity tileEntity, List<IClientScreenModule<?>> modules, Map<Integer, IModuleData> screenData, int size) {
        float f3;
        float factor = size + 1.0f;
        int currenty = 7;
        int moduleIndex = 0;

        float f = 0.0075F;
        float minf3 = -1.0f;
        MatrixStack stack;
        if (tileEntity.isDummy()) {
            stack = new MatrixStack();
            stack.translate(100, 30, 0);
            f = 1.0f;
            minf3 = 1.0f;
        } else {
            stack = matrixStack;
        }

        // @todo 1.15 Use 0xf000f0 for the last parameter of renderText
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

        BlockPos pos = tileEntity.getPos();

        RayTraceResult mouseOver = Minecraft.getInstance().objectMouseOver;
        IClientScreenModule<?> hitModule = null;
        ScreenTileEntity.ModuleRaytraceResult hit = null;
        if (!tileEntity.isDummy()) {
            BlockState blockState = tileEntity.getWorld().getBlockState(pos);
            Block block = blockState.getBlock();
            if ((block != ScreenSetup.SCREEN.get() && block != ScreenSetup.CREATIVE_SCREEN.get() && block != ScreenSetup.SCREEN_HIT.get())) {
                // Safety
                return;
            }
            if (mouseOver instanceof BlockRayTraceResult) {
                Direction sideHit = ((BlockRayTraceResult) mouseOver).getFace();
                if (sideHit == blockState.get(BlockStateProperties.FACING)) {
                    double xx = mouseOver.getHitVec().x - pos.getX();
                    double yy = mouseOver.getHitVec().y - pos.getY();
                    double zz = mouseOver.getHitVec().z - pos.getZ();
                    Direction horizontalFacing = blockState.get(ScreenBlock.HORIZ_FACING);
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
                    stack.push();
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
                            case -1: break;
                            case 1: truetype = true; break;
                            case 0: truetype = ScreenConfiguration.useTruetype.get();
                        }
                        ModuleRenderInfo renderInfo = new ModuleRenderInfo(factor, pos, hitx, hity, truetype, tileEntity.isBright() || tileEntity.isDummy());
                        module.render(stack, buffer, clientScreenModuleHelper, fontrenderer, currenty, data, renderInfo);

                    } catch (ClassCastException e) {
                    }
                    currenty += height;

                    stack.pop();
                }
            }
            moduleIndex++;
        }
    }

    private static void renderScreenBoard(MatrixStack matrixStack, @Nullable IRenderTypeBuffer buffer, int size, int color, int packedLightIn, int packedOverlayIn) {
        matrixStack.push();
        matrixStack.scale(1, -1, -1);

        Matrix4f matrix = matrixStack.getLast().getMatrix();

        IVertexBuilder builder = buffer.getBuffer(CustomRenderTypes.QUADS_NOTEXTURE);

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
        builder.pos(matrix, -ss, -ss, zback)  .color(fr, fg, fb, 1f).lightmap(packedLightIn).endVertex();
        builder.pos(matrix, ss +s, -ss, zback) .color(fr, fg, fb, 1f).lightmap(packedLightIn).endVertex();
        builder.pos(matrix, ss +s, ss +s, zback).color(fr, fg, fb, 1f).lightmap(packedLightIn).endVertex();
        builder.pos(matrix, -ss, ss +s, zback) .color(fr, fg, fb, 1f).lightmap(packedLightIn).endVertex();

        // FRONT
        builder.pos(matrix, -ss, ss +s, zfront) .color(fr * .8f, fg * .8f, fb * .8f, 1f).lightmap(packedLightIn).endVertex();
        builder.pos(matrix, ss +s, ss +s, zfront).color(fr * .8f, fg * .8f, fb * .8f, 1f).lightmap(packedLightIn).endVertex();
        builder.pos(matrix, ss +s, -ss, zfront) .color(fr * .8f, fg * .8f, fb * .8f, 1f).lightmap(packedLightIn).endVertex();
        builder.pos(matrix, -ss, -ss, zfront)  .color(fr * .8f, fg * .8f, fb * .8f, 1f).lightmap(packedLightIn).endVertex();

        // DOWN
        builder.pos(matrix, -ss, ss +s, zback)  .color(fr, fg, fb, 1f).lightmap(packedLightIn).endVertex();
        builder.pos(matrix, ss +s, ss +s, zback) .color(fr, fg, fb, 1f).lightmap(packedLightIn).endVertex();
        builder.pos(matrix, ss +s, ss +s, zfront).color(fr, fg, fb, 1f).lightmap(packedLightIn).endVertex();
        builder.pos(matrix, -ss, ss +s, zfront) .color(fr, fg, fb, 1f).lightmap(packedLightIn).endVertex();

        // UP
        builder.pos(matrix, -ss, -ss, zfront)  .color(fr, fg, fb, 1f).lightmap(packedLightIn).endVertex();
        builder.pos(matrix, ss +s, -ss, zfront) .color(fr, fg, fb, 1f).lightmap(packedLightIn).endVertex();
        builder.pos(matrix, ss +s, -ss, zback)  .color(fr, fg, fb, 1f).lightmap(packedLightIn).endVertex();
        builder.pos(matrix, -ss, -ss, zback)   .color(fr, fg, fb, 1f).lightmap(packedLightIn).endVertex();

        // LEFT
        builder.pos(matrix, -ss, -ss, zfront)  .color(fr, fg, fb, 1f).lightmap(packedLightIn).endVertex();
        builder.pos(matrix, -ss, -ss, zback)   .color(fr, fg, fb, 1f).lightmap(packedLightIn).endVertex();
        builder.pos(matrix, -ss, ss +s, zback)  .color(fr, fg, fb, 1f).lightmap(packedLightIn).endVertex();
        builder.pos(matrix, -ss, ss +s, zfront) .color(fr, fg, fb, 1f).lightmap(packedLightIn).endVertex();

        // RIGHT
        builder.pos(matrix, ss +s, ss +s, zfront).color(fr, fg, fb, 1f).lightmap(packedLightIn).endVertex();
        builder.pos(matrix, ss +s, ss +s, zback) .color(fr, fg, fb, 1f).lightmap(packedLightIn).endVertex();
        builder.pos(matrix, ss +s, -ss, zback)  .color(fr, fg, fb, 1f).lightmap(packedLightIn).endVertex();
        builder.pos(matrix, ss +s, -ss, zfront) .color(fr, fg, fb, 1f).lightmap(packedLightIn).endVertex();


        float r = ((color & 16711680) >> 16) / 255.0F;
        float g = ((color & 65280) >> 8) / 255.0F;
        float b = ((color & 255)) / 255.0F;
        builder.pos(matrix, -.46f, dim, -0.01f).color(r, g, b, 1f).lightmap(packedLightIn).endVertex();
        builder.pos(matrix, dim, dim, -0.01f).color(r, g, b, 1f).lightmap(packedLightIn).endVertex();
        builder.pos(matrix, dim, -.46f, -0.01f).color(r, g, b, 1f).lightmap(packedLightIn).endVertex();
        builder.pos(matrix, -.46f, -.46f, -0.01f).color(r, g, b, 1f).lightmap(packedLightIn).endVertex();

        matrixStack.pop();
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(ScreenSetup.TYPE_SCREEN.get(), ScreenRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ScreenSetup.TYPE_CREATIVE_SCREEN.get(), ScreenRenderer::new);
    }
}
