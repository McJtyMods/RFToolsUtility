package mcjty.rftoolsutility.modules.logic.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.varia.LogicFacing;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.blocks.DigitTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class DigitRenderer extends TileEntityRenderer<DigitTileEntity> {

    public static final ResourceLocation[] DIGITS = new ResourceLocation[] {
            new ResourceLocation(RFToolsUtility.MODID, "block/logic/machineoutput_0"),
            new ResourceLocation(RFToolsUtility.MODID, "block/logic/machineoutput_1"),
            new ResourceLocation(RFToolsUtility.MODID, "block/logic/machineoutput_2"),
            new ResourceLocation(RFToolsUtility.MODID, "block/logic/machineoutput_3"),
            new ResourceLocation(RFToolsUtility.MODID, "block/logic/machineoutput_4"),
            new ResourceLocation(RFToolsUtility.MODID, "block/logic/machineoutput_5"),
            new ResourceLocation(RFToolsUtility.MODID, "block/logic/machineoutput_6"),
            new ResourceLocation(RFToolsUtility.MODID, "block/logic/machineoutput_7"),
            new ResourceLocation(RFToolsUtility.MODID, "block/logic/machineoutput_8"),
            new ResourceLocation(RFToolsUtility.MODID, "block/logic/machineoutput_9"),
            new ResourceLocation(RFToolsUtility.MODID, "block/logic/machineoutput_a"),
            new ResourceLocation(RFToolsUtility.MODID, "block/logic/machineoutput_b"),
            new ResourceLocation(RFToolsUtility.MODID, "block/logic/machineoutput_c"),
            new ResourceLocation(RFToolsUtility.MODID, "block/logic/machineoutput_d"),
            new ResourceLocation(RFToolsUtility.MODID, "block/logic/machineoutput_e"),
            new ResourceLocation(RFToolsUtility.MODID, "block/logic/machineoutput_f")
    };

    public DigitRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(DigitTileEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        matrixStack.push();
        IVertexBuilder builder = buffer.getBuffer(RenderType.getCutout());

        BlockState state = te.getWorld().getBlockState(te.getPos());
        Block block = state.getBlock();
        if (!(block instanceof LogicSlabBlock)) {
            return;
        }

        LogicFacing logicFacing = state.get(LogicSlabBlock.LOGIC_FACING);
        Direction facing = logicFacing.getSide();

        RenderHelper.adjustTransformToDirection(matrixStack, facing);

        int level = te.getPowerLevel();

        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(DIGITS[level]);
        Matrix4f matrix = matrixStack.getLast().getMatrix();

        ModelBuilder.FaceRotation rotation = ModelBuilder.FaceRotation.values()[logicFacing.getRotationStep()];
        RenderHelper.renderNorthSouthQuad(builder, matrix, sprite, rotation, .73f);

        matrixStack.pop();
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(LogicBlockModule.TYPE_DIGIT.get(), DigitRenderer::new);
    }

}
