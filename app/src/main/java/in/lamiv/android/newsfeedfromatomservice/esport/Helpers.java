package in.lamiv.android.newsfeedfromatomservice.esport;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by vimal on 10/5/2016.
 */

public class Helpers {

    public static Date ParseDateFromFeed(String dateString) {
        Date date = null;
        try {
            DateFormat format = new SimpleDateFormat(GlobalVars.DATE_FORMAT_FEED, Locale.ENGLISH);
            format.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));
            date = format.parse(dateString);
        }
        catch (Exception e) {
            Log.i(e.getMessage(), e.getStackTrace().toString());
        }
        return date;
    }
}
