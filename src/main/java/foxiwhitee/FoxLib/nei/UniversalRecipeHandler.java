package foxiwhitee.FoxLib.nei;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import codechicken.nei.PositionedStack;
import foxiwhitee.FoxLib.FoxLib;
import foxiwhitee.FoxLib.utils.helpers.LocalizationUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class UniversalRecipeHandler<T> extends TemplateRecipeHandler {
    private static final Map<String, RecipeDefinition<?>> definitions = new HashMap<>();

    private String id;
    private RecipeDefinition<T> def;
    private final CacheBuilder builder = new CacheBuilder();

    public UniversalRecipeHandler() {}

    public UniversalRecipeHandler(String id, RecipeDefinition<T> def) {
        this.id = id;
        this.def = def;
        definitions.put(id, def);
        this.transferRects.clear();
        this.loadTransferRects();
    }

    @Override
    @SuppressWarnings("unchecked")
    public TemplateRecipeHandler newInstance() {
        if (id == null) return super.newInstance();
        return new UniversalRecipeHandler<>(id, (RecipeDefinition<T>) definitions.get(id));
    }

    @Override
    public void loadTransferRects() {
        if (id != null && def != null) {
            addRectsFor(id, def);
        } else {
            definitions.forEach(this::addRectsFor);
        }
    }

    private void addRectsFor(String targetId, RecipeDefinition<?> targetDef) {
        if (targetDef.rectangles != null) {
            for (Rectangle r : targetDef.rectangles) {
                this.transferRects.add(new RecipeTransferRect(r, targetId));
            }
        }
    }

    @Override
    public int recipiesPerPage() {
        return id != null ? def.perPage : 1;
    }

    @Override
    public String getHandlerId() {
        return id;
    }

    @Override
    public String getRecipeName() {
        return LocalizationUtils.localize(id);
    }

    @Override
    public String getGuiTexture() {
        return FoxLib.MODID + ":textures/gui/guiNeiBlank.png";
    }

    @Override
    public String getOverlayIdentifier() {
        return id;
    }

    @Override
    public void drawBackground(int recipe) {
        if (id == null) return;
        if (def.drawBG != null) {
            def.drawBG.draw((CachedUniversalRecipe) arecipes.get(recipe));
        }
    }

    @Override
    public void drawForeground(int recipe) {
        if (id == null) return;
        if (def.drawFG != null) {
            def.drawFG.draw((CachedUniversalRecipe) arecipes.get(recipe));
        }
    }

    @Override
    public List<String> handleTooltip(GuiRecipe<?> gui, List<String> currenttip, int recipe) {
        currenttip = super.handleTooltip(gui, currenttip, recipe);
        if (id == null) return currenttip;
        if (def.tooltip != null) {
            Point mouse = GuiDraw.getMousePosition();
            Point offset = gui.getRecipePosition(recipe);
            int xSize = 176;
            int ySize = Math.min(Math.max(gui.height - 68, 166), 370);

            int guiLeft = (gui.width - xSize) / 2;
            int guiTop = (gui.height - ySize) / 2 + 10;

            int recipeLeft = guiLeft + offset.x;
            int recipeTop = guiTop + offset.y;

            CachedUniversalRecipe cRecipe = (CachedUniversalRecipe) arecipes.get(recipe);
            Point relMouse = new Point(mouse.x - recipeLeft, mouse.y - recipeTop);
            def.tooltip.add(cRecipe, currenttip, relMouse);
        }
        return currenttip;
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (id == null) return;
        if (outputId.equals(id) && def.recipes != null) {
            if (def.loadCraftingRecipesObjects != null) {
                def.loadCraftingRecipesObjects.load(def.recipes, builder, outputId, results);
            } else {
                for (T r : def.recipes) {
                    this.arecipes.add(new CachedUniversalRecipe(r));
                }
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        if (def.loadCraftingRecipesStack != null) {
            def.loadCraftingRecipesStack.load(def.recipes, builder, result);
            return;
        }
        for (T recipe : def.recipes) {
            if (recipe != null) {
                CachedUniversalRecipe cRecipe = new CachedUniversalRecipe(recipe);
                if (cRecipe.contains(cRecipe.outputs, result)) {
                    this.arecipes.add(cRecipe);
                }
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        if (def.loadUsageRecipes != null) {
            def.loadUsageRecipes.load(def.recipes, builder, ingredient);
            return;
        }
        for(T recipe : def.recipes) {
            if (recipe != null) {
                CachedUniversalRecipe cRecipe = new CachedUniversalRecipe(recipe);
                if (cRecipe.contains(cRecipe.getIngredients(), ingredient)) {
                    this.arecipes.add(cRecipe);
                }
            }
        }
    }

    public class CachedUniversalRecipe extends CachedRecipe {
        public final T raw;
        public final List<PositionedStack> inputs = new ArrayList<>();
        public final List<PositionedStack> outputs = new ArrayList<>();
        public final List<FluidStack> inFluids = new ArrayList<>();
        public final List<FluidStack> outFluids = new ArrayList<>();

        public CachedUniversalRecipe(T recipe) {
            this.raw = recipe;
            if (def.processInputs != null) {
                def.processInputs.process(recipe, inputs);
            }
            if (def.processOutputs != null) {
                def.processOutputs.process(recipe, outputs);
            }
            if (def.processInputsFluids != null) {
                def.processInputsFluids.process(recipe, inFluids);
            }
            if (def.processOutputsFluids != null) {
                def.processOutputsFluids.process(recipe, outFluids);
            }
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return getCycledIngredients(cycleticks / 20, inputs);
        }

        @Override
        public PositionedStack getResult() {
            return outputs.isEmpty() ? null : outputs.get(0);
        }

        @Override
        public List<PositionedStack> getOtherStacks() {
            return outputs;
        }
    }

    public class CacheBuilder {
        private CacheBuilder() {}

        public void invoke(T recipe) {
            UniversalRecipeHandler.this.arecipes.add(new CachedUniversalRecipe(recipe));
        }
    }
}
