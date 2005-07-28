package org.geworkbench.bison.algorithm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A run-time annotation that marks parameters as eligible for external input by the event framework.
 */
@Target({ElementType.PARAMETER}) @Retention(RetentionPolicy.RUNTIME) public @interface DSInput {
    String value();
}
