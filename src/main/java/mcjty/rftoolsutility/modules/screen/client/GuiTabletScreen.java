package mcjty.rftoolsutility.modules.screen.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenContainer;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

import static mcjty.lib.gui.widgets.Widgets.positional;

public class GuiTabletScreen extends GenericGuiContainer<ScreenTileEntity, ScreenContainer> {

    public static final int WIDTH = 200;
    public static final int HEIGHT = 190;

    public GuiTabletScreen(ScreenTileEntity te, ScreenContainer container, PlayerInventory inventory) {
        super(te, container, inventory, /* @todo 1.14 */ ManualEntry.EMPTY);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    public static void register() {
        ScreenManager.IScreenFactory<ScreenContainer, GuiTabletScreen> factory = (container, inventory, title) -> {
            TileEntity te = container.getTe();
            return Tools.safeMap(te, (ScreenTileEntity tile) -> new GuiTabletScreen(tile, container, inventory), "Invalid tile entity!");
        };
        ScreenManager.register(ScreenModule.CONTAINER_SCREEN_REMOTE.get(), factory);
    }

    @Override
    public void init() {
        super.init();

        Panel toplevel = positional();
        toplevel.bounds(leftPos, topPos, imageWidth, imageHeight);
        window = new Window(this, toplevel);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        //            AbstractGui.fill(100, 30, 250, 180, 0xff333333);
        x -= 102;
        y -= 32;

        ScreenTileEntity.ModuleRaytraceResult result = tileEntity.getHitModule(x / 100.0, y / 100.0, 0, Direction.NORTH, Direction.NORTH, 1);
        if (result != null) {
            tileEntity.hitScreenClient(result);
        }
        return false;
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
//        super.render(mouseX, mouseY, partialTicks);
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        ScreenRenderer.renderInternal(tileEntity, matrixStack, buffer, RenderHelper.MAX_BRIGHTNESS, OverlayTexture.NO_OVERLAY);

        buffer.endBatch();
    }
}

