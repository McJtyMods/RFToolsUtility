package mcjty.rftoolsutility.modules.screen.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

/** Submit the screen module GUI primitives on the block's plane in world space. */
public class WorldScreenGraphics extends GuiGraphicsExtractor {

    private final PoseStack worldPose;
    private final SubmitNodeCollector collector;
    private final int light;
    private float depth;

    public WorldScreenGraphics(PoseStack worldPose, SubmitNodeCollector collector, int light) {
        super(Minecraft.getInstance(), new GuiRenderState(), -1, -1);
        this.worldPose = worldPose;
        this.collector = collector;
        this.light = light;
    }

    private PoseStack capturePose() {
        PoseStack captured = new PoseStack();
        captured.last().set(worldPose.last());
        var transform = pose();
        captured.mulPose(new Matrix4f().m00(transform.m00()).m01(transform.m01())
                .m10(transform.m10()).m11(transform.m11()).m30(transform.m20()).m31(transform.m21()));
        // Preserve GUI extraction order for overlapping text, bars, and item overlays.
        captured.translate(0, 0, depth);
        depth += .001f;
        return captured;
    }

    @Override
    public void fill(int x0, int y0, int x1, int y1, int color) {
        PoseStack pose = capturePose();
        collector.submitCustomGeometry(pose, CustomRenderTypes.QUADS_NOTEXTURE, (captured, vertices) -> {
            var matrix = captured.pose();
            vertices.addVertex(matrix, x0, y0, 0).setColor(color).setLight(light);
            vertices.addVertex(matrix, x0, y1, 0).setColor(color).setLight(light);
            vertices.addVertex(matrix, x1, y1, 0).setColor(color).setLight(light);
            vertices.addVertex(matrix, x1, y0, 0).setColor(color).setLight(light);
        });
    }

    @Override
    public void fill(RenderPipeline pipeline, int x0, int y0, int x1, int y1, int color) {
        fill(x0, y0, x1, y1, color);
    }

    @Override
    public void text(Font font, FormattedCharSequence text, int x, int y, int color, boolean shadow) {
        collector.submitText(capturePose(), x, y, text, shadow, Font.DisplayMode.NORMAL, light, color, 0, 0);
    }

    @Override
    public void item(ItemStack stack, int x, int y) {
        if (!stack.isEmpty()) {
            PoseStack pose = capturePose();
            pose.translate(x + 8, y + 8, 0);
            pose.scale(16, -16, 16);
            RenderHelper.renderItemGui(pose, collector, stack, light, 0);
        }
    }
}
