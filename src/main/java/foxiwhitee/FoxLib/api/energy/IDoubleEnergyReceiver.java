package foxiwhitee.FoxLib.api.energy;

import cofh.api.energy.IEnergyReceiver;
import net.minecraftforge.common.util.ForgeDirection;

public interface IDoubleEnergyReceiver extends IDoubleEnergyConnection, IEnergyReceiver {
    double receiveDoubleEnergy(ForgeDirection direction, double maxReceive, boolean simulate);

    double getDoubleEnergyStored(ForgeDirection direction);

    double getMaxDoubleEnergyStored(ForgeDirection direction);

    @Override
    default int receiveEnergy(ForgeDirection forgeDirection, int i, boolean b) {
        return (int) Math.min(Integer.MAX_VALUE, this.receiveDoubleEnergy(forgeDirection, i, b));
    }

    @Override
    default int getEnergyStored(ForgeDirection forgeDirection) {
        return (int) Math.min(Integer.MAX_VALUE, this.getDoubleEnergyStored(forgeDirection));
    }

    @Override
    default int getMaxEnergyStored(ForgeDirection forgeDirection) {
        return (int) Math.min(Integer.MAX_VALUE, this.getMaxDoubleEnergyStored(forgeDirection));
    }
}
