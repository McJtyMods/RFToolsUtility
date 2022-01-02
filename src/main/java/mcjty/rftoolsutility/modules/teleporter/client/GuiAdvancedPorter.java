package mcjty.rftoolsutility.modules.teleporter.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.gui.GuiItemScreen;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsutility.modules.teleporter.items.porter.AdvancedChargedPorterItem;
import mcjty.rftoolsutility.modules.teleporter.items.porter.ChargedPorterItem;
import mcjty.rftoolsutility.setup.CommandHandler;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.client.Minecraft;

import javax.annotation.Nonnull;

import static mcjty.lib.gui.layout.AbstractLayout.DEFAULT_VERTICAL_MARGIN;
import static mcjty.lib.gui.widgets.Widgets.*;

public class GuiAdvancedPorter extends GuiItemScreen {

    private static final int xSize = 340;
    private static final int ySize = 136;

    private Panel[] panels = new Panel[AdvancedChargedPorterItem.MAXTARGETS];
    private TextField[] destinations = new TextField[AdvancedChargedPorterItem.MAXTARGETS];

    private static int target = -1;
    private static int[] targets = new int[AdvancedChargedPorterItem.MAXTARGETS];
    private static String[] names = new String[AdvancedChargedPorterItem.MAXTARGETS];

    public GuiAdvancedPorter() {
        super(RFToolsUtilityMessages.INSTANCE, xSize, ySize, ChargedPorterItem.MANUAL);
    }

    public static void setInfo(int target, int[] targets, String[] names) {
        GuiAdvancedPorter.target = target;
        GuiAdvancedPorter.targets = targets;
        GuiAdvancedPorter.names = names;
    }

    @Override
    public void init() {
        super.init();

        int k = (this.width - xSize) / 2;
        int l = (this.height - ySize) / 2;

        Panel toplevel = vertical(DEFAULT_VERTICAL_MARGIN, 0).filledRectThickness(2);

        for (int i = 0; i < AdvancedChargedPorterItem.MAXTARGETS; i++) {
            destinations[i] = new TextField();
            panels[i] = createPanel(destinations[i], i);
            toplevel.children(panels[i]);
        }

        toplevel.bounds(k, l, xSize, ySize);

        window = new Window(this, toplevel);

        updateInfoFromServer();
    }

    private Panel createPanel(final TextField destination, final int i) {
        return horizontal()
                .desiredHeight(16)
                .children(destination,
                        button("Set").desiredWidth(30).desiredHeight(16).event(() -> {
                            if (targets[i] != -1) {
                                RFToolsUtilityMessages.sendToServer(CommandHandler.CMD_SET_TARGET, TypedMap.builder().put(CommandHandler.PARAM_TARGET, targets[i]));
                                target = targets[i];
                            }
                        }),
                        button("Clear").desiredWidth(40).desiredHeight(16).event(() -> {
                            if (targets[i] != -1 && targets[i] == target) {
                                target = -1;
                            }
                            RFToolsUtilityMessages.sendToServer(CommandHandler.CMD_CLEAR_TARGET, TypedMap.builder().put(CommandHandler.PARAM_TARGET, i));
                            targets[i] = -1;
                        }));
    }

    private void updateInfoFromServer() {
        RFToolsUtilityMessages.sendToServer(CommandHandler.CMD_GET_TARGETS);
    }

    private void setTarget(int i) {
        panels[i].filledBackground(-1);
        if (targets[i] == -1) {
            destinations[i].text("No target set");
        } else {
            destinations[i].text(targets[i] + ": " + names[i]);
            if (targets[i] == target) {
                panels[i].filledBackground(0xffeedd33);
            }
        }
    }

    @Override
    public void render(@Nonnull PoseStack matrixStack, int xSize_lo, int ySize_lo, float par3) {
        super.render(matrixStack, xSize_lo, ySize_lo, par3);

        for (int i = 0; i < AdvancedChargedPorterItem.MAXTARGETS; i++) {
            setTarget(i);
        }

        drawWindow(matrixStack);
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new GuiAdvancedPorter());
    }

}
