package mcjty.rftoolsutility.modules.screen.modules;

import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.WorldTools;
import mcjty.rftoolsbase.api.machineinfo.CapabilityMachineInformation;
import mcjty.rftoolsbase.api.screens.IScreenDataHelper;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.data.IModuleDataString;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

public class MachineInformationScreenModule implements IScreenModule<IModuleDataString> {
    private int tag;
    protected RegistryKey<World> dim = World.OVERWORLD;
    protected BlockPos coordinate = BlockPosTools.INVALID;

    @Override
    public IModuleDataString getData(IScreenDataHelper helper, World worldObj, long millis) {
        World world = WorldTools.getLevel(worldObj, dim);
        if (world == null) {
            return null;
        }

        if (!WorldTools.isLoaded(world, coordinate)) {
            return null;
        }

        TileEntity te = world.getBlockEntity(coordinate);
        if (te == null) {
            return null;
        }
        return te.getCapability(CapabilityMachineInformation.MACHINE_INFORMATION_CAPABILITY).map(h -> {
            String info;
            if (tag < 0 || tag >= h.getTagCount()) {
                info = "[BAD TAG]";
            } else {
                info = h.getData(tag, millis);
            }
            return helper.createString(info);
        }).orElse(null);
    }

    @Override
    public void setupFromNBT(CompoundNBT tagCompound, RegistryKey<World> dim, BlockPos pos) {
        if (tagCompound != null) {
            coordinate = BlockPosTools.INVALID;
            tag = tagCompound.getInt("monitorTag");
            if (tagCompound.contains("monitorx")) {
                this.dim = WorldTools.getId(tagCompound.getString("monitordim"));
                if (Objects.equals(dim, this.dim)) {
                    BlockPos c = new BlockPos(tagCompound.getInt("monitorx"), tagCompound.getInt("monitory"), tagCompound.getInt("monitorz"));
                    int dx = Math.abs(c.getX() - pos.getX());
                    int dy = Math.abs(c.getY() - pos.getY());
                    int dz = Math.abs(c.getZ() - pos.getZ());
                    if (dx <= 64 && dy <= 64 && dz <= 64) {
                        coordinate = c;
                    }
                }
            }
        }

    }

    @Override
    public int getRfPerTick() {
        return ScreenConfiguration.MACHINEINFO_RFPERTICK.get();
    }

    @Override
    public void mouseClick(World world, int x, int y, boolean clicked, PlayerEntity player) {

    }
}
