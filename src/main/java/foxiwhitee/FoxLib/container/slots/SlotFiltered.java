package foxiwhitee.FoxLib.container.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class SlotFiltered extends FoxSlot {
    public static final Map<String, Predicate<ItemStack>> filters = new HashMap<>();
    private final String which;
    private int stackLimit = -1;

    @Deprecated
    public SlotFiltered(String valid, IInventory i, int slotIndex, int x, int y, InventoryPlayer p) {
        this(valid, null, i, slotIndex, x, y, p);
    }

    @Deprecated
    public SlotFiltered(String valid, IOptionalSlotHost host, IInventory i, int slotIndex, int x, int y, InventoryPlayer p) {
        super(i, host, slotIndex, x, y);
        this.which = valid;
    }

    public SlotFiltered(String valid, IInventory i, int slotIndex, int x, int y) {
        this(valid, null, i, slotIndex, x, y);
    }

    public SlotFiltered(String valid, IOptionalSlotHost host, IInventory i, int slotIndex, int x, int y) {
        super(i, host, slotIndex, x, y);
        this.which = valid;
    }

    public int getSlotStackLimit() {
        return this.stackLimit != -1 ? this.stackLimit : super.getSlotStackLimit();
    }

    public Slot setStackLimit(int i) {
        this.stackLimit = i;
        return this;
    }

    public boolean isItemValid(ItemStack i) {
        if (!isEnabled()) return false;
        if (i == null) {
            return false;
        } else if (i.getItem() == null) {
            return false;
        } else if (!this.inventory.isItemValidForSlot(this.getSlotIndex(), i)) {
            return false;
        } else if (!this.isAllowEdit()) {
            return false;
        } else {
            for (Map.Entry<String, Predicate<ItemStack>> entry : filters.entrySet()) {
                if (entry.getKey().equals(which)) {
                    return entry.getValue().test(i);
                }
            }
            return false;
        }
    }

    public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
        return this.isAllowEdit();
    }

    public ItemStack getDisplayStack() {
        return super.getStack();
    }

    private boolean isAllowEdit() {
        return true;
    }

}
