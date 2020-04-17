package mcjty.rftoolsutility.modules.teleporter.client;

import mcjty.lib.base.StyleConfig;
import mcjty.lib.varia.GuiTools;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.DefaultSelectionEvent;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.Slider;
import mcjty.lib.gui.widgets.WidgetList;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.BlockPosTools;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinationClientInfo;
import mcjty.rftoolsutility.modules.teleporter.network.PacketGetAllReceivers;
import mcjty.rftoolsutility.setup.CommandHandler;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.dimension.DimensionType;

import java.util.ArrayList;
import java.util.List;

import static mcjty.lib.gui.widgets.Widgets.horizontal;
import static mcjty.lib.gui.widgets.Widgets.label;

public class GuiTeleportProbe extends Screen {

    /** The X size of the window in pixels. */
    private int xSize = 356;
    /** The Y size of the window in pixels. */
    private int ySize = 180;

    private Window window;
    private WidgetList list;

    private static List<TeleportDestinationClientInfo> serverDestinationList = null;
    private static List<TeleportDestinationClientInfo> destinationList = null;

    private int listDirty;

    public GuiTeleportProbe() {
        super(new StringTextComponent("Teleport Probe"));
        listDirty = 0;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void init() {
        super.init();

        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;

        list = new WidgetList().name("list").event(new DefaultSelectionEvent() {
            @Override
            public void doubleClick(int index) {
                teleport(index);
            }
        });
        Slider listSlider = new Slider().desiredWidth(11).vertical().scrollableName("list");
        Panel toplevel = horizontal(3, 1).children(list, listSlider);
        toplevel.bounds(k, l, xSize, ySize);

        window = new Window(this, toplevel);

        serverDestinationList = null;
        destinationList = null;
        requestReceiversFromServer();
    }

    private void teleport(int index) {
        TeleportDestinationClientInfo destination = destinationList.get(index);
        BlockPos c = destination.getCoordinate();
        RFToolsUtilityMessages.sendToServer(CommandHandler.CMD_FORCE_TELEPORT,
                TypedMap.builder().put(CommandHandler.PARAM_DIMENSION, destination.getDimension().getRegistryName().toString()).put(CommandHandler.PARAM_POS, c));
    }

    public static void setReceivers(List<TeleportDestinationClientInfo> destinationList) {
        serverDestinationList = new ArrayList<>(destinationList);
    }

    private void requestReceiversFromServer() {
        RFToolsUtilityMessages.INSTANCE.sendToServer(new PacketGetAllReceivers());
    }

    private void populateList() {
        if (serverDestinationList == null) {
            return;
        }
        if (serverDestinationList.equals(destinationList)) {
            return;
        }

        destinationList = new ArrayList<>(serverDestinationList);

        list.removeChildren();

        for (TeleportDestinationClientInfo destination : destinationList) {
            BlockPos coordinate = destination.getCoordinate();
            DimensionType dim = destination.getDimension();

            Panel panel = horizontal();

            panel.children(
                    label(destination.getName()).color(StyleConfig.colorTextInListNormal).horizontalAlignment(HorizontalAlignment.ALIGN_LEFT).desiredWidth(100),
                    label(BlockPosTools.toString(coordinate)).color(StyleConfig.colorTextInListNormal).horizontalAlignment(HorizontalAlignment.ALIGN_LEFT).desiredWidth(75),
                    label("Id " + dim).color(StyleConfig.colorTextInListNormal).horizontalAlignment(HorizontalAlignment.ALIGN_LEFT).desiredWidth(75));
            list.children(panel);
        }
    }

    @Override
    public void render(int xSize_lo, int ySize_lo, float par3) {
        super.render(xSize_lo, ySize_lo, par3);

        listDirty--;
        if (listDirty <= 0) {
            populateList();
            listDirty = 10;
        }

        window.draw();
        List<String> tooltips = window.getTooltips();
        if (tooltips != null) {
            int x = GuiTools.getRelativeX(this);
            int y = GuiTools.getRelativeY(this);
            int guiLeft = (this.width - this.xSize) / 2;
            int guiTop = (this.height - this.ySize) / 2;

            renderTooltip(tooltips, x-guiLeft, y-guiTop, minecraft.fontRenderer);
        }
    }

    public static void open() {
        Minecraft.getInstance().displayGuiScreen(new GuiTeleportProbe());
    }

}
