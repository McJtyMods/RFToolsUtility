package mcjty.rftoolsutility.modules.screen.network;

import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsbase.api.screens.data.IModuleData;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;

public record PacketGetScreenData(String modid, GlobalPos pos, Long millis) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "getscreendata");
    public static final CustomPacketPayload.Type<PacketGetScreenData> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PacketGetScreenData> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PacketGetScreenData::modid,
            GlobalPos.STREAM_CODEC, b -> b.pos(),
            ByteBufCodecs.VAR_LONG, PacketGetScreenData::millis,
            PacketGetScreenData::new);

    public static PacketGetScreenData create(String modid, GlobalPos pos, long millis) {
        return new PacketGetScreenData(modid, pos, millis);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
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
    }

}