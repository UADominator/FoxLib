package foxiwhitee.FoxLib.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomShapelessRecipe extends ShapelessOreRecipe {
    private final boolean strictNBT;
    protected ArrayList<Object> inputList = new ArrayList<>();

    public CustomShapelessRecipe(ItemStack result, boolean strictNBT, Object... ingredients) {
        super(result, ingredients);
        this.strictNBT = strictNBT;

        for (Object obj : ingredients) {
            if (obj instanceof String) {
                inputList.add(OreDictionary.getOres((String) obj));
            } else if (obj instanceof ItemStack) {
                inputList.add(((ItemStack) obj).copy());
            } else if (obj instanceof List) {
                inputList.add(obj);
            } else {
                inputList.add(obj);
            }
        }
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        List<Object> required = new ArrayList<>(inputList);

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack slotStack = inv.getStackInSlot(i);

            if (slotStack != null) {
                boolean matched = false;
                Iterator<Object> it = required.iterator();

                while (it.hasNext()) {
                    Object target = it.next();

                    if (checkMatch(slotStack, target)) {
                        it.remove();
                        matched = true;
                        break;
                    }
                }

                if (!matched) return false;
            }
        }

        return required.isEmpty();
    }

    private boolean checkMatch(ItemStack slotStack, Object target) {
        if (target instanceof ItemStack targetStack) {
            if (slotStack.getItem() != targetStack.getItem()) return false;
            if (targetStack.getItemDamage() != OreDictionary.WILDCARD_VALUE && targetStack.getItemDamage() != slotStack.getItemDamage()) return false;

            return !strictNBT || ItemStack.areItemStackTagsEqual(slotStack, targetStack);

        } else if (target instanceof List) {
            for (ItemStack ore : (List<ItemStack>) target) {
                if (OreDictionary.itemMatches(ore, slotStack, false)) {
                    if (!strictNBT || ItemStack.areItemStackTagsEqual(slotStack, ore)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public int getRecipeSize() {
        return inputList.size();
    }

    @Override
    public ArrayList<Object> getInput() {
        return inputList;
    }
}
