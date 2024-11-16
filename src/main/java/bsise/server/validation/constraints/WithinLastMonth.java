package bsise.server.validation.constraints;

import bsise.server.validation.WithinLastMonthValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = WithinLastMonthValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface WithinLastMonth {

    String message() default "{bsise.server.validation.constraints.WithinLastMonth.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
