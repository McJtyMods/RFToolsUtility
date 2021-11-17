package mcjty.rftoolsutility.modules.teleporter.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcjty.lib.client.RenderHelper;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.teleporter.TeleportationTools;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import mcjty.rftoolsutility.modules.teleporter.blocks.MatterTransmitterTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nonnull;

public class BeamRenderer extends TileEntityRenderer<MatterTransmitterTileEntity> {

    public static final ResourceLocation BEAM_OK = new ResourceLocation(RFToolsUtility.MODID, "block/machineteleporter");
    public static final ResourceLocation BEAM_WARN = new ResourceLocation(RFToolsUtility.MODID, "block/machineteleporterwarn");
    public static final ResourceLocation BEAM_UNKNOWN = new ResourceLocation(RFToolsUtility.MODID, "block/machineteleporterunknown");

    public BeamRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(MatterTransmitterTileEntity tileEntity, float v, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int i, int ix) {
        if (tileEntity.isDialed() && !tileEntity.isBeamHidden()) {

            ResourceLocation beamIcon = null;
            switch (tileEntity.getStatus()) {
                case TeleportationTools.STATUS_OK: beamIcon = BEAM_OK; break;
                case TeleportationTools.STATUS_WARN: beamIcon = BEAM_WARN; break;
                default: beamIcon = BEAM_UNKNOWN; break;
            }

            //noinspection deprecation
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(beamIcon);

            IVertexBuilder builder = buffer.getBuffer(RenderType.translucent());

            Matrix4f matrix = matrixStack.last().pose();

            float o = .15f;
            RenderHelper.vt(builder, matrix, o, 4, o, sprite.getU1(), sprite.getV0());
            RenderHelper.vt(builder, matrix, 1-o, 4, o, sprite.getU1(), sprite.getV1());
            RenderHelper.vt(builder, matrix, 1-o, 0, o, sprite.getU0(), sprite.getV1());
            RenderHelper.vt(builder, matrix, o, 0, o, sprite.getU0(), sprite.getV0());

            RenderHelper.vt(builder, matrix, 1-o, 4, 1-o, sprite.getU1(), sprite.getV0());
            RenderHelper.vt(builder, matrix, o, 4, 1-o, sprite.getU1(), sprite.getV1());
            RenderHelper.vt(builder, matrix, o, 0, 1-o, sprite.getU0(), sprite.getV1());
            RenderHelper.vt(builder, matrix, 1-o, 0, 1-o, sprite.getU0(), sprite.getV0());

            RenderHelper.vt(builder, matrix, o, 4, 1-o, sprite.getU1(), sprite.getV0());
            RenderHelper.vt(builder, matrix, o, 4, o, sprite.getU1(), sprite.getV1());
            RenderHelper.vt(builder, matrix, o, 0, o, sprite.getU0(), sprite.getV1());
            RenderHelper.vt(builder, matrix, o, 0, 1-o, sprite.getU0(), sprite.getV0());

            RenderHelper.vt(builder, matrix, 1-o, 4, o, sprite.getU1(), sprite.getV0());
            RenderHelper.vt(builder, matrix, 1-o, 4, 1-o, sprite.getU1(), sprite.getV1());
            RenderHelper.vt(builder, matrix, 1-o, 0, 1-o, sprite.getU0(), sprite.getV1());
            RenderHelper.vt(builder, matrix, 1-o, 0, o, sprite.getU0(), sprite.getV0());
        }

    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(TeleporterModule.TYPE_MATTER_TRANSMITTER.get(), BeamRenderer::new);
    }
}
