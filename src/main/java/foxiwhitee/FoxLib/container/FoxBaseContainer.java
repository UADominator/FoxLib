package foxiwhitee.FoxLib.container;

import foxiwhitee.FoxLib.container.slots.SlotFake;
import foxiwhitee.FoxLib.container.slots.SlotPlayerHotBar;
import foxiwhitee.FoxLib.container.slots.SlotPlayerInv;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public abstract class FoxBaseContainer extends Container {
    private final InventoryPlayer invPlayer;
    private final TileEntity tileEntity;
    private final Slot[] playerSlots;

    public FoxBaseContainer(EntityPlayer player, TileEntity tileEntity) {
        this.invPlayer = player.inventory;
        this.tileEntity = tileEntity;
        this.playerSlots = new Slot[36];
    }

    @Deprecated
    public FoxBaseContainer(InventoryPlayer ip, TileEntity myTile) {
        this.invPlayer = ip;
        this.tileEntity = myTile;
        this.playerSlots = new Slot[36];
    }

    protected void bindPlayerInventory(int offsetX, int offsetY) {
        for(int i = 0; i < 9; ++i) {
            this.playerSlots[i] = this.addSlotToContainer(new SlotPlayerHotBar(invPlayer, i, 8 + i * 18 + offsetX, 58 + offsetY));
        }

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.playerSlots[j + i * 9 + 9] = this.addSlotToContainer(new SlotPlayerInv(invPlayer, j + i * 9 + 9, 8 + j * 18 + offsetX, offsetY + i * 18));
            }
        }
    }

    @Deprecated
    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer, int offsetX, int offsetY) {
        bindPlayerInventory(offsetX, offsetY);
    }

    public InventoryPlayer getInventoryPlayer() {
        return this.invPlayer;
    }

    public TileEntity getTileEntity() {
        return this.tileEntity;
    }

    public boolean canInteractWith(EntityPlayer entityplayer) {
        return !(this.tileEntity instanceof IInventory) || ((IInventory) this.tileEntity).isUseableByPlayer(entityplayer);
    }

    public boolean mergeStackToPlayerInv(ItemStack stack) {
        return mergeItemStack(stack, playerSlots[0].slotNumber, playerSlots[35].slotNumber, false);
    }

    public boolean mergeStackToTileInv(ItemStack stack) {
        return mergeItemStack(stack, playerSlots[35].slotNumber + 1, inventorySlots.size(), false);
    }

    public boolean clickWasInPlayerInventory(Slot slot) {
        return playerSlots[0] != null && slot != null && playerSlots[0].slotNumber <= slot.slotNumber && playerSlots[35] != null && slot.slotNumber <= playerSlots[35].slotNumber;
    }

    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
        Slot slot = inventorySlots.get(slotId);
        if (slot == null || !slot.getHasStack() || (slot instanceof SlotFake && clickWasInPlayerInventory(slot))) {
            return null;
        }
        ItemStack stack = slot.getStack();
        boolean didMerge = clickWasInPlayerInventory(slot) ? mergeStackToTileInv(stack) : mergeStackToPlayerInv(stack);
        if (didMerge) {
            if (stack.stackSize == 0) {
                slot.putStack(null);
            }
            slot.onSlotChanged();
            return null;
        }
        return null;
    }

    protected boolean mergeItemStack(ItemStack stack, int start, int end, boolean reverse) {
        boolean changed = false;
        int index = start;

        if (reverse) {
            index = end - 1;
        }

        Slot slot;
        ItemStack slotStack;

        if (stack.isStackable()) {
            while (stack.stackSize > 0 && (!reverse && index < end || reverse && index >= start)) {

                slot = this.inventorySlots.get(index);
                slotStack = slot.getStack();

                if (!slot.isItemValid(stack)) {
                    if (reverse) --index; else ++index;
                    continue;
                }


                if (slotStack != null
                    && slotStack.getItem() == stack.getItem()
                    && (!stack.getHasSubtypes() || stack.getItemDamage() == slotStack.getItemDamage())
                    && ItemStack.areItemStackTagsEqual(stack, slotStack)) {

                    if (slot instanceof SlotFake) {
                        ItemStack fakeCopy = stack.copy();
                        slot.putStack(fakeCopy);
                        slot.onSlotChanged();
                        return true;
                    }

                    int total = slotStack.stackSize + stack.stackSize;
                    int max = Math.min(stack.getMaxStackSize(), slot.inventory.getInventoryStackLimit());

                    if (total <= max) {
                        stack.stackSize = 0;
                        slotStack.stackSize = total;
                        slot.onSlotChanged();
                        changed = true;
                    } else if (slotStack.stackSize < max) {
                        stack.stackSize -= (max - slotStack.stackSize);
                        slotStack.stackSize = max;
                        slot.onSlotChanged();
                        changed = true;
                    }
                }

                if (reverse) --index; else ++index;
            }
        }

        if (stack.stackSize > 0) {

            if (reverse) index = end - 1;
            else index = start;

            while (!reverse && index < end || reverse && index >= start) {

                slot = this.inventorySlots.get(index);
                slotStack = slot.getStack();

                if (!slot.isItemValid(stack)) {
                    if (reverse) --index; else ++index;
                    continue;
                }

                if (slotStack == null) {
                    int max = Math.min(stack.stackSize, slot.inventory.getInventoryStackLimit());
                    ItemStack newStack = stack.copy();
                    newStack.stackSize = max;
                    slot.putStack(newStack);
                    slot.onSlotChanged();
                    if (slot instanceof SlotFake) {
                        max = 0;
                    }
                    stack.stackSize -= max;
                    changed = true;
                    break;
                }


                if (reverse) --index; else ++index;
            }
        }

        return changed;
    }

    @Override
    public ItemStack slotClick(int slotId, int mouseButton, int modifier, EntityPlayer player) {
        if (slotId >= 0 && slotId < this.inventorySlots.size()) {
            Slot slot = this.inventorySlots.get(slotId);

            if (slot instanceof SlotFake fake) {
                fake.handleFakeClick(player.inventory.getItemStack());
                return null;
            }
        }

        return super.slotClick(slotId, mouseButton, modifier, player);
    }
}
