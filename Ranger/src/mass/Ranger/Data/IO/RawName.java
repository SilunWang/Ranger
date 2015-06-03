package mass.Ranger.Data.IO;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RawName {
    String value();

    int priority() default Integer.MAX_VALUE;

    RawType type() default RawType.Primitive;

    public enum RawType {
        Primitive, Combo,
    }
}
