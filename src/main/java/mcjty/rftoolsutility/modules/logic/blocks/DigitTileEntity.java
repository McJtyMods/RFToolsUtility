package mcjty.rftoolsutility.modules.logic.blocks;


import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.tileentity.LogicTileEntity;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class DigitTileEntity extends LogicTileEntity {

    public DigitTileEntity() {
        super(LogicBlockModule.TYPE_DIGIT.get());
    }

    public static LogicSlabBlock createBlock() {
        return new LogicSlabBlock(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(DigitTileEntity::new));
    }

    @Override
    public void writeClientDataToNBT(CompoundNBT tagCompound) {
        CompoundNBT infoTag = getOrCreateInfo(tagCompound);
        infoTag.putByte("powered", (byte) powerLevel);
    }

    @Override
    public void readClientDataFromNBT(CompoundNBT tagCompound) {
        CompoundNBT infoTag = tagCompound.getCompound("Info");
        if (infoTag.contains("powered")) {
            powerLevel = infoTag.getByte("powered");
        }
    }

    @Override
    public void setPowerInput(int powered) {
        if (powerLevel != powered) {
            powerLevel = powered;
            markDirtyClient();
        }
    }
}
