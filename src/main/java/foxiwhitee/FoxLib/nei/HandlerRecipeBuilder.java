package foxiwhitee.FoxLib.nei;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class HandlerRecipeBuilder<T> {
    private final NeiProcessor processor;
    private final String id;
    private final RecipeDefinition<T> def = new RecipeDefinition<>();
    private final boolean buildIf;

    public HandlerRecipeBuilder(NeiProcessor processor, String id, boolean buildIf) {
        this.processor = processor;
        this.id = id;
        this.buildIf = buildIf;
        this.def.rectangles = new ArrayList<>();
    }

    public HandlerRecipeBuilder<T> recipes(List<T> recipes) {
        this.def.recipes = recipes;
        return this;
    }

    public HandlerRecipeBuilder<T> processInputs(RecipeDefinition.ProcessItems<T> p) {
        this.def.processInputs = p;
        return this;
    }

    public HandlerRecipeBuilder<T> processOutputs(RecipeDefinition.ProcessItems<T> p) {
        this.def.processOutputs = p;
        return this;
    }

    public HandlerRecipeBuilder<T> processInputsFluids(RecipeDefinition.ProcessFluids<T> p) {
        this.def.processInputsFluids = p;
        return this;
    }

    public HandlerRecipeBuilder<T> processOutputsFluids(RecipeDefinition.ProcessFluids<T> p) {
        this.def.processOutputsFluids = p;
        return this;
    }

    public HandlerRecipeBuilder<T> processLoading(RecipeDefinition.LoadRecipesObjects<T> l) {
        this.def.loadCraftingRecipesObjects = l;
        return this;
    }

    public HandlerRecipeBuilder<T> processLoading(RecipeDefinition.LoadRecipesStack<T> l) {
        this.def.loadCraftingRecipesStack = l;
        return this;
    }

    public HandlerRecipeBuilder<T> processUsage(RecipeDefinition.LoadRecipesStack<T> l) {
        this.def.loadUsageRecipes = l;
        return this;
    }

    public HandlerRecipeBuilder<T> tooltip(RecipeDefinition.RecipeTooltip<T> tooltip) {
        this.def.tooltip = tooltip;
        return this;
    }

    public HandlerRecipeBuilder<T> recipeButton(int x, int y, int width, int height) {
        this.def.rectangles.add(new Rectangle(x, y, width, height));
        return this;
    }

    public HandlerRecipeBuilder<T> perPage(int perPage) {
        this.def.perPage = perPage;
        return this;
    }

    public Draw<T> drawBG() {
        if (def.drawBG == null) {
            def.drawBG = new Draw<>(this, processor.modId());
        }
        return def.drawBG;
    }

    public Draw<T> drawFG() {
        if (def.drawFG == null) {
            def.drawFG = new Draw<>(this, processor.modId());
        }
        return def.drawFG;
    }

    public UniversalRecipeHandler<T> register() {
        if (!buildIf) return null;

        UniversalRecipeHandler<T> handler = new UniversalRecipeHandler<>(id, def);
        processor.configuration().addHandler(handler);

        return handler;
    }
}
