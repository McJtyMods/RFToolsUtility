package mcjty.rftoolsutility.modules.teleporter.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mcjty.lib.client.AbstractDynamicBakedModel;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.client.RenderSettings;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.teleporter.TeleportationTools;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import mcjty.rftoolsutility.modules.teleporter.blocks.MatterTransmitterTileEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BeamRenderer implements BlockEntityRenderer<MatterTransmitterTileEntity, BeamRenderer.State> {

    public static final Identifier BEAM_OK = Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "block/machineteleporter");
    public static final Identifier BEAM_WARN = Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "block/machineteleporterwarn");
    public static final Identifier BEAM_UNKNOWN = Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "block/machineteleporterunknown");

    private static final float BEAM_MIN = .15f;
    private static final float BEAM_MAX = .85f;
    private static final int BEAM_HEIGHT = 4;

    public BeamRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static class State extends BlockEntityRenderState {
        TextureAtlasSprite sprite;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(MatterTransmitterTileEntity entity, State state, float partialTicks, Vec3 camera, CrumblingOverlay overlay) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTicks, camera, overlay);
        state.sprite = entity.isDialed() && !entity.isBeamHidden() ? AbstractDynamicBakedModel.getTexture(switch (entity.getStatus()) {
            case TeleportationTools.STATUS_OK -> BEAM_OK;
            case TeleportationTools.STATUS_WARN -> BEAM_WARN;
            default -> BEAM_UNKNOWN;
        }) : null;
    }

    @Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.sprite != null) {
            collector.submitCustomGeometry(pose, CustomRenderTypes.translucent(), (captured, vertices) -> {
                PoseStack copy = new PoseStack();
                copy.last().set(captured);
                drawBeam(copy, vertices, state.sprite);
            });
        }
    }

    private static void drawBeam(PoseStack pose, VertexConsumer vertices, TextureAtlasSprite sprite) {
        drawBeamFace(pose, vertices, sprite, BEAM_MAX, BEAM_MAX, BEAM_MIN, BEAM_MAX);
        drawBeamFace(pose, vertices, sprite, BEAM_MIN, BEAM_MIN, BEAM_MAX, BEAM_MIN);
        drawBeamFace(pose, vertices, sprite, BEAM_MAX, BEAM_MIN, BEAM_MAX, BEAM_MAX);
        drawBeamFace(pose, vertices, sprite, BEAM_MIN, BEAM_MAX, BEAM_MIN, BEAM_MIN);
    }

    private static void drawBeamFace(PoseStack pose, VertexConsumer vertices, TextureAtlasSprite sprite, float x1, float z1, float x2, float z2) {
        // The beam sprite runs along U. Keep U vertical and V across every face.
        drawBeamVertex(pose, vertices, x1, 0, z1, sprite.getU0(), sprite.getV0());
        drawBeamVertex(pose, vertices, x1, BEAM_HEIGHT, z1, sprite.getU1(), sprite.getV0());
        drawBeamVertex(pose, vertices, x2, BEAM_HEIGHT, z2, sprite.getU1(), sprite.getV1());
        drawBeamVertex(pose, vertices, x2, 0, z2, sprite.getU0(), sprite.getV1());
    }

    private static void drawBeamVertex(PoseStack pose, VertexConsumer vertices, float x, float y, float z, float u, float v) {
        RenderSettings settings = RenderHelper.FULLBRIGHT_SETTINGS;
        int light = settings.brightness();
        RenderHelper.vt(vertices, pose, x, y, z, u, v, light >>> 16, light & 0xffff,
                settings.r(), settings.g(), settings.b(), settings.a());
    }

    public static void register() {
        BlockEntityRenderers.register(TeleporterModule.MATTER_TRANSMITTER.be().get(), BeamRenderer::new);
    }

    @Override
    public AABB getRenderBoundingBox(MatterTransmitterTileEntity entity) {
        return new AABB(Vec3.atLowerCornerOf(entity.getBlockPos()), Vec3.atLowerCornerOf(entity.getBlockPos().offset(1, BEAM_HEIGHT, 1)));
    }
}
