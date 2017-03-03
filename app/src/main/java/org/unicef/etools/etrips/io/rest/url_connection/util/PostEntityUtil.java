package org.unicef.etools.etrips.io.rest.url_connection.util;

import android.util.Log;

import org.unicef.etools.etrips.BuildConfig;
import org.unicef.etools.etrips.util.EncodeUtil;

public class PostEntityUtil {

    private static final String LOG_TAG = PostEntityUtil.class.getSimpleName();

    public static String composeSignInPostEntity(String username, String pass) {
        String entityString = "{\"username\": \""
                + EncodeUtil.escapeString(username)
                + "\", \"password\": \"" + EncodeUtil.escapeString(pass) + "\"}";
        if (BuildConfig.isDEBUG) Log.i(LOG_TAG, entityString);
        return entityString;
    }

    public static String composeReportPostEntity(String report) {
        String entityString = "{\"main_observations\": \""
                + EncodeUtil.escapeString(report) + "\"}";
        if (BuildConfig.isDEBUG) Log.i(LOG_TAG, entityString);
        return entityString;
    }
}
