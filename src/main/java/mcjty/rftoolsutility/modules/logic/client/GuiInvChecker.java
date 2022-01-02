package mcjty.rftoolsutility.modules.logic.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.TagSelector;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.blocks.InvCheckerTileEntity;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class GuiInvChecker extends GenericGuiContainer<InvCheckerTileEntity, GenericContainer> {

    public GuiInvChecker(InvCheckerTileEntity te, GenericContainer container, Inventory inventory) {
        super(te, container, inventory, LogicBlockModule.INVCHECKER.get().getManualEntry());
    }

    public static void register() {
        register(LogicBlockModule.CONTAINER_INVCHECKER.get(), GuiInvChecker::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsUtilityMessages.INSTANCE, new ResourceLocation(RFToolsUtility.MODID, "gui/invchecker.gui"));
        super.init();
    }

    private void updateFields() {
        if (window == null) {
            return;
        }

        window.<TagSelector>findChild("tags").current(tileEntity.getTagName());
    }

    @Override
    protected void renderBg(@Nonnull PoseStack matrixStack, float partialTicks, int x, int y) {
        updateFields();
        super.renderBg(matrixStack, partialTicks, x, y);
    }
}
