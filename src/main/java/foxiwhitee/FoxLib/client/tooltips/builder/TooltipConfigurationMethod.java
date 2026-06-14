package foxiwhitee.FoxLib.client.tooltips.builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* Used to detect methods that create custom tooltips
*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TooltipConfigurationMethod {
    String[] needMods() default {};
}
