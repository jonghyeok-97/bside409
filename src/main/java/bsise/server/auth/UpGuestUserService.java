package bsise.server.auth;

import bsise.server.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
//@Service
public class UpGuestUserService implements UserDetailsService {

    // guest 유저에 대한 로그인 처리(인증 처리)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("--- GUEST USER LOGIN PROCESS ---");
        return new UpUserDetails(User.makeGuest());
    }
}
