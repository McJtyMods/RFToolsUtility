package mcjty.rftoolsutility.modules.tank.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.ChoiceLabel;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.WidgetList;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.rftoolsbase.RFToolsBase;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.tank.TankModule;
import mcjty.rftoolsutility.modules.tank.blocks.TankTE;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class GuiTank extends GenericGuiContainer<TankTE, GenericContainer> {
    private EnergyBar energyBar;
    private WidgetList recipeList;
    private ChoiceLabel keepItem;
    private ChoiceLabel internalRecipe;
    private Button applyButton;

    private static final ResourceLocation iconGuiElements = ResourceLocation.fromNamespaceAndPath(RFToolsBase.MODID, "textures/gui/guielements.png");

    public GuiTank(GenericContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, TankModule.TANK.block().get().getManualEntry());
    }

    public static void register(RegisterMenuScreensEvent event) {
        event.register(TankModule.CONTAINER_TANK.get(), GuiTank::new);
    }

    @Override
    public void init() {
        window = new Window(this, getBE(), ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "gui/tank.gui"));
        super.init();
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        if (window == null) {
            return;
        }

        drawWindow(graphics, partialTicks, mouseX, mouseY);
        GenericTileEntity be = getBE();
        IFluidHandler fluidHandler = be.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, be.getBlockPos(), null);
        if (fluidHandler != null) {
//            energyBar.maxValue(fluidHandler.getTankCapacity(0));
//            energyBar.value(fluidHandler.getFluidInTank(0).getAmount());
        }
    }
}
