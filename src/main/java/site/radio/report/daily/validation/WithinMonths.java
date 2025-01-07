package site.radio.report.daily.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = WithinLastMonthValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface WithinMonths {

    int months() default 1;

    String message() default "{bsise.server.validation.WithinLastMonth.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
