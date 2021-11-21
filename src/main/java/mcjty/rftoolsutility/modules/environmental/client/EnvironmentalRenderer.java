package mcjty.rftoolsutility.modules.environmental.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.client.RenderSettings;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
import mcjty.rftoolsutility.modules.environmental.blocks.EnvironmentalControllerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnvironmentalRenderer extends TileEntityRenderer<EnvironmentalControllerTileEntity> {

    public static final ResourceLocation HALO = new ResourceLocation(RFToolsUtility.MODID, "effects/floatingsphere");
    private static Random random = new Random();

    private static List<EnvironmentalControllerTileEntity> toRender = new ArrayList<>();

    private static final RenderSettings RENDER_SETTINGS = RenderSettings.builder()
            .color(255, 255, 255)
            .renderType(CustomRenderTypes.TRANSLUCENT_LIGHTNING_NOLIGHTMAPS)
            .alpha(128)
            .build();

    public EnvironmentalRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(EnvironmentalControllerTileEntity te, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        if (te.isActive()) {
            toRender.add(te);
//            matrixStack.pushPose();
//            matrixStack.translate(0f, 0f, 0f);
//            float s = 0.6f + random.nextFloat() * .1f;
////            RenderHelper.renderBillboardQuadBright(matrixStack, buffer, s, HALO);
//            RenderHelper.renderBillboardQuadBright(matrixStack, buffer, s, HALO, RENDER_SETTINGS);
//            matrixStack.popPose();
        }
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(EnvironmentalModule.TYPE_ENVIRONENTAL_CONTROLLER.get(), EnvironmentalRenderer::new);
    }

    public static void renderEnvironmentals(RenderWorldLastEvent event) {
        if (toRender.isEmpty()) {
            return;
        }

        MatrixStack matrixStack = event.getMatrixStack();
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        matrixStack.pushPose();

        Vector3d projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);

        for (EnvironmentalControllerTileEntity te : toRender) {
            float s = 0.6f + random.nextFloat() * .1f;
            matrixStack.pushPose();
            matrixStack.translate(te.getBlockPos().getX(), te.getBlockPos().getY(), te.getBlockPos().getZ());
            RenderHelper.renderBillboardQuadBright(matrixStack, buffer, s, HALO, RenderSettings.builder()
                    .color(255, 255, 255)
                    .renderType(CustomRenderTypes.TRANSLUCENT_LIGHTNING_NOLIGHTMAPS)
                    .alpha(128)
                    .build());
            matrixStack.popPose();
        }

        matrixStack.popPose();

//        RenderSystem.disableDepthTest();
//        buffer.endBatch(CustomRenderTypes.TRANSLUCENT_LIGHTNING_NOLIGHTMAPS);

        toRender.clear();
    }
}
