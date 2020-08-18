package mcjty.rftoolsutility.modules.tank.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.ChoiceLabel;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.WidgetList;
import mcjty.rftoolsbase.RFToolsBase;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.tank.blocks.TankTE;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class GuiTank extends GenericGuiContainer<TankTE, GenericContainer> {
    private EnergyBar energyBar;
    private WidgetList recipeList;
    private ChoiceLabel keepItem;
    private ChoiceLabel internalRecipe;
    private Button applyButton;

    private static final ResourceLocation iconGuiElements = new ResourceLocation(RFToolsBase.MODID, "textures/gui/guielements.png");

    private static int lastSelected = -1;

    public GuiTank(TankTE te, GenericContainer container, PlayerInventory inventory) {
        super(RFToolsUtility.instance, te, container, inventory, ManualHelper.create("rftoolsutility:machines/tank"));
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsUtilityMessages.INSTANCE, new ResourceLocation(RFToolsUtility.MODID, "gui/tank.gui"));
        super.init();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int x, int y) {
        if (window == null) {
            return;
        }

        drawWindow(xxx);

        tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(e -> {
//            energyBar.setMaxValue(((GenericEnergyStorage)e).getCapacity());
//            energyBar.setValue(((GenericEnergyStorage)e).getEnergy());
        });

    }
}
