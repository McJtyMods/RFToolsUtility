package mcjty.rftoolsutility.compat.jei;

import mcjty.lib.network.CustomPacketPayload;
import mcjty.lib.network.PlayPayloadContext;
import mcjty.lib.varia.ItemStackList;
import mcjty.rftoolsbase.api.compat.JEIRecipeAcceptor;
import mcjty.rftoolsutility.RFToolsUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;


public record PacketSendRecipe(ItemStackList stacks, BlockPos pos) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(RFToolsUtility.MODID, "sendrecipe");

    public static PacketSendRecipe create(ItemStackList items, BlockPos pos) {
        return new PacketSendRecipe(items, pos);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(stacks.size());
        for (ItemStack stack : stacks) {
            buf.writeItem(stack);
        }
        if (pos != null) {
            buf.writeBoolean(true);
            buf.writeBlockPos(pos);
        } else {
            buf.writeBoolean(false);
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static PacketSendRecipe create(FriendlyByteBuf buf) {
        int l = buf.readInt();
        ItemStackList stacks = ItemStackList.create(l);
        for (int i = 0 ; i < l ; i++) {
            stacks.set(i, buf.readItem());
        }
        BlockPos pos;
        if (buf.readBoolean()) {
            pos = buf.readBlockPos();
        } else {
            pos = null;
        }
        return new PacketSendRecipe(stacks, pos);
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ctx.player().ifPresent(player -> {
                Level world = player.getCommandSenderWorld();
                if (pos != null) {
                    BlockEntity te = world.getBlockEntity(pos);
                    if (te instanceof JEIRecipeAcceptor acceptor) {
                        acceptor.setGridContents(stacks);
                    }
                }
            });
        });
    }
}