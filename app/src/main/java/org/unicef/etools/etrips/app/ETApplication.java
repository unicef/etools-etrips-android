package org.unicef.etools.etrips.app;

import android.app.Application;
import android.os.StrictMode;

import com.squareup.leakcanary.LeakCanary;

import org.unicef.etools.etrips.BuildConfig;
import org.unicef.etools.etrips.util.Constant;

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
        LeakCanary.install(this);
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
        //TODO set migration class in release version
        RealmConfiguration config = new RealmConfiguration.Builder()
//                .schemaVersion(0) // Must be bumped when the schema changes
//                .migration(new Migration()) // Migration to run instead of throwing an exception
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}