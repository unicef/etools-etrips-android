package org.unicef.etools.etrips.util;


public class StringUtils {

    public static String textCapSentences(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
