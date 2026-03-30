package foxiwhitee.FoxLib.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import foxiwhitee.FoxLib.FoxLib;
import foxiwhitee.FoxLib.api.FoxLibApi;
import foxiwhitee.FoxLib.config.ConfigHandler;
import foxiwhitee.FoxLib.integration.IntegrationLoader;
import foxiwhitee.FoxLib.network.NetworkManager;
import foxiwhitee.FoxLib.network.packets.C2SNeiOverlayPacket;
import foxiwhitee.FoxLib.recipes.CustomShapedRecipe;
import foxiwhitee.FoxLib.recipes.CustomShapelessRecipe;
import foxiwhitee.FoxLib.recipes.json.RecipesHandler;
import foxiwhitee.FoxLib.registries.RegisterUtils;
import foxiwhitee.FoxLib.tile.FoxBaseTile;
import foxiwhitee.FoxLib.utils.helpers.GuiHandler;
import net.minecraftforge.oredict.RecipeSorter;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.loadConfigs(event);
        RecipesHandler.loadRecipesLocations(event);
        RecipesHandler.loadRecipes(event);
        NetworkRegistry.INSTANCE.registerGuiHandler(FoxLib.instance, new GuiHandler());
        IntegrationLoader.preInit(event);
        FoxLibApi.instance.registries().registerPacket().register(C2SNeiOverlayPacket.class);

        RegisterUtils.registerTile(FoxBaseTile.class);
    }

    public void init(FMLInitializationEvent event) {
        IntegrationLoader.init(event);
        RecipeSorter.register("foxlib:customShaped", CustomShapedRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped before:minecraft:shapeless");
        RecipeSorter.register("foxlib:customShapeless", CustomShapelessRecipe.class, RecipeSorter.Category.SHAPELESS, "after:foxlib:customShaped before:minecraft:shapeless");
    }

    public void postInit(FMLPostInitializationEvent event) {
        NetworkManager.instance = new NetworkManager("FoxLib");
        IntegrationLoader.postInit(event);
        RecipesHandler.init();
    }
}
