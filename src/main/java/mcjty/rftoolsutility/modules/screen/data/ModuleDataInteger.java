package mcjty.rftoolsutility.modules.screen.data;

import io.netty.buffer.ByteBuf;
import mcjty.rftoolsbase.api.screens.data.IModuleDataInteger;
import mcjty.rftoolsutility.RFToolsUtility;
import net.minecraft.network.PacketBuffer;

public class ModuleDataInteger implements IModuleDataInteger {
    public static final String ID = RFToolsUtility.MODID + ":integer";

    public final int i;

    @Override
    public String getId() {
        return ID;
    }

    public ModuleDataInteger(int i) {
        this.i = i;
    }

    public ModuleDataInteger(ByteBuf buf) {
        i = buf.readInt();
    }

    @Override
    public int get() {
        return i;
    }

    @Override
    public void writeToBuf(PacketBuffer buf) {
        buf.writeInt(i);
    }
}
