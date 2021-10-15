package mcjty.rftoolsutility.modules.screen.modules;

import mcjty.rftoolsbase.api.screens.IScreenDataHelper;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.data.IModuleDataBoolean;
import mcjty.rftoolsutility.modules.logic.tools.RedstoneChannels;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ButtonScreenModule implements IScreenModule<IModuleDataBoolean> {
    private String line = "";
    private int channel = -1;
    private boolean toggle;

    @Override
    public IModuleDataBoolean getData(IScreenDataHelper helper, World worldObj, long millis) {
        if (channel != -1 && toggle) {
            RedstoneChannels channels = RedstoneChannels.getChannels(worldObj);
            RedstoneChannels.RedstoneChannel ch = channels.getOrCreateChannel(channel);
            return helper.createBoolean(ch.getValue() > 0);
        }
        return null;
    }

    @Override
    public void setupFromNBT(CompoundNBT tagCompound, RegistryKey<World> dim, BlockPos pos) {
        if (tagCompound != null) {
            line = tagCompound.getString("text");
            if (tagCompound.contains("channel")) {
                channel = tagCompound.getInt("channel");
            }
            toggle = tagCompound.getBoolean("toggle");
        }
    }

    @Override
    public void mouseClick(World world, int x, int y, boolean clicked, PlayerEntity player) {
        int xoffset;
        if (!line.isEmpty()) {
            xoffset = 80;
        } else {
            xoffset = 5;
        }
        if (x >= xoffset) {
            if (channel != -1) {
                if (toggle) {
                    if (clicked) {
                        RedstoneChannels channels = RedstoneChannels.getChannels(world);
                        RedstoneChannels.RedstoneChannel ch = channels.getOrCreateChannel(channel);
                        ch.setValue((ch.getValue() == 0) ? 15 : 0);
                        channels.save();
                    }
                } else {
                    RedstoneChannels channels = RedstoneChannels.getChannels(world);
                    RedstoneChannels.RedstoneChannel ch = channels.getOrCreateChannel(channel);
                    ch.setValue(clicked ? 15 : 0);
                    channels.save();
                }
            } else {
                if (player != null) {
                    player.displayClientMessage(new StringTextComponent(TextFormatting.RED + "Module is not linked to redstone channel!"), false);
                }
            }
        }
    }

    @Override
    public int getRfPerTick() {
        return ScreenConfiguration.BUTTON_RFPERTICK.get();
    }
}
