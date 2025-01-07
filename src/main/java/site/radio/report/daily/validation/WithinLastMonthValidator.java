package site.radio.report.daily.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class WithinLastMonthValidator implements ConstraintValidator<WithinMonths, LocalDate> {

    private int months;

    @Override
    public void initialize(WithinMonths constraintAnnotation) {
        this.months = constraintAnnotation.months();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null은 여기서 유효하다고 간주. NotBlank에서 별도 처리
        }

        LocalDate now = LocalDate.now();
        LocalDate oneMonthAgo = now.minusMonths(months);

        // 오늘 또는 지정된 months 달 이전 날짜까지만 허용
        return !value.isAfter(now) && !value.isBefore(oneMonthAgo);
    }
}