package mcjty.rftoolsutility.modules.logic.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.ChoiceLabel;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.blocks.ThreeLogicTileEntity;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

import static mcjty.rftoolsutility.modules.logic.blocks.ThreeLogicTileEntity.PARAM_INDEX;
import static mcjty.rftoolsutility.modules.logic.blocks.ThreeLogicTileEntity.PARAM_STATE;

public class GuiThreeLogic extends GenericGuiContainer<ThreeLogicTileEntity, GenericContainer> {

    public GuiThreeLogic(ThreeLogicTileEntity te, GenericContainer container, PlayerInventory inventory) {
        super(te, container, inventory, LogicBlockModule.LOGIC.get().getManualEntry());
    }

    public static void register() {
        register(LogicBlockModule.CONTAINER_LOGIC.get(), GuiThreeLogic::new);
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
            int st;
            if ("On".equals(current)) {
                st = 1;
            } else {
                st = "Off".equals(current) ? 0 : -1;
            }
            sendServerCommandTyped(RFToolsUtilityMessages.INSTANCE, ThreeLogicTileEntity.CMD_SETSTATE,
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
                case 0: tl.choice("Off"); break;
                case 1: tl.choice("On"); break;
                default: tl.choice("Keep"); break;
            }
        }
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack matrixStack, float partialTicks, int x, int y) {
        updateFields();
        super.renderBg(matrixStack, partialTicks, x, y);
    }
}
