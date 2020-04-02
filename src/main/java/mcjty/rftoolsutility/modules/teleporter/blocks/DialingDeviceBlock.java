package mcjty.rftoolsutility.modules.teleporter.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;

import static mcjty.lib.builder.TooltipBuilder.*;

public class DialingDeviceBlock extends BaseBlock {

    public DialingDeviceBlock() {
        super(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header(), gold())
                .tileEntitySupplier(DialingDeviceTileEntity::new));
    }
}
