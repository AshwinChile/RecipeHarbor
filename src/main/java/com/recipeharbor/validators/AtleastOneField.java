package com.recipeharbor.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtleastOneFieldValidator.class)
public @interface AtleastOneField {

    String message() default "At least one field must be specified or is misspelled.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
