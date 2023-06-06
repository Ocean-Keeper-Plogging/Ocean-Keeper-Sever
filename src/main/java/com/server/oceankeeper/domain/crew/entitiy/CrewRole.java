package com.server.oceankeeper.domain.crew.entitiy;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CrewRole {
    HOST("호스트"), CREW("크루");
    private final String value;
}
