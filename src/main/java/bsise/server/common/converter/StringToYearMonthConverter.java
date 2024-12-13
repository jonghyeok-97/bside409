package bsise.server.common.converter;

import java.time.YearMonth;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

public class StringToYearMonthConverter implements Converter<String, YearMonth> {

    @Override
    public YearMonth convert(@NonNull String source) {
        try {
            return YearMonth.parse(source);
        } catch (Exception e) {
            throw new IllegalArgumentException("올바른 형식이 아닙니다. 'yyyy-MM' 형식이어야 합니다." + source);
        }
    }
}
