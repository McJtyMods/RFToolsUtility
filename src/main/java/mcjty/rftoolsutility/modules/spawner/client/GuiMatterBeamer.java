package mcjty.rftoolsutility.modules.spawner.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.Panel;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.spawner.blocks.MatterBeamerTileEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.awt.*;

public class GuiMatterBeamer extends GenericGuiContainer<MatterBeamerTileEntity, GenericContainer> {
    private static final int BEAMER_WIDTH = 180;
    private static final int BEAMER_HEIGHT = 152;

    private EnergyBar energyBar;

    private static final ResourceLocation iconLocation = new ResourceLocation(RFToolsUtility.MODID, "textures/gui/matterbeamer.png");

    public GuiMatterBeamer(MatterBeamerTileEntity beamerTileEntity, GenericContainer container, Inventory inventory) {
        super(beamerTileEntity, container, inventory, SpawnerModule.MATTER_BEAMER.get().getManualEntry());

        imageWidth = BEAMER_WIDTH;
        imageHeight = BEAMER_HEIGHT;
    }

    public static void register() {
        register(SpawnerModule.CONTAINER_MATTER_BEAMER.get(), GuiMatterBeamer::new);
    }

    @Override
    public void init() {
        super.init();

        energyBar = new EnergyBar().vertical().hint(10, 7, 8, 54).showText(false);

        Panel toplevel = new Panel().background(iconLocation).layout(new PositionalLayout()).children(energyBar);
        toplevel.setBounds(new Rectangle(leftPos, topPos, imageWidth, imageHeight));

        window = new Window(this, toplevel);
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float v, int i, int i2) {
        drawWindow(graphics);
        updateEnergyBar(energyBar);
    }
}
