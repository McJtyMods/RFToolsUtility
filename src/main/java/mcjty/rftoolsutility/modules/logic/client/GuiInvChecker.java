package mcjty.rftoolsutility.modules.logic.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.ChoiceLabel;
import mcjty.lib.gui.widgets.TagSelector;
import mcjty.lib.gui.widgets.TextField;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.blocks.InvCheckerTileEntity;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class GuiInvChecker extends GenericGuiContainer<InvCheckerTileEntity, GenericContainer> {

    public static final String DMG_MATCH = "Match";
    public static final String DMG_IGNORE = "Ignore";

    public GuiInvChecker(InvCheckerTileEntity te, GenericContainer container, PlayerInventory inventory) {
        super(te, container, inventory, LogicBlockModule.INVCHECKER.get().getManualEntry());
    }

    public static void register() {
        register(LogicBlockModule.CONTAINER_INVCHECKER.get(), GuiInvChecker::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsUtilityMessages.INSTANCE, new ResourceLocation(RFToolsUtility.MODID, "gui/invchecker.gui"));
        super.init();

        initializeFields();
    }

    private void initializeFields() {
        updateFields();
    }

    private void updateFields() {
        if (window == null) {
            return;
        }
        TextField amountField = window.findChild("amount");
        amountField.text(String.valueOf(tileEntity.getAmount()));

        TextField slotField = window.findChild("slot");
        slotField.text(String.valueOf(tileEntity.getSlot()));

        ChoiceLabel damageLabel = window.findChild("damage");
        damageLabel.choice(tileEntity.isUseDamage() ? DMG_MATCH : DMG_IGNORE);

        TagSelector tagSelector = window.findChild("tags");
        tagSelector.current(tileEntity.getTagName());
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack matrixStack, float partialTicks, int x, int y) {
        updateFields();
        super.renderBg(matrixStack, partialTicks, x, y);
    }
}
