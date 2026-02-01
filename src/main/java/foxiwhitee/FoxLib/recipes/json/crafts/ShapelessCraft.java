package foxiwhitee.FoxLib.recipes.json.crafts;

import cpw.mods.fml.common.registry.GameRegistry;
import foxiwhitee.FoxLib.recipes.CustomShapelessRecipe;
import foxiwhitee.FoxLib.recipes.json.IJsonRecipe;
import foxiwhitee.FoxLib.recipes.json.annotations.*;
import foxiwhitee.FoxLib.utils.helpers.StackOreDict;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@JsonRecipe(value = "workbenchShapeless", hasOreDict = true)
public class ShapelessCraft implements IJsonRecipe {
    @RecipeValue("output")
    @RecipeOutput
    private ItemStack output;

    @BooleanValue("strictNbt")
    private boolean strictNbt;

    @OreValue("inputs")
    private List<Object> inputs;

    public ShapelessCraft() {}

    @Override
    public void register() {
        if (this.output == null) {
            return;
        }
        if (inputs == null || inputs.isEmpty()) {
            throw new IllegalArgumentException("Inputs cannot be empty for shapeless recipe");
        }

        List<Object> params = new ArrayList<>();

        for (Object in : inputs) {
            if (in == null) continue;
            if (in instanceof StackOreDict ore) {
                params.add(ore.getOre());
            } else if (in instanceof ItemStack) {
                params.add(((ItemStack) in).copy());
            } else {
                throw new IllegalArgumentException("Unsupported ingredient type: " + in.getClass());
            }
        }

        if (params.isEmpty()) {
            throw new IllegalArgumentException("Recipe must have at least one valid ingredient");
        }

        GameRegistry.addRecipe(new CustomShapelessRecipe(output.copy(), strictNbt, params.toArray()));
    }
}
