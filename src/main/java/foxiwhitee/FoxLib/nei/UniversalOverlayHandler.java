package foxiwhitee.FoxLib.nei;

import codechicken.nei.PositionedStack;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.recipe.IRecipeHandler;
import foxiwhitee.FoxLib.client.gui.FoxBaseGui;
import foxiwhitee.FoxLib.container.FoxBaseContainer;
import foxiwhitee.FoxLib.network.NetworkManager;
import foxiwhitee.FoxLib.network.packets.C2SNeiOverlayPacket;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UniversalOverlayHandler implements IOverlayHandler {
    public final static List<Class<? extends FoxBaseGui>> guis = new ArrayList<>();
    public final static Map<Class<? extends FoxBaseGui>, PositionsSort> sorts = new HashMap<>();

    private static boolean canSend(GuiContainer gui) {
        for (Class<? extends FoxBaseGui> clazz : guis) {
            if (clazz.isInstance(gui)) {
                return true;
            }
        }
        return false;
    }

    private static TileEntity get(GuiContainer gui) {
        if (canSend(gui)) {
            if (gui instanceof FoxBaseGui foxBaseGui) {
                if (foxBaseGui.getContainer() instanceof FoxBaseContainer container) {
                    return container.getTileEntity();
                }
            }
        }
        return null;
    }

    @Override
    public void overlayRecipe(GuiContainer gui, IRecipeHandler recipe, int recipeIndex, boolean shift) {
        TileEntity overlay = get(gui);
        if (overlay != null) {
            List<PositionedStack> ingredients = recipe.getIngredientStacks(recipeIndex);
            PositionsSort sort = sorts.get(gui.getClass());
            if (sort != null) {
                ingredients = sort.sort(ingredients);
            }
            NBTTagCompound root = new NBTTagCompound();

            for (int i = 0; i < ingredients.size(); ++i) {
                PositionedStack pStack = ingredients.get(i);

                if (pStack != null && pStack.items != null && pStack.items.length > 0) {
                    NBTTagList variants = new NBTTagList();
                    for (ItemStack stack : pStack.items) {
                        if (stack != null) {
                            NBTTagCompound itemTag = new NBTTagCompound();
                            stack.writeToNBT(itemTag);
                            variants.appendTag(itemTag);
                        }
                    }
                    root.setTag("#" + i, variants);
                } else {
                    root.setString("#" + i, "null");
                }
            }

            NBTTagCompound tag = new NBTTagCompound();
            PositionedStack result = recipe.getResultStack(recipeIndex);
            if (result != null) {
                result.item.writeToNBT(tag);
                root.setTag("result", tag);
            }
            NetworkManager.instance.sendToServer(new C2SNeiOverlayPacket(overlay.xCoord, overlay.yCoord, overlay.zCoord, root));
        }
    }

    @FunctionalInterface
    public interface PositionsSort {
        List<PositionedStack> sort(List<PositionedStack> stacks);
    }
}
