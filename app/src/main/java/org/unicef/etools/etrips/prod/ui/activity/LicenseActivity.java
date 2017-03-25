package org.unicef.etools.etrips.prod.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.webkit.WebView;

import org.unicef.etools.etrips.prod.R;

public class LicenseActivity extends BaseActivity {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = LicenseActivity.class.getSimpleName();
    public static final String LICENSE_PATH = "file:///android_asset/license/license.htm";

    // ===========================================================
    // Fields
    // ===========================================================

    private WebView mWvLicense;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews();
        customizeActionBar();
        setData();
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_license;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ===========================================================
    // Click Listeners
    // ===========================================================

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    private void findViews() {
        mWvLicense = (WebView) findViewById(R.id.wv_license);
    }

    private void customizeActionBar() {
        setActionBarTitle(getString(R.string.screen_name_licenses));
        setActionBarIcon(R.drawable.ic_close_white);
    }

    private void setData() {
        mWvLicense.loadUrl(LICENSE_PATH);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
