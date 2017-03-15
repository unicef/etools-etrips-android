package org.unicef.etools.etrips.prod.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import org.unicef.etools.etrips.prod.io.rest.entity.TripListResponse;
import org.unicef.etools.etrips.prod.io.rest.retrofit.RetrofitUtil;
import org.unicef.etools.etrips.prod.io.rest.util.APIUtil;
import org.unicef.etools.etrips.prod.ui.activity.TripActivity;
import org.unicef.etools.etrips.prod.ui.adapter.TripAdapter;
import org.unicef.etools.etrips.prod.util.AppUtil;
import org.unicef.etools.etrips.prod.util.Constant;
import org.unicef.etools.etrips.prod.util.NetworkUtil;
import org.unicef.etools.etrips.prod.util.Preference;
import org.unicef.etools.etrips.prod.util.manager.SnackBarManager;
import org.unicef.etools.etrips.prod.util.receiver.NetworkStateReceiver;
import org.unicef.etools.etrips.prod.util.widget.EmptyState;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class SupervisedFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, RealmChangeListener<RealmResults<Trip>>, TripAdapter.OnItemClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = SupervisedFragment.class.getSimpleName();

    private static final int UNDEFINED_PAGE = 0;
    private static final int FIRST_PAGE = 1;

    // ===========================================================
    // Fields
    // ===========================================================

    private boolean isLoading;
    private boolean isRestoringRequest;
    private boolean shouldLoadAllEvents;

    private int mCurrentPage;
    private int mTotalItemsCount;

    private TripAdapter mTripAdapter;
    private EmptyState mEmptyState;
    private RecyclerView mRvTrips;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RealmResults<Trip> mRealmTrips;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supervised, container, false);
        findViews(view);
        setListeners();

        BusProvider.register(this);

        return view;
    }

    private void setListeners() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRvTrips.addOnScrollListener(mScrollListener);
    }

    private void findViews(View view) {
        mEmptyState = (EmptyState) view.findViewById(R.id.es_supervised);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_supervised_trips);
        mRvTrips = (RecyclerView) view.findViewById(R.id.rv_supervised_trips_list);
    }

    private void initListComponents() {
        final LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRvTrips.setLayoutManager(layoutManager);

        mTripAdapter = new TripAdapter(new ArrayList<Trip>(), this);
        mRvTrips.setAdapter(mTripAdapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initListComponents();
        if (savedInstanceState != null) {
            mCurrentPage = savedInstanceState.getInt(Constant.Extra.EXTRA_TRIPS_PAGE);
        }

        if (mCurrentPage == UNDEFINED_PAGE) {
            loadTripsFromServer(FIRST_PAGE);
        } else {
            isRestoringRequest = true;
            startLoading();
            retrieveTripsFromDb();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constant.Extra.EXTRA_TRIPS_PAGE, mCurrentPage);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        releaseResources();
    }

    private void releaseResources() {
        BusProvider.unregister(this);
        if (mRealmTrips != null) {
            mRealmTrips.removeChangeListener(this);
        }
        Realm.getDefaultInstance().close();
        NetworkStateReceiver.unregisterBroadcast(getActivity());

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.destroyDrawingCache();
            mSwipeRefreshLayout.clearAnimation();
        }
    }

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Click Listeners
    // ===========================================================

    @Override
    public void onItemClick(Trip trip) {
        if (trip != null && trip.isValid()) {
            Intent intent = new Intent(getActivity(), TripActivity.class);
            intent.putExtra(Constant.Extra.EXTRA_TRIP_ID, trip.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onItemLongClick(Trip trip) {

    }

    @Override
    public void onRefresh() {
        loadTripsFromServer(FIRST_PAGE);
    }

    @Override
    public void onChange(RealmResults<Trip> element) {
        Log.d(LOG_TAG, "onChange - element size: " + element.size());
        if (element.size() == 0) {
            // There are no Trips in our database, so clear our UI list and show empty state.
            mTripAdapter.clear();
            mEmptyState.setVisibility(View.VISIBLE);
        } else {
//            final List<Trip> page = getPageFromRealResults(element);
            mTripAdapter.add(element, true);
            if (mEmptyState.getVisibility() == View.VISIBLE) {
                mEmptyState.setVisibility(View.GONE);
            }
        }
        if (mTotalItemsCount > mTripAdapter.getItemCount()) {
            mTripAdapter.showLoadMore();
        } else {
            mTripAdapter.removeLoadMore();
        }
        finishLoading();
    }

    private List<Trip> getPageFromRealResults(@NonNull RealmResults<Trip> element) {
        if (element.isEmpty()) {
            return element;
        }

        if (shouldLoadAllEvents) {
            return element;
        }

        final int offset = mCurrentPage - 1;
        final int start;
        int end;

        if (isRestoringRequest) {
            start = 0;
        } else {
            start = offset * APIUtil.PER_PAGE_ACTION_POINTS;
        }
        end = (offset + 1) * APIUtil.PER_PAGE_ACTION_POINTS;
        if (end > element.size()) {
            end = element.size();
        }

        return element.subList(start, end);
    }

    private boolean isPaginationRequest() {
        return mCurrentPage > FIRST_PAGE;
    }

    // ===========================================================
    // Methods
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

    private void handleNetworkEvent(NetworkEvent event) {
        switch (event.getEventType()) {
            case Event.EventType.Network.CONNECTED:
                loadTripsFromServer(FIRST_PAGE);
                break;
        }
    }

    private void handleApiEvents(ApiEvent event) {
        if (event.getEventType() == Event.EventType.Api.TRIPS_LOADED) {
            TripListResponse tripListResponse = (TripListResponse) event.getEventData();
            if (tripListResponse != null) {
                mTotalItemsCount = tripListResponse.getTotalCount();
            }
            // Add Realm change listener only one time. Then onChange will be called by Realm.
            if (mRealmTrips == null) {
                retrieveTripsFromDb();
            } else {
                // If it is not the first request to server, and we have no data,
                // onChange will not be called, so hide progress.
                if (mRealmTrips.isEmpty()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        } else {
            finishLoading();
            if (isPaginationRequest()) {
                --mCurrentPage;
            }

            switch (event.getEventType()) {
                case Event.EventType.Api.Error.NO_NETWORK:
                    // Try load all events from database in case of lack of internet during first load.
                    if (mRealmTrips == null) {
                        shouldLoadAllEvents = true;
                        retrieveTripsFromDb();
                    }

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
    }

    private void loadTripsFromServer(int page) {
        mCurrentPage = page;
        startLoading();

        RetrofitUtil.getSupervisedTrips(getActivity(), mCurrentPage,
                Preference.getInstance(getActivity()).getUserId(), getClass().getSimpleName());
    }

    private void retrieveTripsFromDb() {
        mRealmTrips = Realm.getDefaultInstance()
                .where(Trip.class)
                .equalTo("isMyTrip", false)
                .findAllSortedAsync(new String[]{"startDate", "referenceNumber"}, new Sort[]{Sort.ASCENDING, Sort.ASCENDING});

        mRealmTrips.addChangeListener(this);
    }

    public void startLoading() {
        isLoading = true;
        if (isRestoringRequest || !isPaginationRequest()) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    private void finishLoading() {
        isLoading = false;
        if (isRestoringRequest || !isPaginationRequest()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (isRestoringRequest) {
            isRestoringRequest = false;
        }
        if (shouldLoadAllEvents) {
            shouldLoadAllEvents = false;
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            final LinearLayoutManager layoutManager
                    = (LinearLayoutManager) recyclerView.getLayoutManager();

            final int visibleItemCount = layoutManager.getChildCount();
            final int totalItemCount = layoutManager.getItemCount();
            final int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if (shouldPaginate(visibleItemCount, totalItemCount, firstVisibleItemPosition)) {
                loadTripsFromServer(mCurrentPage + 1);
            }
        }
    };

    private boolean shouldPaginate(int visibleCount, int totalCount, int firstVisiblePosition) {
        return NetworkUtil.getInstance().isConnected(getActivity())
                && !isLoading && (visibleCount + firstVisiblePosition) >= totalCount
                && firstVisiblePosition >= 0
                && mTotalItemsCount > totalCount;
    }
}