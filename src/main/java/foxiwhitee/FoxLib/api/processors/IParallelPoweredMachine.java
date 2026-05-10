package foxiwhitee.FoxLib.api.processors;

import foxiwhitee.FoxLib.processors.ProcessorParallelPoweredMachine;
import foxiwhitee.FoxLib.recipes.IMachineRecipe;

public interface IParallelPoweredMachine<T extends IMachineRecipe> extends IMachine<T> {
    @Override
    ProcessorParallelPoweredMachine<T> getMachine();

    double needPower(T recipe);
}
