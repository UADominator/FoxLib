package foxiwhitee.FoxLib.integration.nei;

import codechicken.nei.api.IConfigureNEI;
import cpw.mods.fml.common.Loader;
import foxiwhitee.FoxLib.nei.NeiConfigurationMethod;
import foxiwhitee.FoxLib.nei.NeiProcessor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NeiConfig implements IConfigureNEI {
    private final NeiProcessor processor;

    public NeiConfig(NeiProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void loadConfig() {
        Class<?> clazz = processor.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method m : methods) {
            if (m.isAnnotationPresent(NeiConfigurationMethod.class)) {
                NeiConfigurationMethod annotation = m.getAnnotation(NeiConfigurationMethod.class);
                if (verifyDependencies(annotation)) {
                    try {
                        m.invoke(processor);
                    } catch (IllegalAccessException | InvocationTargetException ignored) {
                    }
                }
            }
        }
    }

    private boolean verifyDependencies(NeiConfigurationMethod annotation) {
        for (String mod : annotation.needMods()) {
            if (!Loader.isModLoaded(mod)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getName() {
        return processor.modName();
    }

    @Override
    public String getVersion() {
        return processor.version();
    }
}
