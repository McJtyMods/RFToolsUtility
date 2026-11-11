package mcjty.rftoolsutility.modules.environmental.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.client.AbstractDynamicBakedModel;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderSettings;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
import mcjty.rftoolsutility.modules.environmental.blocks.EnvironmentalControllerTileEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public class EnvironmentalRenderer implements BlockEntityRenderer<EnvironmentalControllerTileEntity, EnvironmentalRenderer.State> {

    public static final Identifier HALO = Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "block/effects/floatingsphere");
    private static final RenderSettings SETTINGS = RenderSettings.builder().color(255, 255, 255)
            .renderType(CustomRenderTypes.translucent()).alpha(128).build();

    public EnvironmentalRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static class State extends BlockEntityRenderState {
        boolean active;
        float size;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(EnvironmentalControllerTileEntity entity, State state, float partialTicks, Vec3 camera, CrumblingOverlay overlay) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTicks, camera, overlay);
        state.active = entity.isActive();
        state.size = .65f + .05f * (float) Math.sin(System.currentTimeMillis() / 150.0);
    }

    @Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.active) {
            billboard(pose, collector, camera, state.size, HALO, SETTINGS);
        }
    }

    private static void billboard(PoseStack pose, SubmitNodeCollector collector,
            CameraRenderState camera, float size, Identifier texture, RenderSettings settings) {
        var sprite = AbstractDynamicBakedModel.getTexture(texture);
        pose.pushPose();
        pose.translate(.5f, .5f, .5f);
        pose.mulPose(camera.orientation);
        collector.submitCustomGeometry(pose, settings.renderType(), (captured, vertex) -> {
            var matrix = captured.pose();
            vertex.addVertex(matrix, -size, -size, 0).setColor(settings.r(), settings.g(), settings.b(), settings.a()).setUv(sprite.getU0(), sprite.getV1()).setOverlay(0).setLight(settings.brightness()).setNormal(0, 0, 1);
            vertex.addVertex(matrix, size, -size, 0).setColor(settings.r(), settings.g(), settings.b(), settings.a()).setUv(sprite.getU1(), sprite.getV1()).setOverlay(0).setLight(settings.brightness()).setNormal(0, 0, 1);
            vertex.addVertex(matrix, size, size, 0).setColor(settings.r(), settings.g(), settings.b(), settings.a()).setUv(sprite.getU1(), sprite.getV0()).setOverlay(0).setLight(settings.brightness()).setNormal(0, 0, 1);
            vertex.addVertex(matrix, -size, size, 0).setColor(settings.r(), settings.g(), settings.b(), settings.a()).setUv(sprite.getU0(), sprite.getV0()).setOverlay(0).setLight(settings.brightness()).setNormal(0, 0, 1);
        });
        pose.popPose();
    }

    public static void register() {
        BlockEntityRenderers.register(EnvironmentalModule.ENVIRONENTAL_CONTROLLER.be().get(), EnvironmentalRenderer::new);
    }
}
