package com.snd.server.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTimeUtils {

    public static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    public static Instant instantNowInVietnam() {
        return ZonedDateTime.now(VIETNAM_ZONE).toInstant();
    }
}
