package mcjty.rftoolsutility.modules.spawner.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.client.AbstractDynamicBakedModel;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderGlowEffect;
import mcjty.lib.client.RenderHelper;
import mcjty.rftoolsbase.RFToolsBase;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.spawner.blocks.MatterBeamerTileEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MatterBeamerRenderer implements BlockEntityRenderer<MatterBeamerTileEntity, MatterBeamerRenderer.State> {

    public static final Identifier REDGLOW = Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "block/effects/redglow");
    public static final Identifier BLUEGLOW = Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "block/effects/blueglow");
    private static final Vec3 START = new Vec3(.5, .5, .5);

    public MatterBeamerRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static class State extends BlockEntityRenderState {
        Vec3 destination;
        Vec3 camera;
        TextureAtlasSprite sprite;
        Identifier glow;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(MatterBeamerTileEntity entity, State state, float partialTicks, Vec3 camera, CrumblingOverlay overlay) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTicks, camera, overlay);
        BlockPos pos = entity.getBlockPos();
        BlockPos destination = entity.getDestination();
        state.destination = destination != null && entity.isGlowing()
                ? Vec3.atCenterOf(destination).subtract(Vec3.atLowerCornerOf(pos)) : null;
        state.camera = camera.subtract(Vec3.atLowerCornerOf(pos));
        state.sprite = AbstractDynamicBakedModel.getTexture(REDGLOW);
        var info = RFToolsBase.instance.clientInfo;
        state.glow = pos.equals(info.getSelectedTE()) ? REDGLOW : pos.equals(info.getDestinationTE()) ? BLUEGLOW : null;
    }

    @Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.destination != null) {
            collector.submitCustomGeometry(pose, CustomRenderTypes.translucent(), (captured, vertices) -> {
                PoseStack copy = new PoseStack();
                copy.last().set(captured);
                RenderHelper.drawBeam(copy, vertices, state.sprite, START, state.destination, state.camera, .1f);
            });
        }
        if (state.glow != null) {
            RenderGlowEffect.renderGlow(pose, collector, state.glow);
        }
    }

    public static void register() {
        BlockEntityRenderers.register(SpawnerModule.MATTER_BEAMER.be().get(), MatterBeamerRenderer::new);
    }

    @Override
    public AABB getRenderBoundingBox(MatterBeamerTileEntity entity) {
        return new AABB(entity.getBlockPos()).inflate(4);
    }
}
