package foxiwhitee.FoxLib.utils.helpers;

import foxiwhitee.FoxLib.recipes.RecipeInput;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

@SuppressWarnings("unused")
public class ItemStackUtil {
    public static boolean stackEquals(ItemStack stack1, ItemStack stack2) {
        return stackEquals(stack1, stack2, false);
    }

    public static boolean stackEquals(ItemStack stack1, ItemStack stack2, boolean withCount) {
        boolean equals = stack1 != null && stack2 != null && stack1.getItem() == stack2.getItem() &&
            (stack1.getItemDamage() == stack2.getItemDamage() || stack1.getItemDamage() == OreDictionary.WILDCARD_VALUE || stack2.getItemDamage() == OreDictionary.WILDCARD_VALUE) &&
            ItemStack.areItemStackTagsEqual(stack1, stack2);
        if (equals && withCount) {
            equals = stack1.stackSize == stack2.stackSize;
        }
        return equals;
    }

    public static boolean matches(Object o1, Object o2) {
        return matches(o1, o2, false);
    }

    public static boolean matches(Object o1, Object o2, boolean withCount) {
        if (o1 == null || o2 == null) return false;

        o1 = unwrap(o1);
        o2 = unwrap(o2);

        if (o1 instanceof ItemStack s1 && o2 instanceof ItemStack s2) {
            return stackEquals(s1, s2, withCount);
        }

        if (o1 instanceof ItemStack s1) return matchesStackAndOther(s1, o2, withCount);
        if (o2 instanceof ItemStack s2) return matchesStackAndOther(s2, o1, withCount);

        if (o1 instanceof StackOreDict ore1 && o2 instanceof StackOreDict ore2) {
            return ore1.equals(ore2);
        }

        if (o1 instanceof StackOreDict ore && o2 instanceof String str) return ore.getOre().equals(str);
        if (o2 instanceof StackOreDict ore && o1 instanceof String str) return ore.getOre().equals(str);

        if (o1 instanceof String s1 && o2 instanceof String s2) return s1.equals(s2);

        return false;
    }

    private static Object unwrap(Object o) {
        if (o instanceof RecipeInput input) {
            if (input.isStack()) return input.getAsStack();
            if (input.isOre()) return input.getAsOre();
        }
        return o;
    }

    public static boolean matchesStackAndOther(ItemStack stack, Object object) {
        return matchesStackAndOther(stack, object, false);
    }

    public static boolean matchesStackAndOther(ItemStack stack, Object object, boolean withCount) {
        object = unwrap(object);
        if (object instanceof String s) {
            List<ItemStack> validStacks = OreDictionary.getOres(s);
            for(ItemStack ostack : validStacks) {
                return OreDictionary.itemMatches(ostack, stack, false);
            }
        } else if (object instanceof StackOreDict ore) {
            return ore.check(stack, withCount);
        } else if (object instanceof ItemStack stack2) {
            return stackEquals(stack, stack2, withCount);
        }
        return false;
    }
}
