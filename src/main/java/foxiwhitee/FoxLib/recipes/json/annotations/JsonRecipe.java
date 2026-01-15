package foxiwhitee.FoxLib.recipes.json.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JsonRecipe {
    String value();
    boolean hasOreDict() default false;
    boolean hasMineTweaker() default false;
}
