package mcjty.rftoolsutility.setup;

import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.setup.DefaultModSetup;
import mcjty.rftoolsutility.blocks.crafter.CrafterSetup;
import mcjty.rftoolsutility.network.RFToolsUtilityMessages;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModSetup extends DefaultModSetup {

    public ModSetup() {
        createTab("rftoolsutility", () -> new ItemStack(CrafterSetup.BLOCK_CRAFTER1));
    }

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);

        RFToolsUtilityMessages.registerMessages("rftoolsutility");
    }

    @Override
    protected void setupModCompat() {
        MainCompatHandler.registerWaila();
        MainCompatHandler.registerTOP();
//        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "mcjty.rftools.compat.theoneprobe.TheOneProbeSupport");
    }
}
