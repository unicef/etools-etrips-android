package org.unicef.etools.etrips.prod.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.eventbus.Subscribe;

import org.unicef.etools.etrips.prod.BuildConfig;
import org.unicef.etools.etrips.prod.R;
import org.unicef.etools.etrips.prod.db.entity.trip.Trip;
import org.unicef.etools.etrips.prod.io.bus.BusProvider;
import org.unicef.etools.etrips.prod.io.bus.event.ApiEvent;
import org.unicef.etools.etrips.prod.io.bus.event.Event;
import org.unicef.etools.etrips.prod.io.bus.event.NetworkEvent;
import org.unicef.etools.etrips.prod.io.rest.url_connection.HttpRequestManager;
import org.unicef.etools.etrips.prod.io.rest.util.APIUtil;
import org.unicef.etools.etrips.prod.io.service.ETService;
import org.unicef.etools.etrips.prod.ui.activity.TripActivity;
import org.unicef.etools.etrips.prod.ui.adapter.TripAdapter;
import org.unicef.etools.etrips.prod.util.AppUtil;
import org.unicef.etools.etrips.prod.util.Constant;
import org.unicef.etools.etrips.prod.util.Preference;
import org.unicef.etools.etrips.prod.util.manager.DialogManager;
import org.unicef.etools.etrips.prod.util.manager.SnackBarManager;
import org.unicef.etools.etrips.prod.util.receiver.NetworkStateReceiver;
import org.unicef.etools.etrips.prod.util.widget.EmptyState;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

import static org.unicef.etools.etrips.prod.io.rest.util.APIUtil.SUPERVISED_TRIPS;

public class SupervisedFragment extends BaseFragment implements View.OnClickListener,
        TripAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener,
        RealmChangeListener<RealmResults<Trip>> {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = SupervisedFragment.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private RecyclerView mRvTrips;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<Trip> mTripList;
    private TripAdapter mTripAdapter;
    private EmptyState mEmptyState;

    // ===========================================================
    // Constructors
    // ===========================================================

    public static SupervisedFragment newInstance() {
        return new SupervisedFragment();
    }

    public static SupervisedFragment newInstance(Bundle args) {
        SupervisedFragment fragment = new SupervisedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supervised, container, false);
        findViews(view);
        setListeners();
        init();
        loadTripsFromServer(APIUtil.getURL(String.format(SUPERVISED_TRIPS,
                Preference.getInstance(getActivity()).getUserId())));
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.unregister(this);
        DialogManager.getInstance().dismissPreloader(getActivity().getClass());
        NetworkStateReceiver.unregisterBroadcast(getActivity());
        dismissSwipeRefreshLayout();
        Realm.getDefaultInstance().close();
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
                loadTripsFromServer(APIUtil.getURL(String.format(SUPERVISED_TRIPS,
                        Preference.getInstance(getActivity()).getUserId())));
                break;
        }
    }

    private void handleApiEvents(ApiEvent event) {
        DialogManager.getInstance().dismissPreloader(getActivity().getClass());
        mSwipeRefreshLayout.setRefreshing(false);

        switch (event.getEventType()) {
            case Event.EventType.Api.TRIPS_LOADED:
                retrieveTripsFromDb();
                break;

            case Event.EventType.Api.Error.NO_NETWORK:
                NetworkStateReceiver.registerBroadcast(getActivity());
                SnackBarManager.show(getActivity(), getString(R.string.msg_network_connection_error),
                        SnackBarManager.Duration.LONG);
                break;

            case Event.EventType.Api.Error.PAGE_NOT_FOUND:
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, getString(R.string.msg_page_not_found));
                break;

            case Event.EventType.Api.Error.BAD_REQUEST:
                String body = (String) event.getEventData();
                if (body != null) {
                    SnackBarManager.show(getActivity(), body, SnackBarManager.Duration.LONG);
                    if (BuildConfig.isDEBUG)
                        Log.i(LOG_TAG, getString(R.string.msg_bad_request) + body);
                }
                break;

            case Event.EventType.Api.Error.UNAUTHORIZED:
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, getString(R.string.msg_not_authorized));
                AppUtil.logout(getActivity());
                break;

            case Event.EventType.Api.Error.UNKNOWN:
                SnackBarManager.show(getActivity(), getString(R.string.msg_unknown_error),
                        SnackBarManager.Duration.LONG);
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, getString(R.string.msg_unknown_error));
                break;
        }
    }

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    @Override
    public void onRefresh() {
        loadTripsFromServer(APIUtil.getURL(String.format(SUPERVISED_TRIPS,
                Preference.getInstance(getActivity()).getUserId())));
    }

    @Override
    public void onChange(RealmResults<Trip> element) {
        populateList(element);
    }

    // ===========================================================
    // Click Listeners
    // ===========================================================

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(Trip trip) {
        Intent intent = new Intent(getActivity(), TripActivity.class);
        intent.putExtra(Constant.Extra.EXTRA_TRIP_ID, trip.getId());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Trip trip) {

    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void setListeners() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void findViews(View view) {
        mEmptyState = (EmptyState) view.findViewById(R.id.es_supervised);
        mRvTrips = (RecyclerView) view.findViewById(R.id.rv_supervised_trips_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_supervised_trips);
    }

    private void init() {
        // register BusProvider to receive necessary events from service or other senders
        BusProvider.register(this);

        // init ll manager, list and adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRvTrips.setLayoutManager(linearLayoutManager);
        mTripList = new ArrayList<>();
        mTripAdapter = new TripAdapter(mTripList, this);
        mRvTrips.setAdapter(mTripAdapter);
    }

    private void loadTripsFromServer(String apiUrl) {
        // call server to retrieve supervised trips
        mSwipeRefreshLayout.setRefreshing(true);
        ETService.start(
                getActivity(),
                getClass().getSimpleName(),
                apiUrl,
                HttpRequestManager.RequestType.GET_SUPERVISED_TRIPS
        );
    }

    private void populateList(List<Trip> tripArrayList) {
        Log.d(LOG_TAG, String.valueOf(tripArrayList.size()));
        mTripList.clear();
        mTripList.addAll(tripArrayList);
        mTripAdapter.notifyDataSetChanged();
        if (mTripList.isEmpty()){
            mEmptyState.setVisibility(View.VISIBLE);
        } else {
            mEmptyState.setVisibility(View.GONE);
        }
    }

    private void retrieveTripsFromDb() {
        RealmResults<Trip> trips = Realm.getDefaultInstance()
                .where(Trip.class)
                .equalTo("isMyTrip", false)
                .findAllAsync();
        trips.addChangeListener(this);
    }

    private void dismissSwipeRefreshLayout() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.destroyDrawingCache();
            mSwipeRefreshLayout.clearAnimation();
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}