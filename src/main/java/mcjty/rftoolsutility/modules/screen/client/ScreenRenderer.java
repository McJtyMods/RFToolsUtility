package mcjty.rftoolsutility.modules.screen.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
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
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ScreenRenderer extends TileEntityRenderer<ScreenTileEntity> {

    public static final ResourceLocation SCREEN_FRAME = new ResourceLocation(RFToolsUtility.MODID, "textures/block/screenframe.png");
    private final ModelScreen screenModel = new ModelScreen(ScreenTileEntity.SIZE_NORMAL);
    private final ModelScreen screenModelLarge = new ModelScreen(ScreenTileEntity.SIZE_LARGE);
    private final ModelScreen screenModelHuge = new ModelScreen(ScreenTileEntity.SIZE_HUGE);

    public ScreenRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(ScreenTileEntity tileEntity, float v, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLightIn, int packedOverlayIn) {
        float xRotation = 0.0F, yRotation = 0.0F;

        Direction facing = Direction.SOUTH, horizontalFacing = Direction.SOUTH;
        if (tileEntity != null) {
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
        matrixStack.translate((float) 0.5F, (float) 0.5F, (float) 0.5F);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(yRotation));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(xRotation));
        matrixStack.translate(0.0F, 0.0F, -0.4375F);

        if (tileEntity == null) {
            renderScreenBoard(matrixStack, buffer, 0, 0, packedLightIn, packedOverlayIn);
        } else if (!tileEntity.isTransparent()) {
            renderScreenBoard(matrixStack, buffer, tileEntity.getSize(), tileEntity.getColor(), packedLightIn, packedOverlayIn);
        }

        if (tileEntity != null && tileEntity.isRenderable()) {
            FontRenderer fontrenderer = Minecraft.getInstance().fontRenderer;

            IClientScreenModule.TransformMode mode = IClientScreenModule.TransformMode.NONE;
            // @todo 1.15
//            GlStateManager.depthMask(false);
//            GlStateManager.disableLighting();

            Map<Integer, IModuleData> screenData = updateScreenData(tileEntity);

            List<IClientScreenModule<?>> modules = tileEntity.getClientScreenModules();
            if (tileEntity.isShowHelp()) {
                modules = ScreenTileEntity.getHelpingScreenModules();
            }
            renderModules(matrixStack, buffer, fontrenderer, tileEntity, mode, modules, screenData, tileEntity.getSize());
        }


        matrixStack.pop();
    }

    private Map<Integer, IModuleData> updateScreenData(ScreenTileEntity screenTileEntity) {
        long millis = System.currentTimeMillis();
        if ((millis - screenTileEntity.lastTime > ScreenConfiguration.SCREEN_REFRESH_TIMING.get()) && screenTileEntity.isNeedsServerData()) {
            screenTileEntity.lastTime = millis;
            GlobalCoordinate pos = new GlobalCoordinate(screenTileEntity.getPos(), screenTileEntity.getWorld().getDimension().getType());
            RFToolsUtilityMessages.INSTANCE.sendToServer(new PacketGetScreenData(RFToolsUtility.MODID, pos, millis));
        }

        GlobalCoordinate key = new GlobalCoordinate(screenTileEntity.getPos(), screenTileEntity.getWorld().getDimension().getType());
        Map<Integer,IModuleData> screenData = ScreenTileEntity.screenData.get(key);
        if (screenData == null) {
            screenData = Collections.emptyMap();
        }
        return screenData;
    }

    private ClientScreenModuleHelper clientScreenModuleHelper = new ClientScreenModuleHelper();

    private void renderModules(MatrixStack matrixStack, IRenderTypeBuffer buffer, FontRenderer fontrenderer, ScreenTileEntity tileEntity, IClientScreenModule.TransformMode mode, List<IClientScreenModule<?>> modules, Map<Integer, IModuleData> screenData, int size) {
        float f3;
        float factor = size + 1.0f;
        int currenty = 7;
        int moduleIndex = 0;

        BlockPos pos = tileEntity.getPos();

        RayTraceResult mouseOver = Minecraft.getInstance().objectMouseOver;
        IClientScreenModule<?> hitModule = null;
        ScreenTileEntity.ModuleRaytraceResult hit = null;
        BlockState blockState = tileEntity.getWorld().getBlockState(pos);
        Block block = blockState.getBlock();
        if (block != ScreenSetup.SCREEN.get() && block != ScreenSetup.CREATIVE_SCREEN.get() && block != ScreenSetup.SCREEN_HIT.get()) {
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
                hit = tileEntity.getHitModule(xx, yy, zz, sideHit, horizontalFacing);
                if (hit != null) {
                    hitModule = modules.get(hit.getModuleIndex());
                }
                // @todo 1.14
//                if (RFToolsUtility.setup.top) {
//                    tileEntity.focusModuleClient(xx, yy, zz, sideHit, horizontalFacing);
//                }
            }
        }

        RenderType type;
        if (tileEntity.isBright()) {
            // @todo 1.15 use render type
//            Minecraft.getInstance().gameRenderer.getLightTexture().disableLightmap();
            type = RenderType.lines();
        } else {
            type = RenderType.lines();
        }

        for (IClientScreenModule module : modules) {
            if (module != null) {
                int height = module.getHeight();
                // Check if this module has enough room
                if (currenty + height <= 124) {
                    if (module.getTransformMode() != mode) {
                        if (mode != IClientScreenModule.TransformMode.NONE) {
                            matrixStack.pop();
                        }
                        matrixStack.push();
                        mode = module.getTransformMode();

                        switch (mode) {
                            case TEXT:
                                matrixStack.translate(-0.5F, 0.5F, 0.07F);
                                f3 = 0.0075F;
                                matrixStack.scale(f3 * factor, -f3 * factor, f3);
//                                GL11.glNormal3f(0.0F, 0.0F, -1.0F);
//                                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                                break;
                            case TEXTLARGE:
                                matrixStack.translate(-0.5F, 0.5F, 0.07F);
                                f3 = 0.0075F * 2;
                                matrixStack.scale(f3 * factor, -f3 * factor, f3);
//                                GL11.glNormal3f(0.0F, 0.0F, -1.0F);
//                                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                                break;
                            case ITEM:
                                break;
                            default:
                                break;
                        }
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
                        ModuleRenderInfo renderInfo = new ModuleRenderInfo(factor, pos, hitx, hity, truetype);
                        module.render(matrixStack, buffer, clientScreenModuleHelper, fontrenderer, currenty, data, renderInfo);
                    } catch (ClassCastException e) {
                    }
                    currenty += height;
                }
            }
            moduleIndex++;
        }

        if (tileEntity.isBright()) {
            // @todo 1.15
//            Minecraft.getInstance().gameRenderer.getLightTexture().enableLightmap();
        }

        if (mode != IClientScreenModule.TransformMode.NONE) {
            matrixStack.pop();
        }
    }

    private void renderScreenBoard(MatrixStack matrixStack, IRenderTypeBuffer buffer, int size, int color, int packedLightIn, int packedOverlayIn) {
        TextureAtlasSprite frame = Minecraft.getInstance().getTextureGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(SCREEN_FRAME);
//        IVertexBuilder builder = buffer.getBuffer(ScreenRenderType.QUADS_NOTEXTURE);
        IVertexBuilder builder = buffer.getBuffer(RenderType.solid());

        matrixStack.push();
        matrixStack.scale(1, -1, -1);
        if (size == ScreenTileEntity.SIZE_HUGE) {
            this.screenModelHuge.render(matrixStack, builder, packedLightIn, packedOverlayIn, 1, 1, 1, 1);  // @todo 1.15 is r,g,b,a correct?
        } else if (size == ScreenTileEntity.SIZE_LARGE) {
            this.screenModelLarge.render(matrixStack, builder, packedLightIn, packedOverlayIn, 1, 1, 1, 1);
        } else {
            this.screenModel.render(matrixStack, builder, packedLightIn, packedOverlayIn, 1, 1, 1, 1);
        }

        float dim;
        if (size == ScreenTileEntity.SIZE_HUGE) {
            dim = 2.46f;
        } else if (size == ScreenTileEntity.SIZE_LARGE) {
            dim = 1.46f;
        } else {
            dim = .46f;
        }
        float r = ((color & 16711680) >> 16) / 255.0F;
        float g = ((color & 65280) >> 8) / 255.0F;
        float b = ((color & 255)) / 255.0F;
        Matrix4f matrix = matrixStack.getLast().getPositionMatrix();
        // @todo 1.15 calculate correct normal!
        builder.pos(matrix, -.46f, dim, -0.08f).color(r, g, b, 1f)              .tex(frame.getMinU(), frame.getMinV()).lightmap(packedLightIn, packedOverlayIn).normal(1, 0, 0).endVertex();
        builder.pos(matrix, dim, dim, -0.08f).color(r, g, b, 1f)                            .tex(frame.getMinU(), frame.getMaxV()).lightmap(packedLightIn, packedOverlayIn).normal(1, 0, 0).endVertex();
        builder.pos(matrix, dim, -.46f, -0.08f).color(r, g, b, 1f)              .tex(frame.getMaxU(), frame.getMaxV()).lightmap(packedLightIn, packedOverlayIn).normal(1, 0, 0).endVertex();
        builder.pos(matrix, -.46f, -.46f, -0.08f).color(r, g, b, 1f).tex(frame.getMaxU(), frame.getMinV()).lightmap(packedLightIn, packedOverlayIn).normal(1, 0, 0).endVertex();

        matrixStack.pop();
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(ScreenSetup.TYPE_SCREEN.get(), ScreenRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ScreenSetup.TYPE_CREATIVE_SCREEN.get(), ScreenRenderer::new);
    }
}
