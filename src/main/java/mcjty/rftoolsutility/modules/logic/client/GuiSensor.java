package mcjty.rftoolsutility.modules.logic.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.ChoiceLabel;
import mcjty.lib.varia.NamedEnum;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.blocks.SensorTileEntity;
import mcjty.rftoolsutility.modules.logic.tools.SensorType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class GuiSensor extends GenericGuiContainer<SensorTileEntity, GenericContainer> {

    private ChoiceLabel typeLabel;

    public GuiSensor(GenericContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, LogicBlockModule.SENSOR.block().get().getManualEntry());
    }

    public static void register() {
        MenuScreens.register(LogicBlockModule.CONTAINER_SENSOR.get(), GuiSensor::new);
    }

    @Override
    public void init() {
        window = new Window(this, getBE(), Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "gui/sensor.gui"));
        super.init();

        initializeFields();
    }

    private void initializeFields() {
        typeLabel = window.findChild("type");
    }

    private void updateFields() {
        if (window == null) {
            return;
        }

        SensorType sensorType = NamedEnum.getEnumByName(typeLabel.getCurrentChoice(), SensorType.values());
        if (sensorType != null) {
            window.setFlag("number", sensorType.isSupportsNumber());
            window.setFlag("group", sensorType.isSupportsGroup());
        }
    }


    @Override
    protected void renderBg(@Nonnull GuiGraphicsExtractor graphics, float partialTicks, int mouseX, int mouseY) {
        updateFields();
        drawWindow(graphics, partialTicks, mouseX, mouseY);
    }
}
