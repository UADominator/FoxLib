package foxiwhitee.FoxLib.api.processors;

import foxiwhitee.FoxLib.processors.ProcessorFluidPoweredMachine;
import foxiwhitee.FoxLib.recipes.IFluidMachineRecipe;

public interface IFluidPoweredMachine<T extends IFluidMachineRecipe> extends IFluidMachine<T> {
    @Override
    ProcessorFluidPoweredMachine<T> getMachine();

    double needPower(T recipe);
}
