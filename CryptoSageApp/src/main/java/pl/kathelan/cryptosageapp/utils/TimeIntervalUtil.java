package pl.kathelan.cryptosageapp.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeIntervalUtil {
    private static final long FIVE_MINUTES = 300_000L;
    private static final long THREE_DAYS = 259_200_000L;

    public static TimeInterval determineTimeInterval(boolean isExistingDataPresent) {
        long endTime = System.currentTimeMillis();
        long startTime;
        String interval;
        if (isExistingDataPresent) {
            startTime = endTime - FIVE_MINUTES;
            interval = String.valueOf(300);
        } else {
            startTime = endTime - THREE_DAYS;
            interval = String.valueOf(259_200);
        }
        return new TimeInterval(startTime, endTime, interval);
    }

    public record TimeInterval(long startTime, long endTime, String interval) {
    }
}

