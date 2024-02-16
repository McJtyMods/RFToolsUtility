package mcjty.rftoolsutility.modules.logic.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.rftoolsbase.RFToolsBase;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.items.RedstoneInformationContainer;
import mcjty.rftoolsutility.modules.logic.items.RedstoneInformationItem;
import mcjty.rftoolsutility.modules.logic.network.PacketRemoveChannel;
import mcjty.rftoolsutility.modules.logic.network.PacketSetRedstone;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

import static mcjty.lib.gui.widgets.Widgets.*;

public class GuiRedstoneInformation extends GenericGuiContainer<GenericTileEntity, RedstoneInformationContainer> {

    private static final ResourceLocation iconLocation = new ResourceLocation(RFToolsUtility.MODID, "textures/gui/redstone_information.png");
    private static final ResourceLocation guiElements = new ResourceLocation(RFToolsBase.MODID, "textures/gui/guielements.png");

    public static final int WIDTH = 200;
    public static final int HEIGHT = 190;

    private WidgetList list;

    public GuiRedstoneInformation(RedstoneInformationContainer container, Inventory inventory) {
        super(null, container, inventory, RedstoneInformationItem.MANUAL);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Nonnull
    public static GuiRedstoneInformation createRedstoneInformationGui(RedstoneInformationContainer container, Inventory inventory, Component textComponent) {
        return new GuiRedstoneInformation(container, inventory);
    }

    public static void register() {
        MenuScreens.register(LogicBlockModule.CONTAINER_REDSTONE_INFORMATION.get(), GuiRedstoneInformation::createRedstoneInformationGui);
    }

    @Override
    public void init() {
        super.init();

        list = list(5, 5, 180, 180).name("list").propagateEventsToChildren(true);
        Slider slider = slider(185, 5, 10, 180).scrollableName("list");

        Panel toplevel = positional().background(iconLocation)
                .children(list, slider);
        toplevel.bounds(leftPos, topPos, imageWidth, imageHeight);
        window = new Window(this, toplevel);

        fillList();
    }

    private void removeChannel(int channel) {
        RFToolsUtilityMessages.sendToServer(PacketRemoveChannel.create(channel));
    }

    private void setRedstone(int channel, String newChoice) {
        RFToolsUtilityMessages.sendToServer(PacketSetRedstone.create(channel, "1".equals(newChoice) ? 15 : 0));
    }

    private boolean isDirty() {
        Map<Integer, Pair<String, Integer>> data = menu.getChannelData();
        if (data == null) {
            return true;
        }
        if (data.size() != list.getChildCount()) {
            return true;
        } else {
            for (int i = 0; i < list.getChildCount(); i++) {
                Panel panel = list.getChild(i);
                Integer channel = (Integer) panel.getUserObject();
                if (!data.containsKey(channel)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateList() {
        if (isDirty()) {
            fillList();
        }

        Map<Integer, Pair<String, Integer>> data = menu.getChannelData();
        for (int i = 0 ; i < list.getChildCount() ; i++) {
            Panel panel = list.getChild(i);
            Integer channel = (Integer)panel.getUserObject();
            Pair<String, Integer> pair = data.get(channel);
            if (pair != null) {
                Label name = panel.findChild("name");
                ImageChoiceLabel choice = panel.findChild("choice");
                Label value = panel.findChild("value");

                if (pair.getLeft().isEmpty()) {
                    name.text(String.valueOf(channel));
                } else {
                    name.text(channel + " (" + pair.getLeft() + ")");
                }
                choice.setCurrentChoice(pair.getRight() > 0 ? "1" : "0");
                value.text(Integer.toString(pair.getRight()));
            }
        }
    }

    @Override
    protected void drawWindow(GuiGraphics graphics) {
        updateList();
        super.drawWindow(graphics);
    }

    private void fillList() {
        list.removeChildren();

        Map<Integer, Pair<String, Integer>> data = menu.getChannelData();
        if (data == null) {
            return;
        }

        Set<Integer> channels = data.keySet();
        channels.stream().sorted().forEach(channel -> {
            Panel panel = horizontal().desiredHeight(18).userObject(channel);
            ImageChoiceLabel choice = new ImageChoiceLabel()
                    .name("choice")
                    .desiredWidth(16)
                    .desiredHeight(16)
                    .choice("0", "Redstone off", guiElements, 16, 0)
                    .choice("1", "Redstone on", guiElements, 32, 0)
                    .event(newChoice -> setRedstone(channel, newChoice));
            Label valueLabel = label("0").name("value").desiredWidth(30).horizontalAlignment(HorizontalAlignment.ALIGN_LEFT);
            panel.children(label(String.valueOf(channel)).name("name").desiredWidth(60).horizontalAlignment(HorizontalAlignment.ALIGN_LEFT), choice, valueLabel, button("Remove").event(() -> removeChannel(channel)));
            list.children(panel);
        });
    }

}
