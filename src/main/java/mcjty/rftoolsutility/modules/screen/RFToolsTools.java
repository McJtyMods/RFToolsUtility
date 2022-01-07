package mcjty.rftoolsutility.modules.screen;

import mcjty.lib.api.information.CapabilityPowerInformation;
import mcjty.lib.api.information.IPowerInformation;
import mcjty.lib.varia.EnergyTools;
import mcjty.rftoolsutility.modules.screen.network.PacketReturnRfInRange;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;

import java.util.HashMap;
import java.util.Map;

public class RFToolsTools {

    public static void returnRfInRange(Player player) {
        BlockPos pos = player.blockPosition();
        Level world = player.getCommandSenderWorld();
        Map<BlockPos, MachineInfo> result = new HashMap<>();
        int range = 12;
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos p = pos.offset(x, y, z);
                    BlockEntity te = world.getBlockEntity(p);
                    if (EnergyTools.isEnergyTE(te, null)) {
                        EnergyTools.EnergyLevel level = EnergyTools.getEnergyLevel(te, null);
                        Long usage = te.getCapability(CapabilityPowerInformation.POWER_INFORMATION_CAPABILITY).map(IPowerInformation::getEnergyDiffPerTick).orElse(0L);
                        result.put(p, new MachineInfo(level.energy(), level.maxEnergy(), usage));
                    }
                }
            }
        }

        RFToolsUtilityMessages.INSTANCE.sendTo(new PacketReturnRfInRange(result), ((ServerPlayer) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
