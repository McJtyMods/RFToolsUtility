package mcjty.rftoolsutility.modules.screen.modules;

import mcjty.lib.network.NetworkTools;
import mcjty.lib.varia.Logging;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public enum ScreenDataType {
    TYPE_NULL,
    TYPE_BYTE,
    TYPE_INT,
    TYPE_LONG,
    TYPE_DOUBLE,
    TYPE_FLOAT,
    TYPE_STRING,
    TYPE_BOOLEAN,
    TYPE_ITEMSTACK,
    TYPE_COLOREDTEXT;

    public Object readObject(PacketBuffer buf) {
        switch (this) {
            case TYPE_NULL:
                return null;
            case TYPE_BYTE:
                return buf.readByte();
            case TYPE_INT:
                return buf.readInt();
            case TYPE_LONG:
                return buf.readLong();
            case TYPE_DOUBLE:
                return buf.readDouble();
            case TYPE_FLOAT:
                return buf.readFloat();
            case TYPE_BOOLEAN:
                return buf.readBoolean();
            case TYPE_STRING:
                return buf.readUtf(32767);
            case TYPE_ITEMSTACK:
                return NetworkTools.readItemStack(buf);
            case TYPE_COLOREDTEXT:
                return null;
//                byte[] dst2 = new byte[buf.readInt()];
//                buf.readBytes(dst2);
//                int color = buf.readInt();
//                return new ComputerScreenModule.ColoredText(new String(dst2), color);

        }
        return null;
    }

    public static void writeObject(PacketBuffer buf, Object obj) {
        if (obj == null) {
            buf.writeByte(TYPE_NULL.ordinal());
        } else if (obj instanceof Long) {
            buf.writeByte(TYPE_LONG.ordinal());
            buf.writeLong((Long) obj);
        } else if (obj instanceof Integer) {
            buf.writeByte(TYPE_INT.ordinal());
            buf.writeInt((Integer) obj);
        } else if (obj instanceof Byte) {
            buf.writeByte(TYPE_BYTE.ordinal());
            buf.writeByte((Byte) obj);
        } else if (obj instanceof Float) {
            buf.writeByte(TYPE_FLOAT.ordinal());
            buf.writeFloat((Float) obj);
        } else if (obj instanceof Double) {
            buf.writeByte(TYPE_FLOAT.ordinal());
            buf.writeDouble((Double) obj);
        } else if (obj instanceof Boolean) {
            buf.writeByte(TYPE_BOOLEAN.ordinal());
            buf.writeBoolean((Boolean) obj);
        } else if (obj instanceof String) {
            buf.writeByte(TYPE_STRING.ordinal());
            String s  = (String) obj;
            buf.writeInt(s.length());
            buf.writeBytes(s.getBytes());
        } else if (obj instanceof ItemStack) {
            buf.writeByte(TYPE_ITEMSTACK.ordinal());
            NetworkTools.writeItemStack(buf, (ItemStack) obj);
//        } else if (obj instanceof ComputerScreenModule.ColoredText) {
//            buf.writeByte(TYPE_COLOREDTEXT.ordinal());
//            ComputerScreenModule.ColoredText ct = (ComputerScreenModule.ColoredText) obj;
//            NetworkTools.writeString(buf, ct.getText());
//            buf.writeInt(ct.getColor());
        } else {
            Logging.log("Weird ScreenDataType!");
        }
    }
}
