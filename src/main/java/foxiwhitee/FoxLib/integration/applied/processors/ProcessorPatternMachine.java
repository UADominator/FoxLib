package foxiwhitee.FoxLib.integration.applied.processors;

import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.events.MENetworkCraftingPatternChange;
import appeng.api.networking.security.MachineSource;
import appeng.me.GridAccessException;
import foxiwhitee.FoxLib.tile.inventory.FoxInternalInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;

import java.util.*;

public class ProcessorPatternMachine extends ProcessorNoPatternsCraftingMachine{
    protected final FoxInternalInventory patterns;

    public ProcessorPatternMachine(FoxInternalInventory patterns, MachineSource source, ICraftingProvider provider) {
        super(source, provider);
        this.patterns = patterns;
    }

    @Override
    public void writeToNbt(NBTTagCompound data) {
        super.writeToNbt(data);
        patterns.writeToNBT(data, "patterns");
    }

    @Override
    public void readFromNbt(NBTTagCompound data) {
        super.readFromNbt(data);
        patterns.readFromNBT(data, "patterns");
    }

    public IInventory getPatterns() {
        return this.patterns;
    }

    public void updatePatternList() {
        if (!proxy.isReady()) {
            return;
        }
        Boolean[] tracked = new Boolean[getPatterns().getSizeInventory()];
        Arrays.fill(tracked, false);
        if (patternList != null) {
            patternList.removeIf(pattern -> {
                for (int i = 0; i < getPatterns().getSizeInventory(); i++) {
                    if (pattern.getPattern() == getPatterns().getStackInSlot(i)) {
                        tracked[i] = true;
                        return false;
                    }
                }
                return true;
            });
        }
        for (int i = 0; i < tracked.length; i++) {
            if (!tracked[i]) {
                addPattern(getPatterns().getStackInSlot(i));
            }
        }
        try {
            proxy.getGrid().postEvent(new MENetworkCraftingPatternChange(provider, proxy.getNode()));
        } catch (GridAccessException ignored) {}
    }
}
