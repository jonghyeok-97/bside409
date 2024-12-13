package bsise.server.config;

import java.time.YearMonth;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter((Converter<String, YearMonth>) source -> {
            try {
                return YearMonth.parse(source);
            } catch (Exception e) {
                throw new IllegalArgumentException("올바른 형식이 아닙니다. 'yyyy-MM' 형식이어야 합니다." + source);
            }
        });
    }
}
