package mcjty.rftoolsutility.modules.tank.client;

import mcjty.lib.client.AbstractDynamicBakedModel;
import mcjty.rftoolsbase.RFToolsBase;
import mcjty.rftoolsbase.client.QuadModelPart;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.tank.blocks.TankTE;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class TankBakedModel extends AbstractDynamicBakedModel {

    public static final Identifier TEXTURE_TOP = Identifier.fromNamespaceAndPath(RFToolsBase.MODID, "block/base/machinetop");
    public static final Identifier TEXTURE_BOTTOM = Identifier.fromNamespaceAndPath(RFToolsBase.MODID, "block/base/machinebottom");


    private static TextureAtlasSprite levelMask;

    private static TextureAtlasSprite getTopTexture() {
        return getTexture(TEXTURE_TOP);
    }

    private static TextureAtlasSprite getBottomTexture() {
        return getTexture(TEXTURE_BOTTOM);
    }

    private static TextureAtlasSprite getSideTexture(Integer level) {
        String name = "block/tank" + ((level == null || level < 0) ? 0 : level);
        return getTexture(Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, name));
    }

    @Override
    public void collectParts(BlockAndTintGetter world, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        TankTE tank = world.getBlockEntity(pos) instanceof TankTE found ? found : null;
        parts.add(new QuadModelPart(buildQuads(tank), particleMaterial()));
    }

    private List<BakedQuad> buildQuads(TankTE tank) {
        int level = tank == null ? 0 : tank.getRenderLevel();
        Fluid fluid = tank == null ? null : tank.getRenderFluid();

        List<BakedQuad> quads = new ArrayList<>();

        float hilight = 1.0f;

        quads.add(createQuad(v(0, 1, 0), v(0, 1, 1), v(1, 1, 1), v(1, 1, 0), getTopTexture(), hilight));
        quads.add(createQuad(v(0, 0, 0), v(1, 0, 0), v(1, 0, 1), v(0, 0, 1), getBottomTexture(), hilight));
        quads.add(createQuad(v(1, 1, 1), v(1, 0, 1), v(1, 0, 0), v(1, 1, 0), getSideTexture(level), hilight));
        quads.add(createQuad(v(0, 1, 0), v(0, 0, 0), v(0, 0, 1), v(0, 1, 1), getSideTexture(level), hilight));
        quads.add(createQuad(v(1, 1, 0), v(1, 0, 0), v(0, 0, 0), v(0, 1, 0), getSideTexture(level), hilight));
        quads.add(createQuad(v(0, 1, 1), v(0, 0, 1), v(1, 0, 1), v(1, 1, 1), getSideTexture(level), hilight));

        if (fluid != null) {
            var model = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluid.defaultFluidState());
            {
                TextureAtlasSprite fluidTexture = model.stillMaterial().sprite();
                int color = FluidVariantRendering.getColor(FluidVariant.of(fluid));
                float r;
                float g;
                float b;
                r = ((color >> 16) & 255) / 255.0f;
                g = ((color >> 8) & 255) / 255.0f;
                b = (color & 255) / 255.0f;

                float o = .01f;
                double left = .25;
                double right = 1 - .44;
                double top = .505;
                double bottom = 1 - .19;
                quads.add(createQuad(v(1 + o, bottom, 1-right), v(1 + o, bottom, 1-left), v(1 + o, top, 1-left), v(1 + o, top, 1-right), fluidTexture, r, g, b, 1.0f));
                quads.add(createQuad(v(-o, bottom, right), v(-o, bottom, left), v(-o, top, left), v(-o, top, right), fluidTexture, r, g, b, 1.0f));
                quads.add(createQuad(v(1-right, bottom, -o), v(1-left, bottom, -o), v(1-left, top, -o), v(1-right, top, -o), fluidTexture, r, g, b, 1.0f));
                quads.add(createQuad(v(right, bottom, 1 + o), v(left, bottom, 1 + o), v(left, top, 1 + o), v(right, top, 1 + o), fluidTexture, r, g, b, 1.0f));
            }
        }


        return quads;
    }

    @Nonnull
    @Override
    public Material.Baked particleMaterial() {
        return new Material.Baked(getSideTexture(0), false);
    }
}
