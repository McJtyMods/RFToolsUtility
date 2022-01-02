package mcjty.rftoolsutility.modules.screen.network;

import mcjty.lib.varia.Logging;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsbase.api.screens.data.IModuleData;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class PacketGetScreenData {
    private String modid;
    private GlobalPos pos;
    private long millis;

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(modid);
        buf.writeBlockPos(pos.pos());
        buf.writeResourceLocation(pos.dimension().location());
        buf.writeLong(millis);
    }

    public PacketGetScreenData() {
    }

    public PacketGetScreenData(FriendlyByteBuf buf) {
        modid = buf.readUtf(32767);
        BlockPos pos = buf.readBlockPos();
        ResourceKey<Level> id = LevelTools.getId(buf.readResourceLocation());
        this.pos = GlobalPos.of(id, pos);
        millis = buf.readLong();
    }

    public PacketGetScreenData(String modid, GlobalPos pos, long millis) {
        this.modid = modid;
        this.pos = pos;
        this.millis = millis;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Level world = ctx.getSender().getCommandSenderWorld();
//            if (!pos.getDimension().equals(world.getDimension().getType())) {
//                return;
//            }
            world = LevelTools.getLevel(world, pos.dimension());
            if (world.hasChunkAt(pos.pos())) {
                BlockEntity te = world.getBlockEntity(pos.pos());
                if (!(te instanceof ScreenTileEntity)) {
                    Logging.logError("PacketGetScreenData: TileEntity is not a ScreenTileEntity!");
                    return;
                }
                Map<Integer, IModuleData> screenData = ((ScreenTileEntity) te).getScreenData(millis);

                PacketReturnScreenData msg = new PacketReturnScreenData(pos, screenData);
                RFToolsUtilityMessages.INSTANCE.sendTo(msg, ctx.getSender().connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            }
        });
        ctx.setPacketHandled(true);
    }

}