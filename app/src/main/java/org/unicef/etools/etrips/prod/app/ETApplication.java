package org.unicef.etools.etrips.prod.app;

import android.app.Application;
import android.os.StrictMode;

import com.squareup.leakcanary.LeakCanary;

import org.unicef.etools.etrips.prod.BuildConfig;
import org.unicef.etools.etrips.prod.util.Constant;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ETApplication extends Application {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = ETApplication.class.getName();

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
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public void onCreate() {
        super.onCreate();
        enableStrictMode();
        initLeakCanary();
        initRealm();
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void enableStrictMode() {
        if (BuildConfig.BUILD_TYPE.equals(Constant.BuildType.DEBUG)) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }
    }

    private void initRealm() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    private void initLeakCanary() {
        if (BuildConfig.BUILD_TYPE.equals(Constant.BuildType.DEBUG))
            LeakCanary.install(this);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}