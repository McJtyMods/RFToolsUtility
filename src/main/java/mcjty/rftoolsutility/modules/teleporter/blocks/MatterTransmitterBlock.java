package mcjty.rftoolsutility.modules.teleporter.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import mcjty.rftoolsutility.modules.teleporter.data.MatterTransmitterData;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestination;
import mcjty.rftoolsutility.setup.CommandHandler;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

import static mcjty.lib.builder.TooltipBuilder.*;

public class MatterTransmitterBlock extends BaseBlock {

    public static Integer clientSideId = null;
    public static String clientSideName = "?";

    public MatterTransmitterBlock() {
        super(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsbase:machines/matter_transmitter"))
                .info(key("message.rftoolsutility.shiftmessage"))
                .infusable()
                .infoShift(header(), gold(),
                        parameter("info", MatterTransmitterBlock::getName),
                        parameter("once", MatterTransmitterBlock::hasOnce, stack -> hasOnce(stack) ? "[ONCE]" : ""),
                        parameter("dialed", MatterTransmitterBlock::getDialInfoClient))
                .tileEntitySupplier(MatterTransmitterTileEntity::new));
    }

    public static void setDestinationInfo(Integer id, String name) {
        MatterTransmitterBlock.clientSideId = id;
        MatterTransmitterBlock.clientSideName = name;
    }

    private static String getName(ItemStack stack) {
        MatterTransmitterData data = stack.get(TeleporterModule.ITEM_MATTERTRANSMITTER_DATA);
        if (data != null) {
            return data.name();
        }
        return "<unset>";
    }

    private static boolean hasOnce(ItemStack stack) {
        MatterTransmitterData data = stack.get(TeleporterModule.ITEM_MATTERTRANSMITTER_DATA);
        if (data != null) {
            return data.once();
        }
        return false;
    }

    private static String getDialInfoClient(ItemStack stack) {
        MatterTransmitterData data = stack.get(TeleporterModule.ITEM_MATTERTRANSMITTER_DATA);
        if (data != null) {
            TeleportDestination destination = data.destination();
            boolean dialed = (destination != null && destination.isValid()) || data.destinationId() != null;
            if (dialed) {
                Integer destId = data.destinationId();
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
            return destination.getName();
        }
        return "<undialed>";
    }

    private static long lastTime = 0;

    @Override
    public void setPlacedBy(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, LivingEntity placer, @Nonnull ItemStack stack) {
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
