package mcjty.rftoolsutility.modules.teleporter.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.BlockPosTools;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.setup.CommandHandler;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static mcjty.lib.builder.TooltipBuilder.*;

public class MatterTransmitterBlock extends BaseBlock {

    public static Integer clientSideId = null;
    public static String clientSideName = "?";

    public MatterTransmitterBlock() {
        super(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsutility:machines/matter_transmitter"))
                .info(key("message.rftoolsutility.shiftmessage"))
                .infusable()
                .infoShift(header(), gold(),
                        parameter("info", MatterTransmitterBlock::getName),
                        parameter("once", MatterTransmitterBlock::hasOnce, stack -> hasOnce(stack) ? "[ONCE]" : ""),
                        parameter("dialed", MatterTransmitterBlock::getDialInfo))
                .tileEntitySupplier(MatterTransmitterTileEntity::new));
    }

    public static void setDestinationInfo(Integer id, String name) {
        MatterTransmitterBlock.clientSideId = id;
        MatterTransmitterBlock.clientSideName = name;
    }

    private static String getName(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null) {
            return tag.getString("tpName");
        } else {
            return "<unset>";
        }
    }

    private static boolean hasOnce(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null) {
            return tag.getBoolean("once");
        } else {
            return false;
        }
    }

    private static String getDialInfo(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) {
            return "<undialed>";
        }
        boolean dialed = false;
        BlockPos c = BlockPosTools.read(tag, "dest");
        if (c != null && c.getY() >= 0) {
            dialed = true;
        } else if (tag.contains("destId")) {
            if (tag.getInt("destId") != -1) {
                dialed = true;
            }
        }

        if (dialed) {
            int destId = tag.getInt("destId");
            if (System.currentTimeMillis() - lastTime > 500) {
                lastTime = System.currentTimeMillis();
                RFToolsUtilityMessages.sendToServer(CommandHandler.CMD_GET_DESTINATION_INFO, TypedMap.builder().put(CommandHandler.PARAM_ID, destId));
            }

            String destname = "?";
            if (clientSideId != null && clientSideId == destId) {
                destname = clientSideName;
            }
            return destname;
        }
        return "<undialed>";
    }

    private static long lastTime = 0;

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        // We don't want what BaseBlock does.
        // @todo 1.14
//        restoreBlockFromNBT(world, pos, stack);
        setOwner(world, pos, placer);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

}
