package foxiwhitee.FoxLib.client.tooltips.builder;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Searches for all TooltipProcessors that have the TooltipConfiguration annotation.
 * Activates each creation of custom tooltips
 */
public class TooltipHandler {
    private static final List<TooltipProcessor> processors = new ArrayList<>();

    public static void findAll(FMLPreInitializationEvent event) {
        ASMDataTable asmData = event.getAsmData();
        for (ASMDataTable.ASMData entry : asmData.getAll(TooltipConfiguration.class.getName())) {
            try {
                if (verifyDependencies(entry)) {
                    Class<?> integrationClass = Class.forName(entry.getClassName());
                    if (TooltipProcessor.class.isAssignableFrom(integrationClass)) {
                        TooltipProcessor instance = (TooltipProcessor) integrationClass.getDeclaredConstructor().newInstance();
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

    public static void init() {
        processors.forEach(TooltipProcessor::run);
    }
}
