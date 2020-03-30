package mcjty.rftoolsutility.modules.teleporter.blocks;

import mcjty.lib.McJtyLib;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.BlockPosTools;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.teleporter.client.BeamRenderer;
import mcjty.rftoolsutility.setup.CommandHandler;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class MatterTransmitterBlock extends BaseBlock {

    public static Integer clientSideId = null;
    public static String clientSideName = "?";

    public MatterTransmitterBlock() {
        super(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .tileEntitySupplier(MatterTransmitterTileEntity::new));
    }

    public static void setDestinationInfo(Integer id, String name) {
        MatterTransmitterBlock.clientSideId = id;
        MatterTransmitterBlock.clientSideName = name;
    }

    public void initModel() {
        BeamRenderer.register();
    }

    @Override
    public void addInformation(ItemStack itemStack, @Nullable IBlockReader world, List<ITextComponent> list, ITooltipFlag advanced) {
        super.addInformation(itemStack, world, list, advanced);
        CompoundNBT tagCompound = itemStack.getTag();
        if (tagCompound != null) {
            String name = tagCompound.getString("tpName");
            list.add(new StringTextComponent(TextFormatting.GREEN + "Name: " + name));

            boolean dialed = false;
            BlockPos c = BlockPosTools.read(tagCompound, "dest");
            if (c != null && c.getY() >= 0) {
                dialed = true;
            } else if (tagCompound.contains("destId")) {
                if (tagCompound.getInt("destId") != -1) {
                    dialed = true;
                }
            }

            if (dialed) {
                int destId = tagCompound.getInt("destId");
                if (System.currentTimeMillis() - lastTime > 500) {
                    lastTime = System.currentTimeMillis();
                    RFToolsUtilityMessages.sendToServer(CommandHandler.CMD_GET_DESTINATION_INFO, TypedMap.builder().put(CommandHandler.PARAM_ID, destId));
                }

                String destname = "?";
                if (clientSideId != null && clientSideId == destId) {
                    destname = clientSideName;
                }
                list.add(new StringTextComponent(TextFormatting.YELLOW + "[DIALED to " + destname + "]"));
            }

            boolean once = tagCompound.getBoolean("once");
            if (once) {
                list.add(new StringTextComponent(TextFormatting.YELLOW + "[ONCE]"));
            }
        }
        if (McJtyLib.proxy.isShiftKeyDown()) {
            list.add(new StringTextComponent(TextFormatting.WHITE + "If you place this block near a Dialing Device then"));
            list.add(new StringTextComponent(TextFormatting.WHITE + "you can dial it to a Matter Receiver. Make sure to give"));
            list.add(new StringTextComponent(TextFormatting.WHITE + "it sufficient power!"));
            list.add(new StringTextComponent(TextFormatting.WHITE + "Use a Destination Analyzer adjacent to this block"));
            list.add(new StringTextComponent(TextFormatting.WHITE + "to check destination status (red is bad, green ok,"));
            list.add(new StringTextComponent(TextFormatting.WHITE + "yellow is unknown)."));
            list.add(new StringTextComponent(TextFormatting.WHITE + "Use a  Matter Booster adjacent to this block"));
            list.add(new StringTextComponent(TextFormatting.WHITE + "to be able to teleport to unpowered receivers."));
            list.add(new StringTextComponent(TextFormatting.YELLOW + "Infusing bonus: reduced power consumption and"));
            list.add(new StringTextComponent(TextFormatting.YELLOW + "increased teleportation speed."));
        } else {
            list.add(new StringTextComponent(TextFormatting.WHITE + RFToolsUtility.SHIFT_MESSAGE));
        }
    }

    private static long lastTime = 0;

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
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
