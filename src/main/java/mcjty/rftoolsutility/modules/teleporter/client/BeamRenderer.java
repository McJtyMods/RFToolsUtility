package mcjty.rftoolsutility.modules.teleporter.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mcjty.lib.client.RenderHelper;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.teleporter.TeleportationTools;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import mcjty.rftoolsutility.modules.teleporter.blocks.MatterTransmitterTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class BeamRenderer implements BlockEntityRenderer<MatterTransmitterTileEntity> {

    public static final ResourceLocation BEAM_OK = new ResourceLocation(RFToolsUtility.MODID, "block/machineteleporter");
    public static final ResourceLocation BEAM_WARN = new ResourceLocation(RFToolsUtility.MODID, "block/machineteleporterwarn");
    public static final ResourceLocation BEAM_UNKNOWN = new ResourceLocation(RFToolsUtility.MODID, "block/machineteleporterunknown");

    public BeamRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(MatterTransmitterTileEntity tileEntity, float v, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource buffer, int i, int ix) {
        if (tileEntity.isDialed() && !tileEntity.isBeamHidden()) {

            ResourceLocation beamIcon = switch (tileEntity.getStatus()) {
                case TeleportationTools.STATUS_OK -> BEAM_OK;
                case TeleportationTools.STATUS_WARN -> BEAM_WARN;
                default -> BEAM_UNKNOWN;
            };

            //noinspection deprecation
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(beamIcon);

            VertexConsumer builder = buffer.getBuffer(RenderType.translucent());

            float o = .15f;
            RenderHelper.drawBox(matrixStack, builder, sprite, false, false, true, true, true, true,
                    1-o, o, 0, 4, 1-o, o, RenderHelper.FULLBRIGHT_SETTINGS);
        }

    }

    public static void register() {
        BlockEntityRenderers.register(TeleporterModule.TYPE_MATTER_TRANSMITTER.get(), BeamRenderer::new);
    }
}
