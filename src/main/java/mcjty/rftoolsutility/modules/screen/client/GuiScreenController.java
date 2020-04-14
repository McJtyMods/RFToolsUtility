package mcjty.rftoolsutility.modules.screen.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenControllerTileEntity;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.energy.CapabilityEnergy;

import static mcjty.lib.gui.widgets.Widgets.*;

public class GuiScreenController extends GenericGuiContainer<ScreenControllerTileEntity, GenericContainer> {
    public static final int CONTROLLER_WIDTH = 180;
    public static final int CONTROLLER_HEIGHT = 152;

    private EnergyBar energyBar;
    private Label infoLabel;

    private static final ResourceLocation iconLocation = new ResourceLocation(RFToolsUtility.MODID, "textures/gui/screencontroller.png");

    public GuiScreenController(ScreenControllerTileEntity screenControllerTileEntity, GenericContainer container, PlayerInventory inventory) {
        super(RFToolsUtility.instance, screenControllerTileEntity, container, inventory, 0 /* @todo 1.14 GuiProxy.GUI_MANUAL_MAIN*/, "screens");

        xSize = CONTROLLER_WIDTH;
        ySize = CONTROLLER_HEIGHT;
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
        infoLabel = label(30, 25, 140, 14, "");

        Panel toplevel = positional().background(iconLocation)
                .children(energyBar, scanButton, detachButton, infoLabel);
        toplevel.bounds(guiLeft, guiTop, xSize, ySize);

        window = new Window(this, toplevel);

        window.action(RFToolsUtilityMessages.INSTANCE, "scan", tileEntity, ScreenControllerTileEntity.ACTION_SCAN);
        window.action(RFToolsUtilityMessages.INSTANCE, "detach", tileEntity, ScreenControllerTileEntity.ACTION_DETACH);
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        drawWindow();

        tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> {
            energyBar.maxValue(((GenericEnergyStorage)e).getCapacity());
            energyBar.value(((GenericEnergyStorage)e).getEnergy());
        });
    }
}
