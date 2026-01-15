package foxiwhitee.FoxLib.utils.helpers;

import foxiwhitee.FoxLib.tile.inventory.FoxInternalInventory;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class InventoryUtils {
    public static boolean canInsert(FluidTank tank, FluidStack stack) {
        if (stack == null || stack.getFluid() == null) return true;

        FluidStack inTank = tank.getFluid();

        if (inTank == null) {
            return tank.getCapacity() >= stack.amount;
        }

        return inTank.isFluidEqual(stack) && (tank.getCapacity() - inTank.amount) >= stack.amount;
    }

    public static boolean canInsert(FoxInternalInventory inv, ItemStack stack, int idx) {
        ItemStack inSlot = inv.getStackInSlot(idx);
        if (inSlot == null) return true;
        return ItemStackUtil.stackEquals(stack, inSlot) && (inSlot.getMaxStackSize() - inSlot.stackSize) >= stack.stackSize;
    }

    public static boolean canInsert(FoxInternalInventory inv, ItemStack stack) {
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (canInsert(inv, stack, i)) return true;
        }
        return false;
    }

    public static void insert(FoxInternalInventory inv, ItemStack stack) {
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            boolean b = insert(inv, stack, i);
            if (b) return;
        }
    }

    public static boolean insert(FoxInternalInventory inv, ItemStack stack, int idx) {
        ItemStack inSlot = inv.getStackInSlot(idx);
        stack = stack.copy();
        if (inSlot == null) {
            inv.setInventorySlotContents(idx, stack);
            return true;
        }
        if (ItemStackUtil.stackEquals(inSlot, stack)) {
            int transfer = Math.min(stack.stackSize, inSlot.getMaxStackSize() - inSlot.stackSize);
            inSlot.stackSize += transfer;
            stack.stackSize -= transfer;
            return stack.stackSize <= 0;
        }
        return false;
    }

    public static void writeTankToNbt(NBTTagCompound data, String key, FluidTank tank) {
        NBTTagCompound nbt = new NBTTagCompound();
        tank.writeToNBT(nbt);
        data.setTag(key, nbt);
    }

    public static void readTankFromNbt(NBTTagCompound data, String key, FluidTank tank) {
        tank.readFromNBT(data.getCompoundTag(key));
    }

    public static void writeTankToStream(ByteBuf data, FluidTank tank) {
        FluidStack fs = tank.getFluid();
        if (fs != null) {
            data.writeBoolean(true);
            data.writeInt(tank.getCapacity());
            data.writeInt(FluidRegistry.getFluidID(fs.getFluid()));
            data.writeInt(fs.amount);
        } else {
            data.writeBoolean(false);
        }
    }

    public static boolean readTankFromStream(ByteBuf data, FluidTank tank) {
        boolean changed = false;
        boolean hasFluid = data.readBoolean();
        if (hasFluid) {
            tank.setCapacity(data.readInt());
            int id = data.readInt();
            int amount = data.readInt();
            Fluid f = FluidRegistry.getFluid(id);
            if (tank.getFluid() == null || tank.getFluid().getFluid() != f || tank.getFluid().amount != amount) {
                tank.setFluid(new FluidStack(f, amount));
                changed = true;
            }
        } else {
            if (tank.getFluid() != null) {
                tank.setFluid(null);
                changed = true;
            }
        }
        return changed;
    }

    public static void doMergeInventory(FoxInternalInventory inventory) {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack currentStack = inventory.getStackInSlot(i);

            if (currentStack != null && currentStack.stackSize < currentStack.getMaxStackSize()) {
                for (int j = i + 1; j < inventory.getSizeInventory(); j++) {
                    ItemStack nextStack = inventory.getStackInSlot(j);

                    if (ItemStackUtil.stackEquals(currentStack, nextStack)) {
                        int spaceLeft = currentStack.getMaxStackSize() - currentStack.stackSize;
                        int amountToMove = Math.min(spaceLeft, nextStack.stackSize);

                        currentStack.stackSize += amountToMove;
                        nextStack.stackSize -= amountToMove;

                        if (nextStack.stackSize <= 0) {
                            inventory.setInventorySlotContents(j, null);
                        }

                        if (currentStack.stackSize >= currentStack.getMaxStackSize()) {
                            break;
                        }
                    }
                }
            }
        }
    }

    public static void doFillInventory(FoxInternalInventory inventory) {
        doFillInventory(inventory, (integer, stack) -> true);
    }

    public static void doFillInventory(FoxInternalInventory inventory, BiPredicate<Integer, ItemStack> filter) {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack current = inventory.getStackInSlot(i);
            if (current == null) continue;

            List<Integer> targetSlots = new ArrayList<>();
            int totalAmount = 0;

            for (int j = 0; j < inventory.getSizeInventory(); j++) {
                ItemStack stack = inventory.getStackInSlot(j);
                if (stack == null) {
                    if (filter.test(j, current)) {
                        targetSlots.add(j);
                    }
                } else if (ItemStackUtil.stackEquals(current, stack)) {
                    if (filter.test(j, current)) {
                        targetSlots.add(j);
                        totalAmount += stack.stackSize;
                    }
                }
            }

            if (targetSlots.size() > 1 && totalAmount > 0) {
                int countPerSlot = totalAmount / targetSlots.size();
                int remainder = totalAmount % targetSlots.size();

                for (int k = 0; k < targetSlots.size(); k++) {
                    int slotIndex = targetSlots.get(k);
                    int amountToSet = countPerSlot + (k < remainder ? 1 : 0);

                    if (amountToSet > 0) {
                        ItemStack newStack = current.copy();
                        newStack.stackSize = amountToSet;
                        inventory.setInventorySlotContents(slotIndex, newStack);
                    } else {
                        inventory.setInventorySlotContents(slotIndex, null);
                    }
                }
            }
        }
    }
}
