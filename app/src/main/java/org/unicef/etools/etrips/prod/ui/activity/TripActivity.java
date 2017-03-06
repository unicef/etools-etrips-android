package org.unicef.etools.etrips.prod.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.common.eventbus.Subscribe;

import org.unicef.etools.etrips.prod.BuildConfig;
import org.unicef.etools.etrips.prod.R;
import org.unicef.etools.etrips.prod.io.bus.BusProvider;
import org.unicef.etools.etrips.prod.io.bus.event.ApiEvent;
import org.unicef.etools.etrips.prod.io.bus.event.Event;
import org.unicef.etools.etrips.prod.io.bus.event.NetworkEvent;
import org.unicef.etools.etrips.prod.io.rest.url_connection.HttpRequestManager;
import org.unicef.etools.etrips.prod.io.rest.util.APIUtil;
import org.unicef.etools.etrips.prod.io.service.ETService;
import org.unicef.etools.etrips.prod.ui.adapter.TabFragmentAdapter;
import org.unicef.etools.etrips.prod.ui.fragment.trip.ReportFragment;
import org.unicef.etools.etrips.prod.ui.fragment.trip.TripActionPointsFragment;
import org.unicef.etools.etrips.prod.ui.fragment.trip.TripFragment;
import org.unicef.etools.etrips.prod.util.AppUtil;
import org.unicef.etools.etrips.prod.util.Constant;
import org.unicef.etools.etrips.prod.util.manager.DialogManager;
import org.unicef.etools.etrips.prod.util.manager.SnackBarManager;
import org.unicef.etools.etrips.prod.util.receiver.NetworkStateReceiver;

import static org.unicef.etools.etrips.prod.io.rest.util.APIUtil.TRIP;
import static org.unicef.etools.etrips.prod.io.rest.util.APIUtil.getURL;

public class TripActivity extends BaseActivity implements View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = TripActivity.class.getSimpleName();
    public static final int DEFAULT_VALUE_TRIP_ID = 0;

    // ===========================================================
    // Fields
    // ===========================================================

    private long mTripId;
    private ViewPager mViewPager;

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
        loadTripFromServer(true, APIUtil.getURL(String.format(TRIP, mTripId)));
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
        BusProvider.unregister(this);
        NetworkStateReceiver.unregisterBroadcast(this);
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
        }
    }

    // ===========================================================
    // Observer methods
    // ===========================================================

    private void handleNetworkEvent(NetworkEvent event) {
        switch (event.getEventType()) {
            case Event.EventType.Network.CONNECTED:
                loadTripFromServer(false, getURL(String.format(TRIP, mTripId)));
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

            fragmentAdapter.addFragment(TripFragment.newInstance(mTripId),
                    getString(R.string.tab_text_trip));

            fragmentAdapter.addFragment(ReportFragment.newInstance(mTripId),
                    getString(R.string.tab_text_report));

            fragmentAdapter.addFragment(TripActionPointsFragment.newInstance(mTripId),
                    getString(R.string.tab_text_action_points));

            mViewPager.setAdapter(fragmentAdapter);
            getTabLayout().setupWithViewPager(mViewPager);
        }
    }

    private void loadTripFromServer(boolean showPreloader, String apiUrl) {
        // call server to retrieve single trip
        if(showPreloader) {
            DialogManager.getInstance().showPreloader(this, getClass().getSimpleName());
        }
        ETService.start(
                this,
                getClass().getSimpleName(),
                apiUrl,
                HttpRequestManager.RequestType.GET_TRIP
        );
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}