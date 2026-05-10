package foxiwhitee.FoxLib.integration.applied.tile;

import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.util.item.AEItemStack;
import foxiwhitee.FoxLib.api.IHasNeiOverlay;
import foxiwhitee.FoxLib.tile.inventory.FoxInternalInventory;
import foxiwhitee.FoxLib.tile.inventory.InvOperation;
import foxiwhitee.FoxLib.utils.helpers.ItemStackUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public abstract class TileUniversalPatternEncoder extends TileNetworkInv implements IHasNeiOverlay {
    private final FoxInternalInventory inventory = new FoxInternalInventory(this, 2);
    private final FoxInternalInventory output = new FoxInternalInventory(this, 1);
    private final FoxInternalInventory crafting;
    protected long strictInputCount;

    public TileUniversalPatternEncoder(int craftingInventorySize) {
        this.crafting = new FoxInternalInventory(this, craftingInventorySize);
    }

    public void encode() {
        if (inventory.getStackInSlot(0) == null || inventory.getStackInSlot(0).stackSize < 1) {
            return;
        }
        ItemStack output = new ItemStack(getEncodedPattern());
        if (this.inventory.getStackInSlot(1) != null && !ItemStackUtil.stackEquals(output, this.inventory.getStackInSlot(1))) {
            return;
        }
        IAEItemStack[] in = this.getInputs();
        IAEItemStack out = AEItemStack.create(this.output.getStackInSlot(0));
        if (in.length > 0 && out != null) {
            NBTTagCompound encodedValue = new NBTTagCompound();
            NBTTagList tagIn = new NBTTagList();
            NBTTagList tagOut = new NBTTagList();

            for(IAEItemStack i : in) {
                tagIn.appendTag(this.createItemTag(i, strictInputCount));
            }

            tagOut.appendTag(this.createItemTag(out));

            encodedValue.setBoolean("crafting", false);
            encodedValue.setBoolean("beSubstitute", false);
            encodedValue.setBoolean("substitute", false);

            encodedValue.setTag("in", tagIn);
            encodedValue.setTag("out", tagOut);
            afterEncoding(encodedValue);
            output.setTagCompound(encodedValue);

            if (inventory.getStackInSlot(1) != null && inventory.getStackInSlot(1).stackSize < 64) {
                inventory.getStackInSlot(1).stackSize++;
                inventory.getStackInSlot(0).stackSize--;
            } else if (inventory.getStackInSlot(1) == null) {
                inventory.setInventorySlotContents(1, output);
                inventory.getStackInSlot(0).stackSize--;
            }
            if (inventory.getStackInSlot(0).stackSize <= 0) {
                inventory.setInventorySlotContents(0, null);
            }
        }
    }

    private NBTBase createItemTag(IAEItemStack i, long strictCount) {
        NBTTagCompound c = new NBTTagCompound();
        if (i != null) {
            if (strictCount > 0) {
                i = i.copy();
                i.setStackSize(strictCount);
            }
            i.writeToNBT(c);
        }
        return c;
    }

    private NBTBase createItemTag(IAEItemStack i) {
        return createItemTag(i, 0);
    }

    private IAEItemStack[] getInputs() {
        List<IAEItemStack> in = new ArrayList<>();
        for (int i = 0; i < crafting.getSizeInventory(); i++) {
            IAEItemStack ais = AEItemStack.create(crafting.getStackInSlot(i));
            if (ais == null) {
                continue;
            }
            IAEItemStack con = null;
            for (IAEItemStack st : in) {
                if (ItemStackUtil.stackEquals(ais.getItemStack(), st.getItemStack())) {
                    con = st;
                    break;
                }
            }
            if (con != null) {
                con.setStackSize(con.getStackSize() + ais.getStackSize());
            } else {
                in.add(ais.copy());
            }
        }
        return in.toArray(new IAEItemStack[0]);
    }

    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public void writeToNbt_(NBTTagCompound data) {
        super.writeToNbt_(data);
        crafting.writeToNBT(data, "crafting");
        output.writeToNBT(data, "output");
        data.setLong("strictInputCount", strictInputCount);
    }

    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readFromNbt_(NBTTagCompound data) {
        super.readFromNbt_(data);
        crafting.readFromNBT(data, "crafting");
        output.readFromNBT(data, "output");
        strictInputCount = data.getLong("strictInputCount");
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    @Override
    public AECableType getCableConnectionType(ForgeDirection dir) {
        return AECableType.SMART;
    }

    @Override
    public FoxInternalInventory getInternalInventory() {
        return inventory;
    }

    public FoxInternalInventory getOutputInventory() {
        return output;
    }

    public FoxInternalInventory getCraftingInventory() {
        return crafting;
    }

    @Override
    public void onChangeInventory(IInventory inv, int slot, InvOperation mc, ItemStack removed, ItemStack added) {
        if (inv == crafting && mc != InvOperation.markDirty) {
            updateRecipe();
        }
    }

    @Override
    public int[] getAccessibleSlotsBySide(ForgeDirection whichSide) {
        return new int[0];
    }

    protected void afterEncoding(NBTTagCompound data) {}

    protected abstract Item getEncodedPattern();

    public abstract void updateRecipe();
}
