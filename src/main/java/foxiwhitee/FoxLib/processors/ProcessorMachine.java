package foxiwhitee.FoxLib.processors;

import foxiwhitee.FoxLib.api.processors.IMachine;
import foxiwhitee.FoxLib.processors.hash.CraftingHash;
import foxiwhitee.FoxLib.recipes.IMachineRecipe;
import foxiwhitee.FoxLib.recipes.RecipeInput;
import foxiwhitee.FoxLib.tile.inventory.FoxInternalInventory;
import foxiwhitee.FoxLib.tile.inventory.InvOperation;
import foxiwhitee.FoxLib.utils.helpers.InventoryUtils;
import foxiwhitee.FoxLib.utils.helpers.ItemStackUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class ProcessorMachine<T extends IMachineRecipe> {
    protected final IMachine<T> machine;
    protected final FoxInternalInventory inventory;
    protected final FoxInternalInventory output;
    protected final CraftingHash slotsCraftingHash = new CraftingHash();
    protected final List<T> sortedRecipes;
    protected List<ItemStack> notConsumedStacks = new ArrayList<>();
    protected T currentRecipe;
    protected int progress;
    protected boolean markForUpdate = true;

    public ProcessorMachine(IMachine<T> machine) {
        this.machine = machine;
        this.inventory = machine.getInternalInventory();
        this.output = machine.getOutputInventory();
        this.sortedRecipes = getSortedRecipes();
    }

    @SuppressWarnings("all")
    private List<T> getSortedRecipes() {
        return getMachine().getRecipes().stream()
            .sorted(Comparator.comparingInt((T obj) -> obj.getInputs().size()).reversed())
            .collect(Collectors.toList());
    }

    public void writeToNbt(NBTTagCompound data) {
        slotsCraftingHash.writeToNbt(data);
        data.setInteger("progress", this.progress);
    }

    public void readFromNbt(NBTTagCompound data) {
        slotsCraftingHash.readFromNbt(data);
        this.progress = data.getInteger("progress");
        updateRecipe();
    }

    public void writeToStream(ByteBuf data) {
        data.writeInt(this.progress);
    }

    public boolean readFromStream(ByteBuf data) {
        int oldProgress = this.progress;
        this.progress = data.readInt();
        return oldProgress != this.progress;
    }

    protected boolean doWork() {
        return true;
    }

    public void tick() {
        if (this.markForUpdate) {
            updateRecipe();
            this.markForUpdate = false;
        }
        if (this.currentRecipe != null && doWork() && InventoryUtils.canInsert(output, currentRecipe.getOutput())) {
            this.progress += 1;
            if (this.progress >= getMachine().getRealSpeed()) {
                for (int i = 0; i < getMachine().getOperations(); i++) {
                    if (this.currentRecipe != null && doWork() && InventoryUtils.canInsert(output, currentRecipe.getOutput())) {
                        craftRecipe();
                        afterCrafting();
                    }
                }
            }
            getMachine().markForUpdate();
        } else {
            if (this.progress != 0) {
                this.progress = 0;
                getMachine().markForUpdate();
            }
            this.progress = 0;
        }
    }

    protected void afterCrafting() {
        this.progress = 0;
        this.slotsCraftingHash.clear();
        updateRecipe();
        getMachine().markForUpdate();
    }

    protected void craftRecipe() {
        for (Map.Entry<Integer, Integer> entry : this.slotsCraftingHash.getEntrySet()) {
            int slot = entry.getKey();
            int amount = entry.getValue();

            ItemStack stack = inventory.getStackInSlot(slot);
            stack.stackSize -= amount;
            if (stack.stackSize <= 0) {
                this.inventory.setInventorySlotContents(slot, null);
            }
        }

        ItemStack outputStack = currentRecipe.getOutput().copy();
        InventoryUtils.insert(output, outputStack);
    }

    public void onChangeInventory(IInventory inv, int idx, InvOperation op, ItemStack removed, ItemStack added) {
        if (inv == this.inventory) {
            this.markForUpdate = true;
        }
    }

    public void updateRecipe() {
        this.slotsCraftingHash.clear();
        for (T recipe : this.sortedRecipes) {
            if (tryMatch(recipe)) {
                this.currentRecipe = recipe;
                return;
            }
        }
        this.currentRecipe = null;
    }

    protected boolean tryMatch(IMachineRecipe recipe) {
        Map<Integer, Integer> consumptionMap = new HashMap<>();
        int[] virtualUsage = new int[this.inventory.getSizeInventory()];

        for (RecipeInput required : recipe.getInputs()) {
            int stillNeeded = required.getCount();

            for (int i = 0; i < this.inventory.getSizeInventory(); i++) {
                if (stillNeeded <= 0) break;

                ItemStack stackInSlot = this.inventory.getStackInSlot(i);
                if (stackInSlot == null) continue;

                if (ItemStackUtil.matchesStackAndOther(stackInSlot, required.getInput())) {
                    int available = stackInSlot.stackSize - virtualUsage[i];

                    if (available > 0) {
                        int take = Math.min(stillNeeded, available);
                        if (!notConsumedStacks.contains(stackInSlot)) {
                            consumptionMap.merge(i, take, Integer::sum);
                        }
                        virtualUsage[i] += take;
                        stillNeeded -= take;
                    }
                }
            }

            if (stillNeeded > 0) {
                return false;
            }
        }
        this.slotsCraftingHash.putAll(consumptionMap);
        return true;
    }

    public int getProgress() {
        return progress;
    }

    public IMachine<T> getMachine() {
        return machine;
    }

    public void setNotConsumedStacks(List<ItemStack> notConsumedStacks) {
        this.notConsumedStacks = notConsumedStacks;
        if (this.notConsumedStacks == null) {
            this.notConsumedStacks = new ArrayList<>();
        }
    }
}
