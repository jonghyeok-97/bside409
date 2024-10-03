package bsise.server.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    UserDetails loadUserByOAuth2Username(String username);

    boolean isOAuth2User(String username);
}
