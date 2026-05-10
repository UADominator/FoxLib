package foxiwhitee.FoxLib.processors;

import foxiwhitee.FoxLib.api.processors.IMachine;
import foxiwhitee.FoxLib.processors.hash.CraftingHash;
import foxiwhitee.FoxLib.recipes.IMachineRecipe;
import foxiwhitee.FoxLib.utils.helpers.InventoryUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;

@SuppressWarnings("unused")
public class ProcessorParallelMachine<T extends IMachineRecipe> extends ProcessorMachine<T> {
    protected final int parallels;
    protected final int[] progresses;
    protected final T[] currentRecipes;
    protected final CraftingHash[] craftingHashes;
    protected int inputPerParallel = 1;

    @SuppressWarnings("unchecked")
    public ProcessorParallelMachine(IMachine<T> machine, int parallels) {
        super(machine);
        this.parallels = parallels;
        this.progresses = new int[parallels];
        this.currentRecipes = (T[]) new IMachineRecipe[parallels];
        this.craftingHashes = new CraftingHash[parallels];
        for (int i = 0; i < parallels; i++) {
            this.craftingHashes[i] = new CraftingHash();
        }
    }

    public void setInputPerParallel(int inputPerParallel) {
        this.inputPerParallel = inputPerParallel;
    }

    public int[] getProgresses() {
        return progresses;
    }

    @Override
    public void writeToNbt(NBTTagCompound data) {
        super.writeToNbt(data);
        for (int i = 0; i < parallels; i++) {
            data.setInteger("progress_" + i, progresses[i]);
            NBTTagCompound tag = new NBTTagCompound();
            craftingHashes[i].writeToNbt(tag);
            data.setTag("hash_" + i, tag);
        }
    }

    @Override
    public void readFromNbt(NBTTagCompound data) {
        super.readFromNbt(data);
        for (int i = 0; i < parallels; i++) {
            progresses[i] = data.getInteger("progress_" + i);
            NBTTagCompound tag = data.getCompoundTag("hash_" + i);
            craftingHashes[i].readFromNbt(tag);
        }
    }

    @Override
    public void writeToStream(ByteBuf data) {
        super.writeToStream(data);
        for (int i = 0; i < parallels; i++) {
            data.writeInt(progresses[i]);
        }
    }

    @Override
    public boolean readFromStream(ByteBuf data) {
        boolean changed = super.readFromStream(data);
        for (int i = 0; i < parallels; i++) {
            int old = progresses[i];
            progresses[i] = data.readInt();
            changed |= old != progresses[i];
        }
        return changed;
    }

    public void tick() {
        if (this.markForUpdate) {
            updateRecipe();
            this.markForUpdate = false;
        }
        boolean update = false;
        boolean someWorked = false;
        for (int i = 0; i < parallels; i++) {
            if (this.currentRecipes[i] != null && doWork(i) && InventoryUtils.canInsert(output, currentRecipes[i].getOutput(), i)) {
                someWorked = true;
                this.progresses[i]++;
                afterValidateTick(i);
                if (this.progresses[i] >= getMachine().getRealSpeed()) {
                    boolean worked = false;
                    for (int j = 0; j < getMachine().getOperations(); j++) {
                        if (this.currentRecipes[i] != null && doWork(i) && InventoryUtils.canInsert(output, currentRecipes[i].getOutput(), i)) {
                            craftRecipe(i);
                            afterCrafting(i);
                            worked = true;
                        }
                    }
                    if (worked) {
                        this.progresses[i] = 0;
                    }
                }
                update = true;
            } else {
                if (this.progresses[i] != 0) {
                    update = true;
                    this.progresses[i] = 0;
                }
            }
        }
        if (someWorked && !isActive) {
            isActive = true;
        } else if (!someWorked && isActive) {
            isActive = false;
        }

        if (update) {
            getMachine().markForUpdate();
        }
    }

    protected boolean doWork(int idx) {
        return true;
    }

    protected void afterValidateTick(int idx) {}

    protected void craftRecipe(int idx) {
        for (Map.Entry<Integer, Integer> entry : this.craftingHashes[idx].getEntrySet()) {
            int slot = entry.getKey();
            int amount = entry.getValue();

            ItemStack stack = inventory.getStackInSlot(slot);
            stack.stackSize -= amount;
            if (stack.stackSize <= 0) {
                this.inventory.setInventorySlotContents(slot, null);
            }
        }

        ItemStack outputStack = currentRecipes[idx].getOutput().copy();
        InventoryUtils.insert(output, outputStack, idx);
    }

    protected void afterCrafting(int idx) {
        this.progresses[idx] = 0;
        this.craftingHashes[idx].clear();
        updateRecipe();
        getMachine().markForUpdate();
    }

    @Override
    public void updateRecipe() {
        for (int i = 0; i < parallels; i++) {
            this.craftingHashes[i].clear();
            int firstSlot = i * inputPerParallel;
            int[] slotsCheck = {firstSlot};
            if (inputPerParallel > 1) {
                int secondSlot = firstSlot + 1;
                slotsCheck = new int[]{firstSlot, secondSlot};
            }
            boolean found = false;
            for (T recipe : sortedRecipes) {
                if (tryMatch(recipe, this.craftingHashes[i], slotsCheck)) {
                    if (doesCorectRecipe != null) {
                        if (doesCorectRecipe.test(recipe)) {
                            this.currentRecipes[i] = recipe;
                            found = true;
                            break;
                        }
                    } else {
                        this.currentRecipes[i] = recipe;
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                this.currentRecipes[i] = null;
            }
        }
    }
}
