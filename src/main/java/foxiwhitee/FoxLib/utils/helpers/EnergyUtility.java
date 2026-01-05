package foxiwhitee.FoxLib.utils.helpers;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.common.Optional;
import foxiwhitee.FoxLib.api.energy.IDoubleEnergyContainerItem;
import foxiwhitee.FoxLib.api.energy.IDoubleEnergyReceiver;
import foxiwhitee.FoxLib.config.FoxLibConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.text.DecimalFormat;

public class EnergyUtility {
    private static final String[] POSTFIX = {"", "k", "M", "G", "T", "P", "E", "Z", "Y", "R", "Q"};
    private static final DecimalFormat DF = new DecimalFormat("#.##");

    public static String formatNumber(double energy) {
        if (energy < 0) return "-" + formatNumber(-energy);
        if (energy < 1000) return DF.format(energy).replace(",", ".");

        int offset = 0;
        double value = energy;

        while (value >= 1000 && offset < POSTFIX.length - 1) {
            value /= 1000.0;
            offset++;
        }

        if (value >= 1000) {
            return String.format("%.2e", energy).replace(",", ".");
        }

        return DF.format(value).replace(",", ".") + POSTFIX[offset];
    }

    public static double pushEnergy(ForgeDirection side, double energy, double output, TileEntity thisTile, boolean doIf, boolean useEuRatio) {
        if (!doIf || energy <= 0) return 0;

        TileEntity targetTile = thisTile.getWorldObj().getTileEntity(thisTile.xCoord + side.offsetX, thisTile.yCoord + side.offsetY, thisTile.zCoord + side.offsetZ);
        if (targetTile == null) return 0;

        double ratio = useEuRatio ? FoxLibConfig.rfInEu : 1.0;
        double energyToPush = Math.min(energy, output) * ratio;

        if (targetTile instanceof IDoubleEnergyReceiver receiver) {
            return receiver.receiveDoubleEnergy(side.getOpposite(), energyToPush, false) / ratio;
        }

        return tryPushRF(targetTile, side.getOpposite(), energyToPush) / ratio;
    }

    public static double handleItemEnergy(ItemStack stack, double energy, double output, double maxEnergy, boolean isCharging, boolean doIf, boolean useEuRatio) {
        if (!doIf || stack == null || stack.getItem() == null) return 0;

        double ratio = useEuRatio ? FoxLibConfig.rfInEu : 1.0;
        double limit = Math.min(isCharging ? energy : maxEnergy - energy, output) * ratio;

        if (stack.getItem() instanceof IDoubleEnergyContainerItem item && item.canWorkWithEnergy(stack)) {
            double processed = isCharging ? item.receiveDoubleEnergy(stack, limit, true) : item.extractDoubleEnergy(stack, limit, true);
            if (useEuRatio) processed -= processed % ratio;

            return (isCharging ? item.receiveDoubleEnergy(stack, processed, false) : item.extractDoubleEnergy(stack, processed, false)) / ratio;
        }

        return tryHandleRFItem(stack, limit, isCharging) / ratio;
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
