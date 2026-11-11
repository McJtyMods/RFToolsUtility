package mcjty.rftoolsutility.modules.screen.modulesclient;

import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleRenderHelper;
import mcjty.rftoolsbase.api.screens.ModuleRenderInfo;
import mcjty.rftoolsutility.modules.screen.items.modules.InventoryModuleItem;
import mcjty.rftoolsutility.modules.screen.modules.InventoryScreenModule;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class InventoryClientScreenModule implements IClientScreenModule<InventoryScreenModule.ModuleDataStacks> {

    @Override
    public TransformMode getTransformMode(ItemStack moduleItem) {
        return TransformMode.TEXT;
    }

    @Override
    public int getHeight(ItemStack moduleItem) {
        return 22;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, SubmitNodeCollector collector, IModuleRenderHelper helper, Font font,
                       int currenty, InventoryScreenModule.ModuleDataStacks screenData, ModuleRenderInfo info) {
        if (screenData == null) {
            return;
        }
        InventoryScreenModule module = InventoryModuleItem.data(info.moduleStack);
        int[] slots = {module.getSlot1(), module.getSlot2(), module.getSlot3(), module.getSlot4()};
        int x = 10;
        for (int index = 0; index < slots.length; index++) {
            if (slots[index] == -1) {
                continue;
            }
            ItemStack stack = screenData.getStack(index);
            if (!stack.isEmpty()) {
                graphics.item(stack, x, currenty);
                int count = stack.getCount();
                if (count > 1) {
                    String text = count < 10000 ? Integer.toString(count) : count < 1000000 ? count / 1000 + "k"
                            : count < 1000000000 ? count / 1000000 + "m" : count / 1000000000 + "g";
                    graphics.text(font, text, x + 17 - font.width(text), currenty + 9, 0xffffffff, false);
                }
                if (stack.isBarVisible()) {
                    graphics.fill(x + 2, currenty + 13, x + 15, currenty + 15, 0xff000000);
                    graphics.fill(x + 2, currenty + 13, x + 2 + stack.getBarWidth(), currenty + 14, 0xff000000 | stack.getBarColor());
                }
            }
            x += 30;
        }
    }

    @Override
    public void mouseClick(ItemStack moduleStack, Level world, int x, int y, boolean clicked) {
    }

    @Override
    public boolean needsServerData() {
        return true;
    }
}
