package mcjty.rftoolsutility.modules.screen.modules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsbase.api.screens.IScreenDataHelper;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.data.IModuleDataString;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public class MachineInformationScreenModule implements IScreenModule<IModuleDataString> {
    private int tag;
    private GlobalPos pos = GlobalPos.of(Level.OVERWORLD, BlockPosTools.INVALID);
    private boolean active = false;

    // Client side
    private String line = "";
    private int labcolor = 0xffffff;
    private int txtcolor = 0xffffff;

    public static final Codec<MachineInformationScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("tag").forGetter(module -> module.tag),
            GlobalPos.CODEC.fieldOf("pos").forGetter(module -> module.pos),
            Codec.STRING.fieldOf("line").forGetter(module -> module.line),
            Codec.INT.fieldOf("labcolor").forGetter(module -> module.labcolor),
            Codec.INT.fieldOf("txtcolor").forGetter(module -> module.txtcolor)
    ).apply(instance, MachineInformationScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MachineInformationScreenModule> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, module -> module.tag,
            GlobalPos.STREAM_CODEC, module -> module.pos,
            ByteBufCodecs.STRING_UTF8, module -> module.line,
            ByteBufCodecs.INT, module -> module.labcolor,
            ByteBufCodecs.INT, module -> module.txtcolor,
            MachineInformationScreenModule::new);

    public MachineInformationScreenModule(int tag, GlobalPos pos, String line, int labcolor, int txtcolor) {
        this.tag = tag;
        this.pos = pos;
        this.line = line;
        this.labcolor = labcolor;
        this.txtcolor = txtcolor;
    }

    public MachineInformationScreenModule() {
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public int getLabcolor() {
        return labcolor;
    }

    public void setLabcolor(int labcolor) {
        this.labcolor = labcolor;
    }

    public int getTxtcolor() {
        return txtcolor;
    }

    public void setTxtcolor(int txtcolor) {
        this.txtcolor = txtcolor;
    }

    public GlobalPos getPos() {
        return pos;
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
        // @todo 1.21 cap
        return null;
//        return te.getCapability(CapabilityMachineInformation.MACHINE_INFORMATION_CAPABILITY).map(h -> {
//            String info;
//            if (tag < 0 || tag >= h.getTagCount()) {
//                info = "[BAD TAG]";
//            } else {
//                info = h.getData(tag, millis);
//            }
//            return helper.createString(info);
//        }).orElse(null);
    }

    @Override
    public void validate(Level world, BlockPos p, boolean isPlus) {
        if (isPlus) {
            active = true;
            return;
        }
        // To check if this is active we need to check that the coordinate in this module is correct,
        // the dimension is equal and the coordinate is not too far from the given position (max 64 blocks)
        active = false;
        if (LevelTools.isLoaded(world, pos.pos())) {
            if (Objects.equals(pos.dimension(), world.dimension())) {
                int dx = Math.abs(pos.pos().getX() - p.getX());
                int dy = Math.abs(pos.pos().getY() - p.getY());
                int dz = Math.abs(pos.pos().getZ() - p.getZ());
                if (dx <= 64 && dy <= 64 && dz <= 64) {
                    active = true;
                }
            }
        }
    }

    @Override
    public int getRfPerTick() {
        return ScreenConfiguration.MACHINEINFO_RFPERTICK.get();
    }

    @Override
    public void mouseClick(Level world, int x, int y, boolean clicked, Player player) {

    }
}
