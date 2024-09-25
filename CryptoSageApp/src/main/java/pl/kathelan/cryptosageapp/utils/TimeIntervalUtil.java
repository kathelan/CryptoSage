package pl.kathelan.cryptosageapp.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeIntervalUtil {
    private static final long FIVE_MINUTES = 300_000L;
    private static final long THREE_DAYS = 259_200_000L;
    private static final long ONE_HOUR = 3_600_000L;
    private static final long ONE_WEEK = 604_800_000L;

    public static TimeInterval determineTimeInterval(boolean isExistingDataPresent, Long startTime) {
        long endTime = System.currentTimeMillis();
        long startTimeInterval;
        String interval;

        long timeSinceStart = endTime - startTime;
        if (isExistingDataPresent) {
            if (timeSinceStart < ONE_WEEK) {
                startTimeInterval = endTime - FIVE_MINUTES;
                interval = String.valueOf(300);
            } else {
                startTimeInterval = endTime - ONE_HOUR;
                interval = String.valueOf(3600);
            }
        } else {
            startTimeInterval = endTime - THREE_DAYS;
            interval = String.valueOf(259200);
        }
        return new TimeInterval(startTimeInterval, endTime, interval);
    }

    public record TimeInterval(long startTime, long endTime, String interval) {
    }
}

