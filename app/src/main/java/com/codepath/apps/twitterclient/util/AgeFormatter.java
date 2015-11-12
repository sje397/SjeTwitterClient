package com.codepath.apps.twitterclient.util;

import android.text.format.DateUtils;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * Created by sellis on 11/8/15.
 */
public final class AgeFormatter {
    private static PeriodFormatter ageFormatter = new PeriodFormatterBuilder()
            .appendLiteral("{fa-clock-o} ")
            .appendWeeks()
            .appendSuffix("w")
            .appendSeparator(" ")
            .appendDays()
            .appendSuffix("d")
            .appendSeparator(" ")
            .appendHours()
            .appendSuffix("h")
            .appendSeparator(" ")
            .printZeroRarelyLast()
            .appendMinutes()
            .appendSuffix("m")
            .toFormatter();

    private AgeFormatter() {}

    public static String getAgeString(final DateTime time) {
        Duration age = new Duration(time, DateTime.now());
        Period agePeriod = age.toPeriod().normalizedStandard();
        return ageFormatter.print(agePeriod);
    }
}
