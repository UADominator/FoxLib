package foxiwhitee.FoxLib.api.processors;

import foxiwhitee.FoxLib.processors.ProcessorPoweredMachine;
import foxiwhitee.FoxLib.recipes.IMachineRecipe;

public interface IPoweredMachine<T extends IMachineRecipe> extends IMachine<T> {
    @Override
    ProcessorPoweredMachine<T> getMachine();

    double needPower(T recipe);
}
