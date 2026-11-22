package mcjty.rftoolsutility.modules.teleporter.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.client.RenderSettings;
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

    private static final float BEAM_MIN = .15f;
    private static final float BEAM_MAX = .85f;
    private static final int BEAM_HEIGHT = 4;

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

            drawBeam(matrixStack, builder, sprite);
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
        BlockEntityRenderers.register(TeleporterModule.TYPE_MATTER_TRANSMITTER.get(), BeamRenderer::new);
    }
}
