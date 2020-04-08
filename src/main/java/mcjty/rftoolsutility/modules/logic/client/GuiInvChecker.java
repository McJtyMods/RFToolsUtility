package mcjty.rftoolsutility.modules.logic.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.ChoiceLabel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.blocks.InvCheckerTileEntity;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

public class GuiInvChecker extends GenericGuiContainer<InvCheckerTileEntity, GenericContainer> {

    public static final String OREDICT_USE = "Use";
    public static final String OREDICT_IGNORE = "Ignore";
    public static final String META_MATCH = "Match";
    public static final String META_IGNORE = "Ignore";

    public GuiInvChecker(InvCheckerTileEntity te, GenericContainer container, PlayerInventory inventory) {
        super(RFToolsUtility.instance, te, container, inventory, 0, /*@todo 1.15 */"invchecker");
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsUtilityMessages.INSTANCE, new ResourceLocation(RFToolsUtility.MODID, "gui/invchecker.gui"));
        super.init();

        initializeFields();
    }

    private void initializeFields() {
        TextField amountField = window.findChild("amount");
        amountField.setText(String.valueOf(tileEntity.getAmount()));

        TextField slotField = window.findChild("slot");
        slotField.setText(String.valueOf(tileEntity.getSlot()));

        ChoiceLabel metaLabel = window.findChild("meta");
        metaLabel.setChoice(tileEntity.isUseMeta() ? META_MATCH : META_IGNORE);

        ChoiceLabel oreDictLabel = window.findChild("ore");
        oreDictLabel.setChoice(tileEntity.isOreDict() ? OREDICT_USE : OREDICT_IGNORE);
    }
}
