package foxiwhitee.FoxLib.utils.helpers;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.common.Optional;
import foxiwhitee.FoxLib.api.energy.IDoubleEnergyContainerItem;
import foxiwhitee.FoxLib.api.energy.IDoubleEnergyReceiver;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

@SuppressWarnings("unused")
public class EnergyUtility {
    public static double pushEnergy(ForgeDirection side, double energy, double output, TileEntity thisTile, boolean doIf) {
        if (!doIf || energy <= 0) return 0;

        TileEntity targetTile = thisTile.getWorldObj().getTileEntity(thisTile.xCoord + side.offsetX, thisTile.yCoord + side.offsetY, thisTile.zCoord + side.offsetZ);
        if (targetTile == null) return 0;

        double energyToPush = Math.min(energy, output);

        if (targetTile instanceof IDoubleEnergyReceiver receiver) {
            return receiver.receiveDoubleEnergy(side.getOpposite(), energyToPush, false);
        }

        return tryPushRF(targetTile, side.getOpposite(), energyToPush);
    }

    public static double handleItemEnergy(ItemStack stack, double energy, double output, double maxEnergy, boolean isCharging, boolean doIf ) {
        if (!doIf || stack == null || stack.getItem() == null) return 0;

        double limit = Math.min(isCharging ? energy : maxEnergy - energy, output);

        if (stack.getItem() instanceof IDoubleEnergyContainerItem item) {
            double processed = isCharging ? item.receiveDoubleEnergy(stack, limit, true) : item.extractDoubleEnergy(stack, limit, true);
            return (isCharging ? item.receiveDoubleEnergy(stack, processed, false) : item.extractDoubleEnergy(stack, processed, false));
        }

        return tryHandleRFItem(stack, limit, isCharging);
    }

    private static double tryPushRF(TileEntity tile, ForgeDirection side, double energy) {
        return pushRFInternal(tile, side, (int) energy);
    }

    @Optional.Method(modid = "CoFHCore")
    private static int pushRFInternal(TileEntity tile, ForgeDirection side, int energy) {
        return (tile instanceof IEnergyReceiver receiver) ? receiver.receiveEnergy(side, energy, false) : 0;
    }

    private static double tryHandleRFItem(ItemStack stack, double energy, boolean isCharging) {
        return handleRFItemInternal(stack, (int) energy, isCharging);
    }

    @Optional.Method(modid = "CoFHCore")
    private static int handleRFItemInternal(ItemStack stack, int energy, boolean isCharging) {
        if (stack.getItem() instanceof IEnergyContainerItem item) {
            return isCharging ? item.receiveEnergy(stack, energy, false) : item.extractEnergy(stack, energy, false);
        }
        return 0;
    }
}
