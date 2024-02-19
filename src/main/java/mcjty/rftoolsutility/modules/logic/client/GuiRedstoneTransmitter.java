package mcjty.rftoolsutility.modules.logic.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.blocks.RedstoneTransmitterTileEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GuiRedstoneTransmitter extends GenericGuiContainer<RedstoneTransmitterTileEntity, GenericContainer> {

    public GuiRedstoneTransmitter(RedstoneTransmitterTileEntity te, GenericContainer container, Inventory inventory) {
        super(te, container, inventory, LogicBlockModule.REDSTONE_TRANSMITTER.get().getManualEntry());
    }

    public static void register() {
        register(LogicBlockModule.CONTAINER_REDSTONE_TRANSMITTER.get(), GuiRedstoneTransmitter::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, new ResourceLocation(RFToolsUtility.MODID, "gui/redstone_transmitter.gui"));
        super.init();

        window.bind("name", tileEntity, RedstoneTransmitterTileEntity.VALUE_NAME.key().name());
    }
}
