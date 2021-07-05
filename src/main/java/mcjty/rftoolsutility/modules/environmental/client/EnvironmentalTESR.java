package mcjty.rftoolsutility.modules.environmental.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.client.RenderSettings;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.environmental.blocks.EnvironmentalControllerTileEntity;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.Random;

public class EnvironmentalTESR extends TileEntityRenderer<EnvironmentalControllerTileEntity> {

    private ResourceLocation halo = new ResourceLocation(RFToolsUtility.MODID, "textures/entities/floatingsphere.png");
    private Random random = new Random();

    public EnvironmentalTESR(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(EnvironmentalControllerTileEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        if (te.isActive()) {
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

            matrixStack.pushPose();
            matrixStack.translate(0.5f, 0.5f, 0.5f);
            float s = 0.3f + random.nextFloat() * .05f;
            matrixStack.scale(s, s, s);
            RenderHelper.renderBillboardQuadBright(matrixStack, buffer, 0.5f, halo, RenderSettings.builder()
                    .color(255, 255, 255)
                    .renderType(CustomRenderTypes.TRANSLUCENT_LIGHTNING_NOLIGHTMAPS)
                    .alpha(128)
                    .build());
            matrixStack.popPose();
        }
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(EnvironmentalModule.TYPE_ENVIRONENTAL_CONTROLLER.get(), EnvironmentalTESR::new);
    }
}
