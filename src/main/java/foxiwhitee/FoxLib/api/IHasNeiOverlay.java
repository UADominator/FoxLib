package foxiwhitee.FoxLib.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public interface IHasNeiOverlay {
    void overlayRecipe(NBTTagCompound nbt, EntityPlayer player);

    default List<List<ItemStack>> readIngredients(NBTTagCompound root) {
        List<List<ItemStack>> result = new LinkedList<>();

        for (int i = 0; root.hasKey("#" + i); ++i) {
            if (root.getTag("#" + i) instanceof NBTTagString && root.getString("#" + i).equals("null")) {
                result.add(null);
            } else {
                NBTTagList variantsTagList = root.getTagList("#" + i, 10);
                List<ItemStack> variants = new ArrayList<>();

                for (int j = 0; j < variantsTagList.tagCount(); ++j) {
                    NBTTagCompound itemTag = variantsTagList.getCompoundTagAt(j);
                    ItemStack stack = ItemStack.loadItemStackFromNBT(itemTag);
                    if (stack != null) {
                        variants.add(stack);
                    }
                }
                result.add(variants);
            }
        }
        return result;
    }
}
