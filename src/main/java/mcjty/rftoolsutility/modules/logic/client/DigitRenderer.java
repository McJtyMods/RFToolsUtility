package mcjty.rftoolsutility.modules.logic.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.varia.LogicFacing;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.blocks.DigitTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;

public class DigitRenderer implements BlockEntityRenderer<DigitTileEntity> {

    public static final ResourceLocation[] DIGITS = new ResourceLocation[] {
            ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_0"),
            ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_1"),
            ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_2"),
            ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_3"),
            ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_4"),
            ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_5"),
            ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_6"),
            ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_7"),
            ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_8"),
            ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_9"),
            ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_a"),
            ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_b"),
            ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_c"),
            ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_d"),
            ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_e"),
            ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "block/logic/machineoutput_f")
    };

    public DigitRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(DigitTileEntity te, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        BlockState state = te.getLevel().getBlockState(te.getBlockPos());
        Block block = state.getBlock();
        if (!(block instanceof LogicSlabBlock)) {
            return;
        }

        matrixStack.pushPose();
        VertexConsumer builder = buffer.getBuffer(RenderType.cutout());

        LogicFacing logicFacing = state.getValue(LogicSlabBlock.LOGIC_FACING);
        Direction facing = logicFacing.getSide();

        RenderHelper.adjustTransformToDirection(matrixStack, facing);

        int level = te.getPowerLevel();
        if (level < 0) {
            level = 0;
        } else if (level > 15) {
            level = 15;
        }

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(DIGITS[level]);

        ModelBuilder.FaceRotation rotation = ModelBuilder.FaceRotation.values()[logicFacing.getRotationStep()];
        RenderHelper.renderNorthSouthQuad(matrixStack, builder, sprite, rotation, .73f);

        matrixStack.popPose();
    }

    public static void register() {
        BlockEntityRenderers.register(LogicBlockModule.TYPE_DIGIT.get(), DigitRenderer::new);
    }

}
