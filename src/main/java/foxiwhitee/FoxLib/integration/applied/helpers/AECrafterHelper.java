package foxiwhitee.FoxLib.integration.applied.helpers;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class AECrafterHelper {
    public static void trySendItems(AENetworkProxy proxy, BaseActionSource source, List<IAEStack<?>> needSend) {
        try {
            if (proxy != null) {
                List<IAEStack<?>> remove = new ArrayList<>();
                IMEMonitor<IAEItemStack> storage = proxy.getStorage().getItemInventory();
                for (IAEStack<?> s : needSend) {
                    if (s instanceof IAEItemStack stack) {
                        if (stack.getStackSize() == 0) {
                            remove.add(s);
                            continue;
                        }
                        IAEStack<?> rest = Platform.poweredInsert(proxy.getEnergy(), storage, stack, source);
                        if (rest != null && rest.getStackSize() > 0) {
                            stack.setStackSize(rest.getStackSize());
                            break;
                        }
                        stack.setStackSize(0);
                        remove.add(stack);
                    } else {
                        remove.add(s);
                    }
                }
                needSend.removeAll(remove);
            }
        } catch (GridAccessException ignored) {
        }
    }

    public static List<IAEStack<?>> calculateOutputs(ICraftingPatternDetails activePattern, long craftCount, InventoryCrafting craftingGrid) {
        List<IAEStack<?>> outputs = calculateOutputs(activePattern, craftCount);
        for (int i = 0; i < craftingGrid.getSizeInventory(); i++) {
            ItemStack item = getContainerItem(craftingGrid.getStackInSlot(i));
            if (item != null) {
                outputs.add(AEItemStack.create(item));
            }
        }
        return outputs;
    }

    public static List<IAEStack<?>> calculateOutputs(ICraftingPatternDetails activePattern, long craftCount) {
        List<IAEStack<?>> outputs = new ArrayList<>();
        Arrays.stream(activePattern.getCondensedAEOutputs())
            .map(stack -> {
                IAEStack<?> copy = stack.copy();
                copy.setStackSize(copy.getStackSize() * craftCount);
                return copy;
            }).forEach(outputs::add);
        return outputs;
    }

    public static void writeToNbtNeedItems(NBTTagCompound data, List<IAEStack<?>> needSend) {
        data.setInteger("needSendSize", needSend.size());
        for (int i = 0; i < needSend.size(); i++) {
            IAEStack<?> is = needSend.get(i);
            NBTTagCompound tag = new NBTTagCompound();
            is.writeToNBT(tag);
            data.setTag("needSend_" + i, tag);
        }
    }

    public static void readFromNbtNeedItems(NBTTagCompound data, List<IAEStack<?>> needSend) {
        if (data.hasKey("needSendSize")) {
            int size = data.getInteger("needSendSize");
            needSend.clear();
            for (int i = 0; i < size; i++) {
                NBTTagCompound tag = data.getCompoundTag("needSend_" + i);
                needSend.add(AEItemStack.loadItemStackFromNBT(tag));
            }
        }
    }

    private static ItemStack getContainerItem(final ItemStack stackInSlot) {
        if (stackInSlot == null) {
            return null;
        }

        final Item i = stackInSlot.getItem();
        if (i == null || !i.hasContainerItem(stackInSlot)) {
            return null;
        }

        ItemStack ci = i.getContainerItem(stackInSlot.copy());
        if (ci != null && ci.isItemStackDamageable() && ci.getItemDamage() > ci.getMaxDamage()) {
            ci = null;
        }

        return ci;
    }
}
