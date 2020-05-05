package mcjty.rftoolsutility.modules.screen.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.Panel;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenContainer;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import mcjty.rftoolsutility.modules.screen.items.ScreenTabletContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerInventory;

import static mcjty.lib.gui.widgets.Widgets.positional;

public class GuiTabletScreen extends GenericGuiContainer<ScreenTileEntity, ScreenContainer> {

    public static final int WIDTH = 200;
    public static final int HEIGHT = 190;

    public GuiTabletScreen(ScreenTileEntity te, ScreenContainer container, PlayerInventory inventory) {
        super(RFToolsUtility.instance, te, container, inventory, /* @todo 1.14 */ ManualEntry.EMPTY);
        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    public void init() {
        super.init();

        Panel toplevel = positional();
        toplevel.bounds(guiLeft, guiTop, xSize, ySize);
        window = new Window(this, toplevel);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();

        ScreenRenderer.renderInternal(tileEntity, new MatrixStack(), buffer, 0, 0);
    }
}

