package mcjty.rftoolsutility.modules.teleporter.blocks;

import mcjty.lib.McJtyLib;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.rftoolsutility.RFToolsUtility;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;

import java.util.List;

public class DialingDeviceBlock extends BaseBlock {

    public DialingDeviceBlock() {
        super("dialing_device", new BlockBuilder()
            .tileEntitySupplier(DialingDeviceTileEntity::new));
    }

//    @Override
//    public BiFunction<DialingDeviceTileEntity, EmptyContainer, GenericGuiContainer<? super DialingDeviceTileEntity>> getGuiFactory() {
//        return GuiDialingDevice::new;
//    }

    @Override
    public void addInformation(ItemStack itemStack, IBlockReader world, List<ITextComponent> list, ITooltipFlag flag) {
        super.addInformation(itemStack, world, list, flag);

        if (McJtyLib.proxy.isShiftKeyDown()) {
            list.add(new StringTextComponent(TextFormatting.WHITE + "With the dialing device you can 'dial-up' any"));
            list.add(new StringTextComponent(TextFormatting.WHITE + "nearby matter transmitter to any matter receiver"));
            list.add(new StringTextComponent(TextFormatting.WHITE + "in the Minecraft universe. This requires power."));
            list.add(new StringTextComponent(TextFormatting.WHITE + "If a Destination Analyzer is adjacent to this block"));
            list.add(new StringTextComponent(TextFormatting.WHITE + "you will also be able to check if the destination"));
            list.add(new StringTextComponent(TextFormatting.WHITE + "has enough power to be safe."));
            list.add(new StringTextComponent(TextFormatting.YELLOW + "Infusing bonus: reduced power consumption."));
        } else {
            list.add(new StringTextComponent(TextFormatting.WHITE + RFToolsUtility.SHIFT_MESSAGE));
        }
    }
}
