package uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice.util;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;

public class DateTimeUtilsTest {

    private static final Logger logger = LoggerFactory.getLogger(DateTimeUtilsTest.class.getName());

    @Test
    public void test_successful_date_string_to_timestamp_and_to_display_value() throws Exception {
        final String sourceTime = "11/01/2018 20:00:00.000";
        final String outputTime = "01/11 20:00:00";
        final long sourceTimestamp = 1541102400L;
        logger.info(ZoneId.systemDefault().toString());
        final Long timestamp = DateTimeUtils.getNumberFromStringDate(sourceTime);
        Assert.assertEquals(sourceTimestamp, timestamp.longValue());

        final String displayTime = DateTimeUtils.getDateFromNumber(timestamp);
        Assert.assertEquals(outputTime, displayTime);
    }
}
