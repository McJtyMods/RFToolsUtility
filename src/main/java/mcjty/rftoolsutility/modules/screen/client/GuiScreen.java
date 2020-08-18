package mcjty.rftoolsutility.modules.screen.client;

import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.widgets.ChoiceLabel;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.ToggleButton;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleProvider;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenBlock;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenContainer;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import mcjty.rftoolsutility.modules.screen.modulesclient.helper.ScreenModuleGuiBuilder;
import mcjty.rftoolsutility.modules.screen.network.PacketModuleUpdate;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import static mcjty.lib.gui.widgets.Widgets.label;
import static mcjty.lib.gui.widgets.Widgets.positional;
import static mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity.PARAM_TRUETYPE;

public class GuiScreen  extends GenericGuiContainer<ScreenTileEntity, ScreenContainer> {
    public static final int SCREEN_WIDTH = 256;
    public static final int SCREEN_HEIGHT = 224;

    private static final ResourceLocation BACKGROUND = new ResourceLocation(RFToolsUtility.MODID, "textures/gui/screen.png");

    private Panel toplevel;
    private ToggleButton toggleButtons[] = new ToggleButton[ScreenContainer.SCREEN_MODULES];
    private Panel modulePanels[] = new Panel[ScreenContainer.SCREEN_MODULES];
    private IClientScreenModule<?>[] clientScreenModules = new IClientScreenModule<?>[ScreenContainer.SCREEN_MODULES];

    private ChoiceLabel trueType;

    private int selected = -1;

    public GuiScreen(ScreenTileEntity screenTileEntity, ScreenContainer container, PlayerInventory inventory) {
        super(RFToolsUtility.instance, screenTileEntity, container, inventory, ManualHelper.create("rftoolsutility:machines/screen"));

        xSize = SCREEN_WIDTH;
        ySize = SCREEN_HEIGHT;
    }

    @Override
    public void init() {
        super.init();

        toplevel = positional().background(BACKGROUND);

        for (int i = 0; i < ScreenContainer.SCREEN_MODULES ; i++) {
            toggleButtons[i] = new ToggleButton().hint(30, 7 + i * 18 + 1, 40, 16).enabled(false).tooltips("Open the gui for this", "module");
            final int finalI = i;
            toggleButtons[i].event(() -> selectPanel(finalI));
            toplevel.children(toggleButtons[i]);
            modulePanels[i] = null;
            clientScreenModules[i] = null;
        }

        ToggleButton bright = new ToggleButton()
                .name("bright")
                .text("Bright")
                .checkMarker(true)
                .tooltips("Toggle full brightness")
                .hint(85, 123, 55, 14);
//        .setLayoutHint(7, 208, 63, 14);
        toplevel.children(bright, label(85+50+9, 123, 30, 14, "Font:").horizontalAlignment(HorizontalAlignment.ALIGN_RIGHT));
        trueType = new ChoiceLabel()
                .choices("Default", "Truetype", "Vanilla")
                .tooltips("Set truetype font mode", "for the screen")
                .hint(85+50+14+30, 123, 68, 14);
        int trueTypeMode = tileEntity.getTrueTypeMode();
        trueType.choice(trueTypeMode == 0 ? "Default" : (trueTypeMode == -1 ? "Vanilla" : "Truetype"));
        trueType.event((b) -> sendServerCommandTyped(RFToolsUtilityMessages.INSTANCE, ScreenTileEntity.CMD_SETTRUETYPE,
                TypedMap.builder().put(PARAM_TRUETYPE, getCurrentTruetypeChoice()).build()));
        toplevel.children(trueType);

        toplevel.bounds(guiLeft, guiTop, xSize, ySize);

        window = new Window(this, toplevel);

        window.bind(RFToolsUtilityMessages.INSTANCE, "bright", tileEntity, ScreenTileEntity.VALUE_BRIGHT.getName());

        minecraft.keyboardListener.enableRepeatEvents(true);

        selected = -1;
    }

    private int getCurrentTruetypeChoice() {
        String c = trueType.getCurrentChoice();
        if ("Default".equals(c)) {
            return 0;
        }
        if ("Truetype".equals(c)) {
            return 1;
        }
        return -1;
    }

    private void selectPanel(int i) {
        if (toggleButtons[i].isPressed()) {
            selected = i;
        } else {
            selected = -1;
        }
    }

    private void refreshButtons() {
        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            for (int i = 0; i < ScreenContainer.SCREEN_MODULES; i++) {
                final ItemStack slot = h.getStackInSlot(i);
                if (!slot.isEmpty() && ScreenBlock.hasModuleProvider(slot)) {
                    int finalI = i;
                    ScreenBlock.getModuleProvider(slot).ifPresent(moduleProvider -> {
                        Class<? extends IClientScreenModule<?>> clientScreenModuleClass = moduleProvider.getClientScreenModule();
                        if (!clientScreenModuleClass.isInstance(clientScreenModules[finalI])) {
                            installModuleGui(finalI, slot, moduleProvider, clientScreenModuleClass);
                        }
                    });
                } else {
                    uninstallModuleGui(i);
                }
                if (modulePanels[i] != null) {
                    modulePanels[i].visible(selected == i);
                    toggleButtons[i].pressed(selected == i);
                }
            }
        });
    }

    private void uninstallModuleGui(int i) {
        toggleButtons[i].enabled(false);
        toggleButtons[i].pressed(false);
        toggleButtons[i].text("");
        clientScreenModules[i] = null;
        toplevel.removeChild(modulePanels[i]);
        modulePanels[i] = null;
        if (selected == i) {
            selected = -1;
        }
    }

    private void installModuleGui(int i, ItemStack slot, IModuleProvider moduleProvider, Class<? extends IClientScreenModule<?>> clientScreenModuleClass) {
        toggleButtons[i].enabled(true);
        toplevel.removeChild(modulePanels[i]);
        try {
            IClientScreenModule<?> clientScreenModule = clientScreenModuleClass.newInstance();
            clientScreenModules[i] = clientScreenModule;
        } catch (InstantiationException|IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        CompoundNBT tagCompound = slot.getTag();
        if (tagCompound == null) {
            tagCompound = new CompoundNBT();
        }

        final CompoundNBT finalTagCompound = tagCompound;
        ScreenModuleGuiBuilder guiBuilder = new ScreenModuleGuiBuilder(minecraft, this, tagCompound, () -> {
            slot.setTag(finalTagCompound);
            tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                ((IItemHandlerModifiable)h).setStackInSlot(i, slot);
            });
            RFToolsUtilityMessages.INSTANCE.sendToServer(new PacketModuleUpdate(tileEntity.getPos(), i, finalTagCompound));
        });
        moduleProvider.createGui(guiBuilder);
        modulePanels[i] = guiBuilder.build();
        modulePanels[i].hint(80, 8, 170, 114);
        modulePanels[i].filledRectThickness(-2).filledBackground(0xff8b8b8b);

        toplevel.children(modulePanels[i]);
        toggleButtons[i].text(moduleProvider.getModuleName());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        refreshButtons();
        drawWindow(xxx);
    }
}
