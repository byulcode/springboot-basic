package com.prgrms.springbasic.util;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDUtils {

    public static UUID toUUID(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return new java.util.UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }
}
