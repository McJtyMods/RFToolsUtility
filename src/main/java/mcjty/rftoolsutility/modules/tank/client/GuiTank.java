package mcjty.rftoolsutility.modules.tank.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.ChoiceLabel;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.WidgetList;
import mcjty.rftoolsbase.RFToolsBase;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.tank.TankModule;
import mcjty.rftoolsutility.modules.tank.blocks.TankTE;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import javax.annotation.Nonnull;

public class GuiTank extends GenericGuiContainer<TankTE, GenericContainer> {
    private EnergyBar energyBar;
    private WidgetList recipeList;
    private ChoiceLabel keepItem;
    private ChoiceLabel internalRecipe;
    private Button applyButton;

    private static final ResourceLocation iconGuiElements = new ResourceLocation(RFToolsBase.MODID, "textures/gui/guielements.png");

    public GuiTank(TankTE te, GenericContainer container, Inventory inventory) {
        super(te, container, inventory, TankModule.TANK.get().getManualEntry());
    }

    public static void register() {
        register(TankModule.CONTAINER_TANK.get(), GuiTank::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsUtilityMessages.INSTANCE, new ResourceLocation(RFToolsUtility.MODID, "gui/tank.gui"));
        super.init();
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float v, int x, int y) {
        if (window == null) {
            return;
        }

        drawWindow(graphics);

        tileEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(e -> {
//            energyBar.setMaxValue(((GenericEnergyStorage)e).getCapacity());
//            energyBar.setValue(((GenericEnergyStorage)e).getEnergy());
        });

    }
}
