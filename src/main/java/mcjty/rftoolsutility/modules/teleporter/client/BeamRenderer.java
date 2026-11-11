package mcjty.rftoolsutility.modules.teleporter.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.client.AbstractDynamicBakedModel;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderHelper;
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
                RenderHelper.drawBox(copy, vertices, state.sprite, false, false, true, true, true, true,
                        .85f, .15f, 0, 4, .85f, .15f, RenderHelper.FULLBRIGHT_SETTINGS);
            });
        }
    }

    public static void register() {
        BlockEntityRenderers.register(TeleporterModule.MATTER_TRANSMITTER.be().get(), BeamRenderer::new);
    }

    @Override
    public AABB getRenderBoundingBox(MatterTransmitterTileEntity entity) {
        return new AABB(Vec3.atLowerCornerOf(entity.getBlockPos()), Vec3.atLowerCornerOf(entity.getBlockPos().offset(1, 4, 1)));
    }
}
