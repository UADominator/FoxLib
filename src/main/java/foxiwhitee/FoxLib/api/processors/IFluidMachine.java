package foxiwhitee.FoxLib.api.processors;

import foxiwhitee.FoxLib.processors.ProcessorFluidMachine;
import foxiwhitee.FoxLib.recipes.IFluidMachineRecipe;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.List;

public interface IFluidMachine<T extends IFluidMachineRecipe> extends IMachine<T>, IFluidHandler {
    @Override
    ProcessorFluidMachine<T> getMachine();

    void initializeInputTanks(List<FluidTank> tanks);
    void initializeOutputTanks(List<FluidTank> tanks);

    @Override
    default int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return getMachine().fill(resource, doFill);
    }

    @Override
    default FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return getMachine().drain(maxDrain, doDrain);
    }

    @Override
    default FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return getMachine().drain(resource, doDrain);
    }

    @Override
    default boolean canFill(ForgeDirection from, Fluid fluid) {
        return getMachine().canFill(fluid);
    }

    @Override
    default boolean canDrain(ForgeDirection from, Fluid fluid) {
        return getMachine().canDrain(fluid);
    }

    @Override
    default FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return getMachine().getTankInfo();
    }
}
