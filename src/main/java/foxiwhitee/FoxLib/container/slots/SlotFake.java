package foxiwhitee.FoxLib.container.slots;

import foxiwhitee.FoxLib.container.slots.FoxSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotFake extends FoxSlot {
    public SlotFake(IInventory inv, int idx, int x, int y) {
        super(inv, idx, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return true;
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        return false;
    }

    public void handleFakeClick(ItemStack stackOnCursor) {
        if (stackOnCursor != null) {
            ItemStack fakeStack = stackOnCursor.copy();
            fakeStack.stackSize = 1;
            this.putStack(fakeStack);
        } else {
            this.putStack(null);
        }
    }
}
