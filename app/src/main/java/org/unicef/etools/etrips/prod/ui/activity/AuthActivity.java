package org.unicef.etools.etrips.prod.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.google.common.eventbus.Subscribe;

import org.unicef.etools.etrips.prod.BuildConfig;
import org.unicef.etools.etrips.prod.R;
import org.unicef.etools.etrips.prod.db.entity.user.User;
import org.unicef.etools.etrips.prod.io.bus.BusProvider;
import org.unicef.etools.etrips.prod.io.bus.event.ApiEvent;
import org.unicef.etools.etrips.prod.io.bus.event.Event;
import org.unicef.etools.etrips.prod.io.rest.retrofit.RetrofitUtil;
import org.unicef.etools.etrips.prod.util.AppUtil;
import org.unicef.etools.etrips.prod.util.Constant;
import org.unicef.etools.etrips.prod.util.EncodeUtil;
import org.unicef.etools.etrips.prod.util.Preference;
import org.unicef.etools.etrips.prod.util.manager.DialogManager;
import org.unicef.etools.etrips.prod.util.manager.LogInManager;
import org.unicef.etools.etrips.prod.util.manager.SnackBarManager;
import org.unicef.etools.etrips.prod.util.widget.EdittextSpinner;

import io.realm.Realm;
import retrofit2.Call;

public class AuthActivity extends BaseActivity implements View.OnClickListener,
        TextWatcher {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = AuthActivity.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private Button mBtnSign;
    private TextInputLayout mTilEmail;
    private TextInputEditText mTietEmail;
    private TextInputLayout mTilPass;
    private TextInputEditText mTietPass;
    private EdittextSpinner mEtsLanguages;
    private LogInManager mLogInManager;

    private Call mTokenRequest;
    private Call mProfileRequest;
    private Call mWbsGruntsFundRequest;
    private Call mCurrenciesRequest;
    private Call mStaticDataRequest;


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
        BusProvider.register(this);
        mLogInManager = new LogInManager();
        findViews();
        setListeners();
        initLanguageSpinner();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.unregister(this);
        cancelRequests(false);
        Realm.getDefaultInstance().close();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_auth;
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
    // Observer callback
    // ===========================================================

    @Subscribe
    public void onEventReceived(Event event) {
        if (event instanceof ApiEvent) {
            if (event.getSubscriber().equals(getClass().getSimpleName())) {
                handleApiEvents((ApiEvent) event);
            }
        }
    }

    // ===========================================================
    // Observer methods
    // ===========================================================

    private void handleApiEvents(ApiEvent event) {

        switch (event.getEventType()) {

            case Event.EventType.Api.TOKEN_REQUEST_COMPLETED:
                mLogInManager.setRequestStatus(LogInManager.Request.GET_TOKEN, true);

                mProfileRequest = RetrofitUtil.getProfile(
                        this,
                        getClass().getSimpleName()
                );
                break;

            case Event.EventType.Api.PROFILE_LOADED:
                User user = Realm.getDefaultInstance().where(User.class)
                        .equalTo("id", Preference.getInstance(this).getUserId()).findFirst();

                if (user == null || !user.isValid() || user.getTravel2Field() == null) {
                    cancelRequests(true);
                    SnackBarManager.show(this, getString(R.string.msg_unknown_error), SnackBarManager.Duration.LONG);
                    return;
                }

                mLogInManager.setRequestStatus(LogInManager.Request.GET_PROFILE, true);

                startAdditionalDataRequests(user);
                break;

            case Event.EventType.Api.WBS_GRANTS_FUNDS_LOADED:
                mLogInManager.setRequestStatus(LogInManager.Request.GET_WBS_GRANTS_FUNDS, true);
                break;

            case Event.EventType.Api.CURRENCIES_LOADED:
                mLogInManager.setRequestStatus(LogInManager.Request.GET_CURRENCIES, true);
                break;

            case Event.EventType.Api.STATIC_DATA_LOADED:
                mLogInManager.setRequestStatus(LogInManager.Request.GET_STATIC_DATA, true);
                break;

            case Event.EventType.Api.Error.NO_NETWORK:
                cancelRequests(true);
                SnackBarManager.show(this, getString(R.string.msg_network_connection_error), SnackBarManager.Duration.LONG);
                break;

            case Event.EventType.Api.Error.PAGE_NOT_FOUND:
                cancelRequests(true);
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, getString(R.string.msg_page_not_found));
                break;

            case Event.EventType.Api.Error.BAD_REQUEST:
                cancelRequests(true);
                String body = (String) event.getEventData();
                if (body != null) {
                    SnackBarManager.show(this, body, SnackBarManager.Duration.LONG);
                    if (BuildConfig.isDEBUG)
                        Log.i(LOG_TAG, getString(R.string.msg_bad_request) + body);
                }
                break;

            case Event.EventType.Api.Error.UNAUTHORIZED:
                cancelRequests(true);
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, getString(R.string.msg_not_authorized));
                break;

            case Event.EventType.Api.Error.UNKNOWN:
                cancelRequests(true);
                if (BuildConfig.isDEBUG) {
                    SnackBarManager.show(this, getString(R.string.msg_unknown_error), SnackBarManager.Duration.LONG);
                } else {
                    SnackBarManager.show(this, getString(R.string.msg_wrong_credentials), SnackBarManager.Duration.LONG);
                }
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, getString(R.string.msg_unknown_error));
                break;
        }

        if (mLogInManager.isLogInCompleted()) {
            DialogManager.getInstance().dismissPreloader(getClass());
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (getCurrentFocus() != null) {
            switch (getCurrentFocus().getId()) {
                case R.id.tiet_auth_pass:
                    mTilPass.setError(null);
                    break;

                case R.id.tiet_auth_email:
                    mTilEmail.setError(null);
                    break;
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    // ===========================================================
    // Click Listeners
    // ===========================================================

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_auth_sign:
                String mail = mTietEmail.getText().toString();
                String pass = mTietPass.getText().toString();
                String language = mEtsLanguages.getText().toString();
                grabDataAndSingIn(mail, pass, language);
                break;
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void setListeners() {
        mBtnSign.setOnClickListener(this);
        mTietPass.addTextChangedListener(this);
        mTietEmail.addTextChangedListener(this);
    }

    private void findViews() {
        mTilEmail = (TextInputLayout) findViewById(R.id.til_auth_email);
        mTietEmail = (TextInputEditText) findViewById(R.id.tiet_auth_email);
        mTilPass = (TextInputLayout) findViewById(R.id.til_auth_pass);
        mTietPass = (TextInputEditText) findViewById(R.id.tiet_auth_pass);
        mEtsLanguages = (EdittextSpinner) findViewById(R.id.ets_auth_languages);
        mBtnSign = (Button) findViewById(R.id.btn_auth_sign);
    }

    private void initLanguageSpinner() {
        // retrieve languages list
        String[] languages = getResources().getStringArray(R.array.languages);

        // set English as default language
        mEtsLanguages.setText(languages[0]);

        // init and setup adapter with custom item layout
        ArrayAdapter<String> languageArrayAdapter = new ArrayAdapter<>(
                this,
                R.layout.adapter_item_language,
                R.id.tv_item_language_title,
                languages
        );
        mEtsLanguages.setAdapter(languageArrayAdapter);
    }

    private void grabDataAndSingIn(String mail, String pass, String language) {
        boolean isValidationSucceeded = true;

        // validate password (empty or not)
        if (pass.trim().length() == 0) {
            isValidationSucceeded = false;
            mTilPass.setError(getString(R.string.msg_edt_pass_error));
        }

        // validate email (empty or not)
        if (mail.trim().length() == 0) {
            isValidationSucceeded = false;
            mTilEmail.setError(getString(R.string.msg_edt_error_mail));
        }

        // if required fields are filled up then proceed with sign in
        if (isValidationSucceeded) {
            mTilEmail.setErrorEnabled(false);
            mTilPass.setErrorEnabled(false);
            DialogManager.getInstance().showPreloader(this, getClass().getSimpleName());

            AppUtil.closeKeyboard(this);

            Preference.getInstance(this).setUserLanguage(language);

            if (BuildConfig.isDEBUG) {
                mTokenRequest = RetrofitUtil.getStagingToken(
                        this,
                        getValidatedEmail(EncodeUtil.escapeString(mail)),
                        EncodeUtil.escapeString(pass),
                        getClass().getSimpleName()
                );
            } else {
                mTokenRequest = RetrofitUtil.getProductionToken(
                        this,
                        getValidatedEmail(EncodeUtil.escapeString(mail)),
                        EncodeUtil.escapeString(pass),
                        getClass().getSimpleName()
                );
            }
        }
    }

    private String getValidatedEmail(String mail) {
        if (!mail.contains(Constant.Util.USERNAME_POSTFIX)) {
            mail = mail + Constant.Util.USERNAME_POSTFIX;
        }
        return mail;
    }

    private void startAdditionalDataRequests(User user) {
        mWbsGruntsFundRequest = RetrofitUtil.getWbsGrantsFunds(
                this,
                user.getTravel2Field().getBusinessArea(),
                getClass().getSimpleName()
        );
        mCurrenciesRequest = RetrofitUtil.getCurrencies(
                this,
                getClass().getSimpleName()
        );
        mStaticDataRequest = RetrofitUtil.getStaticData(
                this,
                getClass().getSimpleName()
        );
    }

    private void cancelRequests(boolean releaseToken) {
        if (releaseToken) {
            Preference.getInstance(this).setUserToken(null);
        }
        if (mTokenRequest != null) {
            mTokenRequest.cancel();
        }
        if (mProfileRequest != null) {
            mProfileRequest.cancel();
        }
        if (mWbsGruntsFundRequest != null) {
            mWbsGruntsFundRequest.cancel();
        }
        if (mStaticDataRequest != null) {
            mStaticDataRequest.cancel();
        }
        if (mCurrenciesRequest != null) {
            mCurrenciesRequest.cancel();
        }
        mLogInManager.resetStatuses();
        DialogManager.getInstance().dismissPreloader(getClass());
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}