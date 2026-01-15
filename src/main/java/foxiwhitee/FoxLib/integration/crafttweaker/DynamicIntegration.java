package foxiwhitee.FoxLib.integration.crafttweaker;

import foxiwhitee.FoxLib.recipes.json.IJsonRecipe;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.value.IAny;

import java.util.HashSet;
import java.util.Set;

@ZenClass("mods.foxlib.DynamicIntegration")
public class DynamicIntegration {
    public static final Set<IJsonRecipe> DYNAMIC_TYPES = new HashSet<>();

    @ZenMethod
    public static void addRecipe(String type, IItemStack output, IAny[] inputs) {
        IJsonRecipe recipeType = findType(type);
        if (recipeType == null) {
            MineTweakerAPI.logError("Unknown recipe type: " + type);
            return;
        }
        recipeType.addCraftByMineTweaker(output, inputs);
    }

    @ZenMethod
    public static void removeRecipe(String type, IItemStack output) {
        IJsonRecipe recipeType = findType(type);
        if (recipeType == null) {
            MineTweakerAPI.logError("Unknown recipe type: " + type);
            return;
        }
        recipeType.removeCraftByMineTweaker(output);
    }

    private static IJsonRecipe findType(String id) {
        for (IJsonRecipe t : DYNAMIC_TYPES)
            if (t.getType().equalsIgnoreCase(id))
                return t;
        return null;
    }
}
