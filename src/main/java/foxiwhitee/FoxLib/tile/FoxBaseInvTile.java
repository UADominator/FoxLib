package foxiwhitee.FoxLib.tile;


import foxiwhitee.FoxLib.block.FoxBaseBlock;
import foxiwhitee.FoxLib.tile.event.TileEvent;
import foxiwhitee.FoxLib.tile.event.TileEventType;
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

import java.util.ArrayList;
import java.util.List;

public abstract class FoxBaseInvTile extends FoxBaseTile implements IFoxInternalInventory, ISidedInventory {
    protected final List<IInventory> dropInventory = new ArrayList<>();

    public FoxBaseInvTile() {
        dropInventory.add(this);
    }

    @TileEvent(TileEventType.SERVER_NBT_READ)
    @SuppressWarnings("unused")
    public void readFromNBT_(NBTTagCompound data) {
        getInternalInventory().readFromNBT(data, "inventory");
    }

    @TileEvent(TileEventType.SERVER_NBT_WRITE)
    @SuppressWarnings("unused")
    public void writeToNBT_(NBTTagCompound data) {
        getInternalInventory().writeToNBT(data, "inventory");
    }

    public abstract FoxInternalInventory getInternalInventory();

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        Block blk = this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord);
        if (blk instanceof FoxBaseBlock) {
            ForgeDirection mySide = ForgeDirection.getOrientation(side);
            return this.getAccessibleSlotsBySide(((FoxBaseBlock)blk).mapRotation(this, mySide));
        } else {
            return this.getAccessibleSlotsBySide(ForgeDirection.getOrientation(side));
        }
    }

    @SuppressWarnings("unused")
    public void getDrops(World w, int x, int y, int z, List<ItemStack> drops) {
        for (IInventory inv : dropInventory) {
            for(int l = 0; l < inv.getSizeInventory(); ++l) {
                ItemStack is = inv.getStackInSlot(l);
                if (is != null) {
                    drops.add(is);
                }
            }
        }
    }

    @Override
    public String getInventoryName() {
        return "inventory";
    }

    @Override
    public boolean canInsertItem(int slotIndex, ItemStack insertingItem, int side) {
        return this.isItemValidForSlot(slotIndex, insertingItem);
    }

    @Override
    public boolean canExtractItem(int slotIndex, ItemStack extractedItem, int side) {
        return true;
    }

    @Override
    public int getSizeInventory() {
        return getInternalInventory().getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return getInternalInventory().getStackInSlot(slotIn);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return getInternalInventory().decrStackSize(index, count);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return getInternalInventory().getStackInSlotOnClosing(index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        getInternalInventory().setInventorySlotContents(index, stack);
    }

    @Override
    public boolean hasCustomInventoryName() {
        return getInternalInventory().hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit() {
        return getInternalInventory().getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return getInternalInventory().isUseableByPlayer(player);
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    public abstract int[] getAccessibleSlotsBySide(ForgeDirection whichSide);
}
