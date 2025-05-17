package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.data.RedstoneChannelData;
import mcjty.rftoolsutility.modules.logic.items.RedstoneInformationItem;
import mcjty.rftoolsutility.modules.logic.tools.RedstoneChannels;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.items.modules.ButtonModuleItem;
import mcjty.rftoolsutility.modules.screen.modules.ButtonScreenModule;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class RedstoneChannelBlock extends LogicSlabBlock {

    public RedstoneChannelBlock(BlockBuilder builder) {
        super(builder);
    }

    protected static String getChannelString(ItemStack stack) {
        RedstoneChannelData data = stack.get(LogicBlockModule.ITEM_REDSTONECHANNEL_DATA);
        if (data == null) {
            return "<unset>";
        } else {
            return Integer.toString(data.channel());
        }
    }

    private boolean isRedstoneChannelItem(Item item) {
        return (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof RedstoneChannelBlock) || item instanceof ButtonModuleItem
                || item instanceof RedstoneInformationItem;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState blockState, BlockEntityType<T> blockEntityType) {
        if (!world.isClientSide()) {
            return (pLevel, pPos, pState, pBlockEntity) -> {
                if (pBlockEntity instanceof RedstoneReceiverTileEntity tile) {
                    tile.tickServer();
                }
            };
        } else {
            return null;
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
//        ItemStack stack = player.getItemInHand(player.getUsedItemHand());
        if (isRedstoneChannelItem(stack.getItem())) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof RedstoneChannelTileEntity) {
                if (!world.isClientSide) {
                    RedstoneChannelTileEntity rcte = (RedstoneChannelTileEntity) te;
                    int channel;

                    if (stack.getItem() instanceof RedstoneInformationItem) {
                        // Add the channel (if any) to this module
                        channel = rcte.getChannel(false);
                        if (channel != -1) {
                            if (RedstoneInformationItem.addChannel(stack, channel)) {
                                Logging.message(player, ChatFormatting.YELLOW + "Added channel " + channel + "!");
                            } else {
                                Logging.message(player, ChatFormatting.RED + "Channel " + channel + " was already added!");
                            }
                        } else {
                            Logging.message(player, ChatFormatting.RED + "Block has no channel yet!");
                        }
                    } else if (stack.getItem() instanceof ButtonModuleItem) {
                        if (!player.isCrouching()) {
                            channel = rcte.getChannel(true);
                            stack.set(LogicBlockModule.ITEM_REDSTONECHANNEL_DATA, new RedstoneChannelData(channel));
                        } else {
                            // @todo 1.15: currently not working because onBlockActivated is not called when crouching
                            channel = ButtonModuleItem.getChannel(stack);
                            if (channel == -1) {
                                RedstoneChannels redstoneChannels = RedstoneChannels.getChannels(world);
                                channel = redstoneChannels.newChannel();
                                redstoneChannels.save();
                                ButtonScreenModule data = stack.getOrDefault(ScreenModule.MODULE_BUTTON_DATA, ButtonScreenModule.DEFAULT).withChannel(channel);
                                stack.set(ScreenModule.MODULE_BUTTON_DATA, data);
                            }
                            rcte.setChannel(channel);
                        }
                        Logging.message(player, ChatFormatting.YELLOW + "Channel set to " + channel + "!");
                    } else {
                        if (!player.isCrouching()) {
                            channel = rcte.getChannel(true);
                            stack.set(LogicBlockModule.ITEM_REDSTONECHANNEL_DATA, new RedstoneChannelData(channel));
                        } else {
                            // @todo 1.15: currently not working because onBlockActivated is not called when crouching
                            channel = stack.getOrDefault(LogicBlockModule.ITEM_REDSTONECHANNEL_DATA, new RedstoneChannelData(-1)).channel();
                            if (channel == -1) {
                                RedstoneChannels redstoneChannels = RedstoneChannels.getChannels(world);
                                channel = redstoneChannels.newChannel();
                                redstoneChannels.save();
                                stack.set(LogicBlockModule.ITEM_REDSTONECHANNEL_DATA, new RedstoneChannelData(channel));
                            }
                            rcte.setChannel(channel);
                        }
                        Logging.message(player, ChatFormatting.YELLOW + "Channel set to " + channel + "!");
                    }
                }
                return ItemInteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, world, pos, player, hand, result);
    }
}
