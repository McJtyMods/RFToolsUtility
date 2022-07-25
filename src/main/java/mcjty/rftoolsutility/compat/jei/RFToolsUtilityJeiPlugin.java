package mcjty.rftoolsutility.compat.jei;

import mcjty.lib.varia.ItemStackList;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JeiPlugin
public class RFToolsUtilityJeiPlugin implements IModPlugin {

    public static void transferRecipe(List<IRecipeSlotView> slotViews, BlockPos pos) {
        ItemStackList items = ItemStackList.create(10);
        for (int i = 0 ; i < 10 ; i++) {
            items.set(i, ItemStack.EMPTY);
        }
        for (int i = 0 ; i < slotViews.size() ; i++) {
            List<ITypedIngredient<?>> allIngredients = slotViews.get(i).getAllIngredients().collect(Collectors.toList());
            if (!allIngredients.isEmpty()) {
                ItemStack stack = allIngredients.get(0).getIngredient(VanillaTypes.ITEM_STACK).get();
                items.set(i, stack);
            }
        }
        RFToolsUtilityMessages.INSTANCE.sendToServer(new PacketSendRecipe(items, pos));
    }


    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(RFToolsUtility.MODID, "rftoolsutility");
    }

    @Override
    public void registerRecipeTransferHandlers(@Nonnull IRecipeTransferRegistration registration) {
        CrafterRecipeTransferHandler.register(registration);
//        ModularStorageRecipeTransferHandler.register(registration);
//        ModularStorageItemRecipeTransferHandler.register(registration);
//        RemoteStorageItemRecipeTransferHandler.register(registration);
//        StorageScannerRecipeTransferHandler.register(registration);
    }
}
