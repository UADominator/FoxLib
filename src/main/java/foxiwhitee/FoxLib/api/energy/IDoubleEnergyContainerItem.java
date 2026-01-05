package foxiwhitee.FoxLib.api.energy;

import net.minecraft.item.ItemStack;

public interface IDoubleEnergyContainerItem {
    double receiveDoubleEnergy(ItemStack stack, double maxReceive, boolean simulate);

    double extractDoubleEnergy(ItemStack stack, double maxExtract, boolean simulate);

    double getDoubleEnergyStored(ItemStack stack);

    double getMaxDoubleEnergyStored(ItemStack stack);

    boolean canWorkWithEnergy(ItemStack stack);
}
