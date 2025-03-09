package mcjty.rftoolsutility.modules.environmental.recipes;

import com.google.common.collect.Maps;
import mcjty.lib.crafting.BaseShapedRecipe;
import mcjty.lib.crafting.IRecipeBuilder;
import mcjty.lib.varia.Tools;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SyringeRecipeBuilder implements IRecipeBuilder<SyringeRecipeBuilder> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Item result;
    private final int count;
//    private final List<String> pattern = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
    private ShapedRecipeBuilder builder;
    private String group;
    private final ResourceLocation mobId;
    private int syringeIndex;

    public SyringeRecipeBuilder(ItemLike resultIn, int countIn, ResourceLocation mobId, int syringeIndex) {
        this.result = resultIn.asItem();
        this.count = countIn;
        this.mobId = mobId;
        this.syringeIndex = syringeIndex;
        builder = new ShapedRecipeBuilder(RecipeCategory.MISC, this.result, this.count);
    }

    public static SyringeRecipeBuilder shaped(ItemLike resultIn, ResourceLocation mobId, int syringeIndex) {
        return shaped(resultIn, 1, mobId, syringeIndex);
    }

    public static SyringeRecipeBuilder shaped(ItemLike resultIn, int countIn, ResourceLocation mobId, int syringeIndex) {
        return new SyringeRecipeBuilder(resultIn, countIn, mobId, syringeIndex);
    }


    @Override
    public SyringeRecipeBuilder define(Character symbol, TagKey<Item> tagIn) {
        builder = builder.define(symbol, Ingredient.of(tagIn));
        return this;
    }

    @Override
    public SyringeRecipeBuilder define(Character symbol, ItemLike itemIn) {
        builder = builder.define(symbol, Ingredient.of(itemIn));
        return this;
    }

    @Override
    public SyringeRecipeBuilder define(Character symbol, Ingredient ingredientIn) {
        builder = builder.define(symbol, ingredientIn);
        return this;
    }

    @Override
    public SyringeRecipeBuilder patternLine(String patternIn) {
        builder = builder.pattern(patternIn);
        return this;
    }

    public SyringeRecipeBuilder unlockedBy(String name, Criterion<? extends CriterionTriggerInstance> criterionIn) {
        builder = builder.unlockedBy(name, criterionIn);
        return this;
    }

    @Override
    public SyringeRecipeBuilder setGroup(String groupIn) {
        this.group = groupIn;
        return this;
    }

    @Override
    public void build(RecipeOutput consumerIn) {
        this.build(consumerIn, Tools.getId(this.result));
    }

    @Override
    public void build(RecipeOutput consumerIn, String save) {
        ResourceLocation resourcelocation = Tools.getId(this.result);
        if ((ResourceLocation.parse(save)).equals(resourcelocation)) {
            throw new IllegalStateException("Shaped Recipe " + save + " should remove its 'save' argument");
        } else {
            this.build(consumerIn, ResourceLocation.parse(save));
        }
    }

    @Override
    public void build(RecipeOutput consumerIn, ResourceLocation id) {
        this.validate(id);
        ResourceLocation mob = this.mobId;
        int index = this.syringeIndex;
        builder.save(new RecipeOutput() {
            @Override
            public Advancement.Builder advancement() {
                return advancementBuilder;
            }

            @Override
            public void accept(ResourceLocation resourceLocation, Recipe<?> recipe, @Nullable AdvancementHolder advancementHolder, ICondition... iConditions) {
                consumerIn.accept(resourceLocation, new SyringeBasedRecipe((ShapedRecipe) recipe, mob, index), advancementHolder, iConditions);
            }
        });
    }


    private void validate(ResourceLocation id) {
//        if (this.pattern.isEmpty()) {
//            throw new IllegalStateException("No pattern is defined for shaped recipe " + id + "!");
//        } else {
//            Set<Character> set = Sets.newHashSet(this.key.keySet());
//            set.remove(' ');
//
//            for(String s : this.pattern) {
//                for(int i = 0; i < s.length(); ++i) {
//                    char c0 = s.charAt(i);
//                    if (!this.key.containsKey(c0) && c0 != ' ') {
//                        throw new IllegalStateException("Pattern in recipe " + id + " uses undefined symbol '" + c0 + "'");
//                    }
//
//                    set.remove(c0);
//                }
//            }
//
//            if (!set.isEmpty()) {
//                throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + id);
//            } else if (this.pattern.size() == 1 && this.pattern.get(0).length() == 1) {
//                throw new IllegalStateException("Shaped recipe " + id + " only takes in a single item - should it be a shapeless recipe instead?");
//                // @todo 1.21 recipe
//            } else if (this.advancementBuilder.getCriteria().isEmpty()) {
//                throw new IllegalStateException("No way of obtaining recipe " + id);
//            }
//        }
    }
}