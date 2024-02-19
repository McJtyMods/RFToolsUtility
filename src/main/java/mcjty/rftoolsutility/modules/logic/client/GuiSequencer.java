package mcjty.rftoolsutility.modules.logic.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.ImageChoiceLabel;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.blocks.SequencerTileEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class GuiSequencer extends GenericGuiContainer<SequencerTileEntity, GenericContainer> {

    private final List<ImageChoiceLabel> bits = new ArrayList<>();

    public GuiSequencer(SequencerTileEntity te, GenericContainer container, Inventory inventory) {
        super(te, container, inventory, LogicBlockModule.SEQUENCER.get().getManualEntry());
    }

    public static void register() {
        register(LogicBlockModule.CONTAINER_SEQUENCER.get(), GuiSequencer::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, new ResourceLocation(RFToolsUtility.MODID, "gui/sequencer.gui"));
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

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                final int bit = row * 8 + col;
                ImageChoiceLabel label = window.findChild("grid" + bit);
                label.setCurrentChoice(tileEntity.getCycleBit(bit) ? 1 : 0);
            }
        }
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float partialTicks, int x, int y) {
        updateFields();
        super.renderBg(graphics, partialTicks, x, y);
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
        sendServerCommandTyped(SequencerTileEntity.CMD_FLIPBITS, TypedMap.EMPTY);
    }

    private void fillGrid() {
        for(ImageChoiceLabel bit : bits) {
            bit.setCurrentChoice(0);
        }
        tileEntity.clearCycleBits();
        sendServerCommandTyped(SequencerTileEntity.CMD_CLEARBITS, TypedMap.EMPTY);
    }

    private void changeBit(int bit, String choice) {
        boolean newChoice = "1".equals(choice);
        tileEntity.setCycleBit(bit, newChoice);
        sendServerCommandTyped(SequencerTileEntity.CMD_SETBIT,
                TypedMap.builder()
                        .put(SequencerTileEntity.PARAM_BIT, bit)
                        .put(SequencerTileEntity.PARAM_CHOICE, newChoice)
                        .build());
    }
}
