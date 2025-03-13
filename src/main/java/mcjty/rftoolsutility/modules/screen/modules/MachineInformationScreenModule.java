package mcjty.rftoolsutility.modules.screen.modules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsbase.api.machineinfo.CapabilityMachineInformation;
import mcjty.rftoolsbase.api.machineinfo.IMachineInformation;
import mcjty.rftoolsbase.api.screens.IScreenDataHelper;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.data.IModuleDataString;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public record MachineInformationScreenModule(int tag, GlobalPos pos, boolean active, int labcolor, int txtcolor, String monitor) implements IScreenModule<MachineInformationScreenModule, IModuleDataString> {

    public static final MachineInformationScreenModule DEFAULT = new MachineInformationScreenModule(0, GlobalPos.of(Level.OVERWORLD, BlockPosTools.INVALID), false, 0xffffff, 0xffffff, "");

    public static final Codec<MachineInformationScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("tag").forGetter(module -> module.tag),
            GlobalPos.CODEC.fieldOf("pos").forGetter(module -> module.pos),
            Codec.INT.fieldOf("labcolor").forGetter(module -> module.labcolor),
            Codec.INT.fieldOf("txtcolor").forGetter(module -> module.txtcolor),
            Codec.STRING.fieldOf("monitor").forGetter(module -> module.monitor)
    ).apply(instance, MachineInformationScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MachineInformationScreenModule> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, module -> module.tag,
            GlobalPos.STREAM_CODEC, module -> module.pos,
            ByteBufCodecs.INT, module -> module.labcolor,
            ByteBufCodecs.INT, module -> module.txtcolor,
            ByteBufCodecs.STRING_UTF8, module -> module.monitor,
            MachineInformationScreenModule::new);

    public MachineInformationScreenModule(int tag, GlobalPos pos, int labcolor, int txtcolor, String monitor) {
        this(tag, pos, false, labcolor, txtcolor, monitor);
    }

    public int getTag() {
        return tag;
    }

    public int getLabcolor() {
        return labcolor;
    }

    public int getTxtcolor() {
        return txtcolor;
    }

    public GlobalPos getPos() {
        return pos;
    }

    public String getMonitor() {
        return monitor;
    }

    public MachineInformationScreenModule withLabcolor(int labcolor) {
        return new MachineInformationScreenModule(tag, pos, active, labcolor, txtcolor, monitor);
    }

    public MachineInformationScreenModule withTxtcolor(int txtcolor) {
        return new MachineInformationScreenModule(tag, pos, active, labcolor, txtcolor, monitor);
    }

    public MachineInformationScreenModule withMonitor(String monitor) {
        return new MachineInformationScreenModule(tag, pos, active, labcolor, txtcolor, monitor);
    }

    public MachineInformationScreenModule withTag(int tag) {
        return new MachineInformationScreenModule(tag, pos, active, labcolor, txtcolor, monitor);
    }

    public MachineInformationScreenModule withPos(GlobalPos pos) {
        return new MachineInformationScreenModule(tag, pos, active, labcolor, txtcolor, monitor);
    }

    public MachineInformationScreenModule withActive(boolean active) {
        return new MachineInformationScreenModule(tag, pos, active, labcolor, txtcolor, monitor);
    }

    @Override
    public IModuleDataString getData(IScreenDataHelper helper, Level worldObj, long millis) {
        if (!active) {
            return null;
        }
        Level world = LevelTools.getLevel(worldObj, pos.dimension());
        if (world == null) {
            return null;
        }

        if (!LevelTools.isLoaded(world, pos.pos())) {
            return null;
        }

        BlockEntity te = world.getBlockEntity(pos.pos());
        if (te == null) {
            return null;
        }
        IMachineInformation h  = te.getLevel().getCapability(CapabilityMachineInformation.MACHINE_INFORMATION_CAPABILITY, te.getBlockPos(), null);
        if (h != null) {
            String info;
            if (tag < 0 || tag >= h.getTagCount()) {
                info = "[BAD TAG]";
            } else {
                info = h.getData(tag, millis);
            }
            return helper.createString(info);
        }
        return null;
    }

    @Override
    public MachineInformationScreenModule validate(Level world, BlockPos p, boolean isPlus) {
        if (isPlus) {
            return withActive(true);
        }
        // To check if this is active we need to check that the coordinate in this module is correct,
        // the dimension is equal and the coordinate is not too far from the given position (max 64 blocks)
        if (LevelTools.isLoaded(world, pos.pos())) {
            if (Objects.equals(pos.dimension(), world.dimension())) {
                int dx = Math.abs(pos.pos().getX() - p.getX());
                int dy = Math.abs(pos.pos().getY() - p.getY());
                int dz = Math.abs(pos.pos().getZ() - p.getZ());
                if (dx <= 64 && dy <= 64 && dz <= 64) {
                    return withActive(true);
                }
            }
        }
        return withActive(false);
    }

    @Override
    public int getRfPerTick() {
        return ScreenConfiguration.MACHINEINFO_RFPERTICK.get();
    }

    @Override
    public void mouseClick(Level world, int x, int y, boolean clicked, Player player) {

    }
}
