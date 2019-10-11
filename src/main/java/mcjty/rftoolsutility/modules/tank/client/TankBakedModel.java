package mcjty.rftoolsutility.modules.tank.client;

import mcjty.rftoolsbase.RFToolsBase;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.tank.blocks.TankTE;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class TankBakedModel implements IDynamicBakedModel {

    private VertexFormat format;

    private static TextureAtlasSprite levelMask;

    private static TextureAtlasSprite getTopTexture() {
        String name = RFToolsBase.MODID + ":block/base/machinetop";
        return Minecraft.getInstance().getTextureMap().getAtlasSprite(name);
    }

    private static TextureAtlasSprite getBottomTexture() {
        String name = RFToolsBase.MODID + ":block/base/machinebottom";
        return Minecraft.getInstance().getTextureMap().getAtlasSprite(name);
    }

    private static TextureAtlasSprite getSideTexture(int level) {
        String name = RFToolsUtility.MODID + ":block/tank" + (level < 0 ? 0 : level);
        return Minecraft.getInstance().getTextureMap().getAtlasSprite(name);
    }

    public TankBakedModel(VertexFormat format) {
        this.format = format;
    }

    private void putVertex(UnpackedBakedQuad.Builder builder, Vec3d normal,
                           double x, double y, double z, float u, float v, TextureAtlasSprite sprite, float color) {
        for (int e = 0; e < format.getElementCount(); e++) {
            switch (format.getElement(e).getUsage()) {
                case POSITION:
                    builder.put(e, (float) x, (float) y, (float) z, 1.0f);
                    break;
                case COLOR:
                    builder.put(e, color, color, color, 1.0f);
                    break;
                case UV:
                    if (format.getElement(e).getIndex() == 0) {
                        u = sprite.getInterpolatedU(u);
                        v = sprite.getInterpolatedV(v);
                        builder.put(e, u, v, 0f, 1f);
                        break;
                    }
                case NORMAL:
                    builder.put(e, (float) normal.x, (float) normal.y, (float) normal.z, 0f);
                    break;
                default:
                    builder.put(e);
                    break;
            }
        }
    }

    private BakedQuad createQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, TextureAtlasSprite sprite, float hilight) {
        Vec3d normal = v3.subtract(v2).crossProduct(v1.subtract(v2)).normalize();

        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
        builder.setTexture(sprite);
        putVertex(builder, normal, v1.x, v1.y, v1.z, 0, 0, sprite, hilight);
        putVertex(builder, normal, v2.x, v2.y, v2.z, 0, 16, sprite, hilight);
        putVertex(builder, normal, v3.x, v3.y, v3.z, 16, 16, sprite, hilight);
        putVertex(builder, normal, v4.x, v4.y, v4.z, 16, 0, sprite, hilight);
        return builder.build();
    }

    private static Vec3d v(double x, double y, double z) {
        return new Vec3d(x, y, z);
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
                TextureAtlasSprite fluidTexture = Minecraft.getInstance().getTextureMap().getAtlasSprite(stillTexture.toString());

                float o = .01f;
                double left = .2;
                double right = 1 - .4;
                double top = .2;
                double bottom = 1 - .4;
                quads.add(createQuad(v(1 + o, right, bottom), v(1 + o, left, bottom), v(1 + o, left, top), v(1 + o, right, top), fluidTexture, hilight));
                quads.add(createQuad(v(-o, right, bottom), v(-o, left, bottom), v(-o, left, top), v(-o, right, top), fluidTexture, hilight));
                quads.add(createQuad(v(right, bottom, -o), v(left, bottom, -o), v(left, top, -o), v(right, top, -o), fluidTexture, hilight));
                quads.add(createQuad(v(right, bottom, 1 + o), v(left, bottom, 1 + o), v(left, top, 1 + o), v(right, top, 1 + o), fluidTexture, hilight));
            }
        }


        return quads;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return getSideTexture(0);
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }


}
