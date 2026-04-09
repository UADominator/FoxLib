package foxiwhitee.FoxLib.integration.applied.tile;

import appeng.api.implementations.tiles.ICraftingMachine;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.networking.events.MENetworkCraftingPatternChange;
import appeng.api.networking.events.MENetworkEvent;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.DimensionalCoord;
import appeng.api.util.IInterfaceViewable;
import appeng.me.GridAccessException;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import foxiwhitee.FoxLib.integration.applied.api.crafting.IPreCraftingMedium;
import foxiwhitee.FoxLib.integration.applied.processors.ProcessorPatternMachine;
import foxiwhitee.FoxLib.tile.inventory.FoxInternalInventory;
import foxiwhitee.FoxLib.tile.inventory.InvOperation;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

@SuppressWarnings("unused")
public abstract class TilePatternMachine extends TileNetworkInv implements ICraftingMachine, ICraftingProvider, IGridTickable, IInterfaceViewable, IPreCraftingMedium {
    protected ProcessorPatternMachine processor;

    public TilePatternMachine() {
        this.getProxy().setIdlePowerUsage(32);
        this.getProxy().setFlags(GridFlags.REQUIRE_CHANNEL);
        FoxInternalInventory patterns = new FoxInternalInventory(this, 36, 1);
        this.processor = new ProcessorPatternMachine(patterns, new MachineSource(this), this);
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    @Override
    public ItemStack getSelfRep() {
        return getItemFromTile(this);
    }

    @Override
    public ItemStack getDisplayRep() {
        return getItemFromTile(this);
    }

    @Override
    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public void writeToNbt_(NBTTagCompound data) {
        super.writeToNbt_(data);
        this.processor.writeToNbt(data);
    }

    @Override
    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readFromNbt_(NBTTagCompound data) {
        super.readFromNbt_(data);
        this.processor.readFromNbt(data);
    }

    @Override
    public boolean pushPattern(ICraftingPatternDetails iCraftingPatternDetails, InventoryCrafting inventoryCrafting, ForgeDirection forgeDirection) {
        return false;
    }

    @Override
    public boolean acceptsPlans() {
        return false;
    }

    @Override
    public void provideCrafting(ICraftingProviderHelper helper) {
        this.processor.provideCrafting(helper);
    }

    @Override
    public void onReady() {
        super.onReady();
        if (!worldObj.isRemote) {
            this.processor.setWorld(worldObj);
            this.processor.setProxy(getProxy());
            this.processor.setMaxCount(this::getMaxCount);
            this.processor.setValidation(this::isValidCraft);
            this.processor.updatePatternList();
        }
    }

    @Override
    public boolean pushPattern(ICraftingPatternDetails iCraftingPatternDetails, InventoryCrafting inventoryCrafting) {
        return false;
    }

    @Override
    public boolean isBusy() {
        return this.processor.isBusy();
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode iGridNode) {
        return new TickingRequest(1, 1, false, false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (processor.tick()) {
            markForUpdate();
        }

        return TickRateModulation.SAME;
    }

    @Override
    public int rows() {
        return this.processor.getPatterns().getSizeInventory() / rowSize();
    }

    @Override
    public int rowSize() {
        return 9;
    }

    @Override
    public IInventory getPatterns() {
        return this.processor.getPatterns();
    }

    @Override
    public TileEntity getTileEntity() {
        return this;
    }

    @Override
    public boolean shouldDisplay() {
        return true;
    }

    @Override
    public void onChangeInventory(IInventory iInventory, int i, InvOperation invOperation, ItemStack itemStack, ItemStack itemStack1) {
        if (iInventory == getPatterns()) {
            this.processor.updatePatternList();
        }
    }

    protected abstract long getMaxCount();

    protected abstract boolean isValidCraft(ICraftingPatternDetails pattern);

    @Override
    public boolean pushPattern(ICraftingPatternDetails pattern, InventoryCrafting ic, CraftingCPUCluster cluster) {
        return processor.pushPattern(pattern, ic, cluster);
    }

    @MENetworkEventSubscribe
    @SuppressWarnings("unused")
    public void onNetworkChange(MENetworkEvent event) {
        try {
            getProxy().getGrid().postEvent(new MENetworkCraftingPatternChange(this, getProxy().getNode()));
        } catch (GridAccessException ignored) {
        }
    }

    @Override
    public void getDrops(World w, int x, int y, int z, List<ItemStack> drops) {
        super.getDrops(w, x, y, z, drops);
        if (getPatterns() != getInternalInventory()) {
            for (int i = 0; i < getPatterns().getSizeInventory(); i++) {
                ItemStack stack = getPatterns().getStackInSlot(i);
                if (stack != null) {
                    drops.add(stack);
                }
            }
        }

        for(IAEStack<?> stack : this.processor.getNeedSend()) {
            if (stack instanceof IAEItemStack aes) {
                drops.add(aes.getItemStack());
            }
        }
    }

    @Override
    public String getName() {
        return Objects.requireNonNull(getItemFromTile(this)).getUnlocalizedName();
    }
}
