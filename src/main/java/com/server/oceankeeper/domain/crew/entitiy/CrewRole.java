package com.server.oceankeeper.domain.crew.entitiy;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CrewRole {
    HOST("호스트"), CREW("크루");
    private final String value;

    public static CrewRole getRole(String role) {
        if (role == null) return null;
        switch (role) {
            case "host":
                return HOST;
            case "crew":
                return CREW;
            default:
                return null;
        }
    }
}
