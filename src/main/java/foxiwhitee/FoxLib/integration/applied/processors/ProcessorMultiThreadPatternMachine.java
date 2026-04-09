package foxiwhitee.FoxLib.integration.applied.processors;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.data.IAEStack;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import foxiwhitee.FoxLib.integration.applied.helpers.AECrafterHelper;
import foxiwhitee.FoxLib.tile.inventory.FoxInternalInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class ProcessorMultiThreadPatternMachine extends ProcessorPatternMachine {
    protected final ICraftingPatternDetails[] activePatterns;
    protected final InventoryCrafting[] craftingGrids;
    protected final long[] craftCounts;
    protected AfterCraftingMultiThreadFunction afterCraftingMultiThreadFunction;

    public ProcessorMultiThreadPatternMachine(FoxInternalInventory patterns, MachineSource source, ICraftingProvider provider, int threads) {
        super(patterns, source, provider);
        this.activePatterns = new ICraftingPatternDetails[threads];
        this.craftingGrids = new InventoryCrafting[threads];
        this.craftCounts = new long[threads];
    }

    public void setAfterCraftingMultiThreadFunction(AfterCraftingMultiThreadFunction afterCraftingMultiThreadFunction) {
        this.afterCraftingMultiThreadFunction = afterCraftingMultiThreadFunction;
    }

    @Override
    public void writeToNbt(NBTTagCompound data) {
        patterns.writeToNBT(data, "patterns");
        NBTTagCompound list = new NBTTagCompound();
        for (int i = 0; i < activePatterns.length; i++) {
            NBTTagCompound pattern = writeToNbtPattern(activePatterns[i]);
            if (pattern != null) {
                list.setTag(String.valueOf(i), pattern);
            }
        }
        data.setTag("activePatterns", list);
        list = new NBTTagCompound();
        for (int i = 0; i < craftingGrids.length; i++) {
            NBTTagCompound grid = new NBTTagCompound();
            writeToNbtCraftingGrid(craftingGrids[i], grid);
            list.setTag(String.valueOf(i), grid);
        }
        data.setTag("craftingGrids", list);
        for (int i = 0; i < craftCounts.length; i++) {
            data.setLong("craftCount" + i, craftCounts[i]);
        }
        AECrafterHelper.writeToNbtNeedItems(data, needSend);
    }

    @Override
    public void readFromNbt(NBTTagCompound data) {
        patterns.readFromNBT(data, "patterns");
        NBTTagCompound list = data.getCompoundTag("activePatterns");
        for (String key : list.func_150296_c()) {
            int idx = Integer.parseInt(key);
            activePatterns[idx] = readFromNbtPattern(list.getCompoundTag(key));
        }
        list = data.getCompoundTag("craftingGrids");
        for (String key : list.func_150296_c()) {
            int idx = Integer.parseInt(key);
            craftingGrids[idx] = readFromNbtCraftingGrid(list.getCompoundTag(key));
        }
        for (int i = 0; i < craftCounts.length; i++) {
            craftCounts[i] = data.getLong("craftCount" + i);
        }
        AECrafterHelper.readFromNbtNeedItems(data, needSend);
    }

    @Override
    public boolean isBusy() {
        boolean busy = true;
        for (ICraftingPatternDetails pattern : activePatterns) {
            if (pattern == null) {
                busy = false;
                break;
            }
        }
        return busy;
    }

    @Override
    public boolean tick() {
        boolean markForUpdate = false;
        for (int i = 0; i < activePatterns.length; i++) {
            if (craftingGrids[i] == null) {
                activePatterns[i] = null;
            }

            if (activePatterns[i] != null && needSend.isEmpty()) {
                List<IAEStack<?>> outputs = AECrafterHelper.calculateOutputs(activePatterns[i], craftCounts[i], craftingGrids[i]);
                needSend.addAll(outputs);
            }
            AECrafterHelper.trySendItems(proxy, source, needSend);
            if (needSend.isEmpty()) {
                afterCrafting(i);
                this.craftCounts[i] = 0;
                markForUpdate = true;
            }
        }
        return markForUpdate;
    }

    protected void afterCrafting(int idx) {
        if (activePatterns[idx] != null) {
            if (afterCraftingMultiThreadFunction != null) {
                afterCraftingMultiThreadFunction.afterCrafting(idx, activePatterns[idx]);
            }
            this.activePatterns[idx] = null;
            this.craftingGrids[idx] = null;
        }
    }

    @Override
    protected List<ICraftingPatternDetails> getAllActivePatterns() {
        return Arrays.asList(activePatterns);
    }

    @Override
    public boolean pushPattern(ICraftingPatternDetails pattern, InventoryCrafting ic, CraftingCPUCluster cluster) {
        for (int i = 0; i < activePatterns.length; i++) {
            if (activePatterns[i] == null) {
                ModulatedCraft modulatedCraft = modulateCraft(pattern, ic, cluster);
                if (modulatedCraft == null) {
                    continue;
                }
                this.activePatterns[i] = modulatedCraft.activePattern;
                this.craftCounts[i] = modulatedCraft.craftCount;
                this.craftingGrids[i] = modulatedCraft.craftingGrid;
                return true;
            }
        }

        return false;
    }

    @FunctionalInterface
    public interface AfterCraftingMultiThreadFunction {
        void afterCrafting(int idx, ICraftingPatternDetails pattern);
    }
}
