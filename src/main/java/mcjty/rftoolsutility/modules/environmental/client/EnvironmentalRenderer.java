package mcjty.rftoolsutility.modules.environmental.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.client.RenderSettings;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
import mcjty.rftoolsutility.modules.environmental.blocks.EnvironmentalControllerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnvironmentalRenderer implements BlockEntityRenderer<EnvironmentalControllerTileEntity> {

    public static final ResourceLocation HALO = new ResourceLocation(RFToolsUtility.MODID, "block/effects/floatingsphere");
    private static final Random random = new Random();

    private static final List<EnvironmentalControllerTileEntity> toRender = new ArrayList<>();

    private static final RenderSettings RENDER_SETTINGS = RenderSettings.builder()
            .color(255, 255, 255)
            .renderType(CustomRenderTypes.TRANSLUCENT_LIGHTNING_NOLIGHTMAPS)
            .alpha(128)
            .build();

    public EnvironmentalRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(EnvironmentalControllerTileEntity te, float partialTicks, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
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
        BlockEntityRenderers.register(EnvironmentalModule.TYPE_ENVIRONENTAL_CONTROLLER.get(), EnvironmentalRenderer::new);
    }

    // @todo 1.20 correct event?
    public static void renderEnvironmentals(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        if (toRender.isEmpty()) {
            return;
        }

        PoseStack matrixStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        matrixStack.pushPose();

        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
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
