package foxiwhitee.FoxLib.nei;

import codechicken.nei.PositionedStack;
import net.minecraftforge.fluids.FluidStack;

import java.awt.*;
import java.util.List;

public class RecipeDefinition<T> {
    public List<T> recipes;
    public List<Rectangle> rectangles;
    public Draw<T> drawBG;
    public Draw<T> drawFG;

    public ProcessItems<T> processInputs;
    public ProcessItems<T> processOutputs;
    public ProcessFluids<T> processInputsFluids;
    public ProcessFluids<T> processOutputsFluids;
    public RecipeTooltip<T> tooltip;

    public int perPage = 1;

    @FunctionalInterface
    public interface ProcessItems<T> {
        void process(T recipe, List<PositionedStack> stacks);
    }

    @FunctionalInterface
    public interface ProcessFluids<T> {
        void process(T recipe, List<FluidStack> fluids);
    }

    @FunctionalInterface
    public interface RecipeTooltip<T> {
        void add(UniversalRecipeHandler<T>.CachedUniversalRecipe recipe, List<String> currentTip, Point mouse);
    }
}
