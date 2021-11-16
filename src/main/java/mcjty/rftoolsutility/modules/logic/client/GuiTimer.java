package mcjty.rftoolsutility.modules.logic.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.gui.widgets.ToggleButton;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.blocks.TimerTileEntity;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

public class GuiTimer extends GenericGuiContainer<TimerTileEntity, GenericContainer> {

    public GuiTimer(TimerTileEntity te, GenericContainer container, PlayerInventory inventory) {
        super(te, container, inventory, LogicBlockModule.TIMER.get().getManualEntry());
    }

    public static void register() {
        register(LogicBlockModule.CONTAINER_TIMER.get(), GuiTimer::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsUtilityMessages.INSTANCE, new ResourceLocation(RFToolsUtility.MODID, "gui/timer.gui"));
        super.init();

        initializeFields();
    }

    private void initializeFields() {
    }

    private void updateFields() {
        int delay = tileEntity.getDelay();
        if (delay <= 0) {
            delay = 1;
        }
        TextField delayField = window.findChild("delay");
        delayField.text(String.valueOf(delay));

        ToggleButton redstonePauses = window.findChild("pauses");
        redstonePauses.pressed(tileEntity.getRedstonePauses());
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        updateFields();
        super.renderBg(matrixStack, partialTicks, x, y);
    }
}
