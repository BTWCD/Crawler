package util;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;

public enum TimeFormatUtils {
    INSTANCE;

    private static final FastDateFormat DEFAULT_FORMAT = FastDateFormat.getInstance( "yyyy-MM-dd" );

    private static final FastDateFormat CRAWLER_FORMAT = FastDateFormat.getInstance( "yyyy-MM-dd HH:mm:ss" );

    public String format( long time ) {
        return DEFAULT_FORMAT.format( time );
    }

    public String formatMilli( long milli ) {
        return DurationFormatUtils.formatDurationHMS( milli );
    }

    public String formatFetchTime( long time ) {
        return CRAWLER_FORMAT.format( time );
    }

    public String formatFetchTimeMilli( long milli ) {
        return DurationFormatUtils.formatDurationHMS( milli );
    }

}
