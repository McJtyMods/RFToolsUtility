package mcjty.rftoolsutility.compat.jei;

import mcjty.rftoolsutility.modules.crafter.CrafterModule;
import mcjty.rftoolsutility.modules.crafter.blocks.CrafterContainer;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class CrafterRecipeTransferHandler implements IRecipeTransferHandler<CrafterContainer, CraftingRecipe> {

    public static void register(IRecipeTransferRegistration transferRegistry) {
        transferRegistry.addRecipeTransferHandler(new CrafterRecipeTransferHandler(), RecipeTypes.CRAFTING);
    }

    @Override
    @Nonnull
    public Class<CrafterContainer> getContainerClass() {
        return CrafterContainer.class;
    }

    @Override
    public Optional<MenuType<CrafterContainer>> getMenuType() {
        return Optional.of(CrafterModule.CONTAINER_CRAFTER.get());
    }

    @Override
    public RecipeType<CraftingRecipe> getRecipeType() {
        return RecipeTypes.CRAFTING;
    }

    @Override
    @Nullable
    public IRecipeTransferError transferRecipe(CrafterContainer container, CraftingRecipe recipe, IRecipeSlotsView recipeLayout, Player player, boolean maxTransfer, boolean doTransfer) {
        BlockEntity inventory = container.getTe();
        BlockPos pos = inventory.getBlockPos();
        List<IRecipeSlotView> slotViews = recipeLayout.getSlotViews();

        if (doTransfer) {
            RFToolsUtilityJeiPlugin.transferRecipe(slotViews, pos);
        }

        return null;
    }
}
