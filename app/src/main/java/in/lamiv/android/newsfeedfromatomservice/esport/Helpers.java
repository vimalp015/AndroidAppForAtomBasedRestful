package in.lamiv.android.newsfeedfromatomservice.esport;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by vimal on 10/5/2016.
 * will have static methods that could be invoked application wide
 */

public class Helpers {

    //Parse the date we receive from the feed and convert to Sydney/Melbourne time
    public static Date ParseDateFromFeed(String dateString) {
        Date date = null;
        try {
            DateFormat format = new SimpleDateFormat(GlobalVars.DATE_FORMAT_FEED, Locale.ENGLISH);
            format.setTimeZone(TimeZone.getTimeZone(GlobalVars.TIME_ZONE));
            date = format.parse(dateString);
        }
        catch (Exception e) {
            Log.i(e.getMessage(), e.getStackTrace().toString());
        }
        return date;
    }

    //Parse a date to string for parcelable object
    public static String ParseDateToString(Date date) {
        DateFormat format = new SimpleDateFormat(GlobalVars.DATE_FORMAT_FEED, Locale.ENGLISH);
        return format.format(date);
    }

    //Parse a date to string for display
    public static String ParseDateToStringForDisplay(Date date) {
        DateFormat format = new SimpleDateFormat(GlobalVars.DATE_FORMAT_DISPLAY, Locale.ENGLISH);
        return format.format(date);
    }

}
