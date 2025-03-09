package mcjty.rftoolsutility.modules.logic.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.TagSelector;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.blocks.InvCheckerTileEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import javax.annotation.Nonnull;

public class GuiInvChecker extends GenericGuiContainer<InvCheckerTileEntity, GenericContainer> {

    public GuiInvChecker(GenericContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, LogicBlockModule.INVCHECKER.block().get().getManualEntry());
    }

    public static void register(RegisterMenuScreensEvent event) {
        event.register(LogicBlockModule.CONTAINER_INVCHECKER.get(), GuiInvChecker::new);
    }

    @Override
    public void init() {
        window = new Window(this, getBE(), ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "gui/invchecker.gui"));
        super.init();
    }

    private void updateFields() {
        if (window == null) {
            return;
        }

        InvCheckerTileEntity tileEntity = getBE();
        window.<TagSelector>findChild("tags").current(tileEntity.getTagName());
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float partialTicks, int x, int y) {
        updateFields();
        super.renderBg(graphics, partialTicks, x, y);
    }
}
