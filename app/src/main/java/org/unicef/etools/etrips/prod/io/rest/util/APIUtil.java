package org.unicef.etools.etrips.prod.io.rest.util;

import org.unicef.etools.etrips.prod.BuildConfig;
import org.unicef.etools.etrips.prod.util.Constant;

public class APIUtil {

    private static final String HOST_PRODUCTION = "https://etools.unicef.org/";
    private static final String HOST_STAGING = "https://etools-staging.unicef.org/";
    private static final String HOST_LOGIN_PRODUCTION = "https://login.unicef.org/";

    public static final String LOGIN_STAGING = "login/token-auth/";
    public static final String LOGIN_PROD = "adfs/services/trust/13/UsernameMixed";
    public static final String SUBMIT_REPORT = "api/t2f/travels/%1$s/";
    public static final String UPLOAD_REPORT_FILE = "api/t2f/travels/%1$s/attachments/";
    public static final String PROFILE = "users/api/profile/";
    public static final String ACTION_POINTS = "api/t2f/action_points/";
    public static final String ACTION_POINT = "api/t2f/action_points/{id}/";
    public static final String ADD_ACTION_POINT = "api/t2f/travels/{trip_id}/";
    public static final String TRIP = "api/t2f/travels/{trip_id}/";
    public static final String STATIC_DATA = "api/t2f/static_data/";
    public static final String STATIC_DATA_2 = "api/static_data/";
    public static final String USERS = "api/users/";
    public static final String CHANGE_TRIP_STATUS = "api/t2f/travels/%d/%s/";
    public static final String TRIPS = "api/t2f/travels/";
    public static final String WBS_GRANTS_FUNDS = "api/wbs_grants_funds/";
    public static final String CURRENCIES = "api/currencies/";

    public static final int PER_PAGE_ACTION_POINTS = 20;
    public static final int PER_PAGE_TRIPS = 20;

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

    public static String getHostLogIn() {
        return HOST_LOGIN_PRODUCTION;
    }

    public static class SortBy {
        public static final String DUE_DATE = "due_date";
        public static final String START_DATE = "start_date";
    }
}
