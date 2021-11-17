package mcjty.rftoolsutility.modules.logic.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.ToggleButton;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.blocks.RedstoneReceiverTileEntity;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class GuiRedstoneReceiver extends GenericGuiContainer<RedstoneReceiverTileEntity, GenericContainer> {

    public GuiRedstoneReceiver(RedstoneReceiverTileEntity te, GenericContainer container, PlayerInventory inventory) {
        super(te, container, inventory, LogicBlockModule.REDSTONE_RECEIVER.get().getManualEntry());
    }

    public static void register() {
        register(LogicBlockModule.CONTAINER_REDSTONE_RECEIVER.get(), GuiRedstoneReceiver::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsUtilityMessages.INSTANCE, new ResourceLocation(RFToolsUtility.MODID, "gui/redstone_receiver.gui"));
        super.init();

        initializeFields();
    }

    private void initializeFields() {
    }

    private void updateFields() {
        ToggleButton analog = window.findChild("analog");
        analog.pressed(tileEntity.getAnalog());
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack matrixStack, float partialTicks, int x, int y) {
        updateFields();
        super.renderBg(matrixStack, partialTicks, x, y);
    }
}
