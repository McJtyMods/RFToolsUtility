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
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class MatterBeamerRenderer extends TileEntityRenderer<MatterBeamerTileEntity> {

    public static final ResourceLocation REDGLOW = new ResourceLocation(RFToolsUtility.MODID, "effects/redglow");
    public static final ResourceLocation BLUEGLOW = new ResourceLocation(RFToolsUtility.MODID, "effects/blueglow");

    public MatterBeamerRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(MatterBeamerTileEntity tileEntity, float v, MatrixStack matrixStack, IRenderTypeBuffer buffer, int i, int i1) {
        ResourceLocation txt;

        // @todo 1.15
        BlockPos destination = tileEntity.getDestination();
        if (destination != null) {
            if (tileEntity.isGlowing()) {
                TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(REDGLOW);

                IVertexBuilder builder = buffer.getBuffer(RenderType.getTranslucent());

                int tex = tileEntity.getPos().getX();
                int tey = tileEntity.getPos().getY();
                int tez = tileEntity.getPos().getZ();
                Vector3d projectedView = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView().add(-tex, -tey, -tez);

                RenderHelper.Vector start = new RenderHelper.Vector(.5f, .5f, .5f);
                RenderHelper.Vector end = new RenderHelper.Vector(destination.getX() - tex + .5f, destination.getY() - tey + .5f, destination.getZ() - tez + .5f);
                RenderHelper.Vector player = new RenderHelper.Vector((float)projectedView.x, (float)projectedView.y, (float)projectedView.z);

                Matrix4f matrix = matrixStack.getLast().getMatrix();
                RenderHelper.drawBeam(matrix, builder, sprite, start, end, player, tileEntity.isGlowing() ? .1f : .05f);
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

