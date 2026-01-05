package foxiwhitee.FoxLib.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

public interface IDoubleEnergyReceiver extends IDoubleEnergyConnection {
    double receiveDoubleEnergy(ForgeDirection direction, double maxReceive, boolean simulate);

    double getDoubleEnergyStored(ForgeDirection direction);

    double getMaxDoubleEnergyStored(ForgeDirection direction);
}
