package foxiwhitee.FoxLib.integration.nei;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import foxiwhitee.FoxLib.integration.IIntegration;
import foxiwhitee.FoxLib.integration.Integration;
import foxiwhitee.FoxLib.nei.NeiConfiguration;
import foxiwhitee.FoxLib.nei.NeiProcessor;

import java.util.ArrayList;
import java.util.List;

@Integration(modid = "NotEnoughItems")
@SuppressWarnings("unused")
public class NeiIntegration implements IIntegration {
    private final List<NeiProcessor> processors = new ArrayList<>();

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        ASMDataTable asmData = event.getAsmData();
        for (ASMDataTable.ASMData entry : asmData.getAll(NeiConfiguration.class.getName())) {
            try {
                if (verifyDependencies(entry)) {
                    Class<?> integrationClass = Class.forName(entry.getClassName());
                    if (NeiProcessor.class.isAssignableFrom(integrationClass)) {
                        NeiProcessor instance = (NeiProcessor) integrationClass.getDeclaredConstructor().newInstance();
                        processors.add(instance);
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static boolean verifyDependencies(ASMDataTable.ASMData data) {
        List<String> dependencies = (List<String>) data.getAnnotationInfo().getOrDefault("needMods", new ArrayList<String>());
        for (String dep : dependencies) {
            if (!Loader.isModLoaded(dep)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void init(FMLInitializationEvent paramFMLInitializationEvent) {
        if (isClient()) {
            for (NeiProcessor processor : processors) {
                new NeiConfig(processor).loadConfig();
            }
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent paramFMLPostInitializationEvent) {

    }
}
