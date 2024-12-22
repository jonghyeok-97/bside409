package bsise.server.config;

import static feign.Logger.Level.BASIC;

import feign.Logger;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients("bsise.server")
@Configuration
public class FeignGlobalConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        return BASIC;
    }
}
