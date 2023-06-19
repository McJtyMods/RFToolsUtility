package mcjty.rftoolsutility.modules.teleporter.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.VerticalLayout;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.network.PacketGetListFromServer;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.ClientTools;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import mcjty.rftoolsutility.modules.teleporter.blocks.MatterReceiverTileEntity;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;
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
    private TextField playerNameField;

    // A copy of the players we're currently showing.
    private List<String> players = null;
    private int listDirty = 0;

    private static Set<String> fromServer_allowedPlayers = new HashSet<>();
    public static void storeAllowedPlayersForClient(List<String> players) {
        fromServer_allowedPlayers = new HashSet<>(players);
    }


    public GuiMatterReceiver(MatterReceiverTileEntity matterReceiverTileEntity, GenericContainer container, Inventory inventory) {
        super(matterReceiverTileEntity, container, inventory, TeleporterModule.MATTER_RECEIVER.get().getManualEntry());

        imageWidth = MATTER_WIDTH;
        imageHeight = MATTER_HEIGHT;
    }

    public static void register() {
        register(TeleporterModule.CONTAINER_MATTER_RECEIVER.get(), GuiMatterReceiver::new);
    }

    @Override
    public void init() {
        super.init();

        energyBar = new EnergyBar().filledRectThickness(1).horizontal().desiredHeight(12).desiredWidth(80).showText(false);

        TextField nameField = new TextField()
                .name("name")
                .tooltips("Use this name to", "identify this receiver", "in the dialer");
        Panel namePanel = horizontal().children(label("Name:"), nameField).desiredHeight(16);

        privateSetting = new ChoiceLabel().choices(ACCESS_PUBLIC, ACCESS_PRIVATE).desiredHeight(14).desiredWidth(60).
                name("private").
                choiceTooltip(ACCESS_PUBLIC, "Everyone can dial to this receiver").
                choiceTooltip(ACCESS_PRIVATE, "Only people in the access list below", "can dial to this receiver");
        Panel privatePanel = horizontal().children(label("Access:"), privateSetting).desiredHeight(16);

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
        ClientTools.enableKeyboardRepeat();

        listDirty = 0;
        requestPlayers();

        window.bind(RFToolsUtilityMessages.INSTANCE, "name", tileEntity, "name");
        window.bind(RFToolsUtilityMessages.INSTANCE, "private", tileEntity, "private");
        window.event("addplayer", (source, params) -> addPlayer());
        window.event("delplayer", (source, params) -> delPlayer());
    }

    private void addPlayer() {
        sendServerCommandTyped(RFToolsUtilityMessages.INSTANCE, MatterReceiverTileEntity.CMD_ADDPLAYER,
                TypedMap.builder()
                        .put(PARAM_PLAYER, playerNameField.getText())
                        .build());
        listDirty = 0;
    }

    private void delPlayer() {
        String name = playerNameField.getText();
        int selected = allowedPlayers.getSelected();
        if (selected >= 0 && selected < players.size()) {
            name = players.get(selected);
        }
        sendServerCommandTyped(RFToolsUtilityMessages.INSTANCE, MatterReceiverTileEntity.CMD_DELPLAYER,
                TypedMap.builder()
                        .put(PARAM_PLAYER, name)
                        .build());
        listDirty = 0;
    }


    private void requestPlayers() {
        RFToolsUtilityMessages.INSTANCE.sendToServer(new PacketGetListFromServer(tileEntity.getBlockPos(), MatterReceiverTileEntity.CMD_GETPLAYERS.name()));
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
    protected void renderBg(@Nonnull GuiGraphics graphics, float v, int i, int i2) {
        requestListsIfNeeded();
        populatePlayers();
        enableButtons();

        updateFields();
        drawWindow(graphics);
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
