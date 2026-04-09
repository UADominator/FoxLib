package foxiwhitee.FoxLib.recipes;

import foxiwhitee.FoxLib.utils.helpers.ItemStackUtil;
import foxiwhitee.FoxLib.utils.helpers.StackOreDict;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface IMachineRecipe {
    ItemStack getOutput();
    List<RecipeInput> getInputs();

    @SuppressWarnings("unused")
    static boolean simpleAreStacksEqual(ItemStack stack, ItemStack stack2) {
        return stack.getItem() == stack2.getItem() && stack.getItemDamage() == stack2.getItemDamage();
    }

    @SuppressWarnings("unused")
    default boolean matches(List<ItemStack> stacks, boolean stackOreDict) {
        List<RecipeInput> inputsMissing = new ArrayList<>(this.getInputs());

        for(ItemStack stack : stacks) {
            if (stack == null) {
                break;
            }

            int stackIndex = -1;

            for(int j = 0; j < inputsMissing.size(); ++j) {
                RecipeInput input = inputsMissing.get(j);
                if (input.isStack()) {
                    ItemStack st = input.getAsStack();
                    if (ItemStackUtil.stackEquals(st, stack) && stack.stackSize >= st.stackSize) {
                        stackIndex = j;
                        break;
                    }
                } else if (input.isOre()) {
                    StackOreDict ore = input.getAsOre();
                    if (ore.check(stack, stackOreDict)) {
                        stackIndex = j;
                        break;
                    }
                }
            }

            if (stackIndex != -1) {
                inputsMissing.remove(stackIndex);
            } else {
                return false;
            }
        }

        return inputsMissing.isEmpty();
    }
}
