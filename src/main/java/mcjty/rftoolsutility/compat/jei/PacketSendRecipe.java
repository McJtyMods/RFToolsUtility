package mcjty.rftoolsutility.compat.jei;

import mcjty.lib.varia.ItemStackList;
import mcjty.rftoolsbase.api.compat.JEIRecipeAcceptor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public class PacketSendRecipe {
    private ItemStackList stacks;
    private BlockPos pos;

    public void toBytes(FriendlyByteBuf buf) {
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

    public PacketSendRecipe() {
    }

    public PacketSendRecipe(FriendlyByteBuf buf) {
        int l = buf.readInt();
        stacks = ItemStackList.create(l);
        for (int i = 0 ; i < l ; i++) {
            stacks.set(i, buf.readItem());
        }
        if (buf.readBoolean()) {
            pos = buf.readBlockPos();
        } else {
            pos = null;
        }
    }

    public PacketSendRecipe(ItemStackList stacks, BlockPos pos) {
        this.stacks = stacks;
        this.pos = pos;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            Level world = player.getCommandSenderWorld();
            if (pos != null) {
                BlockEntity te = world.getBlockEntity(pos);
                if (te instanceof JEIRecipeAcceptor) {
                    JEIRecipeAcceptor acceptor = (JEIRecipeAcceptor) te;
                    acceptor.setGridContents(stacks);
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}