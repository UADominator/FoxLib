package foxiwhitee.FoxLib.client.tooltips.builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* Used to detect classes that create custom tooltips
*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SuppressWarnings("unused")
public @interface TooltipConfiguration {
    String[] needMods() default {};
}
