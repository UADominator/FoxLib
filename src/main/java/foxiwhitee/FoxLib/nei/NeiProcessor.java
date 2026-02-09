package foxiwhitee.FoxLib.nei;

import java.util.LinkedList;
import java.util.List;

public abstract class NeiProcessor {
    private final ConfigurationForNei configuration;
    private final NeiConfiguration annotation;
    private final List<HandlerRecipeBuilder<?>> builders = new LinkedList<>();

    public NeiProcessor() {
        configuration = new ConfigurationForNei(this);
        Class<?> clazz = getClass();
        annotation = clazz.getAnnotation(NeiConfiguration.class);
        if (annotation == null) {
            throw new NullPointerException("NeiConfiguration annotation is null");
        }
    }

    public final ConfigurationForNei configuration() {
        return configuration;
    }

    public final <T> HandlerRecipeBuilder<T> newRecipeHandler(String recipeId) {
        return this.<T>newRecipeHandler(recipeId, true);
    }

    public final <T> HandlerRecipeBuilder<T> newRecipeHandler(String recipeId, boolean buildIf) {
        HandlerRecipeBuilder<T> builder = new HandlerRecipeBuilder<>(this, recipeId, buildIf);
        builders.add(builder);
        return builder;
    }

    public final String modName() {
        return annotation.modName();
    }

    public final String version() {
        return annotation.modVersion();
    }

    public final String modId() {
        return annotation.modID();
    }
}
