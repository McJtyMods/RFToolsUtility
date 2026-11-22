package mcjty.rftoolsutility.modules.spawner.client;

import mcjty.lib.blockcommands.ICommand;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.network.Networking;
import mcjty.lib.network.PacketRequestDataFromServer;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.TagTools;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.spawner.SpawnerConfiguration;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.spawner.blocks.SpawnerTileEntity;
import mcjty.rftoolsutility.modules.spawner.items.SyringeItem;
import mcjty.rftoolsutility.modules.spawner.recipes.SpawnerRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

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
    private final BlockRender[] blocks = new BlockRender[3];
    private final Label[] labels = new Label[3];
    private Label name;
    private Label rfTick;

    private static final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "textures/gui/spawner.png");

    public GuiSpawner(GenericContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, SpawnerModule.SPAWNER.block().get().getManualEntry());

        imageWidth = SPAWNER_WIDTH;
        imageHeight = SPAWNER_HEIGHT;
    }

    public static void register(RegisterMenuScreensEvent event) {
        event.register(SpawnerModule.CONTAINER_SPAWNER.get(), GuiSpawner::new);
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

        Panel toplevel = new Panel().background(GUI).layout(new PositionalLayout()).children(energyBar,
                blocks[0], labels[0], blocks[1], labels[1], blocks[2], labels[2], rfTick, name);
        toplevel.setBounds(new Rectangle(leftPos, topPos, imageWidth, imageHeight));

        window = new Window(this, toplevel);
    }

    private long lastTime;

    private void showSyringeInfo() {
        for (int i = 0; i < blocks.length; i++) {
            blocks[i].renderItem(ItemStack.EMPTY);
            labels[i].text("");
        }
        name.text("");
        rfTick.text("");

        SpawnerTileEntity tileEntity = getBE();
        ItemStack stack = tileEntity.getItems().getStackInSlot(SpawnerTileEntity.SLOT_SYRINGE);
        ResourceLocation mobId = SyringeItem.getMobId(stack);
        if (mobId == null) {
            return;
        }
        name.text(SyringeItem.getMobName(stack));

        SpawnerRecipes.MobData recipe = SpawnerRecipes.getMobData(Minecraft.getInstance().level, mobId);
        if (recipe == null) {
            rfTick.text(Component.translatable("gui.rftoolsutility.spawner.no_recipe").getString());
            return;
        }

        rfTick.text(recipe.getSpawnRf() + "RF");
        long now = System.currentTimeMillis();
        if (now - lastTime > 100) {
            lastTime = now;
            Networking.sendToServer(PacketRequestDataFromServer.create(tileEntity.getDimension(), tileEntity.getBlockPos(),
                    ((ICommand) SpawnerTileEntity.CMD_GET_SPAWNERINFO).name(), TypedMap.EMPTY, false));
        }
        float[] matter = {tileEntity.matterReceived0, tileEntity.matterReceived1, tileEntity.matterReceived2};
        DecimalFormat format = new DecimalFormat("#.##");
        format.setRoundingMode(RoundingMode.DOWN);
        for (int i = 0; i < blocks.length; i++) {
            SpawnerRecipes.MobSpawnAmount requirement = recipe.getItem(i);
            List<ItemStack> stacks = getDisplayStacks(requirement);
            if (!stacks.isEmpty()) {
                blocks[i].renderItem(stacks.get((int) ((now / 500) % stacks.size())));
            }
            labels[i].text(format.format(matter[i]) + "/" + requirement.getAmount());
        }
    }

    private static List<ItemStack> getDisplayStacks(SpawnerRecipes.MobSpawnAmount requirement) {
        if (requirement.isLivingMatter()) {
            List<ItemStack> stacks = new ArrayList<>();
            TagTools.getItemsForTag(SpawnerConfiguration.TAG_LIVING).forEach(holder -> stacks.add(new ItemStack(holder.value())));
            return stacks;
        }
        Ingredient ingredient = requirement.getObject();
        return List.of(ingredient.getItems());
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        showSyringeInfo();

        drawWindow(graphics, partialTicks, mouseX, mouseY);
        updateEnergyBar(energyBar);
    }
}
