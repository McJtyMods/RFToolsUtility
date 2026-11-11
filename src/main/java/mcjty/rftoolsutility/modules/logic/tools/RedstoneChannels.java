package mcjty.rftoolsutility.modules.logic.tools;

import mcjty.lib.worlddata.AbstractWorldData;
import mcjty.rftoolsbase.tools.LegacyWorldData;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public class RedstoneChannels extends AbstractWorldData<RedstoneChannels> {

    private static final String REDSTONE_CHANNELS_NAME = "rftoolsutility:redstone_channels";

    private int lastId = 0;

    private final Map<Integer,RedstoneChannel> channels = new HashMap<>();

    public static RedstoneChannels getChannels(Level world) {
        return LegacyWorldData.get(world, RedstoneChannels::new, RedstoneChannels::new, REDSTONE_CHANNELS_NAME, "RfToolsRedstoneChannels");
    }

    public RedstoneChannels() {
    }

    public RedstoneChannels(CompoundTag tagCompound) {
        ListTag lst = tagCompound.getListOrEmpty("channels");
        for (int i = 0 ; i < lst.size() ; i++) {
            CompoundTag tc = lst.getCompoundOrEmpty(i);
            int channel = tc.getIntOr("channel", 0);
            int v = tc.getIntOr("value", 0);
            String name = tc.getStringOr("name", "");

            RedstoneChannel value = new RedstoneChannel();
            value.value = v;
            value.setName(name);
            channels.put(channel, value);
        }
        lastId = tagCompound.getIntOr("lastId", 0);
    }

    public RedstoneChannel getOrCreateChannel(int id) {
        RedstoneChannel channel = channels.get(id);
        if (channel == null) {
            channel = new RedstoneChannel();
            channels.put(id, channel);
        }
        return channel;
    }

    public RedstoneChannel getChannel(int id) {
        return channels.get(id);
    }

    public void deleteChannel(int id) {
        channels.remove(id);
    }

    public int newChannel() {
        lastId++;
        return lastId;
    }

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag tagCompound, HolderLookup.Provider provider) {
        ListTag lst = new ListTag();
        for (Map.Entry<Integer, RedstoneChannel> entry : channels.entrySet()) {
            CompoundTag tc = new CompoundTag();
            tc.putInt("channel", entry.getKey());
            tc.putInt("value", entry.getValue().getValue());
            tc.putString("name", entry.getValue().getName());
            lst.add(tc);
        }
        tagCompound.put("channels", lst);
        tagCompound.putInt("lastId", lastId);
        return tagCompound;
    }

    public static class RedstoneChannel {
        private int value = 0;
        private String name = "";

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
