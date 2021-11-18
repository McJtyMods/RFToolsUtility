package mcjty.rftoolsutility.modules.spawner.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.spawner.SpawnerConfiguration;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.spawner.blocks.SpawnerTileEntity;
import mcjty.rftoolsutility.modules.spawner.items.SyringeItem;
import mcjty.rftoolsutility.modules.spawner.recipes.SpawnerRecipes;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.awt.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
        super(spawnerTileEntity, container, inventory, SpawnerModule.SPAWNER.get().getManualEntry());

        imageWidth = SPAWNER_WIDTH;
        imageHeight = SPAWNER_HEIGHT;
    }

    public static void register() {
        register(SpawnerModule.CONTAINER_SPAWNER.get(), GuiSpawner::new);
    }

    @Override
    public void init() {
        super.init();

        energyBar = new EnergyBar().vertical().hint(10, 7, 8, 54).showText(false);

        blocks[0] = new BlockRender().hint(100, 5, 18, 18);
        blocks[1] = new BlockRender().hint(100, 25, 18, 18);
        blocks[2] = new BlockRender().hint(100, 45, 18, 18);
        labels[0] = Widgets.label(120, 5, 74, 18, "").horizontalAlignment(HorizontalAlignment.ALIGN_LEFT);
        labels[1] = Widgets.label(120, 25, 74, 18, "").horizontalAlignment(HorizontalAlignment.ALIGN_LEFT);
        labels[2] = Widgets.label(120, 45, 74, 18, "").horizontalAlignment(HorizontalAlignment.ALIGN_LEFT);
        name = Widgets.label(22, 31, 78, 16, "").horizontalAlignment(HorizontalAlignment.ALIGN_LEFT);
        rfTick = Widgets.label(22, 47, 78, 16, "").horizontalAlignment(HorizontalAlignment.ALIGN_LEFT);

        Panel toplevel = new Panel().background(iconLocation).layout(new PositionalLayout()).children(energyBar,
                blocks[0], labels[0], blocks[1], labels[1], blocks[2], labels[2], rfTick, name);
        toplevel.setBounds(new Rectangle(leftPos, topPos, imageWidth, imageHeight));

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

        String mobId = SyringeItem.getMobId(stack);
        if (mobId != null) {
            String mobName = SyringeItem.getMobName(stack);
            name.text(mobName);

            SpawnerRecipes.MobData mobData = SpawnerRecipes.getMobData(Minecraft.getInstance().level, mobId);
            if (mobData != null) {

                rfTick.text(mobData.getSpawnRf() + "RF");
                int i = 0;
                if (System.currentTimeMillis() - lastTime > 100) {
                    lastTime = System.currentTimeMillis();
                    tileEntity.requestDataFromServer(RFToolsUtilityMessages.INSTANCE, SpawnerTileEntity.CMD_GET_SPAWNERINFO, TypedMap.EMPTY);
//                    RFToolsMessages.INSTANCE.sendToServer(new PacketGetInfoFromServer(RFToolsUtility.MODID, new SpawnerInfoPacketServer(
//                            tileEntity.getWorld().provider.getDimension(),
//                            tileEntity.getPos())));
                }

                float[] matter = new float[] { tileEntity.matterReceived0, tileEntity.matterReceived1, tileEntity.matterReceived2 };

                for (int index = 0 ; index < 3 ; index++) {
                    SpawnerRecipes.MobSpawnAmount item = mobData.getItem(index);
                    ItemStack[] matchingStacks = item.getObject().getItems();
                    float amount = item.getAmount();
                    if (matchingStacks.length == 0) {
                        ITag<Item> itemTag = ItemTags.getAllTags().getTag(SpawnerConfiguration.LIVING);
                        if (itemTag == null) {
                            this.blocks[i].renderItem(new ItemStack(Blocks.BEDROCK, 1));
                        } else {
                            List<Item> items = new ArrayList<Item>(itemTag.getValues());
                            int idx = (int) ((System.currentTimeMillis() / 500) % items.size());
                            this.blocks[i].renderItem(new ItemStack(items.get(idx), 1));
                        }
                    } else {
                        int idx = (int) ((System.currentTimeMillis() / 500) % matchingStacks.length);
                        ItemStack b = matchingStacks[idx];
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
    protected void renderBg(@Nonnull MatrixStack matrixStack, float v, int i, int i2) {
        showSyringeInfo();

        drawWindow(matrixStack);
        updateEnergyBar(energyBar);
    }
}
