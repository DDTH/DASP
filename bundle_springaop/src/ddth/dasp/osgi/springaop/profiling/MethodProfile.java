package ddth.dasp.osgi.springaop.profiling;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this method-annotation to automatically record the execution time of a
 * method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodProfile {
	String value() default "";

	Class<?> clazz() default Object.class;
}
