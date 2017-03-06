package org.unicef.etools.etrips.prod.io.rest.url_connection;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import org.unicef.etools.etrips.prod.BuildConfig;
import org.unicef.etools.etrips.prod.io.rest.entity.HttpConnection;
import org.unicef.etools.etrips.prod.io.rest.util.HttpErrorUtil;
import org.unicef.etools.etrips.prod.util.Constant;
import org.unicef.etools.etrips.prod.util.NetworkUtil;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestHttpClient {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = RestHttpClient.class.getSimpleName();

    public static final String UTF_8 = "UTF-8";
    public static final String TOKEN_VALUE = "JWT ";

    public class Header {
        public static final String AUTHORIZATION = "Authorization";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String ACCEPT_ENCODING = "Accept-Encoding";
        public static final String X_HTTP_METHOD_OVERRIDE = "X-HTTP-Method-Override";
    }

    public class PayloadType {
        public static final String APPLICATION_JSON = "application/json";
        public static final String APPLICATION_SOAP_XML = "application/soap+xml";
    }

    public class RequestMethod {
        public static final String POST = "POST";
        public static final String GET = "GET";
        public static final String PUT = "PUT";
        public static final String HEAD = "HEAD";
        public static final String DELETE = "DELETE";
        public static final String PATCH = "PATCH";
    }

    /* All extra data - headers, tokens, json post values will be put to bundle */
    public class BundleData {
        public static final String TOKEN = "token";
        public static final String JSON_ENTITY = "entity";
        public static final String IS_SOUP_XML = "is_soup_xml";
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
    // Methods
    // ===========================================================

    /**
     * HEAD Request
     */
    public static HttpConnection executeHeadRequest(Context context, String url, Bundle bundle) {
        Object token = null;
        if (bundle != null) {
            token = bundle.get(BundleData.TOKEN);
        }

        HttpConnection httpConnection = new HttpConnection();
        HttpURLConnection httpURLConnection = null;

        try {
            if (BuildConfig.isDEBUG) Log.i(LOG_TAG, "Calling URL: " + url);
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();

            if (token != null) {
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, "Token: " + String.valueOf(token));
                httpURLConnection.setRequestProperty(Header.AUTHORIZATION, TOKEN_VALUE + token);
            }

            httpURLConnection.setRequestMethod(RequestMethod.HEAD);
            httpURLConnection.setRequestProperty(Header.CONTENT_TYPE, PayloadType.APPLICATION_JSON);
            httpURLConnection.setRequestProperty(Header.ACCEPT_ENCODING, Constant.Symbol.NULL);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.connect();

            if (BuildConfig.isDEBUG)
                Log.i(LOG_TAG, "Server response: " + String.valueOf(httpURLConnection.getResponseCode())
                        + Constant.Symbol.SPACE + httpURLConnection.getResponseMessage());

            httpConnection.setHttpURLConnection(httpURLConnection);
            httpConnection.setHttpConnectionSucceeded(true);
            httpConnection.setHttpConnectionCode(httpURLConnection.getResponseCode());
            httpConnection.setHttpConnectionMessage(httpURLConnection.getResponseMessage());

        } catch (Exception e) {
            e.printStackTrace();

            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }

            if (!NetworkUtil.getInstance().isConnected(context.getApplicationContext())) {
                if (BuildConfig.isDEBUG) Log.e(LOG_TAG, "Internet connection error");
                httpConnection.setHttpConnectionSucceeded(false);
                httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_NO_NETWORK);
                httpConnection.setHttpConnectionMessage(e.getMessage());

            } else if (e.getMessage() != null && e.getMessage().contains(HttpErrorUtil.NonNumericStatusCode.HTTP_SERVER_TIMEOUT)) {
                if (BuildConfig.isDEBUG) Log.e(LOG_TAG, "Connection time out " + url);
                httpConnection.setHttpConnectionSucceeded(false);
                httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_SERVER_TIMEOUT);
                httpConnection.setHttpConnectionMessage(e.getMessage());

            } else if (e.getMessage() != null && e.getMessage().contains(HttpErrorUtil.NonNumericStatusCode.HTTP_CONNECTION_REFUSED)) {
                if (BuildConfig.isDEBUG) Log.e(LOG_TAG, "Connection refused by server " + url);
                httpConnection.setHttpConnectionSucceeded(false);
                httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_CONNECTION_REFUSED);
                httpConnection.setHttpConnectionMessage(e.getMessage());

            } else {
                if (e.getMessage() != null) {
                    if (BuildConfig.isDEBUG) Log.e(LOG_TAG, e.getMessage());
                    httpConnection.setHttpConnectionSucceeded(false);
                    httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_UNKNOWN_SERVER_ERROR);
                    httpConnection.setHttpConnectionMessage(e.getMessage());
                }
            }
        }
        return httpConnection;
    }

    /**
     * POST Request
     */
    public static HttpConnection executePostRequest(Context context, String url, Bundle bundle) {
        boolean isSoupXml = false;
        Object token = null;
        String data = null;
        if (bundle != null) {
            token = bundle.get(BundleData.TOKEN);
            data = String.valueOf(bundle.get(BundleData.JSON_ENTITY));
            isSoupXml = bundle.getBoolean(BundleData.IS_SOUP_XML);
        }

        HttpConnection httpConnection = new HttpConnection();
        HttpURLConnection httpURLConnection = null;

        try {
            if (BuildConfig.isDEBUG) Log.i(LOG_TAG, "Calling URL: " + url);
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();

            if (token != null) {
                httpURLConnection.setRequestProperty(Header.AUTHORIZATION, TOKEN_VALUE + token);
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, "Token: " + String.valueOf(token));
            }

            httpURLConnection.setRequestMethod(RequestMethod.POST);
            httpURLConnection.setRequestProperty(Header.CONTENT_TYPE, isSoupXml ? PayloadType.APPLICATION_SOAP_XML : PayloadType.APPLICATION_JSON);
            httpURLConnection.setRequestProperty(Header.ACCEPT_ENCODING, Constant.Symbol.NULL);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            OutputStream out = httpURLConnection.getOutputStream();
            out.write(data != null ? data.getBytes(UTF_8) : new byte[]{0});
            out.flush();
            out.close();

            if (BuildConfig.isDEBUG)
                Log.i(LOG_TAG, "Server response: " + String.valueOf(httpURLConnection.getResponseCode())
                        + Constant.Symbol.SPACE + httpURLConnection.getResponseMessage());

            httpConnection.setHttpURLConnection(httpURLConnection);
            httpConnection.setHttpConnectionSucceeded(true);
            httpConnection.setHttpConnectionCode(httpURLConnection.getResponseCode());
            httpConnection.setHttpConnectionMessage(httpURLConnection.getResponseMessage());

        } catch (Exception e) {
            e.printStackTrace();

            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }

            if (!NetworkUtil.getInstance().isConnected(context.getApplicationContext())) {
                if (BuildConfig.isDEBUG) Log.e(LOG_TAG, "Internet connection error ");
                httpConnection.setHttpConnectionSucceeded(false);
                httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_NO_NETWORK);
                httpConnection.setHttpConnectionMessage(e.getMessage());

            } else if (e.getMessage() != null && e.getMessage().contains(HttpErrorUtil.NonNumericStatusCode.HTTP_SERVER_TIMEOUT)) {
                if (BuildConfig.isDEBUG) Log.e(LOG_TAG, "Connection time out " + url);
                httpConnection.setHttpConnectionSucceeded(false);
                httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_SERVER_TIMEOUT);
                httpConnection.setHttpConnectionMessage(e.getMessage());

            } else if (e.getMessage() != null && e.getMessage().contains(HttpErrorUtil.NonNumericStatusCode.HTTP_CONNECTION_REFUSED)) {
                if (BuildConfig.isDEBUG) Log.e(LOG_TAG, "Connection refused by server " + url);
                httpConnection.setHttpConnectionSucceeded(false);
                httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_CONNECTION_REFUSED);
                httpConnection.setHttpConnectionMessage(e.getMessage());

            } else {
                if (e.getMessage() != null) {
                    if (BuildConfig.isDEBUG) Log.e(LOG_TAG, e.getMessage());
                    httpConnection.setHttpConnectionSucceeded(false);
                    httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_UNKNOWN_SERVER_ERROR);
                    httpConnection.setHttpConnectionMessage(e.getMessage());
                }
            }
        }
        return httpConnection;
    }

    /**
     * PATCH Request
     */
    public static HttpConnection executePatchRequest(Context context, String url, Bundle bundle) {
        Object token = null;
        String data = null;
        if (bundle != null) {
            token = bundle.get(BundleData.TOKEN);
            data = String.valueOf(bundle.get(BundleData.JSON_ENTITY));
        }

        HttpConnection httpConnection = new HttpConnection();
        HttpURLConnection httpURLConnection = null;

        try {
            if (BuildConfig.isDEBUG) Log.i(LOG_TAG, "Calling URL: " + url);
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();

            if (token != null) {
                httpURLConnection.setRequestProperty(Header.AUTHORIZATION, TOKEN_VALUE + token);
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, "Token: " + String.valueOf(token));
            }

            httpURLConnection.setRequestMethod(RequestMethod.POST);
            httpURLConnection.setRequestProperty(Header.CONTENT_TYPE, PayloadType.APPLICATION_JSON);
            httpURLConnection.setRequestProperty(Header.ACCEPT_ENCODING, Constant.Symbol.NULL);

            Field field = HttpURLConnection.class.getDeclaredField("method");
            field.setAccessible(true);
            field.set(httpURLConnection, RequestMethod.PATCH);

            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(false);
            httpURLConnection.connect();

            OutputStream out = httpURLConnection.getOutputStream();
            out.write(data != null ? data.getBytes(UTF_8) : new byte[]{0});
            out.flush();
            out.close();

            if (BuildConfig.isDEBUG)
                Log.i(LOG_TAG, "Server response: " + String.valueOf(httpURLConnection.getResponseCode())
                        + Constant.Symbol.SPACE + httpURLConnection.getResponseMessage());

            httpConnection.setHttpURLConnection(httpURLConnection);
            httpConnection.setHttpConnectionSucceeded(true);
            httpConnection.setHttpConnectionCode(httpURLConnection.getResponseCode());
            httpConnection.setHttpConnectionMessage(httpURLConnection.getResponseMessage());

        } catch (Exception e) {
            e.printStackTrace();

            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }

            if (!NetworkUtil.getInstance().isConnected(context.getApplicationContext())) {
                if (BuildConfig.isDEBUG) Log.e(LOG_TAG, "Internet connection error ");
                httpConnection.setHttpConnectionSucceeded(false);
                httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_NO_NETWORK);
                httpConnection.setHttpConnectionMessage(e.getMessage());

            } else if (e.getMessage() != null && e.getMessage().contains(HttpErrorUtil.NonNumericStatusCode.HTTP_SERVER_TIMEOUT)) {
                if (BuildConfig.isDEBUG) Log.e(LOG_TAG, "Connection time out " + url);
                httpConnection.setHttpConnectionSucceeded(false);
                httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_SERVER_TIMEOUT);
                httpConnection.setHttpConnectionMessage(e.getMessage());

            } else if (e.getMessage() != null && e.getMessage().contains(HttpErrorUtil.NonNumericStatusCode.HTTP_CONNECTION_REFUSED)) {
                if (BuildConfig.isDEBUG) Log.e(LOG_TAG, "Connection refused by server " + url);
                httpConnection.setHttpConnectionSucceeded(false);
                httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_CONNECTION_REFUSED);
                httpConnection.setHttpConnectionMessage(e.getMessage());

            } else {
                if (e.getMessage() != null) {
                    if (BuildConfig.isDEBUG) Log.e(LOG_TAG, e.getMessage());
                    httpConnection.setHttpConnectionSucceeded(false);
                    httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_UNKNOWN_SERVER_ERROR);
                    httpConnection.setHttpConnectionMessage(e.getMessage());
                }
            }
        }
        return httpConnection;
    }

    /**
     * PUT Request
     */
    public static HttpConnection executePutRequest(Context context, String url, Bundle bundle) {
        Object token = null;
        String data = null;
        if (bundle != null) {
            token = bundle.get(BundleData.TOKEN);
            data = String.valueOf(bundle.get(BundleData.JSON_ENTITY));
        }

        HttpConnection httpConnection = new HttpConnection();
        HttpURLConnection httpURLConnection = null;

        try {
            if (BuildConfig.isDEBUG) Log.i(LOG_TAG, "Calling URL: " + url);
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();

            if (token != null) {
                httpURLConnection.setRequestProperty(Header.AUTHORIZATION, TOKEN_VALUE + token);
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, "Token: " + String.valueOf(token));
            }

            httpURLConnection.setRequestMethod(RequestMethod.PUT);
            httpURLConnection.setRequestProperty(Header.CONTENT_TYPE, PayloadType.APPLICATION_JSON);
            httpURLConnection.setRequestProperty(Header.ACCEPT_ENCODING, Constant.Symbol.NULL);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            OutputStream out = httpURLConnection.getOutputStream();
            out.write(data != null ? data.getBytes(UTF_8) : new byte[]{0});
            out.flush();
            out.close();

            if (BuildConfig.isDEBUG)
                Log.i(LOG_TAG, "Server response: " + String.valueOf(httpURLConnection.getResponseCode())
                        + Constant.Symbol.SPACE + httpURLConnection.getResponseMessage());

            httpConnection.setHttpURLConnection(httpURLConnection);
            httpConnection.setHttpConnectionSucceeded(true);
            httpConnection.setHttpConnectionCode(httpURLConnection.getResponseCode());
            httpConnection.setHttpConnectionMessage(httpURLConnection.getResponseMessage());

        } catch (Exception e) {
            e.printStackTrace();

            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }

            if (!NetworkUtil.getInstance().isConnected(context.getApplicationContext())) {
                if (BuildConfig.isDEBUG) Log.e(LOG_TAG, "Internet connection error ");
                httpConnection.setHttpConnectionSucceeded(false);
                httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_NO_NETWORK);
                httpConnection.setHttpConnectionMessage(e.getMessage());

            } else if (e.getMessage() != null && e.getMessage().contains(HttpErrorUtil.NonNumericStatusCode.HTTP_SERVER_TIMEOUT)) {
                if (BuildConfig.isDEBUG) Log.e(LOG_TAG, "Connection time out " + url);
                httpConnection.setHttpConnectionSucceeded(false);
                httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_SERVER_TIMEOUT);
                httpConnection.setHttpConnectionMessage(e.getMessage());

            } else if (e.getMessage() != null && e.getMessage().contains(HttpErrorUtil.NonNumericStatusCode.HTTP_CONNECTION_REFUSED)) {
                if (BuildConfig.isDEBUG) Log.e(LOG_TAG, "Connection refused by server " + url);
                httpConnection.setHttpConnectionSucceeded(false);
                httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_CONNECTION_REFUSED);
                httpConnection.setHttpConnectionMessage(e.getMessage());

            } else {
                if (e.getMessage() != null) {
                    if (BuildConfig.isDEBUG) Log.e(LOG_TAG, e.getMessage());
                    httpConnection.setHttpConnectionSucceeded(false);
                    httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_UNKNOWN_SERVER_ERROR);
                    httpConnection.setHttpConnectionMessage(e.getMessage());
                }
            }
        }
        return httpConnection;
    }

    /**
     * GET Request
     */
    public static HttpConnection executeGetRequest(final Context context, String url, Bundle bundle) {
        Object token = null;
        if (bundle != null) {
            token = bundle.get(BundleData.TOKEN);
        }

        HttpConnection httpConnection = new HttpConnection();
        HttpURLConnection httpURLConnection = null;

        try {
            if (BuildConfig.isDEBUG) Log.i(LOG_TAG, "Calling URL: " + url);
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();

            if (token != null) {
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, "Token: " + String.valueOf(token));
                httpURLConnection.setRequestProperty(Header.AUTHORIZATION, TOKEN_VALUE + token);
            }

            httpURLConnection.setRequestMethod(RequestMethod.GET);
            httpURLConnection.setRequestProperty(Header.CONTENT_TYPE, PayloadType.APPLICATION_JSON);
            httpURLConnection.setRequestProperty(Header.ACCEPT_ENCODING, Constant.Symbol.NULL);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.connect();

            if (BuildConfig.isDEBUG)
                Log.i(LOG_TAG, "Server response: " + String.valueOf(httpURLConnection.getResponseCode())
                        + Constant.Symbol.SPACE + httpURLConnection.getResponseMessage());

            httpConnection.setHttpURLConnection(httpURLConnection);
            httpConnection.setHttpConnectionSucceeded(true);
            httpConnection.setHttpConnectionCode(httpURLConnection.getResponseCode());
            httpConnection.setHttpConnectionMessage(httpURLConnection.getResponseMessage());

        } catch (Exception e) {
            e.printStackTrace();

            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }

            if (!NetworkUtil.getInstance().isConnected(context.getApplicationContext())) {
                if (BuildConfig.isDEBUG) Log.e(LOG_TAG, "Internet connection error ");
                httpConnection.setHttpConnectionSucceeded(false);
                httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_NO_NETWORK);
                httpConnection.setHttpConnectionMessage(e.getMessage());

            } else if (e.getMessage() != null && e.getMessage().contains(HttpErrorUtil.NonNumericStatusCode.HTTP_SERVER_TIMEOUT)) {
                if (BuildConfig.isDEBUG) Log.e(LOG_TAG, "Connection time out " + url);
                httpConnection.setHttpConnectionSucceeded(false);
                httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_SERVER_TIMEOUT);
                httpConnection.setHttpConnectionMessage(e.getMessage());

            } else if (e.getMessage() != null && e.getMessage().contains(HttpErrorUtil.NonNumericStatusCode.HTTP_CONNECTION_REFUSED)) {
                if (BuildConfig.isDEBUG) Log.e(LOG_TAG, "Connection refused by server " + url);
                httpConnection.setHttpConnectionSucceeded(false);
                httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_CONNECTION_REFUSED);
                httpConnection.setHttpConnectionMessage(e.getMessage());

            } else {
                if (e.getMessage() != null) {
                    if (BuildConfig.isDEBUG) Log.e(LOG_TAG, e.getMessage());
                    httpConnection.setHttpConnectionSucceeded(false);
                    httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_UNKNOWN_SERVER_ERROR);
                    httpConnection.setHttpConnectionMessage(e.getMessage());
                }
            }
        }

        return httpConnection;

    }

    /**
     * DELETE Request
     */
    public static HttpConnection executeDeleteRequest(final Context context, String url, Bundle bundle) {
        Object token = null;
        if (bundle != null) {
            token = bundle.get(BundleData.TOKEN);
        }

        HttpConnection httpConnection = new HttpConnection();
        HttpURLConnection httpURLConnection = null;

        try {
            if (BuildConfig.isDEBUG) Log.i(LOG_TAG, "Calling URL: " + url);
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();

            if (token != null) {
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, "Token: " + String.valueOf(token));
                httpURLConnection.setRequestProperty(Header.AUTHORIZATION, TOKEN_VALUE + token);
            }

            httpURLConnection.setRequestMethod(RequestMethod.DELETE);
            httpURLConnection.setRequestProperty(Header.CONTENT_TYPE, PayloadType.APPLICATION_JSON);
            httpURLConnection.setRequestProperty(Header.ACCEPT_ENCODING, Constant.Symbol.NULL);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.connect();

            if (BuildConfig.isDEBUG)
                Log.i(LOG_TAG, "Server response: " + String.valueOf(httpURLConnection.getResponseCode())
                        + Constant.Symbol.SPACE + httpURLConnection.getResponseMessage());

            httpConnection.setHttpURLConnection(httpURLConnection);
            httpConnection.setHttpConnectionSucceeded(true);
            httpConnection.setHttpConnectionCode(httpURLConnection.getResponseCode());
            httpConnection.setHttpConnectionMessage(httpURLConnection.getResponseMessage());

        } catch (Exception e) {
            e.printStackTrace();

            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }

            if (!NetworkUtil.getInstance().isConnected(context.getApplicationContext())) {
                if (BuildConfig.isDEBUG) Log.e(LOG_TAG, "Internet connection error ");
                httpConnection.setHttpConnectionSucceeded(false);
                httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_NO_NETWORK);
                httpConnection.setHttpConnectionMessage(e.getMessage());

            } else if (e.getMessage() != null && e.getMessage().contains(HttpErrorUtil.NonNumericStatusCode.HTTP_SERVER_TIMEOUT)) {
                if (BuildConfig.isDEBUG) Log.e(LOG_TAG, "Connection time out " + url);
                httpConnection.setHttpConnectionSucceeded(false);
                httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_SERVER_TIMEOUT);
                httpConnection.setHttpConnectionMessage(e.getMessage());

            } else if (e.getMessage() != null && e.getMessage().contains(HttpErrorUtil.NonNumericStatusCode.HTTP_CONNECTION_REFUSED)) {
                if (BuildConfig.isDEBUG) Log.e(LOG_TAG, "Connection refused by server " + url);
                httpConnection.setHttpConnectionSucceeded(false);
                httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_CONNECTION_REFUSED);
                httpConnection.setHttpConnectionMessage(e.getMessage());

            } else {
                if (e.getMessage() != null) {
                    if (BuildConfig.isDEBUG) Log.e(LOG_TAG, e.getMessage());
                    httpConnection.setHttpConnectionSucceeded(false);
                    httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_UNKNOWN_SERVER_ERROR);
                    httpConnection.setHttpConnectionMessage(e.getMessage());
                }
            }
        }
        return httpConnection;
    }

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

}