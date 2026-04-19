package foxiwhitee.FoxLib.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

@SuppressWarnings("unused")
public interface IDoubleEnergyHandler extends IDoubleEnergyProvider, IDoubleEnergyReceiver {

    @Override
    double extractDoubleEnergy(ForgeDirection direction, double maxExtract, boolean simulate);

    @Override
    double receiveDoubleEnergy(ForgeDirection direction, double maxReceive, boolean simulate);

    @Override
    double getDoubleEnergyStored(ForgeDirection direction);

    @Override
    double getMaxDoubleEnergyStored(ForgeDirection direction);

    @Override
    boolean canConnectDoubleEnergy(ForgeDirection direction);

    @Override
    default int extractEnergy(ForgeDirection forgeDirection, int i, boolean b) {
        return IDoubleEnergyProvider.super.extractEnergy(forgeDirection, i, b);
    }

    @Override
    default int getEnergyStored(ForgeDirection forgeDirection) {
        return IDoubleEnergyProvider.super.getEnergyStored(forgeDirection);
    }

    @Override
    default int getMaxEnergyStored(ForgeDirection forgeDirection) {
        return IDoubleEnergyProvider.super.getMaxEnergyStored(forgeDirection);
    }

}
