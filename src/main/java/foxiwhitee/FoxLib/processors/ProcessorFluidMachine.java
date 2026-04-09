package foxiwhitee.FoxLib.processors;

import foxiwhitee.FoxLib.api.processors.IFluidMachine;
import foxiwhitee.FoxLib.processors.hash.CraftingHash;
import foxiwhitee.FoxLib.recipes.IFluidMachineRecipe;
import foxiwhitee.FoxLib.utils.helpers.InventoryUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ProcessorFluidMachine<T extends IFluidMachineRecipe> extends ProcessorMachine<T> {
    protected final List<FluidTank> inputTanks = new ArrayList<>();
    protected final List<FluidTank> outputTanks = new ArrayList<>();
    protected final CraftingHash fluidsCraftingHash = new CraftingHash();
    protected final List<Fluid> ignoredFluids = new ArrayList<>();

    public ProcessorFluidMachine(IFluidMachine<T> machine) {
        super(machine);
        machine.initializeInputTanks(inputTanks);
        machine.initializeOutputTanks(outputTanks);
    }

    public List<Fluid> getIgnoredFluids() {
        return ignoredFluids;
    }

    public List<FluidTank> getInputTanks() {
        return inputTanks;
    }

    public List<FluidTank> getOutputTanks() {
        return outputTanks;
    }

    public int fill(FluidStack resource, boolean doFill) {
        if (resource == null || inputTanks.isEmpty()) return 0;

        for (FluidTank tank : inputTanks) {
            FluidStack inTank = tank.getFluid();
            if (inTank != null && inTank.isFluidEqual(resource)) {
                int filled = tank.fill(resource, doFill);
                if (filled > 0 && doFill) getMachine().markForUpdate();
                return filled;
            }
        }

        for (FluidTank tank : inputTanks) {
            if (tank.getFluid() == null) {
                int filled = tank.fill(resource, doFill);
                if (filled > 0 && doFill) getMachine().markForUpdate();
                return filled;
            }
        }

        return 0;
    }

    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (resource == null || outputTanks.isEmpty()) return null;

        for (FluidTank tank : outputTanks) {
            FluidStack inTank = tank.getFluid();
            if (inTank != null && inTank.isFluidEqual(resource)) {
                FluidStack drained = tank.drain(resource.amount, doDrain);
                if (drained != null && drained.amount > 0 && doDrain) {
                    getMachine().markForUpdate();
                }
                return drained;
            }
        }
        return null;
    }

    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (outputTanks.isEmpty()) return null;

        for (FluidTank tank : outputTanks) {
            if (tank.getFluid() != null) {
                FluidStack drained = tank.drain(maxDrain, doDrain);
                if (drained != null && drained.amount > 0 && doDrain) {
                    getMachine().markForUpdate();
                }
                return drained;
            }
        }
        return null;
    }

    public boolean canFill(Fluid fluid) {
        if (fluid == null || inputTanks.isEmpty()) return false;

        for (FluidTank tank : inputTanks) {
            FluidStack inTank = tank.getFluid();
            // Можна, якщо бак порожній або там така сама рідина і є місце
            if (inTank == null) return true;
            if (inTank.getFluid() == fluid && inTank.amount < tank.getCapacity()) return true;
        }
        return false;
    }

    public boolean canDrain(Fluid fluid) {
        if (fluid == null || outputTanks.isEmpty()) return false;

        for (FluidTank tank : outputTanks) {
            FluidStack inTank = tank.getFluid();
            if (inTank != null && inTank.getFluid() == fluid && inTank.amount > 0) return true;
        }
        return false;
    }

    public FluidTankInfo[] getTankInfo() {
        FluidTankInfo[] info = new FluidTankInfo[inputTanks.size() + outputTanks.size()];
        int i = 0;
        for (FluidTank t : inputTanks) info[i++] = t.getInfo();
        for (FluidTank t : outputTanks) info[i++] = t.getInfo();
        return info;
    }

    @Override
    public void writeToNbt(NBTTagCompound data) {
        super.writeToNbt(data);
        this.fluidsCraftingHash.writeToNbt(data);
        for (int i = 0; i < inputTanks.size(); i++) {
            InventoryUtils.writeTankToNbt(data, "inputTank" + i, inputTanks.get(i));
        }
        for (int i = 0; i < outputTanks.size(); i++) {
            InventoryUtils.writeTankToNbt(data, "outputTank" + i, outputTanks.get(i));
        }
        NBTTagList list = new NBTTagList();
        for (Fluid fluid : ignoredFluids) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setString("FluidName", FluidRegistry.getFluidName(fluid));
            list.appendTag(nbt);
        }
        data.setTag("ignoredFluids", list);
    }

    @Override
    public void readFromNbt(NBTTagCompound data) {
        super.readFromNbt(data);
        this.fluidsCraftingHash.readFromNbt(data);
        for (int i = 0; i < inputTanks.size(); i++) {
            InventoryUtils.readTankFromNbt(data, "inputTank" + i, inputTanks.get(i));
        }
        for (int i = 0; i < outputTanks.size(); i++) {
            InventoryUtils.readTankFromNbt(data, "outputTank" + i, outputTanks.get(i));
        }
        NBTTagList list = data.getTagList("ignoredFluids", 10);
        ignoredFluids.clear();
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound nbt = list.getCompoundTagAt(i);
            ignoredFluids.add(FluidRegistry.getFluid(nbt.getString("FluidName")));
        }
    }

    @Override
    public void writeToStream(ByteBuf data) {
        super.writeToStream(data);
        for (FluidTank tank : inputTanks) {
            InventoryUtils.writeTankToStream(data, tank);
        }
        for (FluidTank tank : outputTanks) {
            InventoryUtils.writeTankToStream(data, tank);
        }
    }

    @Override
    public boolean readFromStream(ByteBuf data) {
        boolean changed = super.readFromStream(data);
        for (FluidTank tank : inputTanks) {
            changed |= InventoryUtils.readTankFromStream(data, tank);
        }
        for (FluidTank tank : outputTanks) {
            changed |= InventoryUtils.readTankFromStream(data, tank);
        }
        return changed;
    }

    @Override
    protected boolean doWork() {
        return canFitOutputs(this.currentRecipe.getOutputFluids());
    }

    protected boolean canFitOutputs(List<FluidStack> results) {
        if (results == null || results.isEmpty()) return true;
        if (outputTanks.isEmpty()) return false;

        List<Integer> reservedTanks = new ArrayList<>();

        for (FluidStack output : results) {
            boolean fits = false;
            for (int i = 0; i < outputTanks.size(); i++) {
                if (reservedTanks.contains(i)) continue;

                FluidTank tank = outputTanks.get(i);
                FluidStack inTank = tank.getFluid();

                if (inTank != null && inTank.isFluidEqual(output)) {
                    if (tank.getCapacity() - inTank.amount >= output.amount) {
                        reservedTanks.add(i);
                        fits = true;
                        break;
                    }
                } else if (inTank == null) {
                    if (tank.getCapacity() >= output.amount) {
                        reservedTanks.add(i);
                        fits = true;
                        break;
                    }
                }
            }
            if (!fits) return false;
        }
        return true;
    }

    protected void fillOutputs(List<FluidStack> results) {
        if (results == null || results.isEmpty()) return;

        for (FluidStack output : results) {
            boolean filled = false;
            for (FluidTank tank : outputTanks) {
                if (tank.getFluid() != null && tank.getFluid().isFluidEqual(output)) {
                    tank.fill(output, true);
                    filled = true;
                    break;
                }
            }
            if (!filled) {
                for (FluidTank tank : outputTanks) {
                    if (tank.getFluid() == null) {
                        tank.fill(output, true);
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void afterCrafting() {
        super.afterCrafting();
        this.fluidsCraftingHash.clear();
    }

    @Override
    protected void craftRecipe() {
        super.craftRecipe();

        for (Map.Entry<Integer, Integer> entry : this.fluidsCraftingHash.getEntrySet()) {
            int tank = entry.getKey();
            int amount = entry.getValue();

            FluidTank t = inputTanks.get(tank);
            t.drain(amount, true);
        }

        fillOutputs(this.currentRecipe.getOutputFluids());
        getMachine().markForUpdate();
    }

    @Override
    public void updateRecipe() {
        for (T recipe : this.sortedRecipes) {
            if (tryMatch(recipe) && tryMatchFluids(recipe)) {
                this.currentRecipe = recipe;
                return;
            }
        }
        this.currentRecipe = null;
    }

    protected boolean tryMatchFluids(T recipe) {
        Map<Integer, Integer> consumptionMap = new HashMap<>();
        List<FluidStack> requiredFluids = recipe.getInputFluids();

        if (requiredFluids == null || requiredFluids.isEmpty()) return true;

        for (FluidStack required : requiredFluids) {
            boolean found = false;
            for (int i = 0; i < inputTanks.size(); i++) {
                FluidTank tank = inputTanks.get(i);
                FluidStack inTank = tank.getFluid();

                if (ignoredFluids.contains(required.getFluid())) {
                    found = true;
                    break;
                }
                if (inTank != null && inTank.isFluidEqual(required)) {
                    if (inTank.amount >= required.amount) {
                        consumptionMap.put(i, required.amount);
                        found = true;
                        break;
                    }
                }
            }
            if (!found) return false;
        }

        this.fluidsCraftingHash.putAll(consumptionMap);
        return true;
    }
}
