package org.unicef.etools.etrips.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;
import net.hockeyapp.android.UpdateManager;

import org.unicef.etools.etrips.BuildConfig;
import org.unicef.etools.etrips.R;

public class HockeyAppUtil {

    private static final String LOG_TAG = HockeyAppUtil.class.getSimpleName();

    public static void registerUpdateManager(Context context) {
//        if (BuildConfig.BUILD_TYPE.equals(Constant.BuildType.RELEASE)) {
        UpdateManager.register((Activity) context);
//        }
    }

    public static void unregisterUpdateManager(Context context) {
        UpdateManager.register((Activity) context);
    }

    public static void registerCrashManager(Context context) {
//        if (BuildConfig.BUILD_TYPE.equals(Constant.BuildType.RELEASE)) {
        CrashManager.register(context, context.getString(R.string.hockey_app_id), new CrashManagerListener() {
            public boolean shouldAutoUploadCrashes() {
                return true;
            }
        });
        if (BuildConfig.isDEBUG) Log.i(LOG_TAG, "Hockey app crash manager registered");
//        }
    }
}
