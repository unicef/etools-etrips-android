package org.unicef.etools.etrips.prod.io.rest.util;

import org.unicef.etools.etrips.prod.BuildConfig;
import org.unicef.etools.etrips.prod.util.Constant;

public class APIUtil {

    private static final String HOST_PRODUCTION = "https://etools.unicef.org/";
    private static final String HOST_STAGING = "https://etools-staging.unicef.org/";

    public static final String LOGIN_STAGING = "/login/token-auth/";
    public static final String LOGIN_PROD = "https://login.unicef.org/adfs/services/trust/13/UsernameMixed";
    public static final String MY_TRIPS = "/api/t2f/travels/?f_traveler=%1$s&page=1&page_size=10000";
    public static final String SUPERVISED_TRIPS = "/api/t2f/travels/?f_supervisor=%1$s&page=1&page_size=10000";
    public static final String SUBMIT_REPORT = "/api/t2f/travels/%1$s/";
    public static final String UPLOAD_REPORT_FILE = "/api/t2f/travels/%1$s/attachments/";
    public static final String PROFILE = "/users/api/profile/";
    public static final String ACTION_POINTS = "api/t2f/action_points/";
    public static final String ACTION_POINT = "api/t2f/action_points/{id}/";
    public static final String ADD_ACTION_POINT = "api/t2f/travels/{trip_id}/";
    public static final String TRIP = "/api/t2f/travels/%1$s/";
    public static final String STATIC_DATA = "/api/t2f/static_data/";
    public static final String STATIC_DATA_2 = "/api/static_data/";
    public static final String USERS = "/users/api/";
    public static final String CHANGE_TRIP_STATUS = "/api/t2f/travels/%d/%s/";

    public static final int PER_PAGE_ACTION_POINTS = 5;

    public static String getURL(String api) {
        return getHost() + api;
    }

    public static String getHost() {
        if (BuildConfig.BUILD_TYPE.equals(Constant.BuildType.RELEASE)) {
            return HOST_PRODUCTION;
        } else {
            return HOST_STAGING;
        }
    }
}
