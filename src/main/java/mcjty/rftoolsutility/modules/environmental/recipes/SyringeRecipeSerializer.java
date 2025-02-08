package mcjty.rftoolsutility.modules.environmental.recipes;

// @todo 1.21 recipe
public class SyringeRecipeSerializer {} /*implements RecipeSerializer<SyringeBasedRecipe> {

    private final ShapedRecipe.Serializer serializer = new ShapedRecipe.Serializer();

    @Nonnull
    @Override
    public SyringeBasedRecipe fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject root) {
        ShapedRecipe shapedRecipe = serializer.fromJson(recipeId, root);
        String mob = root.get("mob").getAsString();
        int syringe = root.get("syringe").getAsInt();
        ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(root, "result"));
        return new SyringeBasedRecipe(shapedRecipe, ResourceLocation.fromNamespaceAndPath(mob), syringe, result);
    }

    @Nullable
    @Override
    public SyringeBasedRecipe fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull FriendlyByteBuf buffer) {
        ShapedRecipe shapedRecipe = serializer.fromNetwork(recipeId, buffer);
        ResourceLocation mobId = buffer.readResourceLocation();
        int syringeIndex = buffer.readInt();
        return new SyringeBasedRecipe(shapedRecipe, mobId, syringeIndex, BaseRecipe.getResultItem(shapedRecipe, null));
    }

    @Override
    public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull SyringeBasedRecipe recipe) {
        serializer.toNetwork(buffer, recipe);
        buffer.writeResourceLocation(recipe.getMobId());
        buffer.writeInt(recipe.getSyringeIndex());
    }
}
*/