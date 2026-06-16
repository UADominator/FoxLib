package foxiwhitee.FoxLib.integration.crafttweaker;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import foxiwhitee.FoxLib.integration.IIntegration;
import foxiwhitee.FoxLib.integration.Integration;
import foxiwhitee.FoxLib.integration.crafttweaker.tooltips.*;
import minetweaker.MineTweakerAPI;

@Integration(modid = "MineTweaker3")
@SuppressWarnings("unused")
public class CraftTweakerIntegration implements IIntegration {

    @Override
    public void preInit(FMLPreInitializationEvent paramFMLPreInitializationEvent) {

    }

    @Override
    public void init(FMLInitializationEvent paramFMLInitializationEvent) {
        MineTweakerAPI.registerClass(DynamicIntegration.class);
        MineTweakerAPI.registerClass(ZenTooltipManager.class);
        MineTweakerAPI.registerClass(ZenFrame.class);
        MineTweakerAPI.registerClass(ZenGlowOutline.class);
        MineTweakerAPI.registerClass(ZenInnerLine.class);
        MineTweakerAPI.registerClass(ZenSeparator.class);
        MineTweakerAPI.registerClass(ZenStyle.class);
        MineTweakerAPI.registerClass(ZenTheme.class);
    }

    @Override
    public void postInit(FMLPostInitializationEvent paramFMLPostInitializationEvent) {

    }
}
