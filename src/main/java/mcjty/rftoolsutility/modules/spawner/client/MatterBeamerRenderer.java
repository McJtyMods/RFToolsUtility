package mcjty.rftoolsutility.modules.spawner.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import mcjty.lib.client.RenderHelper;
import mcjty.rftoolsbase.RFToolsBase;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.spawner.SpawnerSetup;
import mcjty.rftoolsutility.modules.spawner.blocks.MatterBeamerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.opengl.GL11;

public class MatterBeamerRenderer extends TileEntityRenderer<MatterBeamerTileEntity> {

    private static final ResourceLocation redglow = new ResourceLocation(RFToolsUtility.MODID, "textures/blocks/redglow.png");
    private static final ResourceLocation blueglow = new ResourceLocation(RFToolsUtility.MODID, "textures/blocks/blueglow.png");

    public MatterBeamerRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(MatterBeamerTileEntity tileEntity, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {
        ResourceLocation txt;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        // @todo 1.15
        BlockPos destination = tileEntity.getDestination();
        if (destination != null) {
            if (tileEntity.isPowered()) {
                GlStateManager.pushMatrix();

                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LIGHTMAP_COLOR);
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);

                Minecraft mc = Minecraft.getInstance();
                ClientPlayerEntity p = mc.player;
                double doubleX = p.lastTickPosX + (p.getPosX() - p.lastTickPosX) * mc.getRenderPartialTicks();
                double doubleY = p.lastTickPosY + (p.getPosY() - p.lastTickPosY) * mc.getRenderPartialTicks();
                double doubleZ = p.lastTickPosZ + (p.getPosZ() - p.lastTickPosZ) * mc.getRenderPartialTicks();

                RenderHelper.Vector start = new RenderHelper.Vector(tileEntity.getPos().getX() + .5f, tileEntity.getPos().getY() + .5f, tileEntity.getPos().getZ() + .5f);
                RenderHelper.Vector end = new RenderHelper.Vector(destination.getX() + .5f, destination.getY() + .5f, destination.getZ() + .5f);
                RenderHelper.Vector player = new RenderHelper.Vector((float) doubleX, (float) doubleY + p.getEyeHeight(), (float) doubleZ);
                GlStateManager.translated(-doubleX, -doubleY, -doubleZ);

                // @todo 1.15
//                this.bindTexture(redglow);

                RenderHelper.drawBeam(start, end, player, tileEntity.isGlowing() ? .1f : .05f);

                tessellator.draw();
                GlStateManager.popMatrix();
            }
        }

        BlockPos coord = tileEntity.getPos();
        if (coord.equals(RFToolsBase.instance.clientInfo.getSelectedTE())) {
            txt = redglow;
        } else if (coord.equals(RFToolsBase.instance.clientInfo.getDestinationTE())) {
            txt = blueglow;
        } else {
            txt = null;
        }

        if (txt != null) {
            // @todo 1.15
//            this.bindTexture(txt);
//            RenderGlowEffect.renderGlow(tessellator, x, y, z);
        }
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(SpawnerSetup.TYPE_MATTER_BEAMER.get(), MatterBeamerRenderer::new);
    }
}

