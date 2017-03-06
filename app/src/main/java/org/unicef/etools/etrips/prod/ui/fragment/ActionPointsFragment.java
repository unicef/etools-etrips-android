package org.unicef.etools.etrips.prod.ui.fragment;

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
import org.unicef.etools.etrips.prod.db.entity.trip.ActionPoint;
import org.unicef.etools.etrips.prod.io.bus.BusProvider;
import org.unicef.etools.etrips.prod.io.bus.event.ApiEvent;
import org.unicef.etools.etrips.prod.io.bus.event.Event;
import org.unicef.etools.etrips.prod.io.bus.event.NetworkEvent;
import org.unicef.etools.etrips.prod.io.rest.retrofit.RetrofitUtil;
import org.unicef.etools.etrips.prod.io.rest.util.APIUtil;
import org.unicef.etools.etrips.prod.util.AppUtil;
import org.unicef.etools.etrips.prod.util.Constant;
import org.unicef.etools.etrips.prod.util.NetworkUtil;
import org.unicef.etools.etrips.prod.util.Preference;
import org.unicef.etools.etrips.prod.util.manager.SnackBarManager;
import org.unicef.etools.etrips.prod.util.receiver.NetworkStateReceiver;
import org.unicef.etools.etrips.prod.util.widget.EmptyState;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ActionPointsFragment extends BaseActionPointFragment implements
        SwipeRefreshLayout.OnRefreshListener, RealmChangeListener<RealmResults<ActionPoint>> {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = ActionPointsFragment.class.getSimpleName();

    private static final int UNDEFINED_PAGE = 0;
    private static final int FIRST_PAGE = 1;

    // ===========================================================
    // Fields
    // ===========================================================

    private boolean isLoading;
    private boolean isRestoringRequest;
    private boolean shouldLoadAllEvents;

    private int mCurrentPage;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RealmResults<ActionPoint> mRealmActionPoints;

    // ===========================================================
    // Constructors
    // ===========================================================

    public static ActionPointsFragment newInstance() {
        return new ActionPointsFragment();
    }

    public static ActionPointsFragment newInstance(Bundle args) {
        ActionPointsFragment fragment = new ActionPointsFragment();
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
        View view = inflater.inflate(R.layout.fragment_action_points, container, false);
        findViews(view);
        setListeners();

        BusProvider.register(this);

        return view;
    }

    private void setListeners() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRvActionPoints.addOnScrollListener(mScrollListener);
    }

    private void findViews(View view) {
        mEmptyState = (EmptyState) view.findViewById(R.id.es_action_points);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_my_action_points);
        mRvActionPoints = (RecyclerView) view.findViewById(R.id.rv_my_action_points);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initListComponents();
        if (savedInstanceState != null) {
            mCurrentPage = savedInstanceState.getInt(Constant.Extra.EXTRA_ACTION_POINTS_PAGE);
        }

        if (mCurrentPage == UNDEFINED_PAGE) {
            loadActionPointsFromServer(FIRST_PAGE);
        } else {
            isRestoringRequest = true;
            startLoading();
            retrieveActionPointsFromDb();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constant.Extra.EXTRA_ACTION_POINTS_PAGE, mCurrentPage);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        releaseResources();
    }

    private void releaseResources() {
        BusProvider.unregister(this);
        if (mRealmActionPoints != null) {
            mRealmActionPoints.removeChangeListener(this);
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
    public void onRefresh() {
        loadActionPointsFromServer(FIRST_PAGE);
    }

    @Override
    public void onChange(RealmResults<ActionPoint> element) {
        Log.d(LOG_TAG, "onChange - element size: " + element.size());
        if (element.size() == 0) {
            // There are no ActionPoints in our database, so clear our UI list and show empty state.
            mActionPointAdapter.clear();
            mEmptyState.setVisibility(View.VISIBLE);
        } else {
            final List<ActionPoint> page = getPageFromRealResults(element);
            mActionPointAdapter.add(page, !isPaginationRequest());

            if (mEmptyState.getVisibility() == View.VISIBLE) {
                mEmptyState.setVisibility(View.GONE);
            }
        }
        finishLoading();
    }

    private List<ActionPoint> getPageFromRealResults(@NonNull RealmResults<ActionPoint> element) {
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
                loadActionPointsFromServer(FIRST_PAGE);
                break;
        }
    }

    private void handleApiEvents(ApiEvent event) {
        if (event.getEventType() == Event.EventType.Api.ACTION_POINTS_LOADED) {
            // Add Realm change listener only one time. Then onChange will be called by Realm.
            if (mRealmActionPoints == null) {
                retrieveActionPointsFromDb();
            } else {
                // If it is not the first request to server, and we have no data,
                // onChange will not be called, so hide progress.
                if (mRealmActionPoints.isEmpty()) {
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
                    if (mRealmActionPoints == null) {
                        shouldLoadAllEvents = true;
                        retrieveActionPointsFromDb();
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
                        if (BuildConfig.isDEBUG) Log.i(LOG_TAG, getString(R.string.msg_bad_request) + body);
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

    private void loadActionPointsFromServer(int page) {
        mCurrentPage = page;
        startLoading();

        RetrofitUtil.getActionPoints(getActivity(), mCurrentPage,
                Preference.getInstance(getActivity()).getUserId(), getClass().getSimpleName());
    }

    private void retrieveActionPointsFromDb() {
        mRealmActionPoints = Realm.getDefaultInstance()
                .where(ActionPoint.class)
                .equalTo("personResponsible", Preference.getInstance(getActivity()).getUserId())
                .findAllAsync();
        mRealmActionPoints.addChangeListener(this);
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
                loadActionPointsFromServer(mCurrentPage + 1);
            }
        }
    };

    private boolean shouldPaginate(int visibleCount, int totalCount, int firstVisiblePosition) {
        return NetworkUtil.getInstance().isConnected(getActivity())
                && !isLoading && (visibleCount + firstVisiblePosition) >= totalCount
                && firstVisiblePosition >= 0
                && totalCount >= APIUtil.PER_PAGE_ACTION_POINTS;
    }
}