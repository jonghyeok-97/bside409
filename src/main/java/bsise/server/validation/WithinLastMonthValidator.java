package bsise.server.validation;

import bsise.server.validation.constraints.WithinLastMonth;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class WithinLastMonthValidator implements ConstraintValidator<WithinLastMonth, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null은 여기서 유효하다고 간주. NotBlank에서 별도 처리
        }

        LocalDate now = LocalDate.now();
        LocalDate oneMonthAgo = now.minusMonths(1);

        // 오늘 또는 한 달 이전 날짜까지만 허용
        return !value.isAfter(now) && !value.isBefore(oneMonthAgo);
    }
}