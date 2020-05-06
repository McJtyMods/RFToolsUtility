package mcjty.rftoolsutility.modules.teleporter.client;

import mcjty.lib.base.StyleConfig;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.gui.events.DefaultSelectionEvent;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsbase.RFToolsBase;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.teleporter.blocks.DialingDeviceTileEntity;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestination;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinationClientInfo;
import mcjty.rftoolsutility.modules.teleporter.data.TransmitterInfo;
import mcjty.rftoolsutility.modules.teleporter.network.PacketGetReceivers;
import mcjty.rftoolsutility.modules.teleporter.network.PacketGetTransmitters;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.energy.CapabilityEnergy;

import java.util.ArrayList;
import java.util.List;

import static mcjty.lib.gui.layout.AbstractLayout.DEFAULT_SPACING;
import static mcjty.lib.gui.widgets.Widgets.*;
import static mcjty.rftoolsutility.modules.teleporter.blocks.DialingDeviceTileEntity.*;

public class GuiDialingDevice extends GenericGuiContainer<DialingDeviceTileEntity, GenericContainer> {

    public static final int DIALER_WIDTH = 256;
    public static final int DIALER_HEIGHT = 224;

    private static final ResourceLocation guielements = new ResourceLocation(RFToolsBase.MODID, "textures/gui/guielements.png");

    private EnergyBar energyBar;
    private WidgetList transmitterList;
    private WidgetList receiverList;
    private Button dialButton;
    private Button dialOnceButton;
    private Button interruptButton;
    private ImageChoiceLabel favoriteButton;
    private Button statusButton;
    private Label statusLabel;

    private boolean analyzerAvailable = false;
    private boolean lastDialedTransmitter = false;
    private boolean lastCheckedReceiver = false;

    public static int fromServer_receiverStatus = -1;
    public static int fromServer_dialResult = -1;
    public static List<TeleportDestinationClientInfo> fromServer_receivers = null;
    public static List<TransmitterInfo> fromServer_transmitters = null;

    // A copy of the receivers we're currently showing.
    private List<TeleportDestinationClientInfo> receivers = null;
    private boolean receiversFiltered = false;

    // A copy of the transmitters we're currently showing.
    private List<TransmitterInfo> transmitters = null;

    private int listDirty = 10;


    public GuiDialingDevice(DialingDeviceTileEntity dialingDeviceTileEntity, GenericContainer container, PlayerInventory inventory) {
        super(RFToolsUtility.instance, dialingDeviceTileEntity, container, inventory, ManualHelper.create("rftoolsutility:machines/dialing_device"));

        xSize = DIALER_WIDTH;
        ySize = DIALER_HEIGHT;
    }

    @Override
    public void init() {
        super.init();

        Minecraft mc = minecraft;
        energyBar = new EnergyBar().filledRectThickness(1).horizontal().desiredWidth(80).desiredHeight(12).showText(false);

        Panel transmitterPanel = setupTransmitterPanel();
        Panel receiverPanel = setupReceiverPanel();

        dialButton = button("Dial").channel("dial").tooltips("Start a connection between", "the selected transmitter", "and the selected receiver").
                desiredHeight(14).desiredWidth(65);
        dialOnceButton = button("Dial once").channel("dialonce").tooltips("Dial a connection for a", "single teleport").
                desiredHeight(14).desiredWidth(65);
        interruptButton = button("Interrupt").channel("interrupt").tooltips("Interrupt a connection", "for the selected transmitter").
                desiredHeight(14).desiredWidth(65);
        favoriteButton = new ImageChoiceLabel().channel("favorite").desiredWidth(10).desiredHeight(10);
        favoriteButton.choice("No", "Unfavorited receiver", guielements, 131, 19);
        favoriteButton.choice("Yes", "Favorited receiver", guielements, 115, 19);
        favoriteButton.setCurrentChoice(tileEntity.isShowOnlyFavorites() ? 1 : 0);

        Panel buttonPanel = horizontal().children(dialButton, dialOnceButton, interruptButton, favoriteButton).desiredHeight(16);

        analyzerAvailable = DialingDeviceTileEntity.isDestinationAnalyzerAvailable(mc.world, tileEntity.getPos());
        statusButton = button("Check").channel("check")
                .desiredHeight(14).desiredWidth(65)
                .enabled(analyzerAvailable);
        if (analyzerAvailable) {
            statusButton.tooltips("Check the status of", "the selected receiver");
        } else {
            statusButton.tooltips("Check the status of", "the selected receiver", "(needs an adjacent analyzer!)");
        }

        statusLabel = new Label();
        statusLabel.desiredWidth(170).desiredHeight(14).filledRectThickness(1);
        Panel statusPanel = horizontal().children(statusButton, statusLabel).desiredHeight(16);

        Panel toplevel = vertical(3, 1).filledRectThickness(2)
                .children(energyBar, transmitterPanel, receiverPanel, buttonPanel, statusPanel);
        toplevel.bounds(guiLeft, guiTop, DIALER_WIDTH, DIALER_HEIGHT);
        window = new mcjty.lib.gui.Window(this, toplevel);

        window.event("dial", (souce, params) -> dial(false));
        window.event("dialonce", (souce, params) -> dial(true));
        window.event("interrupt", (souce, params) -> interruptDial());
        window.event("favorite", (souce, params) -> changeShowFavorite());
        window.event("check", (souce, params) -> checkStatus());

        minecraft.keyboardListener.enableRepeatEvents(true);

        fromServer_receivers = null;
        fromServer_transmitters = null;

        listDirty = 0;
        clearSelectedStatus();

        requestReceivers();
        requestTransmitters();
    }

    private Panel setupReceiverPanel() {
        receiverList = new WidgetList().name("receivers").rowheight(14).desiredHeight(100).propagateEventsToChildren(true).event(new DefaultSelectionEvent() {
            @Override
            public void select(int index) {
                clearSelectedStatus();
            }

            @Override
            public void doubleClick(int index) {
                hilightSelectedReceiver(index);
            }
        });
        Slider receiverSlider = new Slider().desiredWidth(11).desiredHeight(100).vertical().scrollableName("receivers");
        return horizontal(3, 1).children(receiverList, receiverSlider).desiredHeight(106)
                .filledBackground(0xff9e9e9e);
    }

    private Panel setupTransmitterPanel() {
        transmitterList = new WidgetList().name("transmitters").rowheight(18).desiredHeight(58).event(new DefaultSelectionEvent() {
            @Override
            public void select(int index) {
                clearSelectedStatus();
                selectReceiverFromTransmitter();
            }

            @Override
            public void doubleClick(int index) {
                hilightSelectedTransmitter(index);
            }
        });
        Slider transmitterSlider = new Slider().desiredWidth(11).desiredHeight(58).vertical().scrollableName("transmitters");
        return horizontal(3, 1).children(transmitterList, transmitterSlider).desiredHeight(64)
                .filledBackground(0xff9e9e9e);
    }

    private void clearSelectedStatus() {
        lastDialedTransmitter = false;
        lastCheckedReceiver = false;
    }

    private void hilightSelectedTransmitter(int index) {
        TransmitterInfo transmitterInfo = getSelectedTransmitter(index);
        if (transmitterInfo == null) {
            return;
        }
        BlockPos c = transmitterInfo.getCoordinate();
        // @todo 1.14 better api to get this client info?
        RFToolsBase.instance.clientInfo.hilightBlock(c, System.currentTimeMillis() + 1000 * 5);//@todo StorageScannerConfiguration.hilightTime);
        minecraft.player.closeScreen();
    }

    private void hilightSelectedReceiver(int index) {
        TeleportDestination destination = getSelectedReceiver(index);
        if (destination == null) {
            return;
        }

        BlockPos c = destination.getCoordinate();
        double distance = new Vec3d(c.getX(), c.getY(), c.getZ()).distanceTo(minecraft.player.getPositionVector());

        if (!destination.getDimension().equals(minecraft.world.getDimension().getType()) || distance > 150) {
            Logging.warn(minecraft.player, "Receiver is too far to hilight!");
            minecraft.player.closeScreen();
            return;
        }
        RFToolsBase.instance.clientInfo.hilightBlock(c, System.currentTimeMillis() + 1000 * 5);// @todo StorageScannerConfiguration.hilightTime);
        Logging.message(minecraft.player, "The receiver is now highlighted");
        minecraft.player.closeScreen();
    }

    private void setStatusError(String message) {
        statusLabel.text(message);
        statusLabel.color(0xffffffff);
        statusLabel.filledBackground(0xffff0000, 0xff880000);
    }

    private void setStatusMessage(String message) {
        statusLabel.text(message);
        statusLabel.color(0xff000000);
        statusLabel.filledBackground(0xff00ff00, 0xff008800);
    }

    private void checkStatus() {
        int receiverSelected = receiverList.getSelected();
        TeleportDestination destination = getSelectedReceiver(receiverSelected);
        if (destination == null) {
            return;
        }
        BlockPos c = destination.getCoordinate();
        tileEntity.requestDataFromServer(RFToolsUtilityMessages.INSTANCE,
                DialingDeviceTileEntity.CMD_CHECKSTATUS,
                TypedMap.builder()
                        .put(PARAM_POS, c)
                        .put(PARAM_DIMENSION, destination.getDimension().getRegistryName().toString())
                        .build());

        lastCheckedReceiver = true;
        listDirty = 0;
    }

    private void showStatus(int dialResult) {
        if ((dialResult & DialingDeviceTileEntity.DIAL_DIALER_POWER_LOW_MASK) != 0) {
            setStatusError("Dialing device power low!");
            return;
        }
        if ((dialResult & DialingDeviceTileEntity.DIAL_RECEIVER_POWER_LOW_MASK) != 0) {
            setStatusError("Matter receiver power low!");
            return;
        }
        if ((dialResult & DialingDeviceTileEntity.DIAL_DIMENSION_POWER_LOW_MASK) != 0) {
            setStatusError("Destination dimension power low!");
            return;
        }
        if ((dialResult & DialingDeviceTileEntity.DIAL_TRANSMITTER_NOACCESS) != 0) {
            setStatusError("No access to transmitter!");
            return;
        }
        if ((dialResult & DialingDeviceTileEntity.DIAL_RECEIVER_NOACCESS) != 0) {
            setStatusError("No access to receiver!");
            return;
        }
        if ((dialResult & DialingDeviceTileEntity.DIAL_INVALID_DESTINATION_MASK) != 0) {
            setStatusError("Invalid destination!");
            return;
        }
        if ((dialResult & DialingDeviceTileEntity.DIAL_INVALID_SOURCE_MASK) != 0) {
            setStatusError("Invalid source!");
            return;
        }
        if ((dialResult & DialingDeviceTileEntity.DIAL_RECEIVER_BLOCKED_MASK) != 0) {
            setStatusError("Receiver blocked!");
            return;
        }
        if ((dialResult & DialingDeviceTileEntity.DIAL_TRANSMITTER_BLOCKED_MASK) != 0) {
            setStatusError("Transmitter blocked!");
            return;
        }
        if ((dialResult & DialingDeviceTileEntity.DIAL_INVALID_TRANSMITTER) != 0) {
            setStatusError("Invalid transmitter!!");
            return;
        }
        if ((dialResult & DialingDeviceTileEntity.DIAL_INTERRUPTED) != 0) {
            setStatusMessage("Interrupted!");
            return;
        }
        setStatusMessage("Dial ok!");
    }

    private void selectReceiverFromTransmitter() {
        receiverList.selected(-1);
        TeleportDestination destination = getSelectedTransmitterDestination();
        if (destination == null) {
            return;
        }
        int i = 0;
        for (TeleportDestination receiver : receivers) {
            if (receiver.getDimension() == destination.getDimension() && receiver.getCoordinate().equals(destination.getCoordinate())) {
                receiverList.selected(i);
                return;
            }
            i++;
        }
    }

    private void dial(boolean once) {
        int transmitterSelected = transmitterList.getSelected();
        TransmitterInfo transmitterInfo = getSelectedTransmitter(transmitterSelected);
        if (transmitterInfo == null) {
            return;
        }

        int receiverSelected = receiverList.getSelected();
        TeleportDestination destination = getSelectedReceiver(receiverSelected);
        if (destination == null) {
            return;
        }

        tileEntity.requestDataFromServer(RFToolsUtilityMessages.INSTANCE,
                once ? DialingDeviceTileEntity.CMD_DIALONCE : DialingDeviceTileEntity.CMD_DIAL,
                TypedMap.builder()
                        .put(PARAM_PLAYER_UUID, minecraft.player.getUniqueID())
                        .put(PARAM_TRANSMITTER, transmitterInfo.getCoordinate())
                        .put(PARAM_TRANS_DIMENSION, minecraft.world.getDimension().getType().getRegistryName().toString())
                        .put(PARAM_POS, destination.getCoordinate())
                        .put(PARAM_DIMENSION, destination.getDimension().getRegistryName().toString())
                        .build());

        lastDialedTransmitter = true;
        listDirty = 0;
    }

    private TeleportDestinationClientInfo getSelectedReceiver(int receiverSelected) {
        if (receiverSelected == -1) {
            return null;
        }
        if (receiverSelected >= receivers.size()) {
            return null;
        }
        return receivers.get(receiverSelected);
    }

    private void interruptDial() {
        int transmitterSelected = transmitterList.getSelected();
        TransmitterInfo transmitterInfo = getSelectedTransmitter(transmitterSelected);
        if (transmitterInfo == null) {
            return;
        }
        tileEntity.requestDataFromServer(RFToolsUtilityMessages.INSTANCE, DialingDeviceTileEntity.CMD_DIAL,
                TypedMap.builder()
                        .put(PARAM_PLAYER_UUID, minecraft.player.getUniqueID())
                        .put(PARAM_TRANSMITTER, transmitterInfo.getCoordinate())
                        .put(PARAM_TRANS_DIMENSION, minecraft.world.getDimension().getType().getRegistryName().toString())
                        .put(PARAM_POS, null)
                        .put(PARAM_DIMENSION, DimensionType.OVERWORLD.getRegistryName().toString())
                        .build());

        lastDialedTransmitter = true;
        listDirty = 0;
    }

    private void requestReceivers() {
        // @todo 1.14 use UUID!!!
        RFToolsUtilityMessages.INSTANCE.sendToServer(new PacketGetReceivers(tileEntity.getPos(), minecraft.player.getUniqueID()));
    }

    private void requestTransmitters() {
        RFToolsUtilityMessages.INSTANCE.sendToServer(new PacketGetTransmitters(tileEntity.getPos()));
    }

    private void changeShowFavorite() {
        boolean fav = favoriteButton.getCurrentChoiceIndex() == 1;
        sendServerCommandTyped(RFToolsUtilityMessages.INSTANCE, DialingDeviceTileEntity.CMD_SHOWFAVORITE,
                TypedMap.builder()
                        .put(PARAM_FAVORITE, fav)
                        .build());
        listDirty = 0;
        transmitterList.selected(-1);
        receiverList.selected(-1);
    }

    private void changeFavorite() {
        int receiverSelected = receiverList.getSelected();
        TeleportDestinationClientInfo destination = getSelectedReceiver(receiverSelected);
        if (destination == null) {
            return;
        }
        boolean favorite = destination.isFavorite();
        destination.setFavorite(!favorite);
        sendServerCommandTyped(RFToolsUtilityMessages.INSTANCE, DialingDeviceTileEntity.CMD_FAVORITE,
                TypedMap.builder()
                        .put(PARAM_PLAYER, minecraft.player.getName().getFormattedText())
                        .put(PARAM_POS, destination.getCoordinate())
                        .put(PARAM_DIMENSION, destination.getDimension().getRegistryName().toString())
                        .put(PARAM_FAVORITE, !favorite)
                        .build());
        listDirty = 0;
    }

    private void populateReceivers() {
        List<TeleportDestinationClientInfo> newReceivers = fromServer_receivers;
        if (newReceivers == null) {
            return;
        }

        boolean newReceiversFiltered = favoriteButton.getCurrentChoiceIndex() == 1;

        if (newReceivers.equals(receivers) && newReceiversFiltered == receiversFiltered) {
            return;
        }

        receiversFiltered = newReceiversFiltered;
        if (receiversFiltered) {
            receivers = new ArrayList<>();
            // We only show favorited receivers. Remove the rest.
            for (TeleportDestinationClientInfo receiver : newReceivers) {
                if (receiver.isFavorite()) {
                    receivers.add(receiver);
                }
            }
        } else {
            // Show all receivers.
            receivers = new ArrayList<>(newReceivers);
        }

        receiverList.removeChildren();

        for (TeleportDestinationClientInfo destination : receivers) {
            BlockPos coordinate = destination.getCoordinate();

            String dimName = destination.getDimensionName();
            if (dimName == null || dimName.trim().isEmpty()) {
                dimName = "Id " + destination.getDimension();
            }

            boolean favorite = destination.isFavorite();
            Panel panel = horizontal(3, 1);
            panel.children(label(destination.getName()).color(StyleConfig.colorTextInListNormal).horizontalAlignment(HorizontalAlignment.ALIGN_LEFT).desiredWidth(96).
                    tooltips("The name of the", "destination receiver:", destination.getName() + " (" + BlockPosTools.toString(coordinate) + ")"));
            panel.children(label(dimName).color(StyleConfig.colorTextInListNormal).horizontalAlignment(HorizontalAlignment.ALIGN_LEFT)
                    .dynamic(true).tooltips("The name of the", "destination dimension:", dimName)
                    .desiredWidth(110));
            ImageChoiceLabel choiceLabel = new ImageChoiceLabel().event((newChoice) -> changeFavorite()).desiredWidth(10);
            choiceLabel.choice("No", "Not favorited", guielements, 131, 19);
            choiceLabel.choice("Yes", "Favorited", guielements, 115, 19);
            choiceLabel.setCurrentChoice(favorite ? 1 : 0);
            panel.children(choiceLabel);
            receiverList.children(panel);
        }
    }

    private TeleportDestination getSelectedTransmitterDestination() {
        int transmitterSelected = transmitterList.getSelected();
        TransmitterInfo transmitterInfo = getSelectedTransmitter(transmitterSelected);
        if (transmitterInfo == null) {
            return null;
        }
        TeleportDestination destination = transmitterInfo.getTeleportDestination();
        if (destination.isValid()) {
            return destination;
        } else {
            return null;
        }
    }

    private void populateTransmitters() {
        List<TransmitterInfo> newTransmitters = fromServer_transmitters;
        if (newTransmitters == null) {
            return;
        }
        if (newTransmitters.equals(transmitters)) {
            return;
        }

        transmitters = new ArrayList<>(newTransmitters);
        transmitterList.removeChildren();

        for (TransmitterInfo transmitterInfo : transmitters) {
            BlockPos coordinate = transmitterInfo.getCoordinate();
            TeleportDestination destination = transmitterInfo.getTeleportDestination();

            Panel panel = horizontal(3, DEFAULT_SPACING)
                    .children(
                            label(transmitterInfo.getName()).color(StyleConfig.colorTextInListNormal).horizontalAlignment(HorizontalAlignment.ALIGN_LEFT).desiredWidth(102),
                            label(BlockPosTools.toString(coordinate)).color(StyleConfig.colorTextInListNormal).dynamic(true).desiredWidth(90),
                            new ImageLabel().image(guielements, destination.isValid() ? 80 : 96, 0).desiredWidth(16));
            transmitterList.children(panel);
        }

        if (transmitterList.getChildCount() == 1) {
            transmitterList.selected(0);
        }
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        requestListsIfNeeded();

        populateReceivers();
        populateTransmitters();

        if (lastDialedTransmitter) {
            showStatus(fromServer_dialResult);
        } else if (lastCheckedReceiver) {
            showStatus(fromServer_receiverStatus);
        } else {
            statusLabel.text("");
            statusLabel.color(0xff000000);
            statusLabel.filledBackground(-1);
        }

        enableButtons();

        drawWindow();
        tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> {
            energyBar.maxValue(((GenericEnergyStorage) e).getCapacity());
            energyBar.value(((GenericEnergyStorage) e).getEnergy());
        });
    }

    private void requestListsIfNeeded() {
        if (fromServer_receivers != null && fromServer_transmitters != null) {
            return;
        }
        listDirty--;
        if (listDirty <= 0) {
            requestReceivers();
            requestTransmitters();
            listDirty = 10;
        }
    }

    private String calculateDistance(int transmitterSelected, int receiverSelected) {
        TransmitterInfo transmitterInfo = getSelectedTransmitter(transmitterSelected);
        if (transmitterInfo == null) {
            return "?";
        }
        TeleportDestination teleportDestination = getSelectedReceiver(receiverSelected);
        if (teleportDestination == null) {
            return "?";
        }

        return DialingDeviceTileEntity.calculateDistance(minecraft.world, transmitterInfo, teleportDestination);
    }

    private TransmitterInfo getSelectedTransmitter(int transmitterSelected) {
        if (transmitterSelected == -1) {
            return null;
        }
        if (transmitterSelected >= transmitters.size()) {
            return null;
        }
        return transmitters.get(transmitterSelected);
    }

    private void enableButtons() {
        int transmitterSelected = transmitterList.getSelected();
        if (transmitters == null || transmitterSelected >= transmitters.size()) {
            transmitterSelected = -1;
            transmitterList.selected(-1);
        }
        int receiverSelected = receiverList.getSelected();
        if (receivers == null || receiverSelected >= receivers.size()) {
            receiverSelected = -1;
            receiverList.selected(-1);
        }

        if (transmitterSelected != -1 && receiverSelected != -1) {
            dialButton.enabled(true);
            dialOnceButton.enabled(true);
            String distance = calculateDistance(transmitterSelected, receiverSelected);
            dialButton.tooltips("Start a connection between", "the selected transmitter", "and the selected receiver.", "Distance: " + distance);
            dialOnceButton.tooltips("Dial a connection for a", "single teleport.", "Distance: " + distance);
        } else {
            dialButton.enabled(false);
            dialOnceButton.enabled(false);
            dialButton.tooltips("Start a connection between", "the selected transmitter", "and the selected receiver");
            dialOnceButton.tooltips("Dial a connection for a", "single teleport");
        }
        if (transmitterSelected != -1) {
            TeleportDestination destination = getSelectedTransmitterDestination();
            interruptButton.enabled(destination != null);
        } else {
            interruptButton.enabled(false);
        }
        if (receiverSelected != -1) {
            statusButton.enabled(analyzerAvailable);
        } else {
            statusButton.enabled(false);
        }
    }
}
