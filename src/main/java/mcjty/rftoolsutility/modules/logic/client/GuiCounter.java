package mcjty.rftoolsutility.modules.logic.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.TextField;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.blocks.CounterTileEntity;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

public class GuiCounter extends GenericGuiContainer<CounterTileEntity, GenericContainer> {

    private TextField currentField;

    public GuiCounter(CounterTileEntity te, GenericContainer container, PlayerInventory inventory) {
        super(RFToolsUtility.instance, te, container, inventory, 0, /*@todo 1.15 */"counter");
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsUtilityMessages.INSTANCE, new ResourceLocation(RFToolsUtility.MODID, "gui/counter.gui"));
        super.init();

        requestCurrentCounter();

        initializeFields();
    }

    private void initializeFields() {
        TextField counterField = window.findChild("counter");
        int delay = tileEntity.getCounter();
        if (delay <= 0) {
            delay = 1;
        }
        counterField.setText(String.valueOf(delay));

        currentField = window.findChild("current");
        int current = tileEntity.getCurrent();
        if (current < 0) {
            current = 0;
        }
        currentField.setText(String.valueOf(current));
    }

    private static long lastTime = 0;

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
//        if (System.currentTimeMillis() - lastTime > 500) {
//            requestCurrentCounter();
//        }

        // @todo 1.15 communication through container?
//        currentField.setText(String.valueOf(CounterTileEntity.cntReceived));

        drawWindow();
    }

    private void requestCurrentCounter() {
//        lastTime = System.currentTimeMillis();
        // @todo 1.15 communication through container?
//        RFToolsMessages.sendToServer(CommandHandler.CMD_GET_COUNTER_INFO,
//                TypedMap.builder().put(CommandHandler.PARAM_DIMENSION, tileEntity.getWorld().provider.getDimension()).put(CommandHandler.PARAM_POS, tileEntity.getPos()));
    }
}
