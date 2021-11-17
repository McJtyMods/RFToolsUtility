package mcjty.rftoolsutility.modules.tank.client;

import mcjty.lib.client.AbstractDynamicBakedModel;
import mcjty.rftoolsbase.RFToolsBase;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.tank.blocks.TankTE;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class TankBakedModel extends AbstractDynamicBakedModel {

    public static final ResourceLocation TEXTURE_TOP = new ResourceLocation(RFToolsBase.MODID, "block/base/machinetop");
    public static final ResourceLocation TEXTURE_BOTTOM = new ResourceLocation(RFToolsBase.MODID, "block/base/machinebottom");


    private static TextureAtlasSprite levelMask;

    private static TextureAtlasSprite getTopTexture() {
        return getTexture(TEXTURE_TOP);
    }

    private static TextureAtlasSprite getBottomTexture() {
        return getTexture(TEXTURE_BOTTOM);
    }

    private static TextureAtlasSprite getSideTexture(Integer level) {
        String name = "block/tank" + ((level == null || level < 0) ? 0 : level);
        return getTexture(new ResourceLocation(RFToolsUtility.MODID, name));
    }

    @Override
    @Nonnull
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        if (side != null) {
            return Collections.emptyList();
        }

        Integer level = data.getData(TankTE.AMOUNT);
        Fluid fluid = data.getData(TankTE.FLUID);

        List<BakedQuad> quads = new ArrayList<>();

        float hilight = 1.0f;

        quads.add(createQuad(v(0, 1, 0), v(0, 1, 1), v(1, 1, 1), v(1, 1, 0), getTopTexture(), hilight));
        quads.add(createQuad(v(0, 0, 0), v(1, 0, 0), v(1, 0, 1), v(0, 0, 1), getBottomTexture(), hilight));
        quads.add(createQuad(v(1, 1, 1), v(1, 0, 1), v(1, 0, 0), v(1, 1, 0), getSideTexture(level), hilight));
        quads.add(createQuad(v(0, 1, 0), v(0, 0, 0), v(0, 0, 1), v(0, 1, 1), getSideTexture(level), hilight));
        quads.add(createQuad(v(1, 1, 0), v(1, 0, 0), v(0, 0, 0), v(0, 1, 0), getSideTexture(level), hilight));
        quads.add(createQuad(v(0, 1, 1), v(0, 0, 1), v(1, 0, 1), v(1, 1, 1), getSideTexture(level), hilight));

        if (fluid != null) {
            ResourceLocation stillTexture = fluid.getAttributes().getStillTexture();
            if (stillTexture != null) {
                TextureAtlasSprite fluidTexture = getTexture(stillTexture);
                int color = fluid.getAttributes().getColor();
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
    public TextureAtlasSprite getParticleIcon() {
        return getSideTexture(0);
    }
}
