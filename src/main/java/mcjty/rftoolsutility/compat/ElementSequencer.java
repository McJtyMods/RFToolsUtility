package mcjty.rftoolsutility.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.api.IElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ElementSequencer implements IElement {

    private final long bits;
    private final int current;
    private final boolean large;

    public ElementSequencer(long bits, int current, boolean large) {
        this.bits = bits;
        this.current = current;
        this.large = large;
    }

    public ElementSequencer(ByteBuf buf) {
        bits = buf.readLong();
        current = buf.readInt();
        large = buf.readBoolean();
    }

    private int getSize() {
        return large ? 5 : 3;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y) {
        int size = getSize();
        for (int row = 0 ; row < 8 ; row++) {
            for (int col = 0; col < 8; col++) {
                final int bit = row * 8 + col;
                if (large && bit == current) {
                    graphics.fill(6 + x + col * size, y + row * size, 6 + x + col * size + size - 1, y + row * size + size - 1,
                            0xffff0000);
                    graphics.fill(6 + x + col * size + 1, y + row * size + 1, 6 + x + col * size + size - 2, y + row * size + size - 2,
                            ((bits >> bit) & 1) == 1 ? 0xffffffff : 0xff000000);
                } else {
                    graphics.fill(6 + x + col * size, y + row * size, 6 + x + col * size + size - 1, y + row * size + size - 1,
                            ((bits >> bit) & 1) == 1 ? 0xffffffff : 0xff000000);
                }
            }
        }
    }

    @Override
    public int getWidth() {
        return 12 + getSize() * 8;
    }

    @Override
    public int getHeight() {
        return getSize() * 8;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeLong(bits);
        buf.writeInt(current);
        buf.writeBoolean(large);
    }

    @Override
    public ResourceLocation getID() {
        return TheOneProbeSupport.ELEMENT_SEQUENCER;
    }
}
