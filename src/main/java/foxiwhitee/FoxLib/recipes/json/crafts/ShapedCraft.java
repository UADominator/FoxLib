package foxiwhitee.FoxLib.recipes.json.crafts;

import cpw.mods.fml.common.registry.GameRegistry;
import foxiwhitee.FoxLib.recipes.CustomShapedRecipe;
import foxiwhitee.FoxLib.recipes.json.IJsonRecipe;
import foxiwhitee.FoxLib.recipes.json.annotations.*;
import foxiwhitee.FoxLib.utils.helpers.StackOreDict;
import net.minecraft.item.ItemStack;

import java.util.List;

@JsonRecipe(value = "workbenchShaped", hasOreDict = true)
public class ShapedCraft implements IJsonRecipe {
    @RecipeValue("output")
    @RecipeOutput
    private ItemStack output;

    @BooleanValue("strictNbt")
    private boolean strictNbt;

    @OreValue("inputs")
    private List<Object> inputs;

    public ShapedCraft() {}

    @Override
    public void register() {
        if (this.output == null || this.inputs == null) {
            return;
        }
        if (inputs.size() != 9) {
            throw new IllegalArgumentException("Inputs must be length 9 (3x3 grid)");
        }

        Object[] ins = new Object[9];

        for (int k = 0; k < inputs.size(); k++) {
            Object in = inputs.get(k);
            if (in == null) {
                ins[k] = null;
            } else if (in instanceof StackOreDict ore) {
                ins[k] = ore.getOre();
            } else if (in instanceof ItemStack stack) {
                ins[k] = stack.copy();
            } else {
                throw new IllegalArgumentException("Unsupported ingredient type at slot " + k);
            }
        }

        GameRegistry.addRecipe(new CustomShapedRecipe(output, strictNbt, ins));
    }
}
