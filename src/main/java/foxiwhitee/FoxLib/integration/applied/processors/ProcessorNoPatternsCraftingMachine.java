package foxiwhitee.FoxLib.integration.applied.processors;

import appeng.api.config.Actionable;
import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.data.IAEStack;
import appeng.container.ContainerNull;
import appeng.crafting.MECraftingInventory;
import appeng.items.misc.ItemEncodedPattern;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.me.helpers.AENetworkProxy;
import foxiwhitee.FoxLib.integration.applied.api.crafting.ICraftingCPUClusterAccessor;
import foxiwhitee.FoxLib.integration.applied.helpers.AECrafterHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ProcessorNoPatternsCraftingMachine {
    protected final MachineSource source;
    protected final ICraftingProvider provider;
    protected World world;
    protected AENetworkProxy proxy;
    protected Supplier<Long> maxCount;
    protected Predicate<ICraftingPatternDetails> validation;
    protected List<ICraftingPatternDetails> patternList;
    protected ICraftingPatternDetails activePattern;
    protected long craftCount;
    protected InventoryCrafting craftingGrid;
    protected final List<IAEStack<?>> needSend = new ArrayList<>();
    protected AfterCraftingFunction afterCraftingFunction;

    public ProcessorNoPatternsCraftingMachine(MachineSource source, ICraftingProvider provider) {
        this.source = source;
        this.provider = provider;
    }

    public ICraftingPatternDetails getActivePattern() {
        return activePattern;
    }

    public void setAfterCraftingFunction(AfterCraftingFunction afterCraftingFunction) {
        this.afterCraftingFunction = afterCraftingFunction;
    }

    public List<ICraftingPatternDetails> getPatternList() {
        return patternList;
    }

    public List<IAEStack<?>> getNeedSend() {
        return needSend;
    }

    public long getCraftCount() {
        return craftCount;
    }

    public void setMaxCount(Supplier<Long> maxCount) {
        this.maxCount = maxCount;
    }

    public void setValidation(Predicate<ICraftingPatternDetails> validation) {
        this.validation = validation;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setProxy(AENetworkProxy proxy) {
        this.proxy = proxy;
    }

    protected NBTTagCompound writeToNbtPattern(ICraftingPatternDetails activePattern) {
        if (activePattern != null) {
            ItemStack pattern = this.activePattern.getPattern();
            if (pattern != null) {
                NBTTagCompound compound = new NBTTagCompound();
                pattern.writeToNBT(compound);
                return compound;
            }
        }
        return null;
    }

    protected void writeToNbtCraftingGrid(InventoryCrafting craftingGrid, NBTTagCompound data) {
        if (craftingGrid != null) {
            data.setInteger("invC_size", craftingGrid.getSizeInventory());
            for (int i = 0; i < craftingGrid.getSizeInventory(); i++) {
                ItemStack is = craftingGrid.getStackInSlot(i);
                if (is != null) {
                    NBTTagCompound tag = new NBTTagCompound();
                    is.writeToNBT(tag);
                    data.setTag("invC_" + i, tag);
                }
            }
        }
    }

    protected InventoryCrafting readFromNbtCraftingGrid(NBTTagCompound data) {
        if (data.hasKey("invC_size")) {
            int size = data.getInteger("invC_size");
            InventoryCrafting craftingGrid = new InventoryCrafting(new ContainerNull(), size, 1);
            for (int i = 0; i < size; i++) {
                if (data.hasKey("invC_" + i)) {
                    NBTTagCompound tag = data.getCompoundTag("invC_" + i);
                    craftingGrid.setInventorySlotContents(i, ItemStack.loadItemStackFromNBT(tag));
                }
            }
            return craftingGrid;
        }
        return null;
    }

    protected ICraftingPatternDetails readFromNbtPattern(NBTTagCompound data) {
        ItemStack myPat = ItemStack.loadItemStackFromNBT(data);
        if (myPat != null) {
            Item var4 = myPat.getItem();
            if (var4 instanceof ItemEncodedPattern iep) {
                ICraftingPatternDetails ph = iep.getPatternForItem(myPat, world);
                if (ph != null && ph.isCraftable()) {
                    return ph;
                }
            }
        }
        return null;
    }

    public void writeToNbt(NBTTagCompound data) {
        NBTTagCompound grid = new NBTTagCompound();
        writeToNbtCraftingGrid(this.craftingGrid, grid);
        data.setTag("grid", grid);
        NBTTagCompound pattern = writeToNbtPattern(activePattern);
        if (pattern != null) {
            data.setTag("activePattern", pattern);
        }
        data.setLong("craftCount", craftCount);
        AECrafterHelper.writeToNbtNeedItems(data, needSend);
    }

    public void readFromNbt(NBTTagCompound data) {
        this.craftingGrid = readFromNbtCraftingGrid(data.getCompoundTag("grid"));
        if (data.hasKey("activePattern")) {
            this.activePattern = readFromNbtPattern(data.getCompoundTag("activePattern"));
        }
        craftCount = data.getLong("craftCount");
        AECrafterHelper.readFromNbtNeedItems(data, needSend);
    }

    public void provideCrafting(ICraftingProviderHelper helper) {
        if (patternList != null) {
            patternList.forEach(pattern -> helper.addCraftingOption(provider, pattern));
        }
    }

    public void addPattern(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof ICraftingPatternItem)) {
            return;
        }
        ICraftingPatternDetails pattern = ((ICraftingPatternItem) stack.getItem()).getPatternForItem(stack, world);
        if (pattern != null) {
            if (patternList == null) {
                patternList = new LinkedList<>();
            }
            patternList.add(pattern);
        }
    }

    public boolean isBusy() {
        return activePattern != null;
    }

    public boolean tick() {
        if (craftingGrid == null) {
            activePattern = null;
        }

        if (activePattern != null && needSend.isEmpty()) {
            List<IAEStack<?>> outputs = AECrafterHelper.calculateOutputs(activePattern, craftCount, craftingGrid);
            needSend.addAll(outputs);
        }
        AECrafterHelper.trySendItems(proxy, source, needSend);
        if (needSend.isEmpty()) {
            afterCrafting();
            return true;
        }
        return false;
    }

    protected void afterCrafting() {
        if (activePattern != null) {
            if (afterCraftingFunction != null) {
                afterCraftingFunction.afterCrafting(activePattern);
            }
            this.activePattern = null;
            this.craftingGrid = null;
            this.craftCount = 0;
        }
    }

    public boolean pushPattern(ICraftingPatternDetails pattern, InventoryCrafting ic, CraftingCPUCluster cluster) {
        ModulatedCraft modulatedCraft = modulateCraft(pattern, ic, cluster);
        if (modulatedCraft == null) {
            return false;
        }
        this.activePattern = modulatedCraft.activePattern;
        this.craftCount = modulatedCraft.craftCount;
        this.craftingGrid = modulatedCraft.craftingGrid;

        return true;
    }

    protected List<ICraftingPatternDetails> getAllActivePatterns() {
        return Collections.singletonList(activePattern);
    }

    protected ModulatedCraft modulateCraft(ICraftingPatternDetails pattern, InventoryCrafting ic, CraftingCPUCluster cluster) {
        if (patternList == null || !patternList.contains(pattern) || getAllActivePatterns().contains(pattern) || maxCount == null) {
            return null;
        }
        if (!validation.test(pattern)) {
            return null;
        }
        ICraftingCPUClusterAccessor accessor = (ICraftingCPUClusterAccessor) ((Object) cluster);
        MECraftingInventory inventory = cluster.getInventory();

        long pendingRequests = accessor.getWaitingFor(pattern);
        long maxToProcess = Math.min(pendingRequests - 1, maxCount.get());

        if (maxToProcess < 0) {
            return null;
        }

        long finalCraftCount = maxToProcess;

        for (IAEStack<?> input : pattern.getCondensedAEInputs()) {
            IAEStack<?> request = input.copy();
            request.setStackSize(request.getStackSize() * maxToProcess);

            IAEStack<?> availableStack = inventory.extractItems(request, Actionable.SIMULATE, cluster.getActionSource());
            long availableCount = (availableStack == null) ? 0 : availableStack.getStackSize();

            if (request.getStackSize() > availableCount) {
                finalCraftCount = Math.min(finalCraftCount, availableCount / input.getStackSize());
            }
        }

        if (finalCraftCount >= 1) {
            for (IAEStack<?> input : pattern.getCondensedAEInputs()) {
                IAEStack<?> toExtract = input.copy();
                toExtract.setStackSize(toExtract.getStackSize() * finalCraftCount);
                inventory.extractItems(toExtract, Actionable.MODULATE, cluster.getActionSource());
            }

            for (IAEStack<?> output : pattern.getCondensedAEOutputs()) {
                IAEStack<?> result = output.copy();
                result.setStackSize(result.getStackSize() * finalCraftCount);

                accessor.callPostChange(result, cluster.getActionSource());
                accessor.getWaitingFor().add(result.copy());
                accessor.callPostCraftingStatusChange(result.copy());
            }

            accessor.setWaitingFor(pattern, pendingRequests - finalCraftCount);
        }

        return new ModulatedCraft(pattern, ic, finalCraftCount + 1);
    }

    @SuppressWarnings("all")
    protected static class ModulatedCraft {
        protected final ICraftingPatternDetails activePattern;
        protected final InventoryCrafting craftingGrid;
        protected final long craftCount;

        protected ModulatedCraft(ICraftingPatternDetails activePattern, InventoryCrafting craftingGrid, long craftCount) {
            this.activePattern = activePattern;
            this.craftingGrid = craftingGrid;
            this.craftCount = craftCount;
        }
    }

    @FunctionalInterface
    public interface AfterCraftingFunction {
        void afterCrafting(ICraftingPatternDetails pattern);
    }
}
