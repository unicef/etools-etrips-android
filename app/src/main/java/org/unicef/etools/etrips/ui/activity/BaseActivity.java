package org.unicef.etools.etrips.ui.activity;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.unicef.etools.etrips.R;

public abstract class BaseActivity extends AppCompatActivity {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = BaseActivity.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private TextView mTvToolbarTitle;
    private TextView mTvSubToolbarTitle;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;

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
        setContentView(getLayoutResource());
        findViews();

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    private void findViews() {
        mTabLayout = (TabLayout) findViewById(R.id.ctl);
        mToolbar = (Toolbar) findViewById(R.id.tb);
        if (mToolbar != null) {
            mTvToolbarTitle = (TextView) mToolbar.findViewById(R.id.tv_toolbar_title);
            mTvSubToolbarTitle = (TextView) mToolbar.findViewById(R.id.tv_toolbar_subtitle);
        }
    }

    protected abstract int getLayoutResource();

    @Nullable
    protected Toolbar getToolBar() {
        return mToolbar;
    }

    @Nullable
    protected TabLayout getTabLayout() {
        return mTabLayout;
    }

    protected void hideActionBar() {
        getSupportActionBar().hide();
    }

    protected void showActionBar() {
        getSupportActionBar().show();
    }

    public void setActionBarTitle(String title) {
        mTvToolbarTitle.setText(title);
    }

    public void setActionBarSubTitle(String subtitle) {
        mTvSubToolbarTitle.setVisibility(View.VISIBLE);
        mTvSubToolbarTitle.setText(subtitle);
    }

    protected void hideActionBarTitle() {
        mTvSubToolbarTitle.setVisibility(View.GONE);
    }

    protected void hideActionBarSubTitle() {
        mTvToolbarTitle.setVisibility(View.GONE);
    }

    protected void showActionBarTitle() {
        mTvToolbarTitle.setVisibility(View.VISIBLE);
    }

    public void setActionBarIcon(@DrawableRes int iconRes) {
        getSupportActionBar().setHomeAsUpIndicator(iconRes);
    }

    public void hideActionBarIcon() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public void showActionBarIcon() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
