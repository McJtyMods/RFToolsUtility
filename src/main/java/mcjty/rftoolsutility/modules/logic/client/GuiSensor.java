package mcjty.rftoolsutility.modules.logic.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.ChoiceLabel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.varia.NamedEnum;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.blocks.SensorTileEntity;
import mcjty.rftoolsutility.modules.logic.tools.SensorType;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

public class GuiSensor extends GenericGuiContainer<SensorTileEntity, GenericContainer> {

    private ChoiceLabel typeLabel;

    public GuiSensor(SensorTileEntity te, GenericContainer container, PlayerInventory inventory) {
        super(te, container, inventory, LogicBlockModule.SENSOR.get().getManualEntry());
    }

    public static void register() {
        register(LogicBlockModule.CONTAINER_SENSOR.get(), GuiSensor::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsUtilityMessages.INSTANCE, new ResourceLocation(RFToolsUtility.MODID, "gui/sensor.gui"));
        super.init();

        initializeFields();
    }

    private void initializeFields() {
        typeLabel = window.findChild("type");
        updateFields();
    }

    private void updateFields() {
        TextField numberField = window.findChild("number");
        int number = tileEntity.getNumber();
        numberField.text(String.valueOf(number));

        typeLabel.choice(tileEntity.getSensorType().getName());

        ChoiceLabel areaLabel = window.findChild("area");
        areaLabel.choice(tileEntity.getAreaType().getName());

        ChoiceLabel groupLabel = window.findChild("group");
        groupLabel.choice(tileEntity.getGroupType().getName());
    }


    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        SensorType sensorType = NamedEnum.getEnumByName(typeLabel.getCurrentChoice(), SensorType.values());
        window.setFlag("number", sensorType.isSupportsNumber());
        window.setFlag("group", sensorType.isSupportsGroup());
        updateFields();
        drawWindow(matrixStack);
    }
}
