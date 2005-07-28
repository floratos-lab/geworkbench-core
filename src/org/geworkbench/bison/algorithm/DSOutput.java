package org.geworkbench.bison.algorithm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A run-time annotation that denotes that the return value of a method is eligible for export via the event model.
 */
@Target({ElementType.METHOD}) @Retention(RetentionPolicy.RUNTIME) public @interface DSOutput {
    String value();
}
