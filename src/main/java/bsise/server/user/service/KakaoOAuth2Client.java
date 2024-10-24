package bsise.server.user.service;

import bsise.server.user.dto.KakaoUnlinkRequestDto;
import bsise.server.user.dto.KakaoUnlinkResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakao-oauth2-service", url = "https://kapi.kakao.com/v1/user/unlink")
public interface KakaoOAuth2Client {

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    KakaoUnlinkResponseDto requestUnlinkByUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String appAdminKey,
            @RequestBody KakaoUnlinkRequestDto kakaoUnlinkRequestDto
    );
}
