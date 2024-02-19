package mcjty.rftoolsutility.modules.logic.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.blocks.TimerTileEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class GuiTimer extends GenericGuiContainer<TimerTileEntity, GenericContainer> {

    public GuiTimer(TimerTileEntity te, GenericContainer container, Inventory inventory) {
        super(te, container, inventory, LogicBlockModule.TIMER.get().getManualEntry());
    }

    public static void register() {
        register(LogicBlockModule.CONTAINER_TIMER.get(), GuiTimer::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, new ResourceLocation(RFToolsUtility.MODID, "gui/timer.gui"));
        super.init();
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float partialTicks, int x, int y) {
        super.renderBg(graphics, partialTicks, x, y);
    }
}
