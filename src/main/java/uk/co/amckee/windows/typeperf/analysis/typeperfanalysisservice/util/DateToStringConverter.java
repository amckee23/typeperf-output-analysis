package uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice.util;

import javafx.util.StringConverter;

import static uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice.util.DateTimeUtils.getNumberFromStringDate;

public class DateToStringConverter extends StringConverter<Number> {
    
    @Override
    public String toString(Number object) {
        return DateTimeUtils.getDateFromNumber(object);
    }

    @Override
    public Number fromString(String string) {
        return getNumberFromStringDate(string);
    }

}
