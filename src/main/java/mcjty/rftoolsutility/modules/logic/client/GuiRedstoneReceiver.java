package mcjty.rftoolsutility.modules.logic.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.blocks.RedstoneReceiverTileEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GuiRedstoneReceiver extends GenericGuiContainer<RedstoneReceiverTileEntity, GenericContainer> {

    public GuiRedstoneReceiver(RedstoneReceiverTileEntity te, GenericContainer container, Inventory inventory) {
        super(te, container, inventory, LogicBlockModule.REDSTONE_RECEIVER.get().getManualEntry());
    }

    public static void register() {
        register(LogicBlockModule.CONTAINER_REDSTONE_RECEIVER.get(), GuiRedstoneReceiver::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, new ResourceLocation(RFToolsUtility.MODID, "gui/redstone_receiver.gui"));
        super.init();
    }
}
