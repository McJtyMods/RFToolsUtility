package mcjty.rftoolsutility.modules.screen.modulesclient.helper;

import mcjty.lib.gui.events.BlockRenderEvent;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.widgets.*;
import mcjty.rftoolsbase.api.screens.BarMode;
import mcjty.rftoolsbase.api.screens.FormatStyle;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsutility.modules.screen.IModuleGuiChanged;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static mcjty.lib.gui.widgets.Widgets.horizontal;
import static mcjty.lib.gui.widgets.Widgets.vertical;

public class ScreenModuleGuiBuilder implements IModuleGuiBuilder {
    private Minecraft mc;
    private Screen gui;
    private ItemStack module;
    private IModuleGuiChanged moduleGuiChanged;

    private Panel panel;
    private List<Widget<?>> row = new ArrayList<>();

    public ScreenModuleGuiBuilder(Minecraft mc, Screen gui, ItemStack module, IModuleGuiChanged moduleGuiChanged) {
        this.gui = gui;
        this.mc = mc;
        this.moduleGuiChanged = moduleGuiChanged;
        this.module = module;
        panel = vertical(3, 1);
    }

    @Override
    public ItemStack getCurrentModule() {
        return module;
    }

    @Override
    public Level getWorld() {
        return mc.player.getCommandSenderWorld();
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
    public IModuleGuiBuilder text(BiConsumer<ItemStack, String> setter, Function<ItemStack, String> getter, String... tooltip) {
        TextField textField = new TextField().desiredHeight(15).tooltips(tooltip).event((newText) -> {
            setter.accept(module, newText);
            moduleGuiChanged.updateData();
        });
        row.add(textField);
        if (module != null) {
            textField.text(getter.apply(module));
        }
        return this;
    }

    @Override
    public ScreenModuleGuiBuilder integer(BiConsumer<ItemStack, Integer> setter, Function<ItemStack, Integer> getter, String... tooltip) {
        TextField textField = new TextField().desiredHeight(15).tooltips(tooltip).event((newText) -> {
            int value;
            try {
                value = Integer.parseInt(newText);
            } catch (NumberFormatException e) {
                value = 0;
            }
            setter.accept(module, value);
            moduleGuiChanged.updateData();
        });
        row.add(textField);
        if (module != null) {
            textField.text(Integer.toString(getter.apply(module)));
        }
        return this;
    }

    @Override
    public IModuleGuiBuilder toggle(BiConsumer<ItemStack, Boolean> setter, Function<ItemStack, Boolean> getter, String label, String... tooltip) {
        final ToggleButton toggleButton = new ToggleButton().text(label).tooltips(tooltip).desiredHeight(14).checkMarker(true);
        toggleButton.event(() -> {
            setter.accept(module, toggleButton.isPressed());
            moduleGuiChanged.updateData();
        });

        row.add(toggleButton);
        if (module != null) {
            toggleButton.pressed(getter.apply(module));
        }
        return this;
    }

    @Override
    public ScreenModuleGuiBuilder toggleNegative(BiConsumer<ItemStack, Boolean> setter, Function<ItemStack, Boolean> getter, String label, String... tooltip) {
        final ToggleButton toggleButton = new ToggleButton().text(label).tooltips(tooltip).desiredHeight(14).desiredWidth(36).checkMarker(true);
        toggleButton.event(() -> {
            setter.accept(module, !toggleButton.isPressed());
            moduleGuiChanged.updateData();
        });

        row.add(toggleButton);
        if (module != null) {
            toggleButton.pressed(!getter.apply(module));
        } else {
            toggleButton.pressed(true);
        }
        return this;
    }

    @Override
    public IModuleGuiBuilder color(BiConsumer<ItemStack, Integer> setter, Function<ItemStack, Integer> getter, String... tooltip) {
        ColorSelector colorSelector = new ColorSelector().tooltips(tooltip)
                .desiredWidth(20).desiredHeight(14).event((newColor) -> {
                    setter.accept(module, newColor);
                    moduleGuiChanged.updateData();
                });
        row.add(colorSelector);
        if (module != null) {
            int currentColor = getter.apply(module);
            if (currentColor != 0) {
                colorSelector.currentColor(currentColor);
            }
        }
        return this;
    }

    @Override
    public IModuleGuiBuilder choices(BiConsumer<ItemStack, String> setter, Function<ItemStack, String> getter, String tooltip, String... choices) {
        ChoiceLabel choiceLabel = new ChoiceLabel().tooltips(tooltip)
                .desiredWidth(50).desiredHeight(14);
        for (String s : choices) {
            choiceLabel.choices(s);
        }
        choiceLabel.event((newChoice) -> {
            setter.accept(module, newChoice);
            moduleGuiChanged.updateData();
        });
        row.add(choiceLabel);
        if (module != null) {
            String currentChoice = getter.apply(module);
            if (!currentChoice.isEmpty()) {
                choiceLabel.choice(currentChoice);
            }
        }
        return this;
    }

    @Override
    public IModuleGuiBuilder choices(BiConsumer<ItemStack, Integer> setter, Function<ItemStack, Integer> getter, Choice... choices) {
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
            setter.accept(module, choicesMap.get(newChoice));
            moduleGuiChanged.updateData();
        });
        row.add(choiceLabel);
        if (getCurrentModule() != null) {
            int currentChoice = getter.apply(getCurrentModule());
            if (currentChoice < choices.length && currentChoice >= 0) {
                choiceLabel.choice(choices[currentChoice].getName());
            }
        }
        return this;
    }

    @Override
    public ScreenModuleGuiBuilder format(BiConsumer<ItemStack, FormatStyle> setter, Function<ItemStack, FormatStyle> getter) {
        ChoiceLabel label = setupFormatCombo(mc, gui, setter, getter, module, moduleGuiChanged);
        row.add(label);
        return this;
    }

    @Override
    public ScreenModuleGuiBuilder mode(BiConsumer<ItemStack, BarMode> setter, Function<ItemStack, BarMode> getter, String componentName) {
        ChoiceLabel label = setupModeCombo(mc, gui, setter, getter, componentName, module, moduleGuiChanged);
        row.add(label);
        return this;
    }

    @Override
    public IModuleGuiBuilder block(Function<ItemStack, GlobalPos> getter, Function<ItemStack, String> nameGetter) {
        String monitoring;
        if (module == null) {
            monitoring = "<not set>";
        } else {
            GlobalPos pos = getter.apply(module);
            if (pos != null) {
                Level world = getWorld();
                if (pos.dimension().equals(world.dimension())) {
                    BlockPos p = pos.pos();
                    int x = p.getX();
                    int y = p.getY();
                    int z = p.getZ();
                    monitoring = nameGetter.apply(module);
                    Block block = world.getBlockState(p).getBlock();
                    row.add(new BlockRender().renderItem(block).desiredWidth(20));
                    row.add(Widgets.label(x + "," + y + "," + z).horizontalAlignment(HorizontalAlignment.ALIGN_LEFT).desiredWidth(150));
                } else {
                    monitoring = "<unreachable>";
                }
            } else {
                monitoring = "<not set>";
            }
        }
        row.add(Widgets.label(monitoring));
        return this;
    }

    @Override
    public IModuleGuiBuilder ghostStack(BiConsumer<ItemStack, ItemStack> setter, Function<ItemStack, ItemStack> getter) {
        ItemStack stack = getter.apply(module);
        BlockRender blockRender = new BlockRender().renderItem(stack).desiredWidth(18).desiredHeight(18).filledRectThickness(1).filledBackground(0xff555555);
        row.add(blockRender);
        blockRender.event(new BlockRenderEvent() {
            @Override
            public void select() {
                ItemStack holding = Minecraft.getInstance().player.containerMenu.getCarried();
//                ItemStack holding = Minecraft.getInstance().player.getInventory().getSelected();
                if (holding.isEmpty()) {
                    setter.accept(module, ItemStack.EMPTY);
                    blockRender.renderItem(null);
                } else {
                    ItemStack copy = holding.copy();
                    copy.setCount(1);
                    blockRender.renderItem(copy);
                    setter.accept(module, copy);
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

    private static ChoiceLabel setupFormatCombo(Minecraft mc, Screen gui, BiConsumer<ItemStack, FormatStyle> setter, Function<ItemStack, FormatStyle> getter, final ItemStack module, final IModuleGuiChanged moduleGuiChanged) {
        final String modeFull = FormatStyle.MODE_FULL.getName();
        final String modeCompact = FormatStyle.MODE_COMPACT.getName();
        final String modeCommas = FormatStyle.MODE_COMMAS.getName();
        final ChoiceLabel modeButton = new ChoiceLabel().desiredWidth(58).desiredHeight(14).choices(modeFull, modeCompact, modeCommas).
                choiceTooltip(modeFull, "Full format: 3123555").
                choiceTooltip(modeCompact, "Compact format: 3.1M").
                choiceTooltip(modeCommas, "Comma format: 3,123,555").
                event((newChoice) -> {
//                    currentData.putInt(tagname, FormatStyle.getStyle(newChoice).ordinal());
                    setter.accept(module, FormatStyle.getStyle(newChoice));
                    moduleGuiChanged.updateData();
                });

        //FormatStyle currentFormat = FormatStyle.values()[currentData.getInt(tagname)];
        FormatStyle currentFormat = getter.apply(module);
        modeButton.choice(currentFormat.getName());

        return modeButton;
    }

    private static ChoiceLabel setupModeCombo(Minecraft mc, Screen gui, BiConsumer<ItemStack, BarMode> setter, Function<ItemStack, BarMode> getter, final String componentName, ItemStack module, final IModuleGuiChanged moduleGuiChanged) {
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
                        setter.accept(module, BarMode.MODE_TEXT);
                    } else if (modePertick.equals(newChoice)) {
                        setter.accept(module, BarMode.MODE_PERTICK);
                    } else if (modePct.equals(newChoice)) {
                        setter.accept(module, BarMode.MODE_PERCENTAGE);
                    } else {
                        setter.accept(module, BarMode.MODE_NONE);
                    }
                    moduleGuiChanged.updateData();
                });

        BarMode current = getter.apply(module);
        modeButton.choice(switch (current) {
            case MODE_NONE -> modeNone;
            case MODE_PERTICK -> modePertick;
            case MODE_PERCENTAGE -> modePct;
            default -> componentName;
        });
        return modeButton;
    }
}
