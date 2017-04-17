package org.unicef.etools.etrips.prod.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.common.eventbus.Subscribe;

import org.unicef.etools.etrips.prod.BuildConfig;
import org.unicef.etools.etrips.prod.R;
import org.unicef.etools.etrips.prod.db.entity.trip.Trip;
import org.unicef.etools.etrips.prod.io.bus.BusProvider;
import org.unicef.etools.etrips.prod.io.bus.event.ApiEvent;
import org.unicef.etools.etrips.prod.io.bus.event.Event;
import org.unicef.etools.etrips.prod.io.bus.event.NetworkEvent;
import org.unicef.etools.etrips.prod.io.rest.retrofit.RetrofitUtil;
import org.unicef.etools.etrips.prod.ui.adapter.TabFragmentAdapter;
import org.unicef.etools.etrips.prod.ui.fragment.trip.ReportFragment;
import org.unicef.etools.etrips.prod.ui.fragment.trip.TripActionPointsFragment;
import org.unicef.etools.etrips.prod.ui.fragment.trip.TripFragment;
import org.unicef.etools.etrips.prod.util.AppUtil;
import org.unicef.etools.etrips.prod.util.Constant;
import org.unicef.etools.etrips.prod.util.OnTabTitleChangeListener;
import org.unicef.etools.etrips.prod.util.manager.DialogManager;
import org.unicef.etools.etrips.prod.util.manager.SnackBarManager;
import org.unicef.etools.etrips.prod.util.receiver.NetworkStateReceiver;

import io.realm.Realm;
import retrofit2.Call;

public class TripActivity extends BaseActivity implements View.OnClickListener,
        OnTabTitleChangeListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = TripActivity.class.getSimpleName();
    public static final int DEFAULT_VALUE_TRIP_ID = 0;
    public static final int OFFSCREEN_PAGE_LIMIT = 3;

    // ===========================================================
    // Fields
    // ===========================================================

    private long mTripId;
    private ViewPager mViewPager;
    private Call mTripRequest;

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
        init();
        getData();
        loadTripFromServer(true);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_trip;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTripRequest != null) {
            mTripRequest.cancel();
        }
        BusProvider.unregister(this);
        NetworkStateReceiver.unregisterBroadcast(this);
    }

    // ===========================================================
    // Observer callback
    // ===========================================================

    @Subscribe
    public void onEventReceived(Event event) {
        if (event instanceof NetworkEvent) {
            if (event.getSubscriber().equals(getClass().getSimpleName())) {
                handleNetworkEvent((NetworkEvent) event);
            }
        } else if (event instanceof ApiEvent) {
            if (event.getSubscriber().equals(getClass().getSimpleName())) {
                handleApiEvents((ApiEvent) event);
            }
        }
    }

    // ===========================================================
    // Observer methods
    // ===========================================================

    private void handleNetworkEvent(NetworkEvent event) {
        switch (event.getEventType()) {
            case Event.EventType.Network.CONNECTED:
                loadTripFromServer(true);
                break;
        }
    }

    private void handleApiEvents(ApiEvent event) {
        DialogManager.getInstance().dismissPreloader(getClass());

        switch (event.getEventType()) {
            case Event.EventType.Api.TRIP_LOADED:
                setupTabs();
                break;

            case Event.EventType.Api.Error.NO_NETWORK:
                // Try load all events from database in case of lack of internet.
                setupTabs();

                NetworkStateReceiver.registerBroadcast(this);
                SnackBarManager.show(this, getString(R.string.msg_network_connection_error),
                        SnackBarManager.Duration.LONG);
                break;

            case Event.EventType.Api.Error.PAGE_NOT_FOUND:
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, getString(R.string.msg_page_not_found));
                break;

            case Event.EventType.Api.Error.BAD_REQUEST:
                String body = (String) event.getEventData();
                if (body != null) {
                    SnackBarManager.show(this, body, SnackBarManager.Duration.LONG);
                    if (BuildConfig.isDEBUG)
                        Log.i(LOG_TAG, getString(R.string.msg_bad_request) + body);
                }
                break;

            case Event.EventType.Api.Error.UNAUTHORIZED:
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, getString(R.string.msg_not_authorized));
                AppUtil.logout(this);
                break;

            case Event.EventType.Api.Error.UNKNOWN:
                SnackBarManager.show(this, getString(R.string.msg_unknown_error),
                        SnackBarManager.Duration.LONG);
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, getString(R.string.msg_unknown_error));
                break;
        }
    }

    // ===========================================================
    // Click Listeners
    // ===========================================================

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    @Override
    public void onTabTitleChanged(int tabPosition, String title) {
        if (getTabLayout() == null) {
            return;
        }
        TabLayout.Tab tab = getTabLayout().getTabAt(tabPosition);
        if (tab == null) {
            return;
        }
        tab.setText(title);
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void findViews() {
        mViewPager = (ViewPager) findViewById(R.id.vp_trip);
    }

    private void setListeners() {
    }

    private void init() {
        BusProvider.register(this);
    }

    private void getData() {
        mTripId = getIntent().getLongExtra(Constant.Extra.EXTRA_TRIP_ID, DEFAULT_VALUE_TRIP_ID);
    }

    private void setupTabs() {
        if (mViewPager != null && getTabLayout() != null) {
            TabFragmentAdapter fragmentAdapter = new TabFragmentAdapter(getSupportFragmentManager());

            setupTabTitles(mTripId, fragmentAdapter);

            fragmentAdapter.addFragment(TripFragment.newInstance(mTripId));

            fragmentAdapter.addFragment(ReportFragment.newInstance(mTripId));

            fragmentAdapter.addFragment(TripActionPointsFragment.newInstance(mTripId));

            mViewPager.setOffscreenPageLimit(OFFSCREEN_PAGE_LIMIT);
            mViewPager.setAdapter(fragmentAdapter);
            getTabLayout().setupWithViewPager(mViewPager);
        }
    }

    private void setupTabTitles(long tripId, TabFragmentAdapter adapter) {

        // TRIP tab
        adapter.addTitle(getString(R.string.tab_text_trip));

        // REPORT tab
        Trip trip = Realm.getDefaultInstance().where(Trip.class).equalTo("id", tripId).findFirst();
        if (trip == null || !trip.isValid()) {
            adapter.addTitle(getString(R.string.tab_text_report));
        } else {
            if (trip.getReport() == null || trip.getReport().isEmpty()) {
                adapter.addTitle(getString(R.string.tab_text_report));
            } else {
                if (trip.isNotSynced()) {
                    adapter.addTitle(getString(R.string.tab_text_report_draft));
                } else {
                    adapter.addTitle(getString(R.string.tab_text_report_done));
                }
            }
        }

        // ACTION POINTS tab
        adapter.addTitle(getString(R.string.tab_text_action_points));
    }

    private void loadTripFromServer(boolean showPreloader) {
        // call server to retrieve single trip
        if (showPreloader) {
            DialogManager.getInstance().showPreloader(this, getClass().getSimpleName());
        }
        mTripRequest = RetrofitUtil.getTrip(this, mTripId, getClass().getSimpleName());
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    public static class Tab {
        public static final int TRIP = 0;
        public static final int REPORT = 1;
        public static final int ACTION_POINTS = 2;
    }

}