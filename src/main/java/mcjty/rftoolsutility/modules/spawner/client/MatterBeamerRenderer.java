package mcjty.rftoolsutility.modules.spawner.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mcjty.lib.client.RenderGlowEffect;
import mcjty.lib.client.RenderHelper;
import mcjty.rftoolsbase.RFToolsBase;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.spawner.blocks.MatterBeamerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class MatterBeamerRenderer implements BlockEntityRenderer<MatterBeamerTileEntity> {

    public static final ResourceLocation REDGLOW = new ResourceLocation(RFToolsUtility.MODID, "effects/redglow");
    public static final ResourceLocation BLUEGLOW = new ResourceLocation(RFToolsUtility.MODID, "effects/blueglow");
    public static final Vec3 START = new Vec3(.5, .5, .5);

    public MatterBeamerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(MatterBeamerTileEntity tileEntity, float v, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource buffer, int i, int i1) {
        ResourceLocation txt;

        // @todo 1.15
        BlockPos destination = tileEntity.getDestination();
        if (destination != null) {
            if (tileEntity.isGlowing()) {
                TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(REDGLOW);

                VertexConsumer builder = buffer.getBuffer(RenderType.translucent());

                int tex = tileEntity.getBlockPos().getX();
                int tey = tileEntity.getBlockPos().getY();
                int tez = tileEntity.getBlockPos().getZ();
                Vec3 player = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().add(-tex, -tey, -tez);
                Vec3 end = new Vec3(destination.getX() - tex + .5f, destination.getY() - tey + .5f, destination.getZ() - tez + .5f);

                RenderHelper.drawBeam(matrixStack, builder, sprite, START, end, player, tileEntity.isGlowing() ? .1f : .05f);
            }
        }

        BlockPos coord = tileEntity.getBlockPos();
        if (coord.equals(RFToolsBase.instance.clientInfo.getSelectedTE())) {
            txt = REDGLOW;
        } else if (coord.equals(RFToolsBase.instance.clientInfo.getDestinationTE())) {
            txt = BLUEGLOW;
        } else {
            txt = null;
        }

        if (txt != null) {
            RenderGlowEffect.renderGlow(matrixStack, buffer, txt);
        }
    }

    public static void register() {
        BlockEntityRenderers.register(SpawnerModule.TYPE_MATTER_BEAMER.get(), MatterBeamerRenderer::new);
    }
}

