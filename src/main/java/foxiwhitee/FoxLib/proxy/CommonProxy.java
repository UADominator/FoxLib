package foxiwhitee.FoxLib.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import foxiwhitee.FoxLib.FoxLib;
import foxiwhitee.FoxLib.api.FoxLibApi;
import foxiwhitee.FoxLib.client.render.StaticRenderHandler;
import foxiwhitee.FoxLib.config.ConfigHandler;
import foxiwhitee.FoxLib.config.FoxLibConfig;
import foxiwhitee.FoxLib.integration.IntegrationLoader;
import foxiwhitee.FoxLib.items.ItemProductivityCard;
import foxiwhitee.FoxLib.network.NetworkManager;
import foxiwhitee.FoxLib.network.packets.C2SNeiOverlayPacket;
import foxiwhitee.FoxLib.recipes.BaseFoxRecipe;
import foxiwhitee.FoxLib.recipes.CustomShapedRecipe;
import foxiwhitee.FoxLib.recipes.CustomShapelessRecipe;
import foxiwhitee.FoxLib.recipes.FurnaceRecipeRapper;
import foxiwhitee.FoxLib.recipes.json.RecipesHandler;
import foxiwhitee.FoxLib.registries.RegisterUtils;
import foxiwhitee.FoxLib.tile.FoxBaseTile;
import foxiwhitee.FoxLib.utils.FindDuplicateCraftsScript;
import foxiwhitee.FoxLib.utils.handler.GuiHandlerRegistry;
import foxiwhitee.FoxLib.utils.helpers.GuiHandler;
import foxiwhitee.FoxLib.utils.helpers.ProductivityBlackListHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommonProxy {
    public static final Item productivityCards = new ItemProductivityCard("productivityCard");

    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.loadConfigs(event);
        RecipesHandler.loadRecipesLocations(event);
        RecipesHandler.loadRecipes(event);
        GuiHandlerRegistry.registerGuiHandlers(event);
        NetworkRegistry.INSTANCE.registerGuiHandler(FoxLib.instance, new GuiHandler());
        IntegrationLoader.preInit(event);
        FoxLibApi.instance.registries().registerPacket().register(C2SNeiOverlayPacket.class);

        RegisterUtils.registerTile(FoxBaseTile.class);
        if (FoxLibConfig.enableProductivityCards) {
            RegisterUtils.registerItem(productivityCards);
        }
    }

    public void init(FMLInitializationEvent event) {
        IntegrationLoader.init(event);
        ProductivityBlackListHelper.registerBlackList(FoxLibConfig.productivityBlackList);
        ProductivityBlackListHelper.registerBlackListByModId(FoxLibConfig.productivityModsBlackList);
        RecipeSorter.register("foxlib:customShaped", CustomShapedRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped before:minecraft:shapeless");
        RecipeSorter.register("foxlib:customShapeless", CustomShapelessRecipe.class, RecipeSorter.Category.SHAPELESS, "after:foxlib:customShaped before:minecraft:shapeless");
    }

    public void postInit(FMLPostInitializationEvent event) {
        NetworkManager.instance = new NetworkManager("FoxLib");
        IntegrationLoader.postInit(event);
        RecipesHandler.init();
        recipeConvertorsInit();
    }

    private void recipeConvertorsInit() {
        FindDuplicateCraftsScript.recipeConverters.put(ShapedRecipes.class, recipe -> {
            if (recipe instanceof ShapedRecipes r) {
                ItemStack[] input = r.recipeItems;
                Object[] objects = new Object[input.length];
                for (int i = 0; i < input.length; i++) {
                    if (input[i] != null) {
                        ItemStack temp = input[i].copy();
                        temp.stackSize = 1;
                        objects[i] = temp;
                    }
                }
                return new BaseFoxRecipe(r.getRecipeOutput(), objects);
            }
            return null;
        });
        FindDuplicateCraftsScript.recipeConverters.put(ShapelessRecipes.class, recipe -> {
            if (recipe instanceof ShapelessRecipes r) {
                return new BaseFoxRecipe(r.getRecipeOutput(), r.recipeItems.toArray());
            }
            return null;
        });
        FindDuplicateCraftsScript.recipeConverters.put(ShapedOreRecipe.class, recipe -> {
            if (recipe instanceof ShapedOreRecipe r) {
                return new BaseFoxRecipe(r.getRecipeOutput(), r.getInput());
            }
            return null;
        });
        FindDuplicateCraftsScript.recipeConverters.put(ShapelessOreRecipe.class, recipe -> {
            if (recipe instanceof ShapelessOreRecipe r) {
                return new BaseFoxRecipe(r.getRecipeOutput(), r.getInput());
            }
            return null;
        });
        FindDuplicateCraftsScript.recipes.add(CraftingManager.getInstance().getRecipeList());

        List<FurnaceRecipeRapper> furnaceRecipes = new ArrayList<>();
        for (Map.Entry<ItemStack, ItemStack> entry : FurnaceRecipes.smelting().getSmeltingList().entrySet()) {
            furnaceRecipes.add(new FurnaceRecipeRapper(entry.getKey(), entry.getValue()));
        }
        FindDuplicateCraftsScript.recipes.add(furnaceRecipes);
        FindDuplicateCraftsScript.recipeConverters.put(FurnaceRecipeRapper.class, recipe -> {
            if (recipe instanceof FurnaceRecipeRapper r) {
                return r;
            }
            return null;
        });

    }
}
