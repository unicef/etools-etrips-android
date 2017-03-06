package org.unicef.etools.etrips.prod.io.rest.url_connection.util;

import android.util.Log;

import org.unicef.etools.etrips.prod.BuildConfig;
import org.unicef.etools.etrips.prod.util.EncodeUtil;

public class PostEntityUtil {

    private static final String LOG_TAG = PostEntityUtil.class.getSimpleName();
    public static final String adfsEndpoint = "https://login.unicef.org/adfs/services/trust/13/UsernameMixed";
    public static final String resourceEndpoint = "https://etools.unicef.org/API";

    public static String composeSignInPostEntity(String username, String pass) {
        String entityString = "{\"username\": \""
                + EncodeUtil.escapeString(username)
                + "\", \"password\": \"" + EncodeUtil.escapeString(pass) + "\"}";
        if (BuildConfig.isDEBUG) Log.i(LOG_TAG, entityString);
        return entityString;
    }

    public static String composeSoapSignInPostEntity(String username, String pass) {
        return "<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:a=\"http://www.w3.org/2005/08/addressing\" xmlns:u=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\"><s:Header><a:Action s:mustUnderstand=\"1\">http://docs.oasis-open.org/ws-sx/ws-trust/200512/RST/Issue</a:Action><a:To s:mustUnderstand=\"1\">"+adfsEndpoint+"</a:To><o:Security s:mustUnderstand=\"1\" xmlns:o=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\"><o:UsernameToken u:Id=\"uuid-6a13a244-dac6-42c1-84c5-cbb345b0c4c4-1\"><o:Username>"+username+"</o:Username><o:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">"+pass+"</o:Password></o:UsernameToken></o:Security></s:Header><s:Body><trust:RequestSecurityToken xmlns:trust=\"http://docs.oasis-open.org/ws-sx/ws-trust/200512\"><wsp:AppliesTo xmlns:wsp=\"http://schemas.xmlsoap.org/ws/2004/09/policy\"><a:EndpointReference><a:Address>"+resourceEndpoint+"</a:Address></a:EndpointReference></wsp:AppliesTo><trust:KeyType>http://docs.oasis-open.org/ws-sx/ws-trust/200512/Bearer</trust:KeyType><trust:RequestType>http://docs.oasis-open.org/ws-sx/ws-trust/200512/Issue</trust:RequestType></trust:RequestSecurityToken></s:Body></s:Envelope>";
    }

    public static String composeReportPostEntity(String report) {
        String entityString = "{\"main_observations\": \""
                + EncodeUtil.escapeString(report) + "\"}";
        if (BuildConfig.isDEBUG) Log.i(LOG_TAG, entityString);
        return entityString;
    }
}
