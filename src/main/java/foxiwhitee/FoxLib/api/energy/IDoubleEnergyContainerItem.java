package foxiwhitee.FoxLib.api.energy;

import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IDoubleEnergyContainerItem extends IEnergyContainerItem {
    double getMaxDoubleEnergyStored(ItemStack stack);

    default double receiveDoubleEnergy(ItemStack stack, double maxReceive, boolean simulate) {
        if (maxReceive > 0 && stack.getItem() instanceof IDoubleEnergyContainerItem item) {
            double charge = item.getDoubleEnergyStored(stack);
            maxReceive = Math.min(maxReceive, item.getMaxDoubleEnergyStored(stack) - charge);

            if (!simulate) {
                charge += maxReceive;
                if (charge > 0) {
                    if (stack.getTagCompound() == null) {
                        stack.setTagCompound(new NBTTagCompound());
                    }
                    stack.getTagCompound().setDouble("energy", charge);
                } else {
                    stack.setTagCompound(null);
                }
            }
            return maxReceive;
        }
        return 0;
    }

    default double extractDoubleEnergy(ItemStack stack, double maxExtract, boolean simulate) {
        if (maxExtract > 0 && stack.getItem() instanceof IDoubleEnergyContainerItem item) {
            double charge = item.getDoubleEnergyStored(stack);
            maxExtract = Math.min(maxExtract, charge);

            if (!simulate) {
                charge -= maxExtract;
                if (charge > 0) {
                    if (stack.getTagCompound() == null) {
                        stack.setTagCompound(new NBTTagCompound());
                    }
                    stack.getTagCompound().setDouble("energy", charge);
                } else {
                    stack.setTagCompound(null);
                }
            }
            return maxExtract;
        }
        return 0;
    }

    default double getDoubleEnergyStored(ItemStack stack) {
        if (stack != null) {
            if (stack.getTagCompound() == null) {
                return 0;
            }
            return stack.getTagCompound().getDouble("energy");
        }
        return 0;
    }

    @Override
    default int receiveEnergy(ItemStack itemStack, int i, boolean b) {
        return (int) Math.min(Integer.MAX_VALUE, receiveDoubleEnergy(itemStack, i, b));
    }

    @Override
    default int extractEnergy(ItemStack itemStack, int i, boolean b) {
        return (int) Math.min(Integer.MAX_VALUE, extractDoubleEnergy(itemStack, i, b));
    }

    @Override
    default int getEnergyStored(ItemStack itemStack) {
        return (int) Math.min(Integer.MAX_VALUE, getDoubleEnergyStored(itemStack));
    }

    @Override
    default int getMaxEnergyStored(ItemStack itemStack) {
        return (int) Math.min(Integer.MAX_VALUE, getMaxDoubleEnergyStored(itemStack));
    }
}
