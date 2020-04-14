package mcjty.rftoolsutility.modules.teleporter.client;

import mcjty.lib.base.StyleConfig;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.VerticalLayout;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.teleporter.blocks.MatterReceiverTileEntity;
import mcjty.rftoolsutility.modules.teleporter.network.PacketGetPlayers;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraftforge.energy.CapabilityEnergy;

import java.util.*;

import static mcjty.lib.gui.widgets.Widgets.*;
import static mcjty.rftoolsutility.modules.teleporter.blocks.MatterReceiverTileEntity.PARAM_PLAYER;

public class GuiMatterReceiver extends GenericGuiContainer<MatterReceiverTileEntity, GenericContainer> {
    public static final int MATTER_WIDTH = 180;
    public static final int MATTER_HEIGHT = 160;
    public static final String ACCESS_PRIVATE = "Private";
    public static final String ACCESS_PUBLIC = "Public";

    private EnergyBar energyBar;
    private ChoiceLabel privateSetting;
    private WidgetList allowedPlayers;
    private Button addButton;
    private Button delButton;
    private TextField nameField;

    // A copy of the players we're currently showing.
    private List<String> players = null;
    private int listDirty = 0;

    private static Set<String> fromServer_allowedPlayers = new HashSet<>();
    public static void storeAllowedPlayersForClient(List<String> players) {
        fromServer_allowedPlayers = new HashSet<>(players);
    }


    public GuiMatterReceiver(MatterReceiverTileEntity matterReceiverTileEntity, GenericContainer container, PlayerInventory inventory) {
        super(RFToolsUtility.instance, matterReceiverTileEntity, container, inventory, /*@todo 1.14 GuiProxy.GUI_MANUAL_MAIN*/0, "tpreceiver");

        xSize = MATTER_WIDTH;
        ySize = MATTER_HEIGHT;
    }

    @Override
    public void init() {
        super.init();

        energyBar = new EnergyBar().filledRectThickness(1).horizontal().desiredHeight(12).desiredWidth(80).showText(false);

        TextField textField = new TextField()
                .name("name")
                .tooltips("Use this name to", "identify this receiver", "in the dialer");
        Panel namePanel = horizontal().children(label("Name:"), textField).desiredHeight(16);

        privateSetting = new ChoiceLabel().choices(ACCESS_PUBLIC, ACCESS_PRIVATE).desiredHeight(14).desiredWidth(60).
                name("private").
                choiceTooltip(ACCESS_PUBLIC, "Everyone can dial to this receiver").
                choiceTooltip(ACCESS_PRIVATE, "Only people in the access list below", "can dial to this receiver");
        Panel privatePanel = horizontal().children(label("Access:"), privateSetting).desiredHeight(16);

        allowedPlayers = new WidgetList().name("allowedplayers");
        Slider allowedPlayerSlider = new Slider().desiredWidth(10).vertical().scrollableName("allowedplayers");
        Panel allowedPlayersPanel = horizontal(3, 1).children(allowedPlayers, allowedPlayerSlider)
                .filledBackground(0xff9e9e9e);

        nameField = new TextField();
        addButton = button("Add").channel("addplayer").desiredHeight(13).desiredWidth(34).tooltips("Add a player to the access list");
        delButton = button("Del").channel("delplayer").desiredHeight(13).desiredWidth(34).tooltips("Remove the selected player", "from the access list");
        Panel buttonPanel = horizontal().children(nameField, addButton, delButton).desiredHeight(16);

        Panel toplevel = new Panel().filledRectThickness(2).layout(new VerticalLayout().setHorizontalMargin(3).setVerticalMargin(3).setSpacing(1)).
                children(energyBar, namePanel, privatePanel, allowedPlayersPanel, buttonPanel);
        toplevel.bounds(guiLeft, guiTop, MATTER_WIDTH, MATTER_HEIGHT);
        window = new Window(this, toplevel);
        minecraft.keyboardListener.enableRepeatEvents(true);

        listDirty = 0;
        requestPlayers();

        window.bind(RFToolsUtilityMessages.INSTANCE, "name", tileEntity, MatterReceiverTileEntity.VALUE_NAME.getName());
        window.bind(RFToolsUtilityMessages.INSTANCE, "private", tileEntity, MatterReceiverTileEntity.VALUE_PRIVATE.getName());
        window.event("addplayer", (source, params) -> addPlayer());
        window.event("delplayer", (source, params) -> delPlayer());
    }

    private void addPlayer() {
        sendServerCommandTyped(RFToolsUtilityMessages.INSTANCE, MatterReceiverTileEntity.CMD_ADDPLAYER,
                TypedMap.builder()
                        .put(PARAM_PLAYER, nameField.getText())
                        .build());
        listDirty = 0;
    }

    private void delPlayer() {
        sendServerCommandTyped(RFToolsUtilityMessages.INSTANCE, MatterReceiverTileEntity.CMD_DELPLAYER,
                TypedMap.builder()
                        .put(PARAM_PLAYER, nameField.getText())
                        .build());
        listDirty = 0;
    }


    private void requestPlayers() {
        RFToolsUtilityMessages.INSTANCE.sendToServer(new PacketGetPlayers(tileEntity.getPos(), MatterReceiverTileEntity.CMD_GETPLAYERS, MatterReceiverTileEntity.CLIENTCMD_GETPLAYERS));
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

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        requestListsIfNeeded();
        populatePlayers();
        enableButtons();

        drawWindow();
        tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> {
            energyBar.maxValue(((GenericEnergyStorage)e).getCapacity());
            energyBar.value(((GenericEnergyStorage)e).getEnergy());
        });
    }

    private void enableButtons() {
        boolean isPrivate = ACCESS_PRIVATE.equals(privateSetting.getCurrentChoice());
        allowedPlayers.enabled(isPrivate);
        nameField.enabled(isPrivate);

        int isPlayerSelected = allowedPlayers.getSelected();
        delButton.enabled(isPrivate && (isPlayerSelected != -1));
        String name = nameField.getText();
        addButton.enabled(isPrivate && name != null && !name.isEmpty());
    }
}
