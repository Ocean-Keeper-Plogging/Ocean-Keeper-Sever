package com.server.oceankeeper.util;

import com.fasterxml.uuid.Generators;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class UUIDGenerator {
    public static UUID createUuid(){
        //sequential uuid
        UUID uuid = Generators.timeBasedGenerator().generate();
        String[] uuidArr = uuid.toString().split("-");
        String uuidStr = uuidArr[2]+uuidArr[1]+uuidArr[0]+uuidArr[3]+uuidArr[4];
        StringBuilder sb = new StringBuilder(uuidStr);
        sb.insert(8, "-");
        sb.insert(13, "-");
        sb.insert(18, "-");
        sb.insert(23, "-");
        return UUID.fromString(sb.toString());
    }

    public static UUID changeUuidFromString(String id){
        StringBuilder sb = new StringBuilder(id);
        sb.insert(8, "-");
        sb.insert(13, "-");
        sb.insert(18, "-");
        sb.insert(23, "-");
        return UUID.fromString(sb.toString());
    }

    public static String changeUuidToString(UUID uuid){
        String[] uuidArr = uuid.toString().split("-");
        return uuidArr[0]+uuidArr[1]+uuidArr[2]+uuidArr[3]+uuidArr[4];
    }
}
