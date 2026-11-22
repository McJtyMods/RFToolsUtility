package mcjty.rftoolsutility.modules.screen.client;

import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenContainer;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import javax.annotation.Nonnull;

import static mcjty.lib.gui.widgets.Widgets.positional;

public class GuiTabletScreen extends GenericGuiContainer<ScreenTileEntity, ScreenContainer> {

    public static final int WIDTH = 200;
    public static final int HEIGHT = 190;

    public GuiTabletScreen(ScreenContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, /* @todo 1.14 */ ManualEntry.EMPTY);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    public static void register(RegisterMenuScreensEvent event) {
        MenuScreens.ScreenConstructor<ScreenContainer, GuiTabletScreen> factory = (container, inventory, title) -> {
            BlockEntity te = container.getBe();
            return Tools.safeMap(te, (ScreenTileEntity tile) -> new GuiTabletScreen(container, inventory, Component.literal("Title")), "Invalid tile entity!");
        };
        event.register(ScreenModule.CONTAINER_SCREEN_REMOTE.get(), factory);
        event.register(ScreenModule.CONTAINER_SCREEN_REMOTE_CREATIVE.get(), factory);
    }

    @Override
    public void init() {
        super.init();

        Panel toplevel = positional();
        toplevel.bounds(leftPos, topPos, imageWidth, imageHeight);
        window = new Window(this, toplevel);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        double xOnScreen = (x - leftPos - 25) / ScreenRenderer.TABLET_SIZE;
        double yOnScreen = (y - topPos - 20) / ScreenRenderer.TABLET_SIZE;
        ScreenTileEntity screen = getBE();
        if (xOnScreen >= 0 && xOnScreen < 1 && yOnScreen >= 0 && yOnScreen < 1) {
            var result = screen.getHitModule(1 - xOnScreen, 1 - yOnScreen, 0, Direction.NORTH, Direction.NORTH, 0);
            if (result != null) {
                screen.hitScreenClient(result);
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        ScreenTileEntity screen = getBE();
        screen.tickMe();
        renderBackground(graphics, mouseX, mouseY, partialTicks);
        ScreenRenderer.renderTablet(graphics, screen, leftPos + 25, topPos + 20);
    }
}
