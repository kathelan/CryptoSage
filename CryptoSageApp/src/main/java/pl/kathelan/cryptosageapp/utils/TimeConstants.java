package pl.kathelan.cryptosageapp.utils;

import lombok.experimental.UtilityClass;

import java.time.Duration;

@UtilityClass
public class TimeConstants {
    public static final Duration FIVE_MINUTES = Duration.ofMinutes(5);
    public static final Duration ONE_HOUR = Duration.ofHours(1);
    public static final long ONE_WEEK = Duration.ofDays(7).toMillis();
}
