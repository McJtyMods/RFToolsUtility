package mcjty.rftoolsutility.modules.screen.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;


public class ModelScreen extends Model {

    private ModelRenderer renderer = new ModelRenderer(this, 0, 0);


    public ModelScreen(int size) {
        super(RenderType::entityCutoutNoCull);  // @todo 1.15 check
        if (size == ScreenTileEntity.SIZE_HUGE) {
            this.renderer.addBox(-8.0F, -8.0F, -1.0F, 48, 48, 2, 0.0F);
        } else if (size == ScreenTileEntity.SIZE_LARGE) {
            this.renderer.addBox(-8.0F, -8.0F, -1.0F, 32, 32, 2, 0.0F);
        } else {
            this.renderer.addBox(-8.0F, -8.0F, -1.0F, 16, 16, 2, 0.0F);
        }
        this.renderer.setTextureSize(16, 16);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int i, int i1, float v, float v1, float v2, float v3) {
        this.renderer.render(matrixStack, buffer, i, i1);
    }

}
