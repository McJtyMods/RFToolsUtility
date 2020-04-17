package mcjty.rftoolsutility.modules.logic.client;

import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.rftoolsbase.RFToolsBase;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.items.RedstoneInformationContainer;
import mcjty.rftoolsutility.modules.logic.items.RedstoneInformationItem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static mcjty.lib.gui.widgets.Widgets.*;

public class GuiRedstoneInformation extends GenericGuiContainer<GenericTileEntity, RedstoneInformationContainer> {

    private static final ResourceLocation iconLocation = new ResourceLocation(RFToolsUtility.MODID, "textures/gui/redstone_information.png");
    private static final ResourceLocation guiElements = new ResourceLocation(RFToolsBase.MODID, "textures/gui/guielements.png");

    public static final int WIDTH = 200;
    public static final int HEIGHT = 190;

    private WidgetList list;
    private Slider slider;

    public GuiRedstoneInformation(RedstoneInformationContainer container, PlayerInventory inventory) {
        super(RFToolsBase.instance, null, container, inventory, /* @todo 1.14 */0, "redstone_information");
        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    public void init() {
        super.init();

        list = list(5, 5, 180, 180).name("list");
        slider = slider(185, 5, 10, 180).scrollableName("list");

        Panel toplevel = positional().background(iconLocation)
                .children(list, slider);
        toplevel.bounds(guiLeft, guiTop, xSize, ySize);
        window = new Window(this, toplevel);

        fillList();
    }

    private void removeChannel(int channel) {

    }

    private void setRedstone(int channel, String newChoice) {

    }

    private void updateList() {
        Map<Integer, Pair<String, Integer>> data = container.getChannelData();
    }

    @Override
    protected void drawWindow() {
        updateList();
        super.drawWindow();
    }

    private void fillList() {
        list.removeChildren();

        ItemStack redstoneInformation = RedstoneInformationContainer.getRedstoneInformationItem(minecraft.player);

        if (redstoneInformation.getItem() instanceof RedstoneInformationItem) {
            Set<Integer> channels = RedstoneInformationItem.getChannels(redstoneInformation);
            List<Integer> sortedChannels = channels.stream().sorted().collect(Collectors.toList());
            for (Integer channel : sortedChannels) {
                Panel panel = horizontal().desiredHeight(18);
                ImageChoiceLabel choice = new ImageChoiceLabel()
                        .desiredWidth(16)
                        .desiredHeight(16)
                        .choice("0", "Redstone off", guiElements, 16, 0)
                        .choice("1", "Redstone on", guiElements, 32, 0)
                        .event(newChoice -> setRedstone(channel, newChoice));
                Label valueLabel = label("0").desiredWidth(40);
                panel.children(label("" + channel), choice, valueLabel, button("Remove").event(() -> removeChannel(channel)));
                list.children(panel);
            }
        }
    }

}
