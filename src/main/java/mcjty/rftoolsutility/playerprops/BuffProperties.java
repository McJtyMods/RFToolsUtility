package mcjty.rftoolsutility.playerprops;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import mcjty.rftoolsutility.setup.Registration;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuffProperties {
    public static final int BUFF_MAXTICKS = 180;
    private int buffTimeout;
    private final Map<PlayerBuff,Integer> buffs = new HashMap<>();

    // Here we mirror the flags out of capabilities so that we can restore them.
    private boolean oldAllowFlying = false;
    private boolean allowFlying = false;

    private boolean globalSyncNeeded = true;

    private boolean onElevator = false;

    public static final Codec<BuffProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("onElevator").forGetter(h -> h.onElevator),
            Codec.INT.fieldOf("buffTimeout").forGetter(h -> h.buffTimeout),
            Codec.BOOL.fieldOf("allowFlying").forGetter(h -> h.allowFlying),
            Codec.BOOL.fieldOf("oldAllowFlying").forGetter(h -> h.oldAllowFlying),
            Codec.INT.listOf().fieldOf("buffs").forGetter(BuffProperties::getBuffsAsList),
            Codec.INT.listOf().fieldOf("buffTimeouts").forGetter(BuffProperties::getTimeoutsAsList)
    ).apply(instance, BuffProperties::new));

    public BuffProperties(boolean onElevator, int buffTimeout, boolean allowFlying, boolean oldAllowFlying, List<Integer> buffs, List<Integer> timeouts) {
        this.onElevator = onElevator;
        this.buffTimeout = buffTimeout;
        this.allowFlying = allowFlying;
        this.oldAllowFlying = oldAllowFlying;
        for (int i = 0; i < buffs.size(); i++) {
            this.buffs.put(PlayerBuff.values()[buffs.get(i)], timeouts.get(i));
        }
    }

    public BuffProperties() {
        buffTimeout = 0;
    }

    private List<Integer> getTimeoutsAsList() {
        return buffs.values().stream().toList();
    }

    private List<Integer> getBuffsAsList() {
        return buffs.keySet().stream().map(PlayerBuff::ordinal).toList();
    }

    private void syncBuffs(ServerPlayer player) {
        RFToolsUtilityMessages.sendToPlayer(PacketSendBuffsToClient.create(buffs), player);
    }

    public void tickBuffs(ServerPlayer player) {
        buffTimeout--;
        if (buffTimeout <= 0) {
            buffTimeout = BuffProperties.BUFF_MAXTICKS;

            Map<PlayerBuff,Integer> copyBuffs = new HashMap<>(buffs);
            buffs.clear();

            boolean syncNeeded = false;
            for (Map.Entry<PlayerBuff, Integer> entry : copyBuffs.entrySet()) {
                int timeout = entry.getValue();
                timeout -= BuffProperties.BUFF_MAXTICKS;
                if (timeout > 0) {
                    buffs.put(entry.getKey(), timeout);
                } else {
                    syncNeeded = true;
                }
            }
            if (syncNeeded) {
                syncBuffs(player);
                performBuffs(player);
                globalSyncNeeded = false;
            }
        }

        if (globalSyncNeeded) {
            globalSyncNeeded = false;
            syncBuffs(player);
            performBuffs(player);
        }
    }

    private void performBuffs(ServerPlayer player) {
        // Perform all buffs that we can perform here (not potion effects and also not
        // passive effects like feather falling.
        boolean enableFlight = false;
        if (onElevator) {
            enableFlight = true;
            player.getAbilities().flying = true;
        } else {
            for (PlayerBuff buff : buffs.keySet()) {
                if (buff == PlayerBuff.BUFF_FLIGHT) {
                    enableFlight = true;
                    break;
                }
            }
        }

        boolean oldAllow = player.getAbilities().mayfly;

        if (enableFlight) {
            if (!allowFlying) {
                // We were not already allowing flying.
                oldAllowFlying = player.getAbilities().mayfly;
                allowFlying = true;
            }
            player.getAbilities().mayfly = true;
        } else {
            if (allowFlying) {
                // We were flying before.
                player.getAbilities().mayfly = oldAllowFlying;
                if (player.getAbilities().instabuild) {
                    player.getAbilities().mayfly = true;
                }
                allowFlying = false;
            }
        }

        if (player.getAbilities().mayfly != oldAllow) {
            if (!player.getAbilities().mayfly) {
                player.getAbilities().flying = false;
            }
        }
        player.onUpdateAbilities();
    }

    public static void enableElevatorMode(Player player) {
        BuffProperties data = player.getData(Registration.ATTACHMENT_TYPE_BUFF_PROPERTIES);
        data.onElevator = true;
        data.performBuffs((ServerPlayer) player);
        player.setData(Registration.ATTACHMENT_TYPE_BUFF_PROPERTIES, data);
    }

    public static void disableElevatorMode(Player player) {
        BuffProperties data = player.getData(Registration.ATTACHMENT_TYPE_BUFF_PROPERTIES);
        data.onElevator = false;
        player.getAbilities().flying = false;
        data.performBuffs((ServerPlayer) player);
        player.setData(Registration.ATTACHMENT_TYPE_BUFF_PROPERTIES, data);
    }

    public static void addBuffToPlayer(Player player, PlayerBuff buff, int ticks) {
        BuffProperties data = player.getData(Registration.ATTACHMENT_TYPE_BUFF_PROPERTIES);
        data.addBuff((ServerPlayer) player, buff, ticks);
    }

    public void addBuff(ServerPlayer player, PlayerBuff buff, int ticks) {
        // We add a bit to the ticks to make sure we can live long enough.
        buffs.put(buff, ticks + 5);
        syncBuffs(player);
        performBuffs(player);
    }

    public Map<PlayerBuff, Integer> getBuffs() {
        return buffs;
    }

    public boolean hasBuff(PlayerBuff buff) {
        return buffs.containsKey(buff);
    }
}
