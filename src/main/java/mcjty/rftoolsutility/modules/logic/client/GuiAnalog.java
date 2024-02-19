package mcjty.rftoolsutility.modules.logic.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.blocks.AnalogTileEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GuiAnalog extends GenericGuiContainer<AnalogTileEntity, GenericContainer> {

    public GuiAnalog(AnalogTileEntity te, GenericContainer container, Inventory inventory) {
        super(te, container, inventory, LogicBlockModule.ANALOG.get().getManualEntry());
    }

    public static void register() {
        register(LogicBlockModule.CONTAINER_ANALOG.get(), GuiAnalog::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, new ResourceLocation(RFToolsUtility.MODID, "gui/analog.gui"));
        super.init();
    }
}
