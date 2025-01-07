package bsise.server.user.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;

public enum Preference {

    F,
    T,
    ;

    @JsonCreator
    public static Preference of(String preference) {
        return Arrays.stream(values())
                .filter(pref -> pref.name().equals(preference.toUpperCase()))
                .findAny()
                .orElse(F);
    }
}
