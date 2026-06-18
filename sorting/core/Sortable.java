package sorting.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sortable {
    String value() default "default";

    String algorithm() default "parallel-merge-sort";

    boolean enableMetrics() default true;

    boolean enableValidation() default true;

    boolean enableCaching() default false;

    int cacheSize() default 100;

    int insertionThreshold() default 10;
}
