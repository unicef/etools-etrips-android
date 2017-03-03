package org.unicef.etools.etrips.io.rest.url_connection;

import android.content.Context;
import android.os.Bundle;

import org.unicef.etools.etrips.io.rest.entity.HttpConnection;
import org.unicef.etools.etrips.io.rest.url_connection.util.HttpResponseUtil;

public class HttpRequestManager {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = HttpRequestManager.class.getSimpleName();

    public class RequestType {
        public static final int LOGIN = 1;
        public static final int GET_MY_TRIPS = 2;
        public static final int SUBMIT_REPORT = 3;
        public static final int UPLOAD_REPORT_IMAGES = 4;
        public static final int GET_TRIP = 5;
        public static final int GET_STATIC_DATA = 6;
        public static final int GET_STATIC_DATA_2 = 7;
        public static final int GET_USERS = 8;
        public static final int GET_SUPERVISED_TRIPS = 9;
    }

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * @param url   api url
     * @param token pass authorization token if required, otherwise pass null
     * @param data  pass any additional data (usually post request json entity) if required,
     *              otherwise pass null
     * @param requestMethod post, put, delete, get or other
     */

    public static HttpConnection executeRequest(Context context, String requestMethod, String url,
                                                String token, String data) {
        Bundle bundle = new Bundle();
        bundle.putString(RestHttpClient.BundleData.JSON_ENTITY, data);
        bundle.putString(RestHttpClient.BundleData.TOKEN, token);
        HttpConnection httpConnection = null;

        switch (requestMethod){
            case RestHttpClient.RequestMethod.POST:
                httpConnection = RestHttpClient.executePostRequest(context, url, bundle);
                break;

            case RestHttpClient.RequestMethod.GET:
                httpConnection = RestHttpClient.executeGetRequest(context, url, bundle);
                break;

            case RestHttpClient.RequestMethod.PATCH:
                httpConnection = RestHttpClient.executePatchRequest(context, url, bundle);
                break;

            case RestHttpClient.RequestMethod.PUT:
                httpConnection = RestHttpClient.executePutRequest(context, url, bundle);
                break;

            case RestHttpClient.RequestMethod.DELETE:
                httpConnection = RestHttpClient.executeDeleteRequest(context, url, bundle);
                break;
        }
        httpConnection = HttpResponseUtil.handleConnection(context, httpConnection);
        return httpConnection;
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}