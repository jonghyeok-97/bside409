package bsise.server.auth;

import bsise.server.user.domain.Role;
import bsise.server.user.domain.User;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class UpUserDetails implements UserDetails, OAuth2User {

    private final User user;
    private Map<String, Object> attributes;

    /**
     * 게스트 유저 전용 생성자
     *
     * @param user 게스트
     */
    public UpUserDetails(User user) {
        this.user = user;
    }

    /**
     * OAuth2 유저 전용 생성자
     *
     * @param user OAuth2 유저
     * @param attributes OAuth2 인증 서버로부터 제공받은 속성들
     */
    public UpUserDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    /**
     * 유저의 식별자 역할은 OAuth2 username.
     *
     * @return OAuth2 username
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * OAuth2 유저 및 guest 유저는 비밀번호를 입력받지 않음.
     *
     * @return null
     * @see User
     */
    @Override
    public String getPassword() {
        return null;
    }

    /**
     * enum Role => String 변환
     *
     * @return String 으로 변환된 Role(GrantedAuthority)
     * @see Role
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    /**
     * 계정 만료 정책이 생길 시 추가 구현이 필요.
     *
     * @return true
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠금 정책이 생길 시 추가 구현이 필요.
     *
     * @return true
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 계정 자격 증명 정책이 생길 시 추가 구현이 필요.
     *
     * @return true
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 휴면 계정 정책이 생길 시 추가 구현이 필요.
     *
     * @return true
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * OAuth2 username
     *
     * @return OAuth2 username
     */
    @Override
    public String getName() {
        return user.getUsername();
    }

    /**
     * OAuth2 유저의 속성
     *
     * @return OAuth2 유저 속성
     */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public String getUserId() {
        return user.getId().toString();
    }
}
