package org.unicef.etools.etrips.io.rest.url_connection.util;

import android.content.Context;
import android.util.Log;

import org.unicef.etools.etrips.BuildConfig;
import org.unicef.etools.etrips.io.rest.entity.HttpConnection;
import org.unicef.etools.etrips.io.rest.entity.HttpResponseHeader;
import org.unicef.etools.etrips.io.rest.util.HttpErrorUtil;
import org.unicef.etools.etrips.util.Constant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class HttpResponseUtil {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = HttpResponseUtil.class.getSimpleName();

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

    public static HttpConnection handleConnection(Context context, HttpConnection httpConnection) {

        StringBuilder responseStrBuilder = new StringBuilder();
        HttpURLConnection httpURLConnection = httpConnection.getHttpURLConnection();

        if (httpURLConnection != null) {
            InputStream httpInputStream = null;
            InputStreamReader inputStreamReader = null;
            String httpResponseMsg;
            int httpResponseCode;

            try {
                httpResponseCode = httpURLConnection.getResponseCode();
                httpResponseMsg = httpURLConnection.getResponseMessage();
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, String.valueOf(httpResponseCode) + Constant.Symbol.SPACE + httpResponseMsg);

                 /* 2XX: generally "OK"
                    3XX: relocation/redirect
                    4XX: client error
                    5XX: server error */
                if (httpResponseCode <= HttpURLConnection.HTTP_BAD_REQUEST) {
                    if (httpResponseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                        httpInputStream = httpURLConnection.getErrorStream();
                        httpConnection.setHttpConnectionSucceeded(false);
                    } else {
                        httpInputStream = httpURLConnection.getInputStream();
                        httpConnection.setHttpConnectionSucceeded(true);
                    }

                    inputStreamReader = new InputStreamReader(httpInputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String tempLine = bufferedReader.readLine();
                    while (tempLine != null) {
                        responseStrBuilder.append(tempLine);
                        tempLine = bufferedReader.readLine();
                    }
                    bufferedReader.close();

                    if (BuildConfig.isDEBUG) Log.i(LOG_TAG, responseStrBuilder.toString());

                    httpConnection.setHttpConnectionCode(httpResponseCode);
                    httpConnection.setHttpConnectionMessage(httpResponseMsg);
                    httpConnection.setHttpResponseHeader(readHeader(context, httpConnection));
                    httpConnection.setHttpResponseBody(responseStrBuilder);

                } else {
                    if (httpResponseCode == HttpErrorUtil.NumericStatusCode.HTTP_UNAUTHORIZED) {
                        // 401 user not authorized
                        httpConnection.setHttpConnectionSucceeded(false);
                        httpConnection.setHttpConnectionCode(httpResponseCode);
                        httpConnection.setHttpConnectionMessage(httpResponseMsg);
                        httpConnection.setHttpResponseHeader(readHeader(context, httpConnection));
                        httpConnection.setHttpResponseBody(responseStrBuilder);

                    } else if (httpResponseCode == HttpErrorUtil.NumericStatusCode.HTTP_NOT_FOUND) {
                        // 404 Page not found
                        httpConnection.setHttpConnectionSucceeded(false);
                        httpConnection.setHttpConnectionCode(httpResponseCode);
                        httpConnection.setHttpConnectionMessage(httpResponseMsg);
                        httpConnection.setHttpResponseHeader(readHeader(context, httpConnection));
                        httpConnection.setHttpResponseBody(responseStrBuilder);

                    } else {
                        // Other errors (405, 500, 501 or else)
                        httpConnection.setHttpConnectionSucceeded(false);
                        httpConnection.setHttpConnectionCode(httpResponseCode);
                        httpConnection.setHttpConnectionMessage(httpResponseMsg);
                        httpConnection.setHttpResponseHeader(readHeader(context, httpConnection));
                        httpConnection.setHttpResponseBody(responseStrBuilder);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                try {
                    httpURLConnection.disconnect();
                    if (httpInputStream != null) {
                        httpInputStream.close();
                    }
                    if (inputStreamReader != null) {
                        inputStreamReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return httpConnection;
    }

    private static HttpResponseHeader readHeader(Context context, HttpConnection httpConnection) {

        class Header {
            private static final String AUTHORIZATION = "Authorization";
            private static final String CONTENT_TYPE = "Content-Type";
            private static final String X_TOKEN = "X-Token";
            private static final String E_TAG = "eTag";
            private static final String LAST_MODIFIED = "Last-Modified";
        }

        HttpResponseHeader httpResponseHeader = new HttpResponseHeader();

        try {
            int httpResponseCode = httpConnection.getHttpConnectionCode();
            String httpResponseMsg = httpConnection.getHttpConnectionMessage();

            if (httpConnection.getHttpURLConnection() != null) {
                httpResponseHeader.setToken(httpConnection.getHttpURLConnection().getHeaderField(Header.X_TOKEN));
                httpResponseHeader.setETag(httpConnection.getHttpURLConnection().getHeaderField(Header.E_TAG));
                httpResponseHeader.setLastModified(httpConnection.getHttpURLConnection().getHeaderField(Header.LAST_MODIFIED));
                httpResponseHeader.setHttpConnectionSucceeded(true);
                httpResponseHeader.setHttpConnectionCode(httpResponseCode);
                httpResponseHeader.setHttpConnectionMessage(httpResponseMsg);

                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, "Request header data: "
                        + "\nToken - " + httpResponseHeader.getToken()
                        + "\neTag - " + httpResponseHeader.getETag()
                        + "\nLast-Modified - " + httpResponseHeader.getLastModified());

            } else {
                httpResponseHeader.setHttpConnectionSucceeded(false);
                httpResponseHeader.setHttpConnectionCode(httpResponseCode);
                httpResponseHeader.setHttpConnectionMessage(httpResponseMsg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return httpResponseHeader;
    }


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}