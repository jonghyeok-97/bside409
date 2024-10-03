package bsise.server.auth;


public interface OAuth2UserInfo {

    String getId();

    String getProvider();

    String getUserName();

    String getEmail();

    String getProfileImage();
}

