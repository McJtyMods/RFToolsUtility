package mcjty.rftoolsutility.modules.teleporter.blocks;

import mcjty.lib.varia.Logging;
import mcjty.lib.varia.NBTTools;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestination;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class SimpleDialerItemBlock extends BlockItem {
    public SimpleDialerItemBlock(Block block) {
        super(block, new Properties().tab(RFToolsUtility.setup.getTab()));
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        ItemStack stack = context.getItemInHand();
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        PlayerEntity player = context.getPlayer();
        TileEntity te = world.getBlockEntity(pos);

        if (!world.isClientSide) {

            if (te instanceof MatterTransmitterTileEntity) {
                MatterTransmitterTileEntity matterTransmitterTileEntity = (MatterTransmitterTileEntity) te;

                if (!matterTransmitterTileEntity.checkAccess(player.getDisplayName().getString())) {    // @todo 1.16 getFormattedText, also is this right?
                    Logging.message(player, TextFormatting.RED + "You have no access to this matter transmitter!");
                    return ActionResultType.FAIL;
                }

                BlockPos mpos = matterTransmitterTileEntity.getBlockPos();
                NBTTools.setInfoNBT(stack, CompoundNBT::putInt, "transX", mpos.getX());
                NBTTools.setInfoNBT(stack, CompoundNBT::putInt, "transY", mpos.getY());
                NBTTools.setInfoNBT(stack, CompoundNBT::putInt, "transZ", mpos.getZ());
                NBTTools.setInfoNBT(stack, CompoundNBT::putString, "transDim", world.dimension().location().toString());

                if (matterTransmitterTileEntity.isDialed()) {
                    Integer id = matterTransmitterTileEntity.getTeleportId();
                    boolean access = checkReceiverAccess(player, world, id);
                    if (!access) {
                        Logging.message(player, TextFormatting.RED + "You have no access to the matter receiver!");
                        return ActionResultType.FAIL;
                    }

                    NBTTools.setInfoNBT(stack, CompoundNBT::putInt, "receiver", id);
                    Logging.message(player, TextFormatting.YELLOW + "Receiver set!");
                }

                Logging.message(player, TextFormatting.YELLOW + "Transmitter set!");
            } else if (te instanceof MatterReceiverTileEntity) {
                MatterReceiverTileEntity matterReceiverTileEntity = (MatterReceiverTileEntity) te;

                Integer id = matterReceiverTileEntity.getOrCalculateID();
                boolean access = checkReceiverAccess(player, world, id);
                if (!access) {
                    Logging.message(player, TextFormatting.RED + "You have no access to this matter receiver!");
                    return ActionResultType.FAIL;
                }

                NBTTools.setInfoNBT(stack, CompoundNBT::putInt, "receiver", id);
                Logging.message(player, TextFormatting.YELLOW + "Receiver set!");
            } else {
                return super.useOn(context);
            }

            return ActionResultType.SUCCESS;
        } else {
            return super.useOn(context);
        }
    }

    private boolean checkReceiverAccess(PlayerEntity player, World world, Integer id) {
        boolean access = true;
        TeleportDestinations destinations = TeleportDestinations.get(world);
        GlobalPos coordinate = destinations.getCoordinateForId(id);
        if (coordinate != null) {
            TeleportDestination destination = destinations.getDestination(coordinate);
            if (destination != null) {
                World worldForDimension = LevelTools.getLevel(destination.getDimension());
                if (worldForDimension != null) {
                    TileEntity recTe = worldForDimension.getBlockEntity(destination.getCoordinate());
                    if (recTe instanceof MatterReceiverTileEntity) {
                        MatterReceiverTileEntity matterReceiverTileEntity = (MatterReceiverTileEntity) recTe;
                        if (!matterReceiverTileEntity.checkAccess(player.getUUID())) {
                            access = false;
                        }
                    }
                }
            }
        }
        return access;
    }
}
