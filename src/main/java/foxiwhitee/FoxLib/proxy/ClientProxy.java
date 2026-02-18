package foxiwhitee.FoxLib.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import foxiwhitee.FoxLib.block.FoxBaseBlock;
import foxiwhitee.FoxLib.client.render.FoxBlockRenderer;
import foxiwhitee.FoxLib.client.render.RenderStaticBlock;
import foxiwhitee.FoxLib.client.render.StaticRender;
import foxiwhitee.FoxLib.client.render.StaticRenderHandler;
import foxiwhitee.FoxLib.config.FoxLibConfig;
import foxiwhitee.FoxLib.event.TooltipEventHandler;
import foxiwhitee.FoxLib.registries.RegisterUtils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

import java.util.Map;

public class ClientProxy extends CommonProxy {
    public static int foxBaseBlockRenderId;

    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        StaticRenderHandler.loadStaticRenderData(event);
    }

    public void init(FMLInitializationEvent event) {
        super.init(event);
        foxBaseBlockRenderId = RenderingRegistry.getNextAvailableRenderId();
        FoxBaseBlock.setStaticRenderId(foxBaseBlockRenderId);
        RenderingRegistry.registerBlockHandler(new FoxBlockRenderer(foxBaseBlockRenderId));

        for (Map.Entry<Block, StaticRender> entry : StaticRenderHandler.STATIC_RENDER_DATA.entrySet()) {
            RegisterUtils.registerItemRenderer(Item.getItemFromBlock(entry.getKey()), new RenderStaticBlock<>(entry.getValue()));
            RegisterUtils.registerTileRenderer(entry.getValue().tile(), new RenderStaticBlock<>(entry.getValue()));
        }
        if (FoxLibConfig.enableTooltips && FoxLibConfig.enableTooltipsBlackList) {
            MinecraftForge.EVENT_BUS.register(new TooltipEventHandler());
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }
}
