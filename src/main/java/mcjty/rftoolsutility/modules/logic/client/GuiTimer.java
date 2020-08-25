package mcjty.rftoolsutility.modules.logic.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.gui.widgets.ToggleButton;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.blocks.TimerTileEntity;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

public class GuiTimer extends GenericGuiContainer<TimerTileEntity, GenericContainer> {

    public GuiTimer(TimerTileEntity te, GenericContainer container, PlayerInventory inventory) {
        super(te, container, inventory, ManualHelper.create("rftoolsutility:logic/timer"));
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsUtilityMessages.INSTANCE, new ResourceLocation(RFToolsUtility.MODID, "gui/timer.gui"));
        super.init();

        initializeFields();
    }

    private void initializeFields() {
        int delay = tileEntity.getDelay();
        if (delay <= 0) {
            delay = 1;
        }
        TextField delayField = window.findChild("delay");
        delayField.text(String.valueOf(delay));

        ToggleButton redstonePauses = window.findChild("pauses");
        redstonePauses.pressed(tileEntity.getRedstonePauses());
    }
}
