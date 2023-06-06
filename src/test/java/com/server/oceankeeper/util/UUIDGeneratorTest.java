package com.server.oceankeeper.util;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UUIDGeneratorTest {

    @Test
    void createUuid() {
        UUID uuid = UUIDGenerator.createUuid();
        assertThat(uuid.toString().length()).isEqualTo(36);
        assertThat(uuid.toString().chars().filter(c->c=='-').count()).isEqualTo(4);
    }

    @Test
    void changeUuidFromString() {
        UUID uuid = UUIDGenerator.createUuid();
        String uuidStr = uuid.toString().replace("-","");
        UUID newUuid = UUIDGenerator.changeUuidFromString(uuidStr);

        assertThat(newUuid.toString().chars().filter(c->c=='-').count()).isEqualTo(4);
        assertThat(uuid).isEqualTo(newUuid);
    }

    @Test
    void changeUuidToString() {
        UUID uuid = UUIDGenerator.createUuid();
        String uuidStr = UUIDGenerator.changeUuidToString(uuid);
        UUID newUuid = UUIDGenerator.changeUuidFromString(uuidStr);

        assertThat(newUuid.toString().chars().filter(c->c=='-').count()).isEqualTo(4);
        assertThat(uuid).isEqualTo(newUuid);
    }
}