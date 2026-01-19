package foxiwhitee.FoxLib.integration.applied.api.crafting;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.tile.crafting.TileCraftingTile;

public interface ICraftingCPUClusterAccessor {
    long getWaitingFor(ICraftingPatternDetails paramICraftingPatternDetails);

    void setWaitingFor(ICraftingPatternDetails paramICraftingPatternDetails, long paramLong);

    void callPostChange(IAEStack<?> paramIAEItemStack, BaseActionSource paramBaseActionSource);

    IItemList<IAEStack<?>> getWaitingFor();

    void callPostCraftingStatusChange(IAEStack<?> paramIAEItemStack);

    void addTile$FoxAE2Upgrade(TileCraftingTile te);

    void doneMEServer();
}
