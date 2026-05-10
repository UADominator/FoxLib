package foxiwhitee.FoxLib.recipes.json.crafts;

import cpw.mods.fml.common.registry.GameRegistry;
import foxiwhitee.FoxLib.recipes.json.IJsonRecipe;
import foxiwhitee.FoxLib.recipes.json.annotations.JsonRecipe;
import foxiwhitee.FoxLib.recipes.json.annotations.NumberValue;
import foxiwhitee.FoxLib.recipes.json.annotations.RecipeOutput;
import foxiwhitee.FoxLib.recipes.json.annotations.RecipeValue;
import net.minecraft.item.ItemStack;

@SuppressWarnings("all")
@JsonRecipe("furnace")
public class FurnaceCraft implements IJsonRecipe {

    @RecipeOutput
    @RecipeValue("output")
    private ItemStack output;

    @RecipeValue("input")
    private ItemStack input;

    @NumberValue("xp")
    private double xp;

    @Override
    public void register() {
        if (output == null || input == null || xp < 0) {
            return;
        }
        GameRegistry.addSmelting(input, output, (float) xp);
    }
}
