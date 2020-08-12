package mcjty.rftoolsutility.modules.spawner.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcjty.lib.client.RenderGlowEffect;
import mcjty.lib.client.RenderHelper;
import mcjty.rftoolsbase.RFToolsBase;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.spawner.SpawnerSetup;
import mcjty.rftoolsutility.modules.spawner.blocks.MatterBeamerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class MatterBeamerRenderer extends TileEntityRenderer<MatterBeamerTileEntity> {

    public static final ResourceLocation REDGLOW = new ResourceLocation(RFToolsUtility.MODID, "textures/blocks/redglow.png");
    public static final ResourceLocation BLUEGLOW = new ResourceLocation(RFToolsUtility.MODID, "textures/blocks/blueglow.png");

    public MatterBeamerRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(MatterBeamerTileEntity tileEntity, float v, MatrixStack matrixStack, IRenderTypeBuffer buffer, int i, int i1) {
        ResourceLocation txt;

        // @todo 1.15
        BlockPos destination = tileEntity.getDestination();
        if (destination != null) {
            if (tileEntity.isPowered()) {
                TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(REDGLOW);

                IVertexBuilder builder = buffer.getBuffer(RenderType.getTranslucent());
                matrixStack.push();
                RenderHelper.rotateToPlayer(matrixStack);
                Matrix4f matrix = matrixStack.getLast().getMatrix();

                RenderHelper.Vector start = new RenderHelper.Vector(tileEntity.getPos().getX() + .5f, tileEntity.getPos().getY() + .5f, tileEntity.getPos().getZ() + .5f);
                RenderHelper.Vector end = new RenderHelper.Vector(destination.getX() + .5f, destination.getY() + .5f, destination.getZ() + .5f);
                RenderHelper.Vector player = new RenderHelper.Vector(0, 0, 0);
                RenderHelper.drawBeam(matrix, builder, sprite, start, end, player, tileEntity.isGlowing() ? .1f : .05f);

                matrixStack.pop();
            }
        }

        BlockPos coord = tileEntity.getPos();
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
        ClientRegistry.bindTileEntityRenderer(SpawnerSetup.TYPE_MATTER_BEAMER.get(), MatterBeamerRenderer::new);
    }
}

