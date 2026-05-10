package foxiwhitee.FoxLib.recipes;

import foxiwhitee.FoxLib.utils.helpers.StackOreDict;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class RecipeInput {
    private final int count;
    private final Object input;

    public RecipeInput(Object input) {
        if (input instanceof ItemStack stack) {
            this.count = stack.stackSize;
            this.input = stack;
        } else if (input instanceof String st) {
            this.count = 1;
            this.input = new StackOreDict(st, 1);
        } else if (input instanceof StackOreDict ore) {
            this.count = ore.getCount();
            this.input = ore;
        } else {
            throw new IllegalArgumentException("Invalid input type");
        }
    }

    public RecipeInput(ItemStack input) {
        this.count = input.stackSize;
        this.input = input;
    }

    public RecipeInput(String input) {
        this(input, 1);
    }

    public RecipeInput(String input, int count) {
        this.count = count;
        this.input = new StackOreDict(input, count);
    }

    public RecipeInput(StackOreDict input) {
        this.count = input.getCount();
        this.input = input;
    }

    public static List<RecipeInput> getRecipeInputs(List<Object> inputs) {
        List<Object> ingredients = createIngredients(inputs);
        List<RecipeInput> recipeInputs = new ArrayList<>();
        for (Object ingredient : ingredients) {
            recipeInputs.add(new RecipeInput(ingredient));
        }
        return recipeInputs;
    }

    private static List<Object> createIngredients(List<Object> objects) {
        List<Object> ingredients = new ArrayList<>();

        for (Object ingr : objects) {
            if (ingr instanceof ItemStack stack) {
                boolean found = false;
                for (Object o : ingredients) {
                    if (o instanceof ItemStack temp) {
                        if (ItemStack.areItemStacksEqual(temp, stack)) {
                            temp.stackSize += stack.stackSize;
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    ingredients.add(stack.copy());
                }
            } else if (ingr instanceof String str) {
                boolean found = false;
                for (Object o : ingredients) {
                    if (o instanceof StackOreDict temp) {
                        if (temp.getOre().equals(str)) {
                            temp.setCount(temp.getCount() + 1);
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    ingredients.add(new StackOreDict(str, 1));
                }
            }
        }
        return ingredients;
    }

    public int getCount() {
        return count;
    }

    public ItemStack getAsStack() {
        if (input instanceof ItemStack) {
            return (ItemStack) input;
        }
        return null;
    }

    public StackOreDict getAsOre() {
        if (input instanceof StackOreDict) {
            return (StackOreDict) input;
        }
        return null;
    }

    public boolean isStack() {
        return input instanceof ItemStack;
    }

    public boolean isOre() {
        return input instanceof StackOreDict;
    }

    public Object getInput() {
        return input;
    }
}
