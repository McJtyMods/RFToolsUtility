package mcjty.rftoolsutility.modules.teleporter.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.NBTTools;
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

import javax.annotation.Nonnull;

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
        return NBTTools.getInfoNBT(stack, CompoundNBT::getString, "tpName", "<unset>");
    }

    private static boolean hasOnce(ItemStack stack) {
        return NBTTools.getInfoNBT(stack, CompoundNBT::getBoolean, "once", false);
    }

    private static String getDialInfo(ItemStack stack) {
        if (stack.getTag() == null) {
            return "<undialed>";
        }
        CompoundNBT info = stack.getTag().getCompound("BlockEntityTag").getCompound("Info");
        if (info.isEmpty()) {
            return "<undialed>";
        }
        boolean dialed = false;
        BlockPos c = BlockPosTools.read(info, "dest");
        if (c != null && c.getY() >= 0) {
            dialed = true;
        } else if (info.contains("destId")) {
            if (info.getInt("destId") != -1) {
                dialed = true;
            }
        }

        if (dialed) {
            int destId = info.getInt("destId");
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
    public void setPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState state, LivingEntity placer, @Nonnull ItemStack stack) {
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
