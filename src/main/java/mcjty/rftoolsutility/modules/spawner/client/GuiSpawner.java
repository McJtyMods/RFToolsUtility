package mcjty.rftoolsutility.modules.spawner.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.spawner.SpawnerConfiguration;
import mcjty.rftoolsutility.modules.spawner.items.CommonSyringeItem;
import mcjty.rftoolsutility.modules.spawner.items.SyringeItem;
import mcjty.rftoolsutility.modules.spawner.blocks.SpawnerTileEntity;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;


public class GuiSpawner extends GenericGuiContainer<SpawnerTileEntity, GenericContainer> {
    private static final int SPAWNER_WIDTH = 180;
    private static final int SPAWNER_HEIGHT = 152;

    private EnergyBar energyBar;
    private BlockRender blocks[] = new BlockRender[3];
    private Label labels[] = new Label[3];
    private Label name;
    private Label rfTick;

    private static final ResourceLocation iconLocation = new ResourceLocation(RFToolsUtility.MODID, "textures/gui/spawner.png");

    public GuiSpawner(SpawnerTileEntity spawnerTileEntity, GenericContainer container, PlayerInventory inventory) {
        super(RFToolsUtility.instance, spawnerTileEntity, container, inventory, ManualHelper.create("@todo"));  // @todo

        xSize = SPAWNER_WIDTH;
        ySize = SPAWNER_HEIGHT;
    }

    @Override
    public void init() {
        super.init();

        energyBar = new EnergyBar().vertical().hint(10, 7, 8, 54).showText(false);

        blocks[0] = new BlockRender().hint(80, 5, 18, 18);
        blocks[1] = new BlockRender().hint(80, 25, 18, 18);
        blocks[2] = new BlockRender().hint(80, 45, 18, 18);
        labels[0] = Widgets.label(100, 5, 74, 18, "").horizontalAlignment(HorizontalAlignment.ALIGN_LEFT);
        labels[1] = Widgets.label(100, 25, 74, 18, "").horizontalAlignment(HorizontalAlignment.ALIGN_LEFT);
        labels[2] = Widgets.label(100, 45, 74, 18, "").horizontalAlignment(HorizontalAlignment.ALIGN_LEFT);
        name = Widgets.label(22, 31, 78, 16, "").horizontalAlignment(HorizontalAlignment.ALIGN_LEFT);
        rfTick = Widgets.label(22, 47, 78, 16, "").horizontalAlignment(HorizontalAlignment.ALIGN_LEFT);

        Panel toplevel = new Panel().background(iconLocation).layout(new PositionalLayout()).children(energyBar,
                blocks[0], labels[0], blocks[1], labels[1], blocks[2], labels[2], rfTick, name);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);
    }

    private static long lastTime = 0;

    private void showSyringeInfo() {
        for (int i = 0 ; i < 3 ; i++) {
            blocks[i].renderItem(null);
            labels[i].text("");
        }
        name.text("");
        rfTick.text("");

        ItemStack stack = tileEntity.getItems().getStackInSlot(SpawnerTileEntity.SLOT_SYRINGE);
        if (stack.isEmpty()) {
            return;
        }

        String mobId = CommonSyringeItem.getMobId(stack);
        if (mobId != null) {
            String mobName = CommonSyringeItem.getMobName(stack);
            name.text(mobName);
            rfTick.text(SpawnerConfiguration.mobSpawnRf.get(mobId) + "RF");
            int i = 0;
            List<SpawnerConfiguration.MobSpawnAmount> list = SpawnerConfiguration.mobSpawnAmounts.get(mobId);
            if (list != null) {
                if (System.currentTimeMillis() - lastTime > 100) {
                    lastTime = System.currentTimeMillis();
                    tileEntity.requestDataFromServer(RFToolsUtilityMessages.INSTANCE, SpawnerTileEntity.CMD_GET_SPAWNERINFO, TypedMap.EMPTY);
//                    RFToolsMessages.INSTANCE.sendToServer(new PacketGetInfoFromServer(RFToolsUtility.MODID, new SpawnerInfoPacketServer(
//                            tileEntity.getWorld().provider.getDimension(),
//                            tileEntity.getPos())));
                }

                float[] matter = new float[] { SpawnerTileEntity.matterReceived0, SpawnerTileEntity.matterReceived1, SpawnerTileEntity.matterReceived2 };

                for (SpawnerConfiguration.MobSpawnAmount spawnAmount : list) {
                    ItemStack b = spawnAmount.getObject();
                    float amount = spawnAmount.getAmount();
                    if (b.isEmpty()) {
                        // @todo 1.15 more blocks!
                        Object[] blocks = {Blocks.BIRCH_LEAVES, Blocks.PUMPKIN, Items.WHEAT, Items.POTATO, Items.BEEF};
                        int index = (int) ((System.currentTimeMillis() / 500) % blocks.length);
                        if (blocks[index] instanceof Block) {
                            this.blocks[i].renderItem(new ItemStack((Block) blocks[index], 1));
                        } else {
                            this.blocks[i].renderItem(new ItemStack((Item) blocks[index], 1));
                        }
                    } else {
                        blocks[i].renderItem(b);
                    }
                    DecimalFormat format = new DecimalFormat("#.##");
                    format.setRoundingMode(RoundingMode.DOWN);
                    String mf = format.format(matter[i]);
                    labels[i].text(mf + "/" + Float.toString(amount));
                    i++;
                }
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        showSyringeInfo();

        drawWindow();
        updateEnergyBar(energyBar);
    }
}
