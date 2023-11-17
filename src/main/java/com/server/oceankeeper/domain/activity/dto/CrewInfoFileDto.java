package com.server.oceankeeper.domain.activity.dto;

import lombok.Data;
import org.springframework.core.io.ByteArrayResource;

@Data
public class CrewInfoFileDto {
    private final ByteArrayResource crewInfo;
}
