package mcjty.rftoolsutility.modules.crafter.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.crafting.IComponentsToPreserve;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
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
        // @todo 1.21 data
        return 0;
//        CompoundTag tagCompound = itemStack.getTag();
//        if (tagCompound == null) {
//            return 0;
//        }
//        ListTag recipeTagList = tagCompound.getList("Recipes", Tag.TAG_COMPOUND);
//        int rc = 0;
//        for (int i = 0 ; i < recipeTagList.size() ; i++) {
//            CompoundTag tagRecipe = recipeTagList.getCompound(i);
//            CompoundTag resultCompound = tagRecipe.getCompound("Result");
//            ItemStack stack = ItemStack.of(resultCompound);
//            if (!stack.isEmpty()) {
//                rc++;
//            }
//        }
//        return rc;
    }

    private static int countItems(ItemStack itemStack) {
        // @todo 1.21 data
        return 0;
//        CompoundTag tagCompound = itemStack.getTag();
//        if (tagCompound == null) {
//            return 0;
//        }
//        ListTag bufferTagList = tagCompound.getList("Items", Tag.TAG_COMPOUND);
//
//        int rc = 0;
//        for (int i = 0 ; i < bufferTagList.size() ; i++) {
//            CompoundTag itemTag = bufferTagList.getCompound(i);
//            ItemStack stack = ItemStack.of(itemTag);
//            if (!stack.isEmpty()) {
//                rc++;
//            }
//        }
//        return rc;
    }

    @Override
    public Collection<DataComponentType<?>> getComponentsToPreserve() {
        // @todo 1.21 data
        return List.of();
//        return Collections.singleton("BlockEntityTag");
    }
}
