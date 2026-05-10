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
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class ProcessorMachine<T extends IMachineRecipe> {
    protected final IMachine<T> machine;
    protected final FoxInternalInventory inventory;
    protected final FoxInternalInventory output;
    protected final CraftingHash slotsCraftingHash = new CraftingHash();
    protected final List<T> sortedRecipes;
    protected List<ItemStack> notConsumedStacks = new ArrayList<>();
    protected Predicate<T> doesCorectRecipe;
    protected T currentRecipe;
    protected int progress;
    protected boolean markForUpdate = true, isActive;

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
        data.setBoolean("isActive", isActive);
    }

    public void readFromNbt(NBTTagCompound data) {
        slotsCraftingHash.readFromNbt(data);
        this.progress = data.getInteger("progress");
        this.isActive = data.getBoolean("isActive");
        updateRecipe();
    }

    public void writeToStream(ByteBuf data) {
        data.writeInt(this.progress);
        data.writeBoolean(this.isActive);
    }

    public boolean readFromStream(ByteBuf data) {
        int oldProgress = this.progress;
        boolean oldIsActive = this.isActive;
        this.progress = data.readInt();
        this.isActive = data.readBoolean();
        return oldProgress != this.progress || oldIsActive != this.isActive;
    }

    protected boolean doWork() {
        return true;
    }

    protected void afterValidateTick() {}

    public void tick() {
        if (this.markForUpdate) {
            updateRecipe();
            this.markForUpdate = false;
        }
        if (this.currentRecipe != null && doWork() && InventoryUtils.canInsert(output, currentRecipe.getOutput())) {
            if (!isActive) {
                isActive = true;
            }
            this.progress += 1;
            afterValidateTick();
            if (this.progress >= getMachine().getRealSpeed()) {
                boolean worked = false;
                for (int i = 0; i < getMachine().getOperations(); i++) {
                    if (this.currentRecipe != null && doWork() && InventoryUtils.canInsert(output, currentRecipe.getOutput())) {
                        craftRecipe();
                        afterCrafting();
                        updateRecipe();
                        worked = true;
                    }
                }
                if (worked) {
                    this.progress = 0;
                }
            }
            getMachine().markForUpdate();
        } else {
            boolean update = false;
            if (isActive) {
                this.isActive = false;
                update = true;
            }
            if (this.progress != 0) {
                update = true;
            }
            this.progress = 0;
            if (update) {
                getMachine().markForUpdate();
            }
        }
    }

    protected void afterCrafting() {
        this.progress = 0;
        this.slotsCraftingHash.clear();
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
                if (doesCorectRecipe != null) {
                    if (doesCorectRecipe.test(recipe)) {
                        this.currentRecipe = recipe;
                        return;
                    }
                } else {
                    this.currentRecipe = recipe;
                    return;
                }
            }
        }
        this.currentRecipe = null;
    }

    protected boolean tryMatch(IMachineRecipe recipe) {
        int[] slots = new int[this.inventory.getSizeInventory()];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = i;
        }
        return tryMatch(recipe, this.slotsCraftingHash, slots);
    }

    protected boolean tryMatch(IMachineRecipe recipe, int... useSlots) {
        return tryMatch(recipe, this.slotsCraftingHash, useSlots);
    }

    protected boolean tryMatch(IMachineRecipe recipe, CraftingHash hash) {
        int[] slots = new int[this.inventory.getSizeInventory()];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = i;
        }
        return tryMatch(recipe, hash, slots);
    }

    protected boolean tryMatch(IMachineRecipe recipe, CraftingHash hash, int... useSlots) {
        Map<Integer, Integer> consumptionMap = new HashMap<>();
        int[] virtualUsage = new int[useSlots.length];

        for (RecipeInput required : recipe.getInputs()) {
            int stillNeeded = required.getCount();

            for (int i = 0; i < useSlots.length; i++) {
                int slotId = useSlots[i];
                if (stillNeeded <= 0) break;

                ItemStack stackInSlot = this.inventory.getStackInSlot(slotId);
                if (stackInSlot == null) continue;

                if (ItemStackUtil.matchesStackAndOther(stackInSlot, required.getInput())) {
                    int available = stackInSlot.stackSize - virtualUsage[i];

                    if (available > 0) {
                        int take = Math.min(stillNeeded, available);
                        if (!notConsumedStacks.contains(stackInSlot)) {
                            consumptionMap.merge(slotId, take, Integer::sum);
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
        hash.putAll(consumptionMap);
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

    public void setDoesCorectRecipe(Predicate<T> doesCorectRecipe) {
        this.doesCorectRecipe = doesCorectRecipe;
    }

    public boolean isActive() {
        return isActive;
    }
}
