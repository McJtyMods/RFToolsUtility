package mcjty.rftoolsutility.modules.logic.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.blocks.TimerTileEntity;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class GuiTimer extends GenericGuiContainer<TimerTileEntity, GenericContainer> {

    public GuiTimer(GenericContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, LogicBlockModule.TIMER.block().get().getManualEntry());
    }

    public static void register() {
        MenuScreens.register(LogicBlockModule.CONTAINER_TIMER.get(), GuiTimer::new);
    }

    @Override
    public void init() {
        window = new Window(this, getBE(), Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "gui/timer.gui"));
        super.init();
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphicsExtractor graphics, float partialTicks, int x, int y) {
        super.renderBg(graphics, partialTicks, x, y);
    }
}
