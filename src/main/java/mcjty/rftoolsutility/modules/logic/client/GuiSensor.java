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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class GuiSensor extends GenericGuiContainer<SensorTileEntity, GenericContainer> {

    private ChoiceLabel typeLabel;

    public GuiSensor(SensorTileEntity te, GenericContainer container, Inventory inventory) {
        super(te, container, inventory, LogicBlockModule.SENSOR.get().getManualEntry());
    }

    public static void register() {
        register(LogicBlockModule.CONTAINER_SENSOR.get(), GuiSensor::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, new ResourceLocation(RFToolsUtility.MODID, "gui/sensor.gui"));
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
    protected void renderBg(@Nonnull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        updateFields();
        drawWindow(graphics, xxx, xxx, yyy);
    }
}
