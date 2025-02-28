package mcjty.rftoolsutility.modules.screen.network;

import com.mojang.serialization.Codec;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

// @todo 1.21
public record PacketModuleUpdate(BlockPos pos, Integer slotIndex, CompoundTag tagCompound) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "moduleupdate");
    public static final CustomPacketPayload.Type<PacketModuleUpdate> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PacketModuleUpdate> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketModuleUpdate::pos,
            ByteBufCodecs.INT, PacketModuleUpdate::slotIndex,
            ByteBufCodecs.COMPOUND_TAG, PacketModuleUpdate::tagCompound,
            PacketModuleUpdate::new);
    )

    public static PacketModuleUpdate create(BlockPos blockPos, int slotIndex, CompoundTag finalTagCompound) {
        return new PacketModuleUpdate(blockPos, slotIndex, finalTagCompound);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            Level world = player.getCommandSenderWorld();
            if (world.hasChunkAt(pos)) {
                // adapted from NetHandlerPlayServer.processTryUseItemOnBlock
                double dist = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue() + 3;
                if (player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) >= dist * dist) {
                    return;
                }
                if (world.getBlockEntity(pos) instanceof ScreenTileEntity screen) {
                    screen.updateModuleData(slotIndex, tagCompound);
                }
            }
        });
    }
}
