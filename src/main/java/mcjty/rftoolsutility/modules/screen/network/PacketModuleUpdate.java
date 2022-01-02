package mcjty.rftoolsutility.modules.screen.network;

import mcjty.lib.varia.Logging;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenBlock;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketModuleUpdate {
    private BlockPos pos;

    private int slotIndex;
    private CompoundTag tagCompound;

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(slotIndex);
        FriendlyByteBuf buffer = new FriendlyByteBuf(buf);
        buffer.writeNbt(tagCompound);
    }

    public PacketModuleUpdate() {
    }

    public PacketModuleUpdate(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
        slotIndex = buf.readInt();
        FriendlyByteBuf buffer = new FriendlyByteBuf(buf);
        tagCompound = buffer.readNbt();
    }

    public PacketModuleUpdate(BlockPos pos, int slotIndex, CompoundTag tagCompound) {
        this.pos = pos;
        this.slotIndex = slotIndex;
        this.tagCompound = tagCompound;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            Level world = player.getCommandSenderWorld();
            if (world.hasChunkAt(pos)) {
                Block block = world.getBlockState(pos).getBlock();
                // adapted from NetHandlerPlayServer.processTryUseItemOnBlock
                double dist = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue() + 3;
                if (player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) >= dist * dist) {
                    return;
                }
                if (!(block instanceof ScreenBlock)) {
                    Logging.logError("PacketModuleUpdate: Block is not a ScreenBlock!");
                    return;
                }
                BlockEntity te = world.getBlockEntity(pos);
                // @todo 1.14
//            if(((ScreenBlock)block).checkAccess(world, player, te)) {
//                return;
//            }
                if (!(te instanceof ScreenTileEntity)) {
                    Logging.logError("PacketModuleUpdate: TileEntity is not a SimpleScreenTileEntity!");
                    return;
                }
                ((ScreenTileEntity) te).updateModuleData(slotIndex, tagCompound);
            }
        });
        ctx.setPacketHandled(true);
    }
}
