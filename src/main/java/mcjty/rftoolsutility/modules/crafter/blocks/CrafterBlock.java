package mcjty.rftoolsutility.modules.crafter.blocks;

import mcjty.lib.api.container.ItemInventory;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.crafting.IComponentsToPreserve;
import mcjty.lib.setup.Registration;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.crafter.CrafterModule;
import mcjty.rftoolsutility.modules.crafter.data.CrafterData;
import mcjty.rftoolsutility.modules.crafter.data.CraftingRecipe;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Collection;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;


public class CrafterBlock extends BaseBlock implements IComponentsToPreserve {

    public CrafterBlock(BlockEntityType.BlockEntitySupplier<BlockEntity> tileEntitySupplier) {
        super(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsutility:machines/crafter"))
                .infusable()
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header(), gold(),
                        parameter("contents", stack -> Integer.toString(countItems(stack))),
                        parameter("recipes", stack -> Integer.toString(countRecipes(stack))))
                .tileEntitySupplier(tileEntitySupplier));
    }

    private static int countRecipes(ItemStack itemStack) {
        CrafterData data = itemStack.get(CrafterModule.ITEM_CRAFTER_DATA);
        if (data == null) {
            return 0;
        }
        int rc = 0;
        for (CraftingRecipe recipe : data.recipes()) {
            if (!recipe.getResult().isEmpty()) {
                rc++;
            }
        }
        return rc;
    }

    private static int countItems(ItemStack itemStack) {
        ItemInventory items = itemStack.get(Registration.ITEM_INVENTORY);
        if (items == null) {
            return 0;
        }
        int rc = 0;
        for (int i = 0 ; i < items.items().size() ; i++) {
            ItemStack stack = items.items().get(i);
            if (!stack.isEmpty()) {
                rc++;
            }
        }
        return rc;
    }

    @Override
    public Collection<DataComponentType<?>> getComponentsToPreserve() {
        return List.of(Registration.ITEM_INFUSABLE.get(), Registration.ITEM_ENERGY.get(), Registration.ITEM_INVENTORY.get(), CrafterModule.ITEM_CRAFTER_DATA.get());
    }
}
