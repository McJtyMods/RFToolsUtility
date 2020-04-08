package mcjty.rftoolsutility.modules.logic.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.client.RenderHelper;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockSetup;
import mcjty.rftoolsutility.modules.logic.blocks.DigitTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
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
        if (!(block instanceof BaseBlock)) {
            return;
        }

        BaseBlock gb = (BaseBlock) block;

        Direction facing = gb.getFrontDirection(gb.getRotationType(), state);

        matrixStack.translate(0.5F, 0.75F, 0.5F);

        if (facing == Direction.UP) {
            matrixStack.rotate(Vector3f.XP.rotationDegrees(-90.0F));
            matrixStack.translate(0.0F, 0.0F, -0.68F);
        } else if (facing == Direction.DOWN) {
            matrixStack.rotate(Vector3f.XP.rotationDegrees(90.0F));
            matrixStack.translate(0.0F, 0.0F, -.184F);
        } else {
            float rotY = 0.0F;
            if (facing == Direction.NORTH) {
                rotY = 180.0F;
            } else if (facing == Direction.WEST) {
                rotY = 90.0F;
            } else if (facing == Direction.EAST) {
                rotY = -90.0F;
            }
            matrixStack.rotate(Vector3f.YP.rotationDegrees(-rotY));
            matrixStack.translate(0.0F, -0.2500F, -0.4375F);
        }

        int level = te.getPowerLevel();
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(DIGITS[level]);
        Matrix4f matrix = matrixStack.getLast().getMatrix();

        float o = 0f;
        RenderHelper.vt(builder, matrix, o, 4, o, sprite.getMaxU(), sprite.getMinV());
        RenderHelper.vt(builder, matrix, 1-o, 4, o, sprite.getMaxU(), sprite.getMaxV());
        RenderHelper.vt(builder, matrix, 1-o, 0, o, sprite.getMinU(), sprite.getMaxV());
        RenderHelper.vt(builder, matrix, o, 0, o, sprite.getMinU(), sprite.getMinV());

        RenderHelper.vt(builder, matrix, o, 0, o, sprite.getMinU(), sprite.getMinV());
        RenderHelper.vt(builder, matrix, 1-o, 0, o, sprite.getMinU(), sprite.getMaxV());
        RenderHelper.vt(builder, matrix, 1-o, 4, o, sprite.getMaxU(), sprite.getMaxV());
        RenderHelper.vt(builder, matrix, o, 4, o, sprite.getMaxU(), sprite.getMinV());

        matrixStack.pop();
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(LogicBlockSetup.TYPE_DIGIT.get(), DigitRenderer::new);
    }

}
