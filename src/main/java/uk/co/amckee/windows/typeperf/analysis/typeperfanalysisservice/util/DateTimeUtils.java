package uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import static java.time.Instant.ofEpochSecond;
import static java.time.OffsetDateTime.now;

public final class DateTimeUtils {

    protected static final String CSV_INPUT_TIME_FORMAT = "MM/dd/yyyy HH:mm:ss.SSS";
    protected static final String CMD_LINE_INPUT_FORMAT = "dd/MM HH:mm:ss";


    private DateTimeUtils() {
        //NoOp Constructors
    }

    public static String getDateFromNumber(final Number date) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(ofEpochSecond(date.longValue()), ZoneId.systemDefault());
        return dateTime.format(DateTimeFormatter.ofPattern(CMD_LINE_INPUT_FORMAT));
    }

    public static Long getNumberFromStringDate(final String dateString) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CSV_INPUT_TIME_FORMAT, Locale.ENGLISH);
        final LocalDateTime date = LocalDateTime.parse(dateString, formatter);
        return date.toEpochSecond(now().getOffset());
    }

}
