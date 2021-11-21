package mcjty.rftoolsutility.modules.teleporter.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.VerticalLayout;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.network.PacketGetListFromServer;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import mcjty.rftoolsutility.modules.teleporter.blocks.MatterTransmitterTileEntity;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.entity.player.PlayerInventory;

import javax.annotation.Nonnull;
import java.util.*;

import static mcjty.lib.gui.widgets.Widgets.*;
import static mcjty.rftoolsutility.modules.teleporter.blocks.MatterTransmitterTileEntity.PARAM_PLAYER;

public class GuiMatterTransmitter extends GenericGuiContainer<MatterTransmitterTileEntity, GenericContainer> {
    public static final int MATTER_WIDTH = 180;
    public static final int MATTER_HEIGHT = 160;
    public static final String ACCESS_PRIVATE = "Private";
    public static final String ACCESS_PUBLIC = "Public";

    private EnergyBar energyBar;
    private ChoiceLabel privateSetting;
    private WidgetList allowedPlayers;
    private Button addButton;
    private Button delButton;
    private TextField playerNameField;

    // A copy of the players we're currently showing.
    private List<String> players = null;
    private int listDirty = 0;

    private static Set<String> fromServer_allowedPlayers = new HashSet<>();

    public static void storeAllowedPlayersForClient(List<String> players) {
        fromServer_allowedPlayers = new HashSet<>(players);
    }


    public GuiMatterTransmitter(MatterTransmitterTileEntity te, GenericContainer container, PlayerInventory inventory) {
        super(te, container, inventory, TeleporterModule.MATTER_TRANSMITTER.get().getManualEntry());

        imageWidth = MATTER_WIDTH;
        imageHeight = MATTER_HEIGHT;
    }

    public static void register() {
        register(TeleporterModule.CONTAINER_MATTER_TRANSMITTER.get(), GuiMatterTransmitter::new);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void init() {
        super.init();

        energyBar = new EnergyBar().filledRectThickness(1).horizontal().desiredHeight(12).desiredWidth(80).showText(false);

        TextField nameField = new TextField()
                .name("name")
                .tooltips("Use this name to", "identify this transmitter", "in the dialer");
        Panel namePanel = horizontal().children(label("Name:"), nameField).desiredHeight(16);

        privateSetting = new ChoiceLabel()
                .name("private")
                .choices(ACCESS_PUBLIC, ACCESS_PRIVATE).desiredHeight(14).desiredWidth(60).
                        choiceTooltip(ACCESS_PUBLIC, "Everyone can access this transmitter", "and change the dialing destination").
                        choiceTooltip(ACCESS_PRIVATE, "Only people in the access list below", "can access this transmitter");
        ToggleButton beamToggle = new ToggleButton()
                .name("beam")
                .text("Hide").checkMarker(true).desiredHeight(14).desiredWidth(49).tooltips("Hide the teleportation beam");
        Panel privatePanel = horizontal().children(label("Access:"), privateSetting, beamToggle).desiredHeight(16);

        allowedPlayers = new WidgetList().name("allowedplayers");
        Slider allowedPlayerSlider = new Slider().desiredWidth(10).vertical().scrollableName("allowedplayers");
        Panel allowedPlayersPanel = horizontal(3, 1).children(allowedPlayers, allowedPlayerSlider)
                .filledBackground(0xff9e9e9e);

        playerNameField = new TextField();
        addButton = button("Add").channel("addplayer").desiredHeight(13).desiredWidth(34).tooltips("Add a player to the access list");
        delButton = button("Del").channel("delplayer").desiredHeight(13).desiredWidth(34).tooltips("Remove the selected player", "from the access list");
        Panel buttonPanel = horizontal().children(playerNameField, addButton, delButton).desiredHeight(16);

        Panel toplevel = new Panel().filledRectThickness(2).layout(new VerticalLayout().setHorizontalMargin(3).setVerticalMargin(3).setSpacing(1)).
                children(energyBar, namePanel, privatePanel, allowedPlayersPanel, buttonPanel);
        toplevel.bounds(leftPos, topPos, MATTER_WIDTH, MATTER_HEIGHT);
        window = new Window(this, toplevel);

        minecraft.keyboardHandler.setSendRepeatsToGui(true);

        listDirty = 0;
        requestPlayers();

        window.bind(RFToolsUtilityMessages.INSTANCE, "name", tileEntity, MatterTransmitterTileEntity.VALUE_NAME.getKey().getName());
        window.bind(RFToolsUtilityMessages.INSTANCE, "private", tileEntity, MatterTransmitterTileEntity.VALUE_PRIVATE.getKey().getName());
        window.bind(RFToolsUtilityMessages.INSTANCE, "beam", tileEntity, MatterTransmitterTileEntity.VALUE_BEAM.getKey().getName());
        window.event("addplayer", (source, params) -> addPlayer());
        window.event("delplayer", (source, params) -> delPlayer());
    }

    private void addPlayer() {
        sendServerCommandTyped(RFToolsUtilityMessages.INSTANCE, MatterTransmitterTileEntity.CMD_ADDPLAYER,
                TypedMap.builder()
                        .put(PARAM_PLAYER, playerNameField.getText())
                        .build());
        listDirty = 0;
    }

    private void delPlayer() {
        sendServerCommandTyped(RFToolsUtilityMessages.INSTANCE, MatterTransmitterTileEntity.CMD_DELPLAYER,
                TypedMap.builder()
                        .put(PARAM_PLAYER, playerNameField.getText())
                        .build());
        listDirty = 0;
    }


    private void requestPlayers() {
        RFToolsUtilityMessages.INSTANCE.sendToServer(new PacketGetListFromServer(tileEntity.getBlockPos(), MatterTransmitterTileEntity.CMD_GETPLAYERS.getName()));
    }

    private void populatePlayers() {
        List<String> newPlayers = new ArrayList<>(fromServer_allowedPlayers);
        Collections.sort(newPlayers);
        if (newPlayers.equals(players)) {
            return;
        }

        players = new ArrayList<>(newPlayers);
        allowedPlayers.removeChildren();
        for (String player : players) {
            allowedPlayers.children(label(player).color(StyleConfig.colorTextInListNormal).horizontalAlignment(HorizontalAlignment.ALIGN_LEFT));
        }
    }


    private void requestListsIfNeeded() {
        listDirty--;
        if (listDirty <= 0) {
            requestPlayers();
            listDirty = 20;
        }
    }

    private void updateFields() {
        if (window == null) {
            return;
        }
        updateEnergyBar(energyBar);
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack matrixStack, float v, int i, int i2) {
        requestListsIfNeeded();
        populatePlayers();
        enableButtons();

        updateFields();
        drawWindow(matrixStack);
    }

    private void enableButtons() {
        boolean isPrivate = ACCESS_PRIVATE.equals(privateSetting.getCurrentChoice());
        allowedPlayers.enabled(isPrivate);
        playerNameField.enabled(isPrivate);

        int isPlayerSelected = allowedPlayers.getSelected();
        delButton.enabled(isPrivate && (isPlayerSelected != -1));
        String name = playerNameField.getText();
        addButton.enabled(isPrivate && name != null && !name.isEmpty());
    }
}
