package foxiwhitee.FoxLib.recipes.json;

import foxiwhitee.FoxLib.recipes.json.annotations.JsonRecipe;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import stanhebben.zenscript.value.IAny;

public interface IJsonRecipe {
    void register();

    default String getType() {
        JsonRecipe type = this.getClass().getAnnotation(JsonRecipe.class);
        return type != null ? type.value() : "unknown";
    }

    default boolean hasOreDict() {
        JsonRecipe type = this.getClass().getAnnotation(JsonRecipe.class);
        return type != null && type.hasOreDict();
    }

    default boolean hasMineTweakerIntegration() {
        JsonRecipe type = this.getClass().getAnnotation(JsonRecipe.class);
        return type != null && type.hasMineTweaker();
    }

    default void addCraftByMineTweaker(IItemStack stack, IAny... inputs) {
        IIngredient[] ingredients = new IIngredient[inputs.length];

        for(int i = 0; i < inputs.length; ++i) {
            if (inputs[i].is(IIngredient.class)) {
                ingredients[i] = inputs[i].as(IIngredient.class);
            }
        }

        this.addCraftByMineTweaker(stack, ingredients);
    }

    default void addCraftByMineTweaker(IItemStack stack, IIngredient... inputs) {}

    default void removeCraftByMineTweaker(IItemStack stack) {}
}
