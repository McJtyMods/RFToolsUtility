package mcjty.rftoolsutility.modules.screen.network;

import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketModuleUpdate(BlockPos pos, Integer slotIndex, ItemStack module) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "moduleupdate");
    public static final CustomPacketPayload.Type<PacketModuleUpdate> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketModuleUpdate> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketModuleUpdate::pos,
            ByteBufCodecs.INT, PacketModuleUpdate::slotIndex,
            ItemStack.OPTIONAL_STREAM_CODEC, PacketModuleUpdate::module,
            PacketModuleUpdate::new);

    public static PacketModuleUpdate create(BlockPos blockPos, int slotIndex, ItemStack module) {
        return new PacketModuleUpdate(blockPos, slotIndex, module);
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
                    screen.updateModuleData(slotIndex, module);
                }
            }
        });
    }
}
