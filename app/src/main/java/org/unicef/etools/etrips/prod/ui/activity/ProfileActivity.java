package org.unicef.etools.etrips.prod.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.unicef.etools.etrips.prod.R;
import org.unicef.etools.etrips.prod.db.entity.user.User;
import org.unicef.etools.etrips.prod.util.AppUtil;
import org.unicef.etools.etrips.prod.util.Preference;
import org.unicef.etools.etrips.prod.util.manager.DialogManager;

import io.realm.Realm;

public class ProfileActivity extends BaseActivity implements View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = ProfileActivity.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private TextView mTvLanguage;
    private LinearLayout mLlLogout;
    private ImageView mIvProfileAvatar;
    private TextView mTvProfileName;
    private TextView mTvProfileUsername;
    private TextView mTvProfileCountry;
    private TextView mTvProfileOffice;
    private TextView mTvProfilePosition;
    private TextView mTvProfileLanguage;
    private TextView mTvLicenses;
    private User mUser;

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
        setListeners();
        customizeActionBar();
        getData();
        setData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        DialogManager.getInstance().dismissAlertDialog(getClass());
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_profile;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_profile_logout_container:
                DialogManager.getInstance().showAlertDialog(
                        this,
                        null,
                        getString(R.string.dialog_text_logout),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AppUtil.logout(ProfileActivity.this);
                            }
                        },
                        null,
                        DialogManager.DialogIdentifier.LOGOUT_DIALOG,
                        true
                );
                break;

            case R.id.tv_profile_licenses:
                startActivity(new Intent(this, LicenseActivity.class));
                break;

        }
    }

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    private void setListeners() {
        mTvLicenses.setOnClickListener(this);
        mLlLogout.setOnClickListener(this);
    }

    private void findViews() {
        mTvLanguage = (TextView) findViewById(R.id.tv_profile_language);
        mLlLogout = (LinearLayout) findViewById(R.id.ll_profile_logout_container);
        mIvProfileAvatar = (ImageView) findViewById(R.id.iv_profile_avatar);
        mTvProfileName = (TextView) findViewById(R.id.tv_profile_name);
        mTvProfileUsername = (TextView) findViewById(R.id.tv_profile_username);
        mTvProfileCountry = (TextView) findViewById(R.id.tv_profile_country);
        mTvProfileOffice = (TextView) findViewById(R.id.tv_profile_office);
        mTvProfilePosition = (TextView) findViewById(R.id.tv_profile_position);
        mTvLicenses = (TextView) findViewById(R.id.tv_profile_licenses);
    }

    private void customizeActionBar() {
        setActionBarTitle(getString(R.string.text_nav_profile));
    }

    private void setData() {
        if (mUser != null) {
            mTvLanguage.setText(Preference.getInstance(this).getUserLanguage());
            mTvProfileName.setText(mUser.getFirstName() + " " + mUser.getLastName());
            mTvProfileUsername.setText(mUser.getUserName());
            mTvProfileCountry.setText(mUser.getProfile().getCountryName());
            mTvProfileOffice.setText(mUser.getProfile().getOffice());
            mTvProfilePosition.setText(mUser.getProfile().getJobTitle());
//        Glide.with(this)
//                .load(mUser.getProfile().getJobTitle())
//                .asBitmap()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(mIvProfileAvatar);
        }
    }

    public void getData() {
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mUser = realm.where(User.class).equalTo("id",
                        Preference.getInstance(ProfileActivity.this).getUserId()).findFirst();
            }
        });
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}