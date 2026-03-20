package foxiwhitee.FoxLib.recipes.json.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@SuppressWarnings("unused")
public @interface StringValue {
    String value();
    boolean canBeNull() default false;
}
