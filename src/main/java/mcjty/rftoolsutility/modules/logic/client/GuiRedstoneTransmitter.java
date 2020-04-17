package mcjty.rftoolsutility.modules.logic.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.blocks.RedstoneTransmitterTileEntity;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

public class GuiRedstoneTransmitter extends GenericGuiContainer<RedstoneTransmitterTileEntity, GenericContainer> {

    public GuiRedstoneTransmitter(RedstoneTransmitterTileEntity te, GenericContainer container, PlayerInventory inventory) {
        super(RFToolsUtility.instance, te, container, inventory, 0, /*@todo 1.15 */"redtrans");
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsUtilityMessages.INSTANCE, new ResourceLocation(RFToolsUtility.MODID, "gui/redstone_transmitter.gui"));
        super.init();

        window.bind(RFToolsUtilityMessages.INSTANCE, "name", tileEntity, RedstoneTransmitterTileEntity.VALUE_NAME.getName());
    }
}
