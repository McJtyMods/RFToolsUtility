package mcjty.rftoolsutility.modules.teleporter.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.rftoolsutility.setup.Registration.standardBlockProperties;

public class DialingDeviceBlock extends BaseBlock {

    public DialingDeviceBlock(BlockBehaviour.Properties properties) {
        super(new BlockBuilder().properties(standardBlockProperties(properties))
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsbase:machines/dialing_device"))
                .infusable()
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header(), gold())
                .tileEntitySupplier(DialingDeviceTileEntity::new));
    }
}
