package mcjty.rftoolsutility.modules.logic.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.blocks.RedstoneTransmitterTileEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class GuiRedstoneTransmitter extends GenericGuiContainer<RedstoneTransmitterTileEntity, GenericContainer> {

    public GuiRedstoneTransmitter(GenericContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, LogicBlockModule.REDSTONE_TRANSMITTER.block().get().getManualEntry());
    }

    public static void register(RegisterMenuScreensEvent event) {
        event.register(LogicBlockModule.CONTAINER_REDSTONE_TRANSMITTER.get(), GuiRedstoneTransmitter::new);
    }

    @Override
    public void init() {
        window = new Window(this, getBE(), ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "gui/redstone_transmitter.gui"));
        super.init();

        window.bind("name", getBE(), RedstoneTransmitterTileEntity.VALUE_NAME.key().name());
    }
}
