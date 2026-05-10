package foxiwhitee.FoxLib.processors;

import foxiwhitee.FoxLib.api.processors.IMachine;
import foxiwhitee.FoxLib.api.processors.IParallelPoweredMachine;
import foxiwhitee.FoxLib.recipes.IMachineRecipe;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

@SuppressWarnings("unused")
public class ProcessorParallelPoweredMachine<T extends IMachineRecipe> extends ProcessorParallelMachine<T> {
    protected final double[] needPowers;
    protected double power, maxPower;
    protected boolean perTick;

    public ProcessorParallelPoweredMachine(IMachine<T> machine, int parallels, double maxPower) {
        super(machine, parallels);
        this.maxPower = maxPower;
        this.needPowers = new double[parallels];
    }

    public void setConsumePowerPerTick(boolean perTick) {
        this.perTick = perTick;
    }

    @Override
    public IParallelPoweredMachine<T> getMachine() {
        return (IParallelPoweredMachine<T>) super.getMachine();
    }

    public double getPower() {
        return power;
    }

    public double getMaxPower() {
        return maxPower;
    }

    public void setPower(double power) {
        this.power = power;
        getMachine().markForUpdate();
    }

    public void setMaxPower(double maxPower) {
        this.maxPower = maxPower;
        getMachine().markForUpdate();
    }


    public double receivePower(double power) {
        return receivePower(power, false);
    }

    public double receivePower(double power, boolean simulate) {
        if (power >= 0) {
            double energyReceived = Math.min(maxPower - this.power, power);

            if (!simulate) {
                this.power += energyReceived;
                getMachine().markForUpdate();
            }
            return energyReceived;
        } else {
            double energyExtracted = Math.min(this.power, -power);
            if (!simulate) {
                this.power -= energyExtracted;
                getMachine().markForUpdate();
            }
            return energyExtracted;
        }
    }

    @Override
    public void writeToNbt(NBTTagCompound data) {
        super.writeToNbt(data);
        data.setDouble("power", this.power);
        data.setDouble("maxPower", this.maxPower);
        for (int i = 0; i < parallels; i++) {
            data.setDouble("needPower_" + i, needPowers[i]);
        }
    }

    @Override
    public void readFromNbt(NBTTagCompound data) {
        super.readFromNbt(data);
        this.power = data.getDouble("power");
        this.maxPower = data.getDouble("maxPower");
        for (int i = 0; i < parallels; i++) {
            this.needPowers[i] = data.getDouble("needPower_" + i);
        }
    }

    @Override
    public void writeToStream(ByteBuf data) {
        super.writeToStream(data);
        data.writeDouble(this.power);
        data.writeDouble(this.maxPower);
    }

    @Override
    public boolean readFromStream(ByteBuf data) {
        boolean old = super.readFromStream(data);
        double oldPower = this.power;
        double oldMaxPower = this.maxPower;
        this.power = data.readDouble();
        this.maxPower = data.readDouble();
        return old || oldPower != this.power || oldMaxPower != this.maxPower;
    }

    @Override
    protected boolean doWork(int idx) {
        return this.power >= this.needPowers[idx];
    }

    @Override
    protected void afterCrafting(int idx) {
        super.afterCrafting(idx);
        this.power -= this.needPowers[idx];
        this.needPowers[idx] = 0;
        getMachine().markForUpdate();
    }


    @Override
    protected void afterValidateTick(int idx) {
        if (perTick) {
            this.power -= this.needPowers[idx];
        }
    }

    @Override
    public void updateRecipe() {
        super.updateRecipe();
        for (int i = 0; i < parallels; i++) {
            T current = this.currentRecipes[i];
            if (current != null) {
                this.needPowers[i] = getMachine().needPower(current);
            } else {
                this.needPowers[i] = 0;
            }
        }
    }
}
