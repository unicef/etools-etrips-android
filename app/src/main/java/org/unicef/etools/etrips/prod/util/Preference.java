package org.unicef.etools.etrips.prod.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preference {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String PREF_USER_TOKEN = "PREF_USER_TOKEN";
    private static final String PREF_USER_NAME = "PREF_USER_NAME";
    private static final String PREF_USER_ID = "PREF_USER_ID";
    private static final String PREF_USER_LANGUAGE = "PREF_USER_LANGUAGE";

    // ===========================================================
    // Fields
    // ===========================================================

    private static Preference sInstance;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    // ===========================================================
    // Constructors
    // ===========================================================

    private Preference(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mSharedPreferences.edit();
    }

    public static Preference getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Preference(context);
        }

        return sInstance;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public void setUserToken(String token) {
        mEditor.putString(PREF_USER_TOKEN, token);
        mEditor.apply();
    }

    public String getUserToken() {
        return mSharedPreferences.getString(PREF_USER_TOKEN, null);
    }

    public void setUserName(String userName) {
        mEditor.putString(PREF_USER_NAME, userName);
        mEditor.apply();
    }

    public String getUserName() {
        return mSharedPreferences.getString(PREF_USER_NAME, null);
    }

    public void setUserLanguage(String language) {
        mEditor.putString(PREF_USER_LANGUAGE, language);
        mEditor.apply();
    }

    public String getUserLanguage() {
        return mSharedPreferences.getString(PREF_USER_LANGUAGE, null);
    }

    public void setUserId(long userId) {
        mEditor.putLong(PREF_USER_ID, userId);
        mEditor.apply();
    }

    public Long getUserId() {
        return mSharedPreferences.getLong(PREF_USER_ID, 0);
    }


    // ===========================================================
    // Listeners
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
