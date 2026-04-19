package foxiwhitee.FoxLib.api.energy;

import cofh.api.energy.IEnergyProvider;
import net.minecraftforge.common.util.ForgeDirection;

public interface IDoubleEnergyProvider extends IDoubleEnergyConnection, IEnergyProvider {
    double extractDoubleEnergy(ForgeDirection direction, double maxExtract, boolean simulate);

    double getDoubleEnergyStored(ForgeDirection direction);

    double getMaxDoubleEnergyStored(ForgeDirection direction);

    @Override
    default int extractEnergy(ForgeDirection forgeDirection, int i, boolean b) {
        return (int) Math.min(Integer.MAX_VALUE, extractDoubleEnergy(forgeDirection, i, b));
    }

    @Override
    default int getEnergyStored(ForgeDirection forgeDirection) {
        return (int) Math.min(Integer.MAX_VALUE, getDoubleEnergyStored(forgeDirection));
    }

    @Override
    default int getMaxEnergyStored(ForgeDirection forgeDirection) {
        return (int) Math.min(Integer.MAX_VALUE, getMaxDoubleEnergyStored(forgeDirection));
    }
}
