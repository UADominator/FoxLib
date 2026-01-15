package foxiwhitee.FoxLib.tile.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IFoxInternalInventory {
    void onChangeInventory(IInventory inventory, int idx, InvOperation operation, ItemStack removed, ItemStack added);
}
