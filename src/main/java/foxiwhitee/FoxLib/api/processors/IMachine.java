package foxiwhitee.FoxLib.api.processors;

import foxiwhitee.FoxLib.processors.ProcessorMachine;
import foxiwhitee.FoxLib.recipes.IMachineRecipe;
import foxiwhitee.FoxLib.tile.inventory.FoxInternalInventory;

import java.util.List;

public interface IMachine<T extends IMachineRecipe> {
    ProcessorMachine<T> getMachine();
    FoxInternalInventory getInternalInventory();
    FoxInternalInventory getOutputInventory();
    List<T> getRecipes();
    void markForUpdate();
    double getRealSpeed();

    default int getOperations() {
        return 1;
    }
}
