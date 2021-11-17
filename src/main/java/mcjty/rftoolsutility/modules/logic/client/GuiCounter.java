package mcjty.rftoolsutility.modules.logic.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.TextField;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.blocks.CounterTileEntity;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class GuiCounter extends GenericGuiContainer<CounterTileEntity, GenericContainer> {

    public GuiCounter(CounterTileEntity te, GenericContainer container, PlayerInventory inventory) {
        super(te, container, inventory, LogicBlockModule.COUNTER.get().getManualEntry());
    }

    public static void register() {
        register(LogicBlockModule.CONTAINER_COUNTER.get(), GuiCounter::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsUtilityMessages.INSTANCE, new ResourceLocation(RFToolsUtility.MODID, "gui/counter.gui"));
        super.init();

        initializeFields();
    }

    private void initializeFields() {
    }

    private void updateFields() {
        TextField counterField = window.findChild("counter");
        int delay = tileEntity.getCounter();
        if (delay <= 0) {
            delay = 1;
        }
        counterField.text(String.valueOf(delay));

        TextField currentField = window.findChild("current");
        int current = tileEntity.getCurrent();
        if (current < 0) {
            current = 0;
        }
        currentField.text(String.valueOf(current));
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        updateFields();
        drawWindow(matrixStack);
    }
}
