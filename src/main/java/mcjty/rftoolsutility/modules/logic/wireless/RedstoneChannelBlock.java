package mcjty.rftoolsutility.modules.logic.wireless;

import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsutility.modules.screen.items.ButtonModuleItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class RedstoneChannelBlock extends LogicSlabBlock {

    public RedstoneChannelBlock(BlockBuilder builder) {
        super(builder);
    }

//    @SideOnly(Side.CLIENT)
//    @Override
//    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag whatIsThis) {
//        super.addInformation(itemStack, player, list, whatIsThis);
//        CompoundNBT tagCompound = itemStack.getTag();
//        if (tagCompound != null) {
//            int channel = tagCompound.getInt("channel");
//            list.add(TextFormatting.GREEN + "Channel: " + channel);
//        }
//    }

    protected static final String getChannelString(ItemStack stack) {
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound != null) {
            int channel = tagCompound.getInt("channel");
            return Integer.toString(channel);
        }
        return "<unset>";
    }

    private boolean isRedstoneChannelItem(Item item) {
        return (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof RedstoneChannelBlock) || item instanceof ButtonModuleItem;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        ItemStack stack = player.getHeldItem(hand);
        if (isRedstoneChannelItem(stack.getItem())) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof RedstoneChannelTileEntity) {
                if (!world.isRemote) {
                    RedstoneChannelTileEntity rcte = (RedstoneChannelTileEntity) te;
                    CompoundNBT tagCompound = stack.getOrCreateTag();
                    int channel;
                    if (!player.isCrouching()) {
                        channel = rcte.getChannel(true);
                        tagCompound.putInt("channel", channel);
                    } else {
                        if (tagCompound.contains("channel")) {
                            channel = tagCompound.getInt("channel");
                        } else {
                            channel = -1;
                        }
                        if (channel == -1) {
                            RedstoneChannels redstoneChannels = RedstoneChannels.getChannels(world);
                            channel = redstoneChannels.newChannel();
                            redstoneChannels.save();
                            tagCompound.putInt("channel", channel);
                        }
                        rcte.setChannel(channel);
                    }
                    Logging.message(player, TextFormatting.YELLOW + "Channel set to " + channel + "!");
                }
                return ActionResultType.SUCCESS;
            }
        }
        return super.onBlockActivated(state, world, pos, player, hand, result);
    }
}
