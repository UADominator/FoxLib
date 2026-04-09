package foxiwhitee.FoxLib.integration.applied.tile;

import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import foxiwhitee.FoxLib.block.FoxBaseBlock;
import foxiwhitee.FoxLib.tile.inventory.FoxInternalInventory;
import foxiwhitee.FoxLib.tile.inventory.IFoxInternalInventory;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public abstract class TileNetworkInv extends TileNetworkOrientable implements IFoxInternalInventory, ISidedInventory {
    public TileNetworkInv() {

    }

    public abstract FoxInternalInventory getInternalInventory();

    @Override
    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public void writeToNbt_(NBTTagCompound data) {
        super.writeToNbt_(data);
        getInternalInventory().writeToNBT(data, "inventory");
    }

    @Override
    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readFromNbt_(NBTTagCompound data) {
        super.readFromNbt_(data);
        getInternalInventory().readFromNBT(data, "inventory");
    }

    public int[] getAccessibleSlotsFromSide(int side) {
        Block blk = this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord);
        if (blk instanceof FoxBaseBlock) {
            ForgeDirection mySide = ForgeDirection.getOrientation(side);
            return this.getAccessibleSlotsBySide(((FoxBaseBlock)blk).mapRotation(this, mySide));
        } else {
            return this.getAccessibleSlotsBySide(ForgeDirection.getOrientation(side));
        }
    }

    public void getDrops(World w, int x, int y, int z, List<ItemStack> drops) {
        IInventory inv = this;

        for(int l = 0; l < inv.getSizeInventory(); ++l) {
            ItemStack is = inv.getStackInSlot(l);
            if (is != null) {
                drops.add(is);
            }
        }

    }

    public boolean canInsertItem(int slotIndex, ItemStack insertingItem, int side) {
        return this.isItemValidForSlot(slotIndex, insertingItem);
    }

    public boolean canExtractItem(int slotIndex, ItemStack extractedItem, int side) {
        return true;
    }

    public int getSizeInventory() {
        return this.getInternalInventory().getSizeInventory();
    }

    public ItemStack getStackInSlot(int slotIn) {
        return this.getInternalInventory().getStackInSlot(slotIn);
    }

    public ItemStack decrStackSize(int index, int count) {
        return this.getInternalInventory().decrStackSize(index, count);
    }

    public ItemStack getStackInSlotOnClosing(int index) {
        return this.getInternalInventory().getStackInSlotOnClosing(index);
    }

    public void setInventorySlotContents(int index, ItemStack stack) {
        this.getInternalInventory().setInventorySlotContents(index, stack);
    }

    public String getInventoryName() {
        return "internalInventory";
    }

    public boolean hasCustomInventoryName() {
        return this.getInternalInventory().hasCustomInventoryName();
    }

    public int getInventoryStackLimit() {
        return this.getInternalInventory().getInventoryStackLimit();
    }

    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.getInternalInventory().isUseableByPlayer(player);
    }

    public void openInventory() {
    }

    public void closeInventory() {
    }

    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    public abstract int[] getAccessibleSlotsBySide(ForgeDirection var1);
}
