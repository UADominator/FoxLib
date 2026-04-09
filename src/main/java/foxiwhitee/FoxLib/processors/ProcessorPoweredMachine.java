package foxiwhitee.FoxLib.processors;

import foxiwhitee.FoxLib.api.processors.IPoweredMachine;
import foxiwhitee.FoxLib.recipes.IMachineRecipe;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

@SuppressWarnings("unused")
public class ProcessorPoweredMachine<T extends IMachineRecipe> extends ProcessorMachine<T> {
    protected double power, maxPower, needPower;

    public ProcessorPoweredMachine(IPoweredMachine<T> machine, double maxPower) {
        super(machine);
        this.maxPower = maxPower;
    }

    @Override
    public IPoweredMachine<T> getMachine() {
        return (IPoweredMachine<T>) super.getMachine();
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
        double receivedPower = this.power + power;
        if (receivedPower < 0) {
            this.power = 0;
            getMachine().markForUpdate();
            return receivedPower;
        } else if (receivedPower > this.maxPower) {
            this.power = this.maxPower;
            getMachine().markForUpdate();
            return this.maxPower - receivedPower;
        } else {
            this.power = receivedPower;
            getMachine().markForUpdate();
            return receivedPower;
        }
    }

    @Override
    public void writeToNbt(NBTTagCompound data) {
        super.writeToNbt(data);
        data.setDouble("power", this.power);
        data.setDouble("maxPower", this.maxPower);
        data.setDouble("needPower", this.needPower);
    }

    @Override
    public void readFromNbt(NBTTagCompound data) {
        super.readFromNbt(data);
        this.power = data.getDouble("power");
        this.maxPower = data.getDouble("maxPower");
        this.needPower = data.getDouble("needPower");
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
    public void updateRecipe() {
        super.updateRecipe();
        if (this.currentRecipe != null) {
            this.needPower = getMachine().needPower(this.currentRecipe);
        } else {
            this.needPower = 0;
        }
    }

    @Override
    protected boolean doWork() {
        return this.power >= this.needPower;
    }

    @Override
    protected void afterCrafting() {
        super.afterCrafting();
        this.power -= this.needPower;
        this.needPower = 0;
        getMachine().markForUpdate();
    }
}
