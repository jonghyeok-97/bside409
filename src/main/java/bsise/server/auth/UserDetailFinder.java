package bsise.server.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailFinder {

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    UserDetails loadUserByOAuth2UserId(String username);

    boolean isOAuth2User(String username);
}
