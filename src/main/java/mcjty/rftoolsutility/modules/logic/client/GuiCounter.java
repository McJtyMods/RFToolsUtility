package mcjty.rftoolsutility.modules.logic.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.blocks.CounterTileEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class GuiCounter extends GenericGuiContainer<CounterTileEntity, GenericContainer> {

    public GuiCounter(GenericContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, LogicBlockModule.COUNTER.block().get().getManualEntry());
    }

    public static void register(RegisterMenuScreensEvent event) {
        event.register(LogicBlockModule.CONTAINER_COUNTER.get(), GuiCounter::new);
    }

    @Override
    public void init() {
        window = new Window(this, getBE(), ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "gui/counter.gui"));
        super.init();
    }
}
