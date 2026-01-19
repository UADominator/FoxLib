package foxiwhitee.FoxLib.mixins.ae2;

import appeng.api.networking.crafting.ICraftingMedium;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.tile.crafting.TileCraftingTile;
import foxiwhitee.FoxLib.integration.applied.api.ITileMEServer;
import foxiwhitee.FoxLib.integration.applied.api.crafting.ICraftingCPUClusterAccessor;
import foxiwhitee.FoxLib.integration.applied.api.crafting.IPreCraftingMedium;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Map;

@Mixin(value = CraftingCPUCluster.class, remap = false)
public abstract class CraftingCPUClusterMixin implements ICraftingCPUClusterAccessor {

    @Shadow(remap = false)
    private IItemList<IAEStack<?>> waitingFor;

    @Shadow(remap = false)
    protected abstract void postChange(IAEStack<?> diff, BaseActionSource src);

    @Shadow(remap = false)
    protected abstract void postCraftingStatusChange(IAEStack<?> aeDiff);

    @Final
    @Shadow(remap = false)
    private Map<ICraftingPatternDetails, ?> tasks;

    @Shadow(remap = false)
    private long availableStorage;

    @Shadow(remap = false)
    private int accelerator;

    @Shadow(remap = false)
    @Final
    private LinkedList<TileCraftingTile> storage;

    @Shadow
    abstract void addTile(TileCraftingTile te);

    @Shadow
    abstract void done();

    @Shadow
    protected abstract TileCraftingTile getCore();

    @Shadow
    public abstract void readFromNBT(NBTTagCompound data);

    @Shadow
    protected abstract void updateCPU();

    @Shadow
    public abstract void updateName();

    @Redirect(method = "readFromNBT",
        at = @At(value = "INVOKE",
            target = "Lappeng/me/cluster/implementations/CraftingCPUCluster;getWorld()Lnet/minecraft/world/World;"))
    private World replaceGetWorldWithNull(CraftingCPUCluster instance) {
        return null;
    }

    @Inject(method = "addTile", at = @At("TAIL"))
    private void onAddTileEnd(TileCraftingTile te, CallbackInfo ci) {
        if (te instanceof ITileMEServer) {
            ITileMEServer server = (ITileMEServer) te;
            int index = server.getClusterIndex((CraftingCPUCluster) (Object) this);

            this.availableStorage += server.getClusterStorageBytes(index);
            this.accelerator += server.getClusterAccelerator(index);
            this.storage.add(te);
        }
    }

    @Redirect(method = "executeCrafting",
        at = @At(value = "INVOKE",
            target = "Lappeng/api/networking/crafting/ICraftingMedium;pushPattern(Lappeng/api/networking/crafting/ICraftingPatternDetails;Lnet/minecraft/inventory/InventoryCrafting;)Z"))
    private boolean redirectPushPattern(ICraftingMedium medium,
                                        ICraftingPatternDetails details,
                                        InventoryCrafting craftingInventory) {
        if (medium instanceof IPreCraftingMedium) {
            return ((IPreCraftingMedium) medium).pushPattern(details, craftingInventory, (CraftingCPUCluster) (Object) this);
        }
        return medium.pushPattern(details, craftingInventory);
    }

    @Override
    public long getWaitingFor(ICraftingPatternDetails pattern) {
        Object progress = tasks.get(pattern);
        if (progress == null) return 0L;
        try {
            Field valueField = progress.getClass().getDeclaredField("value");
            valueField.setAccessible(true);
            return valueField.getLong(progress);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setWaitingFor(ICraftingPatternDetails pattern, long value) {
        Object progress = tasks.get(pattern);
        if (progress == null) return;
        try {
            Field valueField = progress.getClass().getDeclaredField("value");
            valueField.setAccessible(true);
            valueField.setLong(progress, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void callPostChange(IAEStack<?> paramIAEItemStack, BaseActionSource paramBaseActionSource) {
        postChange(paramIAEItemStack, paramBaseActionSource);
    }

    @Override
    public IItemList<IAEStack<?>> getWaitingFor() {
        return waitingFor;
    }

    @Override
    public void callPostCraftingStatusChange(IAEStack<?> paramIAEItemStack) {
        postCraftingStatusChange(paramIAEItemStack);
    }

    @Override
    public void addTile$FoxAE2Upgrade(TileCraftingTile te) {
        addTile(te);
    }

    @Override
    public void doneMEServer() {
        TileCraftingTile core = getCore();
        core.setCoreBlock(true);
        if (core instanceof ITileMEServer) {
            ITileMEServer server = (ITileMEServer) core;
            int index = server.getClusterIndex((CraftingCPUCluster) (Object) this);
            if (server.getPreviousState(index) != null) {
                readFromNBT(server.getPreviousState(index));
                server.setPreviousState(index, null);
            }
        }

        updateCPU();
        updateName();
    }
}
