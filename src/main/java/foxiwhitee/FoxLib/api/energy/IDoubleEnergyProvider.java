package foxiwhitee.FoxLib.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

public interface IDoubleEnergyProvider extends IDoubleEnergyConnection {
    double extractDoubleEnergy(ForgeDirection direction, double maxExtract, boolean simulate);

    double getDoubleEnergyStored(ForgeDirection direction);

    double getMaxDoubleEnergyStored(ForgeDirection direction);
}
