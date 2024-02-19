package mcjty.rftoolsutility.modules.logic.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.ChoiceLabel;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.blocks.ThreeLogicTileEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

import static mcjty.rftoolsutility.modules.logic.blocks.ThreeLogicTileEntity.PARAM_INDEX;
import static mcjty.rftoolsutility.modules.logic.blocks.ThreeLogicTileEntity.PARAM_STATE;

public class GuiThreeLogic extends GenericGuiContainer<ThreeLogicTileEntity, GenericContainer> {

    public GuiThreeLogic(ThreeLogicTileEntity te, GenericContainer container, Inventory inventory) {
        super(te, container, inventory, LogicBlockModule.LOGIC.get().getManualEntry());
    }

    public static void register() {
        register(LogicBlockModule.CONTAINER_LOGIC.get(), GuiThreeLogic::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, new ResourceLocation(RFToolsUtility.MODID, "gui/threelogic.gui"));
        super.init();

        initializeFields();
        setupEvents();
    }

    private void setupEvents() {
        window.event("choice", (source, params) -> {
            String name = source.getName();
            int i = Integer.parseInt(name.substring(name.length()-1));
            String current = params.get(ChoiceLabel.PARAM_CHOICE);
            int st;
            if ("On".equals(current)) {
                st = 1;
            } else {
                st = "Off".equals(current) ? 0 : -1;
            }
            sendServerCommandTyped(ThreeLogicTileEntity.CMD_SETSTATE,
                    TypedMap.builder()
                        .put(PARAM_INDEX, i)
                        .put(PARAM_STATE, st)
                        .build());
        });
    }

    private void initializeFields() {
    }

    private void updateFields() {
        if (window == null) {
            return;
        }
        for (int i = 0 ; i < 8 ; i++) {
            ChoiceLabel tl = window.findChild("choice" + i);
            int state = tileEntity.getState(i);
            switch (state) {
                case 0 -> tl.choice("Off");
                case 1 -> tl.choice("On");
                default -> tl.choice("Keep");
            }
        }
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float partialTicks, int x, int y) {
        updateFields();
        super.renderBg(graphics, partialTicks, x, y);
    }
}
