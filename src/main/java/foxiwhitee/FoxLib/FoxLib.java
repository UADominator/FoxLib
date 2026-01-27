package foxiwhitee.FoxLib;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import foxiwhitee.FoxLib.commands.CommandFindDuplicateCrafts;
import foxiwhitee.FoxLib.commands.CommandHand;
import foxiwhitee.FoxLib.config.FoxLibConfig;
import foxiwhitee.FoxLib.proxy.CommonProxy;
import foxiwhitee.FoxLib.recipes.json.annotations.RecipesLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

import static foxiwhitee.FoxLib.FoxLib.*;

@Mod(modid = MODID, name = MODNAME, version = VERSION)
public class FoxLib {
    public static final String
        MODID = "foxlib",
        MODNAME = "FoxLib",
        VERSION = "1.3.0";

    @Mod.Instance(MODID)
    public static FoxLib instance;

    public final static CreativeTabs FOX_TAB = new CreativeTabs("FOX_LIB_TAB") {
        @Override
        public Item getTabIconItem() {
            return CommonProxy.productivityCards;
        }
    };;

    @RecipesLocation(modId = "foxlib")
    public static final String[] recipes = {"recipes"};

    @SidedProxy(clientSide = "foxiwhitee.FoxLib.proxy.ClientProxy", serverSide = "foxiwhitee.FoxLib.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        proxy.preInit(e);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        if (FoxLibConfig.enableHandCommand) {
            event.registerServerCommand(new CommandHand());
        }
        if (FoxLibConfig.enableFindDuplicateCraftsCommand) {
            event.registerServerCommand(new CommandFindDuplicateCrafts());
        }
    }

}
