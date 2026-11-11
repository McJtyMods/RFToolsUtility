package mcjty.rftoolsutility.modules.logic.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.client.AbstractDynamicBakedModel;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.varia.LogicFacing;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.blocks.DigitTileEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public class DigitRenderer implements BlockEntityRenderer<DigitTileEntity, DigitRenderer.State> {

    public static final Identifier[] DIGITS = new Identifier[] {
            Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_0"),
            Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_1"),
            Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_2"),
            Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_3"),
            Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_4"),
            Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_5"),
            Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_6"),
            Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_7"),
            Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_8"),
            Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_9"),
            Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_a"),
            Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_b"),
            Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_c"),
            Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_d"),
            Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_e"),
            Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_f")
    };

    public DigitRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static class State extends BlockEntityRenderState {
        LogicFacing facing;
        TextureAtlasSprite sprite;
    }

    @Override public State createRenderState() { return new State(); }

    @Override public void extractRenderState(DigitTileEntity te, State state, float partialTicks,
            Vec3 camera, ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderer.super.extractRenderState(te, state, partialTicks, camera, overlay);
        state.facing = te.getBlockState().getValue(LogicSlabBlock.LOGIC_FACING);
        int digit = Math.clamp(te.getPowerLevel(), 0, 15);
        state.sprite = AbstractDynamicBakedModel.getTexture(DIGITS[digit]);
    }

    @Override public void submit(State state, PoseStack pose, SubmitNodeCollector collector,
            CameraRenderState camera) {
        pose.pushPose();
        RenderHelper.adjustTransformToDirection(pose, state.facing.getSide());
        var rotation = RenderHelper.FaceRotation.values()[state.facing.getRotationStep()];
        collector.submitCustomGeometry(pose, RenderTypes.cutoutMovingBlock(), (captured, builder) -> {
            PoseStack copy = new PoseStack();
            copy.last().pose().set(captured.pose());
            RenderHelper.renderNorthSouthQuad(copy, builder, state.sprite, rotation, .73f);
        });
        pose.popPose();
    }

    public static void register() {
        BlockEntityRenderers.register(LogicBlockModule.DIGIT.be().get(), DigitRenderer::new);
    }

}
