package mcjty.rftoolsutility.modules.screen.modules;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import mcjty.rftoolsbase.api.screens.IScreenDataHelper;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.data.IModuleData;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.ArrayList;

public class ComputerScreenModule implements IScreenModule<ComputerScreenModule.ModuleComputerInfo> {
    private String tag = "";
    private final ModuleComputerInfo textList = new ModuleComputerInfo();

    public static class ModuleComputerInfo extends ArrayList<ColoredText> implements IModuleData {
        public static final String ID = RFToolsUtility.MODID + ":computer";

        @Override
        public String getId() {
            return ID;
        }

        public ModuleComputerInfo() {
        }

        public ModuleComputerInfo(ByteBuf buf) {
            for (int i = buf.readInt(); i > 0; --i) {
                add(new ColoredText(NetworkTools.readString(buf), buf.readInt()));
            }
        }

        @Override
        public void writeToBuf(ByteBuf buf) {
            buf.writeInt(size());
            for (ColoredText i : this) {
                NetworkTools.writeString(buf, i.getText());
                buf.writeInt(i.getColor());
            }
        }
    }

    @Override
    public ModuleComputerInfo getData(IScreenDataHelper helper, World worldObj, long millis) {
        return textList;
    }

    @Override
    public void setupFromNBT(CompoundNBT tagCompound, DimensionType dim, BlockPos pos) {
        if (tagCompound != null) {
            tag = tagCompound.getString("moduleTag");
        }
    }

    public String getTag() {
        return tag;
    }

    public void addText(String text, int color) {
        textList.add(new ColoredText(text, color));
    }

    public void clearText() {
        textList.clear();
    }

    @Override
    public int getRfPerTick() {
        return ScreenConfiguration.COMPUTER_RFPERTICK.get();
    }

    @Override
    public void mouseClick(World world, int x, int y, boolean clicked, PlayerEntity player) {
    }

    public static class ColoredText {
        private final String text;
        private final int color;

        public ColoredText(String text, int color) {
            this.text = text;
            this.color = color;
        }

        public String getText() {
            return text;
        }

        public int getColor() {
            return color;
        }
    }

    @Override
    public boolean needsController() {
        return true;
    }
}