package foxiwhitee.FoxLib.nei;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NeiConfiguration {
    String[] needMods() default {};
    String modID();
    String modName();
    String modVersion();
}
