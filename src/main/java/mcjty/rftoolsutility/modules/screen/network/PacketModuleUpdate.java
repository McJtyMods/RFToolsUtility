package mcjty.rftoolsutility.modules.screen.network;

import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
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
                // adapted from NetHandlerPlayServer.processTryUseItemOnBlock
                double dist = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue() + 3;
                if (player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) >= dist * dist) {
                    return;
                }
                if (world.getBlockEntity(pos) instanceof ScreenTileEntity screen) {
                    screen.updateModuleData(slotIndex, tagCompound);
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
