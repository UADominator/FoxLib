package foxiwhitee.FoxLib.recipes;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;

public interface IFluidMachineRecipe extends IMachineRecipe {
    List<FluidStack> getInputFluids();
    List<FluidStack> getOutputFluids();

    @SuppressWarnings("unused")
    default boolean matches(List<FluidStack> inputFluids, boolean exactQuantity, Fluid... notNeededFluids) {
        List<FluidStack> recipeInputs = getInputFluids();
        if (inputFluids == null || recipeInputs == null) return false;

        Map<Fluid, Integer> inputMap = new HashMap<>();
        for (FluidStack stack : inputFluids) {
            if (stack != null && stack.amount > 0) {
                inputMap.merge(stack.getFluid(), stack.amount, Integer::sum);
            }
        }

        Set<Fluid> ignored = new HashSet<>(Arrays.asList(notNeededFluids));

        if (exactQuantity) {
            for (Fluid fluid : inputMap.keySet()) {
                if (ignored.contains(fluid)) continue;

                boolean foundInRecipe = false;
                for (FluidStack recipeStack : recipeInputs) {
                    if (recipeStack != null && recipeStack.getFluid() == fluid) {
                        foundInRecipe = true;
                        break;
                    }
                }
                if (!foundInRecipe) return false;
            }
        }

        for (FluidStack needed : recipeInputs) {
            if (needed == null) continue;

            if (ignored.contains(needed.getFluid())) continue;

            int available = inputMap.getOrDefault(needed.getFluid(), 0);

            if (exactQuantity) {
                if (available != needed.amount) return false;
            } else {
                if (available < needed.amount) return false;
            }
        }

        return true;
    }
}
