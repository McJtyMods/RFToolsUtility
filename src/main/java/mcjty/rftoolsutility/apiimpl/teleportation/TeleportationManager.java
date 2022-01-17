package mcjty.rftoolsutility.apiimpl.teleportation;

import mcjty.rftoolsbase.api.teleportation.ITeleportationManager;
import mcjty.rftoolsutility.modules.teleporter.TeleportConfiguration;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import mcjty.rftoolsutility.modules.teleporter.blocks.MatterReceiverTileEntity;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestination;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.Level;

public class TeleportationManager implements ITeleportationManager {

    @Override
    public String getReceiverName(Level world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() == TeleporterModule.MATTER_RECEIVER.get()) {
            MatterReceiverTileEntity te = (MatterReceiverTileEntity) world.getBlockEntity(pos);
            return te.getName();
        } else {
            return null;
        }
    }

    @Override
    public boolean createReceiver(Level world, BlockPos pos, String name, int power) {
        world.setBlock(pos, TeleporterModule.MATTER_RECEIVER.get().defaultBlockState(), 2);
        if (world.getBlockEntity(pos) instanceof MatterReceiverTileEntity te) {
            if (power == -1) {
                te.storeEnergy(TeleportConfiguration.RECEIVER_MAXENERGY.get());
            } else {
                te.storeEnergy(Math.min(power, TeleportConfiguration.RECEIVER_MAXENERGY.get()));
            }
            te.setName(name);
            te.setChanged();
            registerReceiver(world, pos, name);
        }
        return true;
    }

    private void registerReceiver(Level world, BlockPos pos, String name) {
        TeleportDestinations destinations = TeleportDestinations.get(world);
        GlobalPos gc = GlobalPos.of(world.dimension(), pos);
        TeleportDestination destination = destinations.addDestination(gc);
        destination.setName(name);
        destinations.save();
    }

    @Override
    public void teleportPlayer(Player player, ResourceKey<Level> dimension, BlockPos location) {
        mcjty.lib.varia.TeleportationTools.teleportToDimension(player, dimension, location.getX(), location.getY(), location.getZ());
    }

    @Override
    public void removeReceiverDestinations(Level world, ResourceKey<Level> dim) {
        TeleportDestinations destinations = TeleportDestinations.get(world);
        destinations.removeDestinationsInDimension(dim);
        destinations.save();
    }
}
