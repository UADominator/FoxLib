package foxiwhitee.FoxLib.api.energy;

import cofh.api.energy.IEnergyConnection;
import net.minecraftforge.common.util.ForgeDirection;

public interface IDoubleEnergyConnection extends IEnergyConnection {
    boolean canConnectDoubleEnergy(ForgeDirection direction);

    @Override
    default boolean canConnectEnergy(ForgeDirection forgeDirection) {
        return canConnectDoubleEnergy(forgeDirection);
    }
}
