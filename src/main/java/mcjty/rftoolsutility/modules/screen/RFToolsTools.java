package mcjty.rftoolsutility.modules.screen;

import mcjty.lib.api.information.CapabilityPowerInformation;
import mcjty.lib.api.information.IPowerInformation;
import mcjty.lib.varia.EnergyTools;
import mcjty.rftoolsutility.modules.screen.network.PacketReturnRfInRange;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;

import java.util.HashMap;
import java.util.Map;

public class RFToolsTools {

    public static void returnRfInRange(PlayerEntity player) {
        BlockPos pos = player.getPosition();
        World world = player.getEntityWorld();
        Map<BlockPos, MachineInfo> result = new HashMap<>();
        int range = 12;
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos p = pos.add(x, y, z);
                    TileEntity te = world.getTileEntity(p);
                    if (EnergyTools.isEnergyTE(te, null)) {
                        EnergyTools.EnergyLevel level = EnergyTools.getEnergyLevel(te, null);
                        Long usage = te.getCapability(CapabilityPowerInformation.POWER_INFORMATION_CAPABILITY).map(IPowerInformation::getEnergyDiffPerTick).orElse(0L);
                        result.put(p, new MachineInfo(level.getEnergy(), level.getMaxEnergy(), usage));
                    }
                }
            }
        }

        RFToolsUtilityMessages.INSTANCE.sendTo(new PacketReturnRfInRange(result), ((ServerPlayerEntity) player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }
}
