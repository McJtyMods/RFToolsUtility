package mcjty.rftoolsutility.modules.logic.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.ChoiceLabel;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.blocks.ThreeLogicTileEntity;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

import static mcjty.rftoolsutility.modules.logic.blocks.ThreeLogicTileEntity.PARAM_INDEX;
import static mcjty.rftoolsutility.modules.logic.blocks.ThreeLogicTileEntity.PARAM_STATE;

public class GuiThreeLogic extends GenericGuiContainer<ThreeLogicTileEntity, GenericContainer> {

    public GuiThreeLogic(ThreeLogicTileEntity te, GenericContainer container, PlayerInventory inventory) {
        super(RFToolsUtility.instance, te, container, inventory,  /*@todo 1.15 */ManualEntry.EMPTY);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsUtilityMessages.INSTANCE, new ResourceLocation(RFToolsUtility.MODID, "gui/threelogic.gui"));
        super.init();

        initializeFields();
        setupEvents();
    }

    private void setupEvents() {
        window.event("choice", (source, params) -> {
            String name = source.getName();
            int i = Integer.parseInt(name.substring(name.length()-1));
            String current = params.get(ChoiceLabel.PARAM_CHOICE);
            int st = "On".equals(current) ? 1 : "Off".equals(current) ? 0 : -1;
            sendServerCommandTyped(RFToolsUtilityMessages.INSTANCE, ThreeLogicTileEntity.CMD_SETSTATE,
                    TypedMap.builder()
                        .put(PARAM_INDEX, i)
                        .put(PARAM_STATE, st)
                        .build());
        });
    }

    private void initializeFields() {
        for (int i = 0 ; i < 8 ; i++) {
            ChoiceLabel tl = window.findChild("choice" + i);
            int state = tileEntity.getState(i);
            switch (state) {
                case 0: tl.choice("Off"); break;
                case 1: tl.choice("On"); break;
                default: tl.choice("Keep"); break;
            }
        }
    }
}
