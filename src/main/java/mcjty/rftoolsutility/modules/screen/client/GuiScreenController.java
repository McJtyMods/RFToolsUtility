package mcjty.rftoolsutility.modules.screen.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenControllerTileEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

import static mcjty.lib.gui.widgets.Widgets.*;

public class GuiScreenController extends GenericGuiContainer<ScreenControllerTileEntity, GenericContainer> {
    public static final int CONTROLLER_WIDTH = 180;
    public static final int CONTROLLER_HEIGHT = 152;

    private EnergyBar energyBar;

    private static final ResourceLocation BACKGROUND = new ResourceLocation(RFToolsUtility.MODID, "textures/gui/screencontroller.png");

    public GuiScreenController(ScreenControllerTileEntity screenControllerTileEntity, GenericContainer container, Inventory inventory) {
        super(screenControllerTileEntity, container, inventory, ScreenModule.SCREEN_CONTROLLER.get().getManualEntry());

        imageWidth = CONTROLLER_WIDTH;
        imageHeight = CONTROLLER_HEIGHT;
    }

    public static void register() {
        register(ScreenModule.CONTAINER_SCREEN_CONTROLLER.get(), GuiScreenController::new);
    }

    @Override
    public void init() {
        super.init();

        energyBar = new EnergyBar().vertical().hint(10, 7, 8, 54).showText(false);

        Button scanButton = button(30, 7, 50, 14, "Scan")
                .name("scan")
                .tooltips("Find all nearby screens", "and connect to them");
        Button detachButton = button(90, 7, 50, 14, "Detach")
                .name("detach")
                .tooltips("Detach from all screens");
        Label infoLabel = label(30, 25, 140, 14, "");

        Panel toplevel = positional().background(BACKGROUND)
                .children(energyBar, scanButton, detachButton, infoLabel);
        toplevel.bounds(leftPos, topPos, imageWidth, imageHeight);

        window = new Window(this, toplevel);

        window.action("scan", tileEntity, ScreenControllerTileEntity.CMD_SCAN);
        window.action("detach", tileEntity, ScreenControllerTileEntity.CMD_DETACH);
    }


    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float v, int i, int i2) {
        drawWindow(graphics, xxx, xxx, yyy);
        updateEnergyBar(energyBar);
    }
}
