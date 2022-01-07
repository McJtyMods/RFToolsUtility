package mcjty.rftoolsutility.modules.teleporter.blocks;

import mcjty.lib.varia.Logging;
import mcjty.lib.varia.NBTTools;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestination;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

import net.minecraft.world.item.Item.Properties;

public class SimpleDialerItemBlock extends BlockItem {
    public SimpleDialerItemBlock(Block block) {
        super(block, new Properties().tab(RFToolsUtility.setup.getTab()));
    }

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockEntity te = world.getBlockEntity(pos);

        if (!world.isClientSide) {

            if (te instanceof MatterTransmitterTileEntity transmitter) {

                if (!transmitter.checkAccess(player.getDisplayName().getString())) {    // @todo 1.16 getFormattedText, also is this right?
                    Logging.message(player, ChatFormatting.RED + "You have no access to this matter transmitter!");
                    return InteractionResult.FAIL;
                }

                BlockPos mpos = transmitter.getBlockPos();
                NBTTools.setInfoNBT(stack, CompoundTag::putInt, "transX", mpos.getX());
                NBTTools.setInfoNBT(stack, CompoundTag::putInt, "transY", mpos.getY());
                NBTTools.setInfoNBT(stack, CompoundTag::putInt, "transZ", mpos.getZ());
                NBTTools.setInfoNBT(stack, CompoundTag::putString, "transDim", world.dimension().location().toString());

                if (transmitter.isDialed()) {
                    Integer id = transmitter.getTeleportId();
                    boolean access = checkReceiverAccess(player, world, id);
                    if (!access) {
                        Logging.message(player, ChatFormatting.RED + "You have no access to the matter receiver!");
                        return InteractionResult.FAIL;
                    }

                    NBTTools.setInfoNBT(stack, CompoundTag::putInt, "receiver", id);
                    Logging.message(player, ChatFormatting.YELLOW + "Receiver set!");
                }

                Logging.message(player, ChatFormatting.YELLOW + "Transmitter set!");
            } else if (te instanceof MatterReceiverTileEntity receiver) {
                Integer id = receiver.getOrCalculateID();
                boolean access = checkReceiverAccess(player, world, id);
                if (!access) {
                    Logging.message(player, ChatFormatting.RED + "You have no access to this matter receiver!");
                    return InteractionResult.FAIL;
                }

                NBTTools.setInfoNBT(stack, CompoundTag::putInt, "receiver", id);
                Logging.message(player, ChatFormatting.YELLOW + "Receiver set!");
            } else {
                return super.useOn(context);
            }

            return InteractionResult.SUCCESS;
        } else {
            return super.useOn(context);
        }
    }

    private boolean checkReceiverAccess(Player player, Level world, Integer id) {
        boolean access = true;
        TeleportDestinations destinations = TeleportDestinations.get(world);
        GlobalPos coordinate = destinations.getCoordinateForId(id);
        if (coordinate != null) {
            TeleportDestination destination = destinations.getDestination(coordinate);
            if (destination != null) {
                Level worldForDimension = LevelTools.getLevel(destination.getDimension());
                if (worldForDimension != null) {
                    BlockEntity recTe = worldForDimension.getBlockEntity(destination.getCoordinate());
                    if (recTe instanceof MatterReceiverTileEntity receiver) {
                        destination = receiver.updateDestination();
                        if (!destination.checkAccess(world, player.getUUID())) {
                            access = false;
                        }
                    }
                }
            }
        }
        return access;
    }
}
