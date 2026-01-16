package foxiwhitee.FoxLib.recipes.json.crafts;

import cpw.mods.fml.common.registry.GameRegistry;
import foxiwhitee.FoxLib.recipes.json.IJsonRecipe;
import foxiwhitee.FoxLib.recipes.json.annotations.JsonRecipe;
import foxiwhitee.FoxLib.recipes.json.annotations.OreValue;
import foxiwhitee.FoxLib.recipes.json.annotations.RecipeOutput;
import foxiwhitee.FoxLib.recipes.json.annotations.RecipeValue;
import foxiwhitee.FoxLib.utils.helpers.StackOreDict;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.List;

@JsonRecipe(value = "workbenchShaped", hasOreDict = true)
public class ShapedCraft implements IJsonRecipe {
    @RecipeValue("output")
    @RecipeOutput
    private ItemStack output;

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

        List<Object> params = new java.util.ArrayList<>();
        params.add("ABC");
        params.add("DEF");
        params.add("GHI");

        char[] keys = {'A','B','C','D','E','F','G','H','I'};
        for (int k = 0; k < inputs.size(); k++) {
            Object in = inputs.get(k);
            if (in == null) continue;
            if (in instanceof StackOreDict ore) {
                params.add(keys[k]);
                params.add(ore.getOre());
            } else if (in instanceof ItemStack) {
                params.add(keys[k]);
                params.add(((ItemStack) in).copy());
            } else {
                throw new IllegalArgumentException("Unsupported ingredient type at slot " + k);
            }
        }

        GameRegistry.addRecipe(new ShapedOreRecipe(output.copy(), params.toArray()));
    }
}
