package mcjty.rftoolsutility.modules.screen.network;

import mcjty.lib.network.CustomPacketPayload;
import mcjty.lib.network.PlayPayloadContext;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsbase.api.screens.data.IModuleData;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Map;

public record PacketGetScreenData(String modid, GlobalPos pos, Long millis) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(RFToolsUtility.MODID, "getscreendata");

    public static PacketGetScreenData create(String modid, GlobalPos pos, long millis) {
        return new PacketGetScreenData(modid, pos, millis);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(modid);
        buf.writeBlockPos(pos.pos());
        buf.writeResourceLocation(pos.dimension().location());
        buf.writeLong(millis);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static PacketGetScreenData create(FriendlyByteBuf buf) {
        String modid = buf.readUtf(32767);
        BlockPos pos = buf.readBlockPos();
        ResourceKey<Level> id = LevelTools.getId(buf.readResourceLocation());
        GlobalPos gpos = GlobalPos.of(id, pos);
        long millis = buf.readLong();
        return new PacketGetScreenData(modid, gpos, millis);
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ctx.player().ifPresent(player -> {
                Level world = player.getCommandSenderWorld();
                world = LevelTools.getLevel(world, pos.dimension());
                if (world.hasChunkAt(pos.pos())) {
                    if (world.getBlockEntity(pos.pos()) instanceof ScreenTileEntity screen) {
                        Map<Integer, IModuleData> screenData = screen.getScreenData(millis);
                        PacketReturnScreenData msg = new PacketReturnScreenData(pos, screenData);
                        RFToolsUtilityMessages.sendToPlayer(msg, player);
                    }
                }
            });
        });
    }

}