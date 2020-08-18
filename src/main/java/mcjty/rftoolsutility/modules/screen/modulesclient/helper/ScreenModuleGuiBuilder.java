package mcjty.rftoolsutility.modules.screen.modulesclient.helper;

import mcjty.lib.gui.events.BlockRenderEvent;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.varia.DimensionId;
import mcjty.rftoolsbase.api.screens.FormatStyle;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsutility.modules.screen.IModuleGuiChanged;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mcjty.lib.gui.widgets.Widgets.horizontal;
import static mcjty.lib.gui.widgets.Widgets.vertical;

public class ScreenModuleGuiBuilder implements IModuleGuiBuilder {
    private Minecraft mc;
    private Screen gui;
    private CompoundNBT currentData;
    private IModuleGuiChanged moduleGuiChanged;

    private Panel panel;
    private List<Widget<?>> row = new ArrayList<>();

    public ScreenModuleGuiBuilder(Minecraft mc, Screen gui, CompoundNBT currentData, IModuleGuiChanged moduleGuiChanged) {
        this.gui = gui;
        this.mc = mc;
        this.moduleGuiChanged = moduleGuiChanged;
        this.currentData = currentData;
        panel = vertical(3, 1);
    }

    @Override
    public CompoundNBT getCurrentData() {
        return currentData;
    }

    @Override
    public World getWorld() {
        return mc.player.getEntityWorld();
    }

    public Panel build() {
        nl();
        return panel;
    }

    @Override
    public ScreenModuleGuiBuilder label(String text) {
        row.add(Widgets.label(text));
        return this;
    }

    @Override
    public ScreenModuleGuiBuilder leftLabel(String text) {
        row.add(Widgets.label(text).horizontalAlignment(HorizontalAlignment.ALIGN_LEFT));
        return this;
    }

    @Override
    public ScreenModuleGuiBuilder text(final String tagname, String... tooltip) {
        TextField textField = new TextField().desiredHeight(15).tooltips(tooltip).event((newText) -> {
            currentData.putString(tagname, newText);
            moduleGuiChanged.updateData();
        });
        row.add(textField);
        if (currentData != null) {
            textField.text(currentData.getString(tagname));
        }
        return this;
    }

    @Override
    public ScreenModuleGuiBuilder integer(final String tagname, String... tooltip) {
        TextField textField = new TextField().desiredHeight(15).tooltips(tooltip).event((newText) -> {
            int value;
            try {
                value = Integer.parseInt(newText);
            } catch (NumberFormatException e) {
                value = 0;
            }
            currentData.putInt(tagname, value);
            moduleGuiChanged.updateData();
        });
        row.add(textField);
        if (currentData != null) {
            if (currentData.contains(tagname)) {
                int dd = currentData.getInt(tagname);
                textField.text(Integer.toString(dd));
            }
        }
        return this;
    }

    @Override
    public ScreenModuleGuiBuilder toggle(final String tagname, String label, String... tooltip) {
        final ToggleButton toggleButton = new ToggleButton().text(label).tooltips(tooltip).desiredHeight(14).checkMarker(true);
        toggleButton.event(() -> {
            currentData.putBoolean(tagname, toggleButton.isPressed());
            moduleGuiChanged.updateData();
        });

        row.add(toggleButton);
        if (currentData != null) {
            toggleButton.pressed(currentData.getBoolean(tagname));
        }
        return this;
    }

    @Override
    public ScreenModuleGuiBuilder toggleNegative(final String tagname, String label, String... tooltip) {
        final ToggleButton toggleButton = new ToggleButton().text(label).tooltips(tooltip).desiredHeight(14).desiredWidth(36).checkMarker(true);
        toggleButton.event(() -> {
            currentData.putBoolean(tagname, !toggleButton.isPressed());
            moduleGuiChanged.updateData();
        });

        row.add(toggleButton);
        if (currentData != null) {
            toggleButton.pressed(!currentData.getBoolean(tagname));
        } else {
            toggleButton.pressed(true);
        }
        return this;
    }

    @Override
    public ScreenModuleGuiBuilder color(final String tagname, String... tooltip) {
        ColorSelector colorSelector = new ColorSelector().tooltips(tooltip)
                .desiredWidth(20).desiredHeight(14).event((newColor) -> {
                    currentData.putInt(tagname, newColor);
                    moduleGuiChanged.updateData();
                });
        row.add(colorSelector);
        if (currentData != null) {
            int currentColor = currentData.getInt(tagname);
            if (currentColor != 0) {
                colorSelector.currentColor(currentColor);
            }
        }
        return this;
    }

    @Override
    public IModuleGuiBuilder choices(String tagname, String tooltip, String... choices) {
        ChoiceLabel choiceLabel = new ChoiceLabel().tooltips(tooltip)
                .desiredWidth(50).desiredHeight(14);
        for (String s : choices) {
            choiceLabel.choices(s);
        }
        choiceLabel.event((newChoice) -> {
            currentData.putString(tagname, newChoice);
            moduleGuiChanged.updateData();
        });
        row.add(choiceLabel);
        if (currentData != null) {
            String currentChoice = currentData.getString(tagname);
            if (!currentChoice.isEmpty()) {
                choiceLabel.choice(currentChoice);
            }
        }
        return this;
    }

    @Override
    public IModuleGuiBuilder choices(String tagname, Choice... choices) {
        ChoiceLabel choiceLabel = new ChoiceLabel()
                .desiredWidth(50).desiredHeight(14);
        Map<String, Integer> choicesMap = new HashMap<>(choices.length);
        for (int i = 0; i < choices.length; ++i) {
            Choice c = choices[i];
            String name = c.getName();
            choicesMap.put(name, i);
            choiceLabel.choices(name);
            choiceLabel.choiceTooltip(name, c.getTooltips());
        }
        choiceLabel.event((newChoice) -> {
            currentData.putInt(tagname, choicesMap.get(newChoice));
            moduleGuiChanged.updateData();
        });
        row.add(choiceLabel);
        if (currentData != null) {
            int currentChoice = currentData.getInt(tagname);
            if (currentChoice < choices.length && currentChoice >= 0) {
                choiceLabel.choice(choices[currentChoice].getName());
            }
        }
        return this;
    }

    @Override
    public ScreenModuleGuiBuilder format(String tagname) {
        ChoiceLabel label = setupFormatCombo(mc, gui, tagname, currentData, moduleGuiChanged);
        row.add(label);
        return this;
    }

    @Override
    public ScreenModuleGuiBuilder mode(String componentName) {
        ChoiceLabel label = setupModeCombo(mc, gui, componentName, currentData, moduleGuiChanged);
        row.add(label);
        return this;
    }

    @Override
    public ScreenModuleGuiBuilder block(String tagnamePos) {
        String monitoring;
        if (currentData.contains(tagnamePos + "x")) {
            ResourceLocation dim;
            if (currentData.contains(tagnamePos + "dim")) {
                dim = new ResourceLocation(currentData.getString(tagnamePos + "dim"));
            } else {
                // For compatibility reasons.
                dim = new ResourceLocation(currentData.getString("dim"));
            }
            World world = getWorld();
            if (dim.equals(DimensionId.fromWorld(world).getRegistryName())) {
                int x = currentData.getInt(tagnamePos+"x");
                int y = currentData.getInt(tagnamePos+"y");
                int z = currentData.getInt(tagnamePos+"z");
                monitoring = currentData.getString(tagnamePos+"name");
                Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
                row.add(new BlockRender().renderItem(block).desiredWidth(20));
                row.add(Widgets.label(x + "," + y + "," + z).horizontalAlignment(HorizontalAlignment.ALIGN_LEFT).desiredWidth(150));
            } else {
                monitoring = "<unreachable>";
            }
        } else {
            monitoring = "<not set>";
        }
        row.add(Widgets.label(monitoring));

        return this;
    }

    @Override
    public IModuleGuiBuilder ghostStack(String tagname) {
        ItemStack stack = ItemStack.EMPTY;
        if (currentData.contains(tagname)) {
            stack = ItemStack.read(currentData.getCompound(tagname));
        }

        BlockRender blockRender = new BlockRender().renderItem(stack).desiredWidth(18).desiredHeight(18).filledRectThickness(1).filledBackground(0xff555555);
        row.add(blockRender);
        blockRender.event(new BlockRenderEvent() {
            @Override
            public void select() {
                ItemStack holding = Minecraft.getInstance().player.inventory.getItemStack();
                if (holding.isEmpty()) {
                    currentData.remove(tagname);
                    blockRender.renderItem(null);
                } else {
                    ItemStack copy = holding.copy();
                    copy.setCount(1);
                    blockRender.renderItem(copy);
                    CompoundNBT tc = new CompoundNBT();
                    copy.write(tc);
                    currentData.put(tagname, tc);
                }
                moduleGuiChanged.updateData();
            }

            @Override
            public void doubleClick() {

            }
        });

        return this;
    }

    @Override
    public ScreenModuleGuiBuilder nl() {
        if (row.size() == 1) {
            panel.children(row.get(0).desiredHeight(16));
            row.clear();
        } else if (!row.isEmpty()) {
            Panel rowPanel = horizontal().desiredHeight(16);
            for (Widget<?> widget : row) {
                rowPanel.children(widget);
            }
            panel.children(rowPanel);
            row.clear();
        }

        return this;
    }

    private static ChoiceLabel setupFormatCombo(Minecraft mc, Screen gui, String tagname, final CompoundNBT currentData, final IModuleGuiChanged moduleGuiChanged) {
        final String modeFull = FormatStyle.MODE_FULL.getName();
        final String modeCompact = FormatStyle.MODE_COMPACT.getName();
        final String modeCommas = FormatStyle.MODE_COMMAS.getName();
        final ChoiceLabel modeButton = new ChoiceLabel().desiredWidth(58).desiredHeight(14).choices(modeFull, modeCompact, modeCommas).
                choiceTooltip(modeFull, "Full format: 3123555").
                choiceTooltip(modeCompact, "Compact format: 3.1M").
                choiceTooltip(modeCommas, "Comma format: 3,123,555").
                event((newChoice) -> {
//                    currentData.putInt(tagname, FormatStyle.getStyle(newChoice).ordinal());
                    currentData.putString(tagname, FormatStyle.getStyle(newChoice).getName());
                    moduleGuiChanged.updateData();
                });

        //FormatStyle currentFormat = FormatStyle.values()[currentData.getInt(tagname)];
        FormatStyle currentFormat = FormatStyle.getStyle(currentData.getString(tagname));
        modeButton.choice(currentFormat.getName());

        return modeButton;
    }

    private static ChoiceLabel setupModeCombo(Minecraft mc, Screen gui, final String componentName, final CompoundNBT currentData, final IModuleGuiChanged moduleGuiChanged) {
        String modeNone = "None";
        final String modePertick = componentName + "/t";
        final String modePct = componentName + "%";
        final ChoiceLabel modeButton = new ChoiceLabel().desiredWidth(50).desiredHeight(14).choices(modeNone, componentName, modePertick, modePct).
                choiceTooltip(modeNone, "No text is shown").
                choiceTooltip(componentName, "Show the amount of " + componentName).
                choiceTooltip(modePertick, "Show the average "+componentName+"/tick", "gain or loss").
                choiceTooltip(modePct, "Show the amount of "+componentName, "as a percentage").
                event((newChoice) -> {
                    if (componentName.equals(newChoice)) {
                        currentData.putBoolean("showdiff", false);
                        currentData.putBoolean("showpct", false);
                        currentData.putBoolean("hidetext", false);
                    } else if (modePertick.equals(newChoice)) {
                        currentData.putBoolean("showdiff", true);
                        currentData.putBoolean("showpct", false);
                        currentData.putBoolean("hidetext", false);
                    } else if (modePct.equals(newChoice)) {
                        currentData.putBoolean("showdiff", false);
                        currentData.putBoolean("showpct", true);
                        currentData.putBoolean("hidetext", false);
                    } else {
                        currentData.putBoolean("showdiff", false);
                        currentData.putBoolean("showpct", false);
                        currentData.putBoolean("hidetext", true);
                    }
                    moduleGuiChanged.updateData();
                });


        if (currentData.getBoolean("hidetext")) {
            modeButton.choice(modeNone);
        } else if (currentData.getBoolean("showdiff")) {
            modeButton.choice(modePertick);
        } else if (currentData.getBoolean("showpct")) {
            modeButton.choice(modePct);
        } else {
            modeButton.choice(componentName);
        }

        return modeButton;
    }
}
