package mcjty.rftoolsutility.modules.logic.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.blocks.RedstoneTransmitterTileEntity;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;

public class GuiRedstoneTransmitter extends GenericGuiContainer<RedstoneTransmitterTileEntity, GenericContainer> {

    public GuiRedstoneTransmitter(RedstoneTransmitterTileEntity te, GenericContainer container, Inventory inventory) {
        super(te, container, inventory, LogicBlockModule.REDSTONE_TRANSMITTER.get().getManualEntry());
    }

    public static void register() {
        register(LogicBlockModule.CONTAINER_REDSTONE_TRANSMITTER.get(), GuiRedstoneTransmitter::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsUtilityMessages.INSTANCE, new ResourceLocation(RFToolsUtility.MODID, "gui/redstone_transmitter.gui"));
        super.init();

        window.bind(RFToolsUtilityMessages.INSTANCE, "name", tileEntity, RedstoneTransmitterTileEntity.VALUE_NAME.key().name());
    }
}
