package mcjty.rftoolsutility.modules.screen.network;

import mcjty.lib.varia.Logging;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenBlock;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketModuleUpdate {
    private BlockPos pos;

    private int slotIndex;
    private CompoundNBT tagCompound;

    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(slotIndex);
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeCompoundTag(tagCompound);
    }

    public PacketModuleUpdate() {
    }

    public PacketModuleUpdate(PacketBuffer buf) {
        pos = buf.readBlockPos();
        slotIndex = buf.readInt();
        PacketBuffer buffer = new PacketBuffer(buf);
        tagCompound = buffer.readCompoundTag();
    }

    public PacketModuleUpdate(BlockPos pos, int slotIndex, CompoundNBT tagCompound) {
        this.pos = pos;
        this.slotIndex = slotIndex;
        this.tagCompound = tagCompound;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayerEntity player = ctx.getSender();
            World world = player.getEntityWorld();
            if (world.isBlockLoaded(pos)) {
                Block block = world.getBlockState(pos).getBlock();
                // adapted from NetHandlerPlayServer.processTryUseItemOnBlock
                double dist = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue() + 3;
                if (player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) >= dist * dist) {
                    return;
                }
                if (!(block instanceof ScreenBlock)) {
                    Logging.logError("PacketModuleUpdate: Block is not a ScreenBlock!");
                    return;
                }
                TileEntity te = world.getTileEntity(pos);
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
