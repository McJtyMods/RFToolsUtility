package mcjty.rftoolsutility.modules.environmental.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.network.PacketGetListFromServer;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
import mcjty.rftoolsutility.modules.environmental.blocks.EnvironmentalControllerTileEntity;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static mcjty.rftoolsutility.modules.environmental.blocks.EnvironmentalControllerTileEntity.*;

public class GuiEnvironmentalController extends GenericGuiContainer<EnvironmentalControllerTileEntity, GenericContainer> {

    public static final String BLACKLIST = "BL";
    public static final String WHITELIST = "WL";
    public static final String HOSTILE = "Host";
    public static final String PASSIVE = "Pass";
    public static final String MOBS = "Mobs";
    public static final String ALL = "All";

    // A copy of the players we're currently showing.
    private List<String> players = null;
    private int listDirty = 0;
    private EnergyBar energyBar;

    private TextField minyTextField;
    private TextField maxyTextField;
    private TextField nameField;
    private WidgetList playersList;
    private ChoiceLabel modeLabel;

    public GuiEnvironmentalController(EnvironmentalControllerTileEntity te, GenericContainer container, Inventory inventory) {
        super(te, container, inventory, ManualHelper.create("rftoolsutility:machines/environmental"));
    }

    public static void register() {
        register(EnvironmentalModule.CONTAINER_ENVIRONENTAL_CONTROLLER.get(), GuiEnvironmentalController::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsUtilityMessages.INSTANCE, new ResourceLocation(RFToolsUtility.MODID, "gui/environmental.gui"));
        super.init();

        initializeFields();
        setupEvents();

        listDirty = 0;
        requestPlayers();
    }

    private void initializeFields() {
        energyBar = window.findChild("energybar");

        playersList = window.findChild("players");

        minyTextField = window.findChild("miny");
        maxyTextField = window.findChild("maxy");
        nameField = window.findChild("name");
        modeLabel = window.findChild("mode");
    }

    private void updateFields() {
        if (window == null) {
            return;
        }
        ((ImageChoiceLabel)window.findChild("redstone")).setCurrentChoice(tileEntity.getRSMode().ordinal());

        int r = tileEntity.getRadius();
        if (r < 5) {
            r = 5;
        } else if (r > 100) {
            r = 100;
        }
        ((ScrollableLabel)window.findChild("radius")).realValue(r);
        minyTextField.text(Integer.toString(tileEntity.getMiny()));
        maxyTextField.text(Integer.toString(tileEntity.getMaxy()));
        switch (tileEntity.getMode()) {
            case MODE_BLACKLIST -> modeLabel.choice(BLACKLIST);
            case MODE_WHITELIST -> modeLabel.choice(WHITELIST);
            case MODE_HOSTILE -> modeLabel.choice(HOSTILE);
            case MODE_PASSIVE -> modeLabel.choice(PASSIVE);
            case MODE_MOBS -> modeLabel.choice(MOBS);
            case MODE_ALL -> modeLabel.choice(ALL);
        }

        updateEnergyBar(energyBar);
    }

    private void setupEvents() {
        window.event("add", (source, params) -> addPlayer());
        window.event("del", (source, params) -> delPlayer());
        window.event("mode", (source, params) -> changeMode(params.get(ChoiceLabel.PARAM_CHOICE)));
        window.event("miny", (source, params) -> sendBounds(true));
        window.event("maxy", (source, params) -> sendBounds(false));
    }


    private void changeMode(String newAccess) {
        EnvironmentalControllerTileEntity.EnvironmentalMode newmode;
        if (ALL.equals(newAccess)) {
            newmode = EnvironmentalControllerTileEntity.EnvironmentalMode.MODE_ALL;
        } else if (BLACKLIST.equals(newAccess)) {
            newmode = EnvironmentalControllerTileEntity.EnvironmentalMode.MODE_BLACKLIST;
        } else if (WHITELIST.equals(newAccess)) {
            newmode = EnvironmentalControllerTileEntity.EnvironmentalMode.MODE_WHITELIST;
        } else if (MOBS.equals(newAccess)) {
            newmode = EnvironmentalControllerTileEntity.EnvironmentalMode.MODE_MOBS;
        } else if (PASSIVE.equals(newAccess)) {
            newmode = EnvironmentalControllerTileEntity.EnvironmentalMode.MODE_PASSIVE;
        } else {
            newmode = EnvironmentalControllerTileEntity.EnvironmentalMode.MODE_HOSTILE;
        }
        sendServerCommandTyped(RFToolsUtilityMessages.INSTANCE, EnvironmentalControllerTileEntity.CMD_SETMODE,
            TypedMap.builder()
                    .put(PARAM_MODE, newmode.ordinal())
                    .build());
    }

    private void addPlayer() {
        sendServerCommandTyped(RFToolsUtilityMessages.INSTANCE, EnvironmentalControllerTileEntity.CMD_ADDPLAYER,
                TypedMap.builder().put(PARAM_NAME, nameField.getText()).build());
        listDirty = 0;
    }

    private void delPlayer() {
        sendServerCommandTyped(RFToolsUtilityMessages.INSTANCE, EnvironmentalControllerTileEntity.CMD_DELPLAYER,
                TypedMap.builder().put(PARAM_NAME, players.get(playersList.getSelected())).build());
        listDirty = 0;
    }

    private void requestPlayers() {
        RFToolsUtilityMessages.INSTANCE.sendToServer(new PacketGetListFromServer(tileEntity.getBlockPos(), EnvironmentalControllerTileEntity.CMD_GETPLAYERS.name()));
    }

    private void populatePlayers() {
        players = new ArrayList<>(tileEntity.players);
        players.sort(null);
        playersList.removeChildren();
        for (String player : players) {
            playersList.children(new Label().text(player).color(StyleConfig.colorTextInListNormal).horizontalAlignment(HorizontalAlignment.ALIGN_LEFT));
        }
    }


    private void requestListsIfNeeded() {
        listDirty--;
        if (listDirty <= 0) {
            requestPlayers();
            listDirty = 20;
        }
    }

    private void sendBounds(boolean minchanged) {
        int miny = Integer.MIN_VALUE;
        int maxy = Integer.MIN_VALUE;
        try {
            miny = Integer.parseInt(minyTextField.getText());
        } catch (NumberFormatException ignored) {
        }
        try {
            maxy = Integer.parseInt(maxyTextField.getText());
        } catch (NumberFormatException ignored) {
        }
        sendServerCommandTyped(RFToolsUtilityMessages.INSTANCE, EnvironmentalControllerTileEntity.CMD_SETBOUNDS,
                TypedMap.builder()
                        .put(PARAM_MIN, miny)
                        .put(PARAM_MAX, maxy)
                        .build());
    }

    @Override
    protected void renderBg(@Nonnull PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        updateFields();
        requestListsIfNeeded();
        populatePlayers();
        enableButtons();

        drawWindow(matrixStack);
    }

    private void enableButtons() {
        window.setFlag("selected", playersList.getSelected() != -1);
        String name = nameField.getText();
        window.setFlag("name", name != null && !name.isEmpty());
    }
}
