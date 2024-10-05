package bsise.server.auth;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDetailFinderImpl implements UserDetailFinder {

    private final UserDetailsService userDetailsService;
    private final UpOAuth2UserService oAuth2UserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDetailsService.loadUserByUsername(username);
    }

    @Override
    public UserDetails loadUserByOAuth2UserId(String username) {
        return oAuth2UserService.loadUserByUsername(username);
    }

    @Override
    public boolean isOAuth2User(String username) {
        return oAuth2UserService.isOAuth2User(username);
    }
}
