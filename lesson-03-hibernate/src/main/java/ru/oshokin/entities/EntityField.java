package ru.oshokin.entities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EntityField {
    String presentation();
    boolean skippedOnSearch() default false;
    boolean skippedOnUpdate() default false;
    boolean skippedOnInsert() default false;
}