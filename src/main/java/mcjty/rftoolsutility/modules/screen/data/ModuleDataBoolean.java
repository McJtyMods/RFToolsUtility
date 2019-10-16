package mcjty.rftoolsutility.modules.screen.data;

import io.netty.buffer.ByteBuf;
import mcjty.rftoolsbase.api.screens.data.IModuleDataBoolean;
import mcjty.rftoolsutility.RFToolsUtility;

public class ModuleDataBoolean implements IModuleDataBoolean {

    public static final String ID = RFToolsUtility.MODID + ":bool";

    private final boolean b;

    @Override
    public String getId() {
        return ID;
    }

    public ModuleDataBoolean(boolean b) {
        this.b = b;
    }

    public ModuleDataBoolean(ByteBuf buf) {
        b = buf.readBoolean();
    }

    @Override
    public boolean get() {
        return b;
    }

    @Override
    public void writeToBuf(ByteBuf buf) {
        buf.writeBoolean(b);
    }
}
