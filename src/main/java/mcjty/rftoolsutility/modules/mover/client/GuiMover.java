package mcjty.rftoolsutility.modules.mover.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.ImageChoiceLabel;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.mover.MoverModule;
import mcjty.rftoolsutility.modules.mover.blocks.MoverTileEntity;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class GuiMover extends GenericGuiContainer<MoverTileEntity, GenericContainer> {

    private EnergyBar energyBar;

    public GuiMover(MoverTileEntity builderTileEntity, GenericContainer container, Inventory inventory) {
        super(builderTileEntity, container, inventory, MoverModule.MOVER.get().getManualEntry());
    }

    public static void register() {
        register(MoverModule.CONTAINER_MOVER.get(), GuiMover::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsUtilityMessages.INSTANCE, new ResourceLocation(RFToolsUtility.MODID, "gui/mover.gui"));
        super.init();

        initializeFields();
        setupEvents();
    }

    private void setupEvents() {
//        window.event("cardgui", (source, params) -> openCardGui());
    }

    private void initializeFields() {
        energyBar = window.findChild("energybar");
        updateFields();
    }

    private void updateFields() {
        ((ImageChoiceLabel) window.findChild("redstone")).setCurrentChoice(tileEntity.getRSMode().ordinal());
        updateEnergyBar(energyBar);
    }

    @Override
    protected void renderBg(@Nonnull PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        updateFields();
        drawWindow(matrixStack);
    }
}
