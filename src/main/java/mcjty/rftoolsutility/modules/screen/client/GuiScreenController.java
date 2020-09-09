package mcjty.rftoolsutility.modules.screen.client;

import com.mojang.blaze3d.matrix.MatrixStack;
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
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

import static mcjty.lib.gui.widgets.Widgets.*;

public class GuiScreenController extends GenericGuiContainer<ScreenControllerTileEntity, GenericContainer> {
    public static final int CONTROLLER_WIDTH = 180;
    public static final int CONTROLLER_HEIGHT = 152;

    private EnergyBar energyBar;

    private static final ResourceLocation BACKGROUND = new ResourceLocation(RFToolsUtility.MODID, "textures/gui/screencontroller.png");

    public GuiScreenController(ScreenControllerTileEntity screenControllerTileEntity, GenericContainer container, PlayerInventory inventory) {
        super(screenControllerTileEntity, container, inventory, ScreenModule.SCREEN_CONTROLLER.get().getManualEntry());

        xSize = CONTROLLER_WIDTH;
        ySize = CONTROLLER_HEIGHT;
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
        toplevel.bounds(guiLeft, guiTop, xSize, ySize);

        window = new Window(this, toplevel);

        window.action(RFToolsUtilityMessages.INSTANCE, "scan", tileEntity, ScreenControllerTileEntity.ACTION_SCAN);
        window.action(RFToolsUtilityMessages.INSTANCE, "detach", tileEntity, ScreenControllerTileEntity.ACTION_DETACH);
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float v, int i, int i2) {
        drawWindow(matrixStack);
        updateEnergyBar(energyBar);
    }
}
