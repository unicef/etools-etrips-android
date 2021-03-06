package org.unicef.etools.etrips.prod.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;

import org.unicef.etools.etrips.prod.BuildConfig;
import org.unicef.etools.etrips.prod.R;
import org.unicef.etools.etrips.prod.db.entity.user.UserStatic;
import org.unicef.etools.etrips.prod.io.bus.BusProvider;
import org.unicef.etools.etrips.prod.io.bus.event.ApiEvent;
import org.unicef.etools.etrips.prod.io.bus.event.ChangeDataEvent;
import org.unicef.etools.etrips.prod.io.bus.event.Event;
import org.unicef.etools.etrips.prod.io.bus.event.NetworkEvent;
import org.unicef.etools.etrips.prod.io.rest.retrofit.RetrofitUtil;
import org.unicef.etools.etrips.prod.ui.fragment.ActionPointsFragment;
import org.unicef.etools.etrips.prod.ui.fragment.SupervisedFragment;
import org.unicef.etools.etrips.prod.ui.fragment.TripsFragment;
import org.unicef.etools.etrips.prod.util.HockeyAppUtil;
import org.unicef.etools.etrips.prod.util.Preference;
import org.unicef.etools.etrips.prod.util.manager.DialogManager;
import org.unicef.etools.etrips.prod.util.manager.FragmentTransactionManager;
import org.unicef.etools.etrips.prod.util.manager.SnackBarManager;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;

import static org.unicef.etools.etrips.prod.io.bus.event.Event.EventType.ChangeData.STOP_LOADING_USERS;
import static org.unicef.etools.etrips.prod.util.Constant.Bundle.BUNDLE_SAVED_TOOLBAR_TITLE;
import static org.unicef.etools.etrips.prod.util.manager.SnackBarManager.show;

public class MainActivity extends BaseActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private TextView mTvUserName;

    private Call mUserStaticRequest;

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
        findViews();
        setListeners();
        customizeActionBar();
        initDrawer();
        loadUsersInBackground();

        if (savedInstanceState == null) {
            openScreen(TripsFragment.newInstance(), getString(R.string.text_nav_trips));
        } else {
            setActionBarTitle(savedInstanceState.getString(BUNDLE_SAVED_TOOLBAR_TITLE));
        }
    }

    private void loadUsersInBackground() {
        // load user in background
        RealmResults<UserStatic> users = Realm.getDefaultInstance().where(UserStatic.class).findAll();
        if (users == null || users.size() == 0) {
            mUserStaticRequest = RetrofitUtil.getUsers(this, getClass().getSimpleName());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        HockeyAppUtil.registerCrashManager(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUserStaticRequest != null) {
            mUserStaticRequest.cancel();
        }
        BusProvider.unregister(this);
        DialogManager.getInstance().dismissAlertDialog(getClass());
        Realm.getDefaultInstance().close();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(BUNDLE_SAVED_TOOLBAR_TITLE, getActionBarTitle());
        super.onSaveInstanceState(outState);
    }

    // ===========================================================
    // Observer callback
    // ===========================================================

    @Subscribe
    public void onEventReceived(Event event) {
        if (event instanceof NetworkEvent) {
            handleNetworkEvent((NetworkEvent) event);

        } else if (event instanceof ApiEvent) {
            if (event.getSubscriber().equals(getClass().getSimpleName())) {
                handleApiEvents((ApiEvent) event);
            }
        } else if (event instanceof ChangeDataEvent) {
            handleChangeDataEvent((ChangeDataEvent) event);
        }
    }

    // ===========================================================
    // Observer methods
    // ===========================================================

    private void handleChangeDataEvent(ChangeDataEvent event) {
        switch (event.getEventType()) {
            case STOP_LOADING_USERS:
                if (mUserStaticRequest != null) {
                    mUserStaticRequest.cancel();
                }
                break;
        }
    }

    private void handleNetworkEvent(NetworkEvent event) {
        switch (event.getEventType()) {
            case Event.EventType.Network.CONNECTED:
                break;
        }
    }

    private void handleApiEvents(ApiEvent event) {
        switch (event.getEventType()) {
            case Event.EventType.Api.Error.NO_NETWORK:
                show(this, getString(R.string.msg_network_connection_error),
                        SnackBarManager.Duration.LONG);
                break;

            case Event.EventType.Api.Error.PAGE_NOT_FOUND:
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, getString(R.string.msg_page_not_found));
                break;

            case Event.EventType.Api.Error.BAD_REQUEST:
                String body = (String) event.getEventData();
                if (body != null) {
                    show(this, body, SnackBarManager.Duration.LONG);
                    if (BuildConfig.isDEBUG)
                        Log.i(LOG_TAG, getString(R.string.msg_bad_request) + body);
                }
                break;

            case Event.EventType.Api.Error.UNAUTHORIZED:
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, getString(R.string.msg_not_authorized));
                break;

            case Event.EventType.Api.Error.UNKNOWN:
                show(this, getString(R.string.msg_unknown_error),
                        SnackBarManager.Duration.LONG);
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, getString(R.string.msg_unknown_error));
                break;
        }
    }

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_trips:
                openScreen(TripsFragment.newInstance(), getString(R.string.text_nav_trips));
                break;

            case R.id.nav_supervised:
                openScreen(SupervisedFragment.newInstance(), getString(R.string.text_nav_supervised));
                break;

            case R.id.nav_action_points:
                openScreen(ActionPointsFragment.newInstance(), getString(R.string.text_nav_action_points));
                break;

            case R.id.nav_settings:
                // we open another activity and there is no need to set R.id.nav_settings chacked
                mNavigationView.getMenu().setGroupCheckable(R.id.menu_bottom, false, false);
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // ===========================================================
    // Click Listeners
    // ===========================================================

    @Override
    public void onClick(View v) {
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void setListeners() {
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void findViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_main);
        mNavigationView = (NavigationView) findViewById(R.id.nav_main);
        mTvUserName = (TextView) mNavigationView.getHeaderView(0)
                .findViewById(R.id.tv_drawer_header_user_name);
    }

    private void customizeActionBar() {
        setActionBarTitle(getString(R.string.app_name));
    }

    private void initDrawer() {
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                getToolBar(),
                R.string.msg_navigation_drawer_open,
                R.string.msg_navigation_drawer_close
        );
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // set authorized user name to drawer header
        mTvUserName.setText(Preference.getInstance(this).getUserName());
    }

    private void openScreen(Fragment fragment, String screenName) {

        // on first start we have to open "My trips" screen
        // and set nav_trips item in drawer checked
        if (screenName.equals(getString(R.string.text_nav_trips)))
            mNavigationView.setCheckedItem(R.id.nav_trips);

        // set screen name for corresponding fragment
        setActionBarTitle(screenName);

        // add corresponding fragment
        FragmentTransactionManager.displayFragment(
                getSupportFragmentManager(),
                fragment,
                R.id.fl_main_container,
                false
        );
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}