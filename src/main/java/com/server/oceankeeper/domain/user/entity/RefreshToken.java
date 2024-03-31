package com.server.oceankeeper.domain.user.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "refresh", timeToLive = 1209600) //2 weeks
@ToString
public class RefreshToken {
    @Id
    @Indexed
    private String key;

    private String value;

    public RefreshToken updateValue(String token) {
        this.value = token;
        return this;
    }
}
