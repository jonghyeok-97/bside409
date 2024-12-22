package bsise.server.clova.client;

import static feign.Logger.Level.HEADERS;

import feign.Logger;
import feign.Request;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StreamUtils;

public class ClovaFeignConfig {

    @Bean
    public CustomFeignLogging customFeignLogging() {
        return new CustomFeignLogging();
    }

    @Bean
    public CustomFeignErrorDecoder customFeignErrorDecoder() {
        return new CustomFeignErrorDecoder();
    }

    @Slf4j
    static class CustomFeignLogging extends Logger {

        @Override
        protected void logRequest(String configKey, Level logLevel, Request request) {
            if (logLevel.ordinal() >= HEADERS.ordinal()) {
                super.logRequest(configKey, logLevel, request);
                return;
            }
            String stringBody = createRequestStringBody(request);
            log.info("[threadId={}] ---> {} {} {} [Headers]: {} [Body]: {}",
                    Thread.currentThread().getId(),
                    request.httpMethod(),
                    request.url(),
                    request.protocolVersion(),
                    request.headers(),
                    stringBody);
        }

        private String createRequestStringBody(Request request) {
            return request.body() == null ? "" : new String(request.body(), StandardCharsets.UTF_8);
        }

        @Override
        protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime)
                throws IOException {
            if (logLevel.ordinal() >= HEADERS.ordinal() || response.status() >= 400) {
                return super.logAndRebufferResponse(configKey, logLevel, response, elapsedTime);
            }
            byte[] byteArray = getResponseBodyByteArray(response);
            log.info("[threadId={}] <--- {} {} ({}ms) [Body]: {}",
                    Thread.currentThread().getId(),
                    response.protocolVersion(),
                    response.status(),
                    elapsedTime,
                    new String(byteArray, StandardCharsets.UTF_8));

            return response.toBuilder().body(byteArray).build();
        }

        private byte[] getResponseBodyByteArray(Response response) throws IOException {
            if (response.body() == null) {
                return new byte[]{};
            }
            return StreamUtils.copyToByteArray(response.body().asInputStream());
        }

        /**
         * customeFeignLogging 을 빈으로 등록하고, log()와 format()이 없으면 feign 로그 레벨이 HEADERS 이상일 떄, DEBUG 레벨의 로그가 나오지 않음
         */
        @Override
        protected void log(String configKey, String format, Object... args) {
            log.debug(format(configKey, format, args));
        }

        protected String format(String configKey, String format, Object... args) {
            return String.format(methodTag(configKey) + format, args);
        }
    }

    @Slf4j
    static class CustomFeignErrorDecoder implements ErrorDecoder {

        @Override
        public Exception decode(String methodKey, Response response) {
            int statusCode = response.status();
            String stringBody;
            try {
                byte[] byteArray = getResponseBodyByteArray(response);
                stringBody = new String(byteArray, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (400 <= statusCode && statusCode < 500) {
                log.error("[Status: {}] UpUpRadio 서버 --> 클로바 서버 호출 실패 [Body: {}]", statusCode, stringBody);
            }
            if (500 <= statusCode) {
                log.error("[Status: {}] 클로바 서버 오류 [Body: {}]", statusCode, stringBody);
            }
            throw new RuntimeException();
        }

        private byte[] getResponseBodyByteArray(Response response) throws IOException {
            if (response.body() == null) {
                return new byte[]{};
            }
            return StreamUtils.copyToByteArray(response.body().asInputStream());
        }
    }
}
