package mcjty.rftoolsutility.playerprops;

import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.network.NetworkDirection;

import java.util.HashMap;
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

    public BuffProperties() {
        buffTimeout = 0;
    }

    private void syncBuffs(ServerPlayer player) {
        RFToolsUtilityMessages.INSTANCE.sendTo(new PacketSendBuffsToClient(buffs), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
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
        PlayerExtendedProperties.getBuffProperties(player).ifPresent(h -> {
            h.onElevator = true;
            h.performBuffs((ServerPlayer) player);
        });
    }

    public static void disableElevatorMode(Player player) {
        PlayerExtendedProperties.getBuffProperties(player).ifPresent(h -> {
            h.onElevator = false;
            player.getAbilities().flying = false;
            h.performBuffs((ServerPlayer) player);
        });
    }

    public static void addBuffToPlayer(Player player, PlayerBuff buff, int ticks) {
        PlayerExtendedProperties.getBuffProperties(player).ifPresent(h -> h.addBuff((ServerPlayer) player, buff, ticks));
    }

    public void addBuff(ServerPlayer player, PlayerBuff buff, int ticks) {
        //. We add a bit to the ticks to make sure we can live long enough.
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

    public void saveNBTData(CompoundTag compound) {
        compound.putBoolean("onElevator", onElevator);
        compound.putInt("buffTicks", buffTimeout);
        compound.putBoolean("allowFlying", allowFlying);
        compound.putBoolean("oldAllowFlying", oldAllowFlying);
        int[] buffArray = new int[buffs.size()];
        int[] timeoutArray = new int[buffs.size()];
        int idx = 0;
        for (Map.Entry<PlayerBuff, Integer> entry : buffs.entrySet()) {
            PlayerBuff buff = entry.getKey();
            buffArray[idx] = buff.ordinal();
            timeoutArray[idx] = entry.getValue();
            idx++;
        }
        compound.putIntArray("buffs", buffArray);
        compound.putIntArray("buffTimeouts", timeoutArray);
    }

    public void loadNBTData(CompoundTag compound) {
        onElevator = compound.getBoolean("onElevator");
        buffTimeout = compound.getInt("buffTicks");
        int[] buffArray = compound.getIntArray("buffs");
        int[] timeoutArray = compound.getIntArray("buffTimeouts");
        buffs.clear();
        for (int i = 0; i < buffArray.length; i++) {
            int buffIdx = buffArray[i];
            buffs.put(PlayerBuff.values()[buffIdx], timeoutArray[i]);
        }
        allowFlying = compound.getBoolean("allowFlying");
        oldAllowFlying = compound.getBoolean("oldAllowFlying");
        globalSyncNeeded = true;
    }


}
