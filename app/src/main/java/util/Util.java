package util;

import java.util.Locale;

/**
 * Created by tuanbg on 12/27/16.
 */
public class Util {
    public static String convertToTime(long duration) {
        String type;
        int hours = (int) (duration / 3600);
        int minutes = (int) ((duration / 60) - (hours * 60));
        int seconds = (int) (duration - (hours * 3600) - (minutes * 60));
        type = (hours == 0 ? String.format(Locale.ENGLISH, "%02d:%02d", minutes,
            seconds) : String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, seconds));
        return type;
    }
}
