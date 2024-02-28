package mcjty.rftoolsutility.modules.screen.network;

import mcjty.lib.network.CustomPacketPayload;
import mcjty.lib.network.PlayPayloadContext;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ForgeMod;

public record PacketModuleUpdate(BlockPos pos, Integer slotIndex, CompoundTag tagCompound) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(RFToolsUtility.MODID, "moduleupdate");

    public static PacketModuleUpdate create(BlockPos blockPos, int slotIndex, CompoundTag finalTagCompound) {
        return new PacketModuleUpdate(blockPos, slotIndex, finalTagCompound);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(slotIndex);
        FriendlyByteBuf buffer = new FriendlyByteBuf(buf);
        buffer.writeNbt(tagCompound);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static PacketModuleUpdate create(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int slotIndex = buf.readInt();
        FriendlyByteBuf buffer = new FriendlyByteBuf(buf);
        CompoundTag tagCompound = buffer.readNbt();
        return new PacketModuleUpdate(pos, slotIndex, tagCompound);
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ctx.player().ifPresent(player -> {
                Level world = player.getCommandSenderWorld();
                if (world.hasChunkAt(pos)) {
                    // adapted from NetHandlerPlayServer.processTryUseItemOnBlock
                    double dist = player.getAttribute(ForgeMod.BLOCK_REACH.get()).getValue() + 3;
                    if (player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) >= dist * dist) {
                        return;
                    }
                    if (world.getBlockEntity(pos) instanceof ScreenTileEntity screen) {
                        screen.updateModuleData(slotIndex, tagCompound);
                    }
                }
            });
        });
    }
}
