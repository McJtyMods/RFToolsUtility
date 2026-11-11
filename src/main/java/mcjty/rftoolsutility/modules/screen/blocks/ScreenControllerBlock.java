package mcjty.rftoolsutility.modules.screen.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.rftoolsutility.setup.Registration.standardBlockProperties;

public class ScreenControllerBlock extends BaseBlock {

    public ScreenControllerBlock(BlockBehaviour.Properties properties) {
        super(new BlockBuilder().properties(standardBlockProperties(properties))
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsbase:machines/screen_controller"))
                .infusable()
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header(), gold())
                .tileEntitySupplier(ScreenControllerTileEntity::new));
    }


}
