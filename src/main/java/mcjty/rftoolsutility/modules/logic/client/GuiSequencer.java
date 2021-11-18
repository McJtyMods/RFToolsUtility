package mcjty.rftoolsutility.modules.logic.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.ChoiceLabel;
import mcjty.lib.gui.widgets.ImageChoiceLabel;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.blocks.SequencerTileEntity;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class GuiSequencer extends GenericGuiContainer<SequencerTileEntity, GenericContainer> {

    private List<ImageChoiceLabel> bits = new ArrayList<>();

    public GuiSequencer(SequencerTileEntity te, GenericContainer container, PlayerInventory inventory) {
        super(te, container, inventory, LogicBlockModule.SEQUENCER.get().getManualEntry());
    }

    public static void register() {
        register(LogicBlockModule.CONTAINER_SEQUENCER.get(), GuiSequencer::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsUtilityMessages.INSTANCE, new ResourceLocation(RFToolsUtility.MODID, "gui/sequencer.gui"));
        super.init();

        initializeFields();
        setupEvents();
    }

    private void initializeFields() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                final int bit = row * 8 + col;
                bits.add(window.findChild("grid" + bit));
            }
        }
    }

    private void updateFields() {
        if (window == null) {
            return;
        }
        ImageChoiceLabel choiceLabel = window.findChild("endchoice");
        choiceLabel.setCurrentChoice(tileEntity.getEndState() ? 1 : 0);

//        TextField countField = window.findChild("stepcount");
//        int count = tileEntity.getStepCount();
//        if (count < 1 || count > 64) {
//            count = 64;
//        }
//        countField.text(String.valueOf(count));

//        TextField speedField = window.findChild("delay");
//        int delay = tileEntity.getDelay();
//        if (delay <= 0) {
//            delay = 1;
//        }
//        speedField.text(String.valueOf(delay));

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                final int bit = row * 8 + col;
                ImageChoiceLabel label = window.findChild("grid" + bit);
                label.setCurrentChoice(tileEntity.getCycleBit(bit) ? 1 : 0);
            }
        }

        ChoiceLabel mode = window.findChild("mode");
        mode.choice(tileEntity.getMode().getDescription());
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack matrixStack, float partialTicks, int x, int y) {
        updateFields();
        super.renderBg(matrixStack, partialTicks, x, y);
    }

    private void setupEvents() {
        window.event("grid", (source, params) -> {
            int bit = Integer.parseInt(source.getName().substring("grid".length()));
            changeBit(bit, params.get(ImageChoiceLabel.PARAM_CHOICE));
        });
        window.event("flip", (source, params) -> flipGrid());
        window.event("clear", (source, params) -> fillGrid());
    }

    private void flipGrid() {
        for(ImageChoiceLabel bit : bits) {
            bit.setCurrentChoice(1 - bit.getCurrentChoiceIndex());
        }
        tileEntity.flipCycleBits();
        sendServerCommandTyped(RFToolsUtilityMessages.INSTANCE, SequencerTileEntity.CMD_FLIPBITS, TypedMap.EMPTY);
    }

    private void fillGrid() {
        for(ImageChoiceLabel bit : bits) {
            bit.setCurrentChoice(0);
        }
        tileEntity.clearCycleBits();
        sendServerCommandTyped(RFToolsUtilityMessages.INSTANCE, SequencerTileEntity.CMD_CLEARBITS, TypedMap.EMPTY);
    }

    private void changeBit(int bit, String choice) {
        boolean newChoice = "1".equals(choice);
        tileEntity.setCycleBit(bit, newChoice);
        sendServerCommandTyped(RFToolsUtilityMessages.INSTANCE, SequencerTileEntity.CMD_SETBIT,
                TypedMap.builder()
                        .put(SequencerTileEntity.PARAM_BIT, bit)
                        .put(SequencerTileEntity.PARAM_CHOICE, newChoice)
                        .build());
    }
}
